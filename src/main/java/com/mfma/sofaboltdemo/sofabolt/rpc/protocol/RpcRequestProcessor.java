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
package com.mfma.sofaboltdemo.sofabolt.rpc.protocol;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import org.slf4j.Logger;

import com.mfma.sofaboltdemo.sofabolt.AbstractRemotingProcessor;
import com.mfma.sofaboltdemo.sofabolt.CommandFactory;
import com.mfma.sofaboltdemo.sofabolt.InvokeContext;
import com.mfma.sofaboltdemo.sofabolt.RemotingCommand;
import com.mfma.sofaboltdemo.sofabolt.RemotingContext;
import com.mfma.sofaboltdemo.sofabolt.ResponseStatus;
import com.mfma.sofaboltdemo.sofabolt.exception.DeserializationException;
import com.mfma.sofaboltdemo.sofabolt.exception.SerializationException;
import com.mfma.sofaboltdemo.sofabolt.log.BoltLoggerFactory;
import com.mfma.sofaboltdemo.sofabolt.rpc.RpcCommandType;
import com.mfma.sofaboltdemo.sofabolt.util.RemotingUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

/**
 * Process Rpc request.
 *
 * @author jiangping
 * @version $Id: RpcRequestProcessor.java, v 0.1 2015-10-1 PM10:56:10 tao Exp $
 */
public class RpcRequestProcessor extends AbstractRemotingProcessor<RpcRequestCommand> {
    /** logger */
    private static final Logger logger = BoltLoggerFactory.getLogger("RpcRemoting");

    /**
     * Default constructor.
     */
    public RpcRequestProcessor() {
    }

    /**
     * Constructor.
     */
    public RpcRequestProcessor(CommandFactory commandFactory) {
        super(commandFactory);
    }

    /**
     * Constructor.
     */
    public RpcRequestProcessor(ExecutorService executor) {
        super(executor);
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.AbstractRemotingProcessor#process(com.mfma.sofaboltdemo.sofabolt.RemotingContext, com.mfma.sofaboltdemo.sofabolt.RemotingCommand, java.util.concurrent.ExecutorService)
     */
    @Override
    public void process(RemotingContext ctx, RpcRequestCommand cmd, ExecutorService defaultExecutor)
                                                                                                    throws Exception {
        if (!deserializeRequestCommand(ctx, cmd, RpcDeserializeLevel.DESERIALIZE_CLAZZ)) {
            return;
        }
        UserProcessor userProcessor = ctx.getUserProcessor(cmd.getRequestClass());
        if (userProcessor == null) {
            String errMsg = "No user processor found for request: " + cmd.getRequestClass();
            logger.error(errMsg);
            sendResponseIfNecessary(ctx, cmd.getType(), this.getCommandFactory()
                .createExceptionResponse(cmd.getId(), errMsg));
            return;// must end process
        }

        // set timeout check state from user's processor
        ctx.setTimeoutDiscard(userProcessor.timeoutDiscard());

        // to check whether to process in io thread
        if (userProcessor.processInIOThread()) {
            if (!deserializeRequestCommand(ctx, cmd, RpcDeserializeLevel.DESERIALIZE_ALL)) {
                return;
            }
            // process in io thread
            new ProcessTask(ctx, cmd).run();
            return;// end
        }

        Executor executor;
        // to check whether get executor using executor selector
        if (null == userProcessor.getExecutorSelector()) {
            executor = userProcessor.getExecutor();
        } else {
            // in case haven't deserialized in io thread
            // it need to deserialize clazz and header before using executor dispath strategy
            if (!deserializeRequestCommand(ctx, cmd, RpcDeserializeLevel.DESERIALIZE_HEADER)) {
                return;
            }
            //try get executor with strategy
            executor = userProcessor.getExecutorSelector().select(cmd.getRequestClass(),
                cmd.getRequestHeader());
        }

        // Till now, if executor still null, then try default
        if (executor == null) {
            executor = (this.getExecutor() == null ? defaultExecutor : this.getExecutor());
        }

        // use the final executor dispatch process task
        executor.execute(new ProcessTask(ctx, cmd));
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.AbstractRemotingProcessor#doProcess(com.mfma.sofaboltdemo.sofabolt.RemotingContext, com.mfma.sofaboltdemo.sofabolt.RemotingCommand)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void doProcess(final RemotingContext ctx, RpcRequestCommand cmd) throws Exception {
        long currentTimestamp = System.currentTimeMillis();

        preProcessRemotingContext(ctx, cmd, currentTimestamp);
        if (ctx.isTimeoutDiscard() && ctx.isRequestTimeout()) {
            timeoutLog(cmd, currentTimestamp, ctx);// do some log
            return;// then, discard this request
        }
        debugLog(ctx, cmd, currentTimestamp);
        // decode request all
        if (!deserializeRequestCommand(ctx, cmd, RpcDeserializeLevel.DESERIALIZE_ALL)) {
            return;
        }
        dispatchToUserProcessor(ctx, cmd);
    }

    /**
     * Send response using remoting context if necessary.<br>
     * If request type is oneway, no need to send any response nor exception.
     *
     * @param ctx remoting context
     * @param type type code
     * @param response remoting command
     */
    public void sendResponseIfNecessary(final RemotingContext ctx, byte type,
                                        final RemotingCommand response) {
        final int id = response.getId();
        if (type != RpcCommandType.REQUEST_ONEWAY) {
            RemotingCommand serializedResponse = response;
            try {
                response.serialize();
            } catch (SerializationException e) {
                String errMsg = "SerializationException occurred when sendResponseIfNecessary in RpcRequestProcessor, id="
                                + id;
                logger.error(errMsg, e);
                serializedResponse = this.getCommandFactory().createExceptionResponse(id,
                    ResponseStatus.SERVER_SERIAL_EXCEPTION, e);
                try {
                    serializedResponse.serialize();// serialize again for exception response
                } catch (SerializationException e1) {
                    // should not happen
                    logger.error("serialize SerializationException response failed!");
                }
            } catch (Throwable t) {
                String errMsg = "Serialize RpcResponseCommand failed when sendResponseIfNecessary in RpcRequestProcessor, id="
                                + id;
                logger.error(errMsg, t);
                serializedResponse = this.getCommandFactory()
                    .createExceptionResponse(id, t, errMsg);
            }

            ctx.writeAndFlush(serializedResponse).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Rpc response sent! requestId="
                                     + id
                                     + ". The address is "
                                     + RemotingUtil.parseRemoteAddress(ctx.getChannelContext()
                                         .channel()));
                    }
                    if (!future.isSuccess()) {
                        logger.error(
                            "Rpc response send failed! id="
                                    + id
                                    + ". The address is "
                                    + RemotingUtil.parseRemoteAddress(ctx.getChannelContext()
                                        .channel()), future.cause());
                    }
                }
            });
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Oneway rpc request received, do not send response, id=" + id
                             + ", the address is "
                             + RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
            }
        }
    }

    /**
     * dispatch request command to user processor
     * @param ctx remoting context
     * @param cmd rpc request command
     */
    private void dispatchToUserProcessor(RemotingContext ctx, RpcRequestCommand cmd) {
        final int id = cmd.getId();
        final byte type = cmd.getType();
        // processor here must not be null, for it have been checked before
        UserProcessor processor = ctx.getUserProcessor(cmd.getRequestClass());

        ClassLoader classLoader = null;
        try {
            ClassLoader bizClassLoader = processor.getBizClassLoader();
            if (bizClassLoader != null) {
                classLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(bizClassLoader);
            }

            if (processor instanceof AsyncUserProcessor
                || processor instanceof AsyncMultiInterestUserProcessor) {
                try {
                    processor.handleRequest(
                        processor.preHandleRequest(ctx, cmd.getRequestObject()),
                        new RpcAsyncContext(ctx, cmd, this), cmd.getRequestObject());
                } catch (RejectedExecutionException e) {
                    logger
                        .warn("RejectedExecutionException occurred when do ASYNC process in RpcRequestProcessor");
                    sendResponseIfNecessary(ctx, type, this.getCommandFactory()
                        .createExceptionResponse(id, ResponseStatus.SERVER_THREADPOOL_BUSY));
                } catch (Throwable t) {
                    String errMsg = "AYSNC process rpc request failed in RpcRequestProcessor, id="
                                    + id;
                    logger.error(errMsg, t);
                    sendResponseIfNecessary(ctx, type, this.getCommandFactory()
                        .createExceptionResponse(id, t, errMsg));
                }
            } else {
                try {
                    Object responseObject = processor.handleRequest(
                        processor.preHandleRequest(ctx, cmd.getRequestObject()),
                        cmd.getRequestObject());

                    sendResponseIfNecessary(ctx, type,
                        this.getCommandFactory().createResponse(responseObject, cmd));
                } catch (RejectedExecutionException e) {
                    logger
                        .warn("RejectedExecutionException occurred when do SYNC process in RpcRequestProcessor");
                    sendResponseIfNecessary(ctx, type, this.getCommandFactory()
                        .createExceptionResponse(id, ResponseStatus.SERVER_THREADPOOL_BUSY));
                } catch (Throwable t) {
                    String errMsg = "SYNC process rpc request failed in RpcRequestProcessor, id="
                                    + id;
                    logger.error(errMsg, t);
                    sendResponseIfNecessary(ctx, type, this.getCommandFactory()
                        .createExceptionResponse(id, t, errMsg));
                }
            }
        } finally {
            if (classLoader != null) {
                Thread.currentThread().setContextClassLoader(classLoader);
            }
        }
    }

    /**
     * deserialize request command
     *
     * @return true if deserialize success; false if exception catched
     */
    private boolean deserializeRequestCommand(RemotingContext ctx, RpcRequestCommand cmd, int level) {
        boolean result;
        try {
            cmd.deserialize(level);
            result = true;
        } catch (DeserializationException e) {
            logger
                .error(
                    "DeserializationException occurred when process in RpcRequestProcessor, id={}, deserializeLevel={}",
                    cmd.getId(), RpcDeserializeLevel.valueOf(level), e);
            sendResponseIfNecessary(ctx, cmd.getType(), this.getCommandFactory()
                .createExceptionResponse(cmd.getId(), ResponseStatus.SERVER_DESERIAL_EXCEPTION, e));
            result = false;
        } catch (Throwable t) {
            String errMsg = "Deserialize RpcRequestCommand failed in RpcRequestProcessor, id="
                            + cmd.getId() + ", deserializeLevel=" + level;
            logger.error(errMsg, t);
            sendResponseIfNecessary(ctx, cmd.getType(), this.getCommandFactory()
                .createExceptionResponse(cmd.getId(), t, errMsg));
            result = false;
        }
        return result;
    }

    /**
     * pre process remoting context, initial some useful infos and pass to biz
     *
     * @param ctx remoting context
     * @param cmd rpc request command
     * @param currentTimestamp current timestamp
     */
    private void preProcessRemotingContext(RemotingContext ctx, RpcRequestCommand cmd,
                                           long currentTimestamp) {
        ctx.setArriveTimestamp(cmd.getArriveTime());
        ctx.setTimeout(cmd.getTimeout());
        ctx.setRpcCommandType(cmd.getType());
        ctx.getInvokeContext().putIfAbsent(InvokeContext.BOLT_PROCESS_WAIT_TIME,
            currentTimestamp - cmd.getArriveTime());
    }

    /**
     * print some log when request timeout and discarded in io thread.
     */
    private void timeoutLog(final RpcRequestCommand cmd, long currentTimestamp, RemotingContext ctx) {
        if (logger.isDebugEnabled()) {
            logger
                .debug(
                    "request id [{}] currenTimestamp [{}] - arriveTime [{}] = server cost [{}] >= timeout value [{}].",
                    cmd.getId(), currentTimestamp, cmd.getArriveTime(),
                    (currentTimestamp - cmd.getArriveTime()), cmd.getTimeout());
        }

        String remoteAddr = "UNKNOWN";
        if (null != ctx) {
            ChannelHandlerContext channelCtx = ctx.getChannelContext();
            Channel channel = channelCtx.channel();
            if (null != channel) {
                remoteAddr = RemotingUtil.parseRemoteAddress(channel);
            }
        }
        logger
            .warn(
                "Rpc request id[{}], from remoteAddr[{}] stop process, total wait time in queue is [{}], client timeout setting is [{}].",
                cmd.getId(), remoteAddr, (currentTimestamp - cmd.getArriveTime()), cmd.getTimeout());
    }

    /**
     * print some debug log when receive request
     */
    private void debugLog(RemotingContext ctx, RpcRequestCommand cmd, long currentTimestamp) {
        if (logger.isDebugEnabled()) {
            logger.debug("Rpc request received! requestId={}, from {}", cmd.getId(),
                RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
            logger.debug(
                "request id {} currenTimestamp {} - arriveTime {} = server cost {} < timeout {}.",
                cmd.getId(), currentTimestamp, cmd.getArriveTime(),
                (currentTimestamp - cmd.getArriveTime()), cmd.getTimeout());
        }
    }

    /**
     * Inner process task
     *
     * @author xiaomin.cxm
     * @version $Id: RpcRequestProcessor.java, v 0.1 May 19, 2016 4:01:28 PM xiaomin.cxm Exp $
     */
    class ProcessTask implements Runnable {

        RemotingContext   ctx;
        RpcRequestCommand msg;

        public ProcessTask(RemotingContext ctx, RpcRequestCommand msg) {
            this.ctx = ctx;
            this.msg = msg;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            try {
                RpcRequestProcessor.this.doProcess(ctx, msg);
            } catch (Throwable e) {
                //protect the thread running this task
                String remotingAddress = RemotingUtil.parseRemoteAddress(ctx.getChannelContext()
                    .channel());
                logger
                    .error(
                        "Exception caught when process rpc request command in RpcRequestProcessor, Id="
                                + msg.getId() + "! Invoke source address is [" + remotingAddress
                                + "].", e);
            }
        }

    }
}
