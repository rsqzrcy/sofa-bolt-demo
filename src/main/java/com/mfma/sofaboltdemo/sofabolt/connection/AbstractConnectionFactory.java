/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mfma.sofaboltdemo.sofabolt.connection;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;

import com.mfma.sofaboltdemo.sofabolt.Connection;
import com.mfma.sofaboltdemo.sofabolt.ConnectionEventHandler;
import com.mfma.sofaboltdemo.sofabolt.ConnectionEventType;
import com.mfma.sofaboltdemo.sofabolt.NamedThreadFactory;
import com.mfma.sofaboltdemo.sofabolt.ProtocolCode;
import com.mfma.sofaboltdemo.sofabolt.Url;
import com.mfma.sofaboltdemo.sofabolt.codec.Codec;
import com.mfma.sofaboltdemo.sofabolt.config.ConfigManager;
import com.mfma.sofaboltdemo.sofabolt.config.ConfigurableInstance;
import com.mfma.sofaboltdemo.sofabolt.constant.Constants;
import com.mfma.sofaboltdemo.sofabolt.config.switches.GlobalSwitch;
import com.mfma.sofaboltdemo.sofabolt.log.BoltLoggerFactory;
import com.mfma.sofaboltdemo.sofabolt.rpc.RpcConfigManager;
import com.mfma.sofaboltdemo.sofabolt.rpc.protocol.RpcProtocol;
import com.mfma.sofaboltdemo.sofabolt.rpc.protocol.RpcProtocolV2;
import com.mfma.sofaboltdemo.sofabolt.util.IoUtils;
import com.mfma.sofaboltdemo.sofabolt.util.NettyEventLoopUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * ConnectionFactory to create connection.
 *
 * @author chengyi (mark.lx@antfin.com) 2018-06-20 14:29
 */
public abstract class AbstractConnectionFactory implements ConnectionFactory {

    private static final Logger         logger      = BoltLoggerFactory
                                                        .getLogger(AbstractConnectionFactory.class);

    private static final EventLoopGroup workerGroup = NettyEventLoopUtil.newEventLoopGroup(Runtime
                                                        .getRuntime().availableProcessors() + 1,
                                                        new NamedThreadFactory(
                                                            "bolt-netty-client-worker", true));

    private final ConfigurableInstance  confInstance;
    private final Codec                 codec;
    private final ChannelHandler        heartbeatHandler;
    private final ChannelHandler        handler;

    protected Bootstrap                 bootstrap;

    public AbstractConnectionFactory(Codec codec, ChannelHandler heartbeatHandler,
                                     ChannelHandler handler, ConfigurableInstance confInstance) {
        if (codec == null) {
            throw new IllegalArgumentException("null codec");
        }
        if (handler == null) {
            throw new IllegalArgumentException("null handler");
        }

        this.confInstance = confInstance;
        this.codec = codec;
        this.heartbeatHandler = heartbeatHandler;
        this.handler = handler;
    }

    @Override
    public void init(final ConnectionEventHandler connectionEventHandler) {
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).channel(NettyEventLoopUtil.getClientSocketChannelClass())
            .option(ChannelOption.TCP_NODELAY, ConfigManager.tcp_nodelay())
            .option(ChannelOption.SO_REUSEADDR, ConfigManager.tcp_so_reuseaddr())
            .option(ChannelOption.SO_KEEPALIVE, ConfigManager.tcp_so_keepalive())
            .option(ChannelOption.SO_SNDBUF, ConfigManager.tcp_so_sndbuf())
            .option(ChannelOption.SO_RCVBUF, ConfigManager.tcp_so_rcvbuf());

        // init netty write buffer water mark
        initWriteBufferWaterMark();

        // init byte buf allocator
        if (ConfigManager.netty_buffer_pooled()) {
            this.bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        } else {
            this.bootstrap.option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
        }

        final boolean flushConsolidationSwitch = this.confInstance.switches().isOn(
            GlobalSwitch.CODEC_FLUSH_CONSOLIDATION);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel channel) {
                ChannelPipeline pipeline = channel.pipeline();
                if (RpcConfigManager.client_ssl_enable()) {
                    SSLEngine engine = initSSLContext().newEngine(channel.alloc());
                    engine.setUseClientMode(true);
                    pipeline.addLast(Constants.SSL_HANDLER, new SslHandler(engine));
                }
                if (flushConsolidationSwitch) {
                    pipeline.addLast("flushConsolidationHandler", new FlushConsolidationHandler(
                        1024, true));
                }
                pipeline.addLast("decoder", codec.newDecoder());
                pipeline.addLast("encoder", codec.newEncoder());

                boolean idleSwitch = ConfigManager.tcp_idle_switch();
                if (idleSwitch) {
                    pipeline.addLast("idleStateHandler",
                        new IdleStateHandler(ConfigManager.tcp_idle(), ConfigManager.tcp_idle(), 0,
                            TimeUnit.MILLISECONDS));
                    pipeline.addLast("heartbeatHandler", heartbeatHandler);
                }

                pipeline.addLast("connectionEventHandler", connectionEventHandler);
                pipeline.addLast("handler", handler);
            }
        });
    }

    @Override
    public Connection createConnection(Url url) throws Exception {
        Channel channel = doCreateConnection(url.getIp(), url.getPort(), url.getConnectTimeout());
        Connection conn = new Connection(channel, ProtocolCode.fromBytes(url.getProtocol(),url.getVersion()),
            url.getVersion(), url);
        if (channel.isActive()) {
            channel.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT);
        } else {
            channel.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT_FAILED);
        }
        return conn;
    }

    @Override
    public Connection createConnection(String targetIP, int targetPort, int connectTimeout)
                                                                                           throws Exception {
        Channel channel = doCreateConnection(targetIP, targetPort, connectTimeout);
        Connection conn = new Connection(channel,
            ProtocolCode.fromBytes(RpcProtocolV2.PROTOCOL_CODE,RpcProtocolV2.PROTOCOL_VERSION), RpcProtocolV2.PROTOCOL_VERSION,
            new Url(targetIP, targetPort));
        if (channel.isActive()) {
            channel.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT);
        } else {
            channel.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT_FAILED);
        }
        return conn;
    }

    @Override
    public Connection createConnection(String targetIP, int targetPort, String version,
                                       int connectTimeout) throws Exception {
        Channel channel = doCreateConnection(targetIP, targetPort, connectTimeout);
        Connection conn = new Connection(channel,
            ProtocolCode.fromBytes(RpcProtocolV2.PROTOCOL_CODE,RpcProtocolV2.PROTOCOL_VERSION), version, new Url(targetIP,
                targetPort));
        if (channel.isActive()) {
            channel.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT);
        } else {
            channel.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT_FAILED);
        }
        return conn;
    }

    /**
     * init netty write buffer water mark
     */
    private void initWriteBufferWaterMark() {
        int lowWaterMark = this.confInstance.netty_buffer_low_watermark();
        int highWaterMark = this.confInstance.netty_buffer_high_watermark();
        if (lowWaterMark > highWaterMark) {
            throw new IllegalArgumentException(
                String
                    .format(
                        "[client side] bolt netty high water mark {%s} should not be smaller than low water mark {%s} bytes)",
                        highWaterMark, lowWaterMark));
        } else {
            logger.warn(
                "[client side] bolt netty low water mark is {} bytes, high water mark is {} bytes",
                lowWaterMark, highWaterMark);
        }
        this.bootstrap.option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(
            lowWaterMark, highWaterMark));
    }

    private SslContext initSSLContext() {
        InputStream in = null;
        try {
            KeyStore ks = KeyStore.getInstance(RpcConfigManager.client_ssl_keystore_type());
            in = new FileInputStream(RpcConfigManager.client_ssl_keystore());
            char[] passChs = RpcConfigManager.client_ssl_keystore_pass().toCharArray();
            ks.load(in, passChs);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(RpcConfigManager
                .client_ssl_tmf_algorithm());
            tmf.init(ks);
            return SslContextBuilder.forClient().trustManager(tmf).build();
        } catch (Exception e) {
            logger.error("Fail to init SSL context for connection factory.", e);
            throw new IllegalStateException("Fail to init SSL context", e);
        } finally {
            IoUtils.closeQuietly(in);
        }

    }

    protected Channel doCreateConnection(String targetIP, int targetPort, int connectTimeout)
                                                                                             throws Exception {
        // prevent unreasonable value, at least 1000
        connectTimeout = Math.max(connectTimeout, 1000);
        String address = targetIP + ":" + targetPort;
        if (logger.isDebugEnabled()) {
            logger.debug("connectTimeout of address [{}] is [{}].", address, connectTimeout);
        }
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(targetIP, targetPort));

        future.awaitUninterruptibly();
        if (!future.isDone()) {
            String errMsg = "Create connection to " + address + " timeout!";
            logger.warn(errMsg);
            throw new Exception(errMsg);
        }
        if (future.isCancelled()) {
            String errMsg = "Create connection to " + address + " cancelled by user!";
            logger.warn(errMsg);
            throw new Exception(errMsg);
        }
        if (!future.isSuccess()) {
            String errMsg = "Create connection to " + address + " error!";
            logger.warn(errMsg);
            throw new Exception(errMsg, future.cause());
        }
        return future.channel();
    }
}
