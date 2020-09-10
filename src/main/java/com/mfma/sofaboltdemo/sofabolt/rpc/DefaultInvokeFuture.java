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
package com.mfma.sofaboltdemo.sofabolt.rpc;

import com.mfma.sofaboltdemo.sofabolt.*;
import com.mfma.sofaboltdemo.sofabolt.log.BoltLoggerFactory;
import io.netty.util.Timeout;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The default implementation of InvokeFuture.
 *
 * @author jiangping
 * @version $Id: DefaultInvokeFuture.java, v 0.1 2015-9-27 PM6:30:22 tao Exp $
 */
public class DefaultInvokeFuture implements InvokeFuture {

    private static final Logger logger = BoltLoggerFactory.getLogger("RpcRemoting");

    private int invokeId;

    private InvokeCallbackListener callbackListener;

    private InvokeCallback callback;

    private volatile ResponseCommand responseCommand;

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private final AtomicBoolean executeCallbackOnlyOnce = new AtomicBoolean(false);

    private Timeout timeout;

    private Throwable cause;

    private ClassLoader classLoader;

    private ProtocolCode protocolCode;

    private InvokeContext invokeContext;

    private CommandFactory commandFactory;

    /**
     * Constructor.
     *
     * @param invokeId         invoke id
     * @param callbackListener callback listener
     * @param callback         callback
     * @param protocolCode     protocol code
     * @param commandFactory   command factory
     */
    public DefaultInvokeFuture(int invokeId, InvokeCallbackListener callbackListener,
                               InvokeCallback callback, ProtocolCode protocolCode, CommandFactory commandFactory) {
        this.invokeId = invokeId;
        this.callbackListener = callbackListener;
        this.callback = callback;
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.protocolCode = protocolCode;
        this.commandFactory = commandFactory;
    }

    /**
     * Constructor.
     *
     * @param invokeId         invoke id
     * @param callbackListener callback listener
     * @param callback         callback
     * @param protocolCode     protocolCode
     * @param commandFactory   command factory
     * @param invokeContext    invoke context
     */
    public DefaultInvokeFuture(int invokeId, InvokeCallbackListener callbackListener,
                               InvokeCallback callback, ProtocolCode protocolCode,
                               CommandFactory commandFactory, InvokeContext invokeContext) {
        this(invokeId, callbackListener, callback, protocolCode, commandFactory);
        this.invokeContext = invokeContext;
    }

    @Override
    public ResponseCommand waitResponse(long timeoutMillis) throws InterruptedException {
        this.countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        return this.responseCommand;
    }

    @Override
    public ResponseCommand waitResponse() throws InterruptedException {
        this.countDownLatch.await();
        return this.responseCommand;
    }

    @Override
    public RemotingCommand createConnectionClosedResponse(InetSocketAddress responseHost) {
        return this.commandFactory.createConnectionClosedResponse(responseHost, null);
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.InvokeFuture#putResponse(com.mfma.sofaboltdemo.sofabolt.RemotingCommand)
     */
    @Override
    public void putResponse(RemotingCommand response) {
        this.responseCommand = (ResponseCommand) response;
        this.countDownLatch.countDown();
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.InvokeFuture#isDone()
     */
    @Override
    public boolean isDone() {
        return this.countDownLatch.getCount() <= 0;
    }

    @Override
    public ClassLoader getAppClassLoader() {
        return this.classLoader;
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.InvokeFuture#invokeId()
     */
    @Override
    public int invokeId() {
        return this.invokeId;
    }

    @Override
    public void executeInvokeCallback() {
        if (callbackListener != null) {
            if (this.executeCallbackOnlyOnce.compareAndSet(false, true)) {
                callbackListener.onResponse(this);
            }
        }
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.InvokeFuture#getInvokeCallback()
     */
    @Override
    public InvokeCallback getInvokeCallback() {
        return this.callback;
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.InvokeFuture#addTimeout(io.netty.util.Timeout)
     */
    @Override
    public void addTimeout(Timeout timeout) {
        this.timeout = timeout;
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.InvokeFuture#cancelTimeout()
     */
    @Override
    public void cancelTimeout() {
        if (this.timeout != null) {
            this.timeout.cancel();
        }
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.InvokeFuture#setCause(java.lang.Throwable)
     */
    @Override
    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.InvokeFuture#getCause()
     */
    @Override
    public Throwable getCause() {
        return this.cause;
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.InvokeFuture#getProtocolCode()
     */
    @Override
    public ProtocolCode getProtocolCode() {
        return this.protocolCode;
    }

    /**
     * @see InvokeFuture#getInvokeContext()
     */
    @Override
    public void setInvokeContext(InvokeContext invokeContext) {
        this.invokeContext = invokeContext;
    }

    /**
     * @see InvokeFuture#setInvokeContext(InvokeContext)
     */
    @Override
    public InvokeContext getInvokeContext() {
        return invokeContext;
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.InvokeFuture#tryAsyncExecuteInvokeCallbackAbnormally()
     */
    @Override
    public void tryAsyncExecuteInvokeCallbackAbnormally() {
        try {
            Protocol protocol = ProtocolManager.getProtocol(ProtocolCode.fromBytes(protocolCode.getCode()));
            if (null != protocol) {
                CommandHandler commandHandler = protocol.getCommandHandler();
                if (null != commandHandler) {
                    ExecutorService executor = commandHandler.getDefaultExecutor();
                    if (null != executor) {
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                ClassLoader oldClassLoader = null;
                                try {
                                    if (DefaultInvokeFuture.this.getAppClassLoader() != null) {
                                        oldClassLoader = Thread.currentThread()
                                                .getContextClassLoader();
                                        Thread.currentThread().setContextClassLoader(
                                                DefaultInvokeFuture.this.getAppClassLoader());
                                    }
                                    DefaultInvokeFuture.this.executeInvokeCallback();
                                } finally {
                                    if (null != oldClassLoader) {
                                        Thread.currentThread()
                                                .setContextClassLoader(oldClassLoader);
                                    }
                                }
                            }
                        });
                    }
                } else {
                    logger.error("Executor null in commandHandler of protocolCode [{}].",
                            this.protocolCode.toString());
                }
            } else {
                logger.error("protocolCode [{}] not registered!", this.protocolCode.toString());
            }
        } catch (Exception e) {
            logger.error("Exception caught when executing invoke callback abnormally.", e);
        }
    }

}
