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

import com.mfma.sofaboltdemo.sofabolt.CommandFactory;
import com.mfma.sofaboltdemo.sofabolt.Connection;
import com.mfma.sofaboltdemo.sofabolt.DefaultConnectionManager;
import com.mfma.sofaboltdemo.sofabolt.InvokeCallback;
import com.mfma.sofaboltdemo.sofabolt.InvokeContext;
import com.mfma.sofaboltdemo.sofabolt.RemotingAddressParser;
import com.mfma.sofaboltdemo.sofabolt.RemotingCommand;
import com.mfma.sofaboltdemo.sofabolt.Url;
import com.mfma.sofaboltdemo.sofabolt.exception.RemotingException;
import com.mfma.sofaboltdemo.sofabolt.util.RemotingUtil;

/**
 * Rpc server remoting
 * 
 * @author xiaomin.cxm
 * @version $Id: RpcServerRemoting.java, v 0.1 Apr 14, 2016 12:00:39 PM xiaomin.cxm Exp $
 */
public class RpcServerRemoting extends RpcRemoting {

    /**
     * default constructor
     */
    public RpcServerRemoting(CommandFactory commandFactory) {
        super(commandFactory);
    }

    /**
     * @param addressParser
     * @param connectionManager
     */
    public RpcServerRemoting(CommandFactory commandFactory, RemotingAddressParser addressParser,
                             DefaultConnectionManager connectionManager) {
        super(commandFactory, addressParser, connectionManager);
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.rpc.RpcRemoting#invokeSync(com.mfma.sofaboltdemo.sofabolt.Url, java.lang.Object, InvokeContext, int)
     */
    @Override
    public Object invokeSync(Url url, Object request, InvokeContext invokeContext, int timeoutMillis)
                                                                                                     throws RemotingException,
                                                                                                     InterruptedException {
        Connection conn = this.connectionManager.get(url.getUniqueKey());
        if (null == conn) {
            throw new RemotingException("Client address [" + url.getUniqueKey()
                                        + "] not connected yet!");
        }
        this.connectionManager.check(conn);
        return this.invokeSync(conn, request, invokeContext, timeoutMillis);
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.rpc.RpcRemoting#oneway(com.mfma.sofaboltdemo.sofabolt.Url, java.lang.Object, InvokeContext)
     */
    @Override
    public void oneway(Url url, Object request, InvokeContext invokeContext)
                                                                            throws RemotingException {
        Connection conn = this.connectionManager.get(url.getUniqueKey());
        if (null == conn) {
            throw new RemotingException("Client address [" + url.getOriginUrl()
                                        + "] not connected yet!");
        }
        this.connectionManager.check(conn);
        this.oneway(conn, request, invokeContext);
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.rpc.RpcRemoting#invokeWithFuture(com.mfma.sofaboltdemo.sofabolt.Url, java.lang.Object, InvokeContext, int)
     */
    @Override
    public RpcResponseFuture invokeWithFuture(Url url, Object request, InvokeContext invokeContext,
                                              int timeoutMillis) throws RemotingException {
        Connection conn = this.connectionManager.get(url.getUniqueKey());
        if (null == conn) {
            throw new RemotingException("Client address [" + url.getUniqueKey()
                                        + "] not connected yet!");
        }
        this.connectionManager.check(conn);
        return this.invokeWithFuture(conn, request, invokeContext, timeoutMillis);
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.rpc.RpcRemoting#invokeWithCallback(com.mfma.sofaboltdemo.sofabolt.Url, java.lang.Object, InvokeContext, com.mfma.sofaboltdemo.sofabolt.InvokeCallback, int)
     */
    @Override
    public void invokeWithCallback(Url url, Object request, InvokeContext invokeContext,
                                   InvokeCallback invokeCallback, int timeoutMillis)
                                                                                    throws RemotingException {
        Connection conn = this.connectionManager.get(url.getUniqueKey());
        if (null == conn) {
            throw new RemotingException("Client address [" + url.getUniqueKey()
                                        + "] not connected yet!");
        }
        this.connectionManager.check(conn);
        this.invokeWithCallback(conn, request, invokeContext, invokeCallback, timeoutMillis);
    }

    @Override
    protected void preProcessInvokeContext(InvokeContext invokeContext, RemotingCommand cmd,
                                           Connection connection) {
        if (null != invokeContext) {
            invokeContext.putIfAbsent(InvokeContext.SERVER_REMOTE_IP,
                RemotingUtil.parseRemoteIP(connection.getChannel()));
            invokeContext.putIfAbsent(InvokeContext.SERVER_REMOTE_PORT,
                RemotingUtil.parseRemotePort(connection.getChannel()));
            invokeContext.putIfAbsent(InvokeContext.SERVER_LOCAL_IP,
                RemotingUtil.parseLocalIP(connection.getChannel()));
            invokeContext.putIfAbsent(InvokeContext.SERVER_LOCAL_PORT,
                RemotingUtil.parseLocalPort(connection.getChannel()));
            invokeContext.putIfAbsent(InvokeContext.BOLT_INVOKE_REQUEST_ID, cmd.getId());
        }
    }
}
