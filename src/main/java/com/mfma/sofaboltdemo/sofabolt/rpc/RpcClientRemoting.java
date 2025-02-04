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
 * Rpc client remoting
 * 
 * @author xiaomin.cxm
 * @version $Id: RpcClientRemoting.java, v 0.1 Apr 14, 2016 11:58:56 AM xiaomin.cxm Exp $
 */
public class RpcClientRemoting extends RpcRemoting {

    public RpcClientRemoting(CommandFactory commandFactory, RemotingAddressParser addressParser,
                             DefaultConnectionManager connectionManager) {
        super(commandFactory, addressParser, connectionManager);
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.rpc.RpcRemoting#oneway(com.mfma.sofaboltdemo.sofabolt.Url, java.lang.Object, InvokeContext)
     */
    @Override
    public void oneway(Url url, Object request, InvokeContext invokeContext)
                                                                            throws RemotingException,
                                                                            InterruptedException {
        final Connection conn = getConnectionAndInitInvokeContext(url, invokeContext);
        this.connectionManager.check(conn);
        this.oneway(conn, request, invokeContext);
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.rpc.RpcRemoting#invokeSync(com.mfma.sofaboltdemo.sofabolt.Url, java.lang.Object, InvokeContext, int)
     */
    @Override
    public Object invokeSync(Url url, Object request, InvokeContext invokeContext, int timeoutMillis)
                                                                                                     throws RemotingException,
                                                                                                     InterruptedException {
        final Connection conn = getConnectionAndInitInvokeContext(url, invokeContext);
        this.connectionManager.check(conn);
        return this.invokeSync(conn, request, invokeContext, timeoutMillis);
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.rpc.RpcRemoting#invokeWithFuture(com.mfma.sofaboltdemo.sofabolt.Url, java.lang.Object, InvokeContext, int)
     */
    @Override
    public RpcResponseFuture invokeWithFuture(Url url, Object request, InvokeContext invokeContext,
                                              int timeoutMillis) throws RemotingException,
                                                                InterruptedException {
        final Connection conn = getConnectionAndInitInvokeContext(url, invokeContext);
        this.connectionManager.check(conn);
        return this.invokeWithFuture(conn, request, invokeContext, timeoutMillis);
    }

    /**
     * @see com.mfma.sofaboltdemo.sofabolt.rpc.RpcRemoting#invokeWithCallback(com.mfma.sofaboltdemo.sofabolt.Url, java.lang.Object, InvokeContext, com.mfma.sofaboltdemo.sofabolt.InvokeCallback, int)
     */
    @Override
    public void invokeWithCallback(Url url, Object request, InvokeContext invokeContext,
                                   InvokeCallback invokeCallback, int timeoutMillis)
                                                                                    throws RemotingException,
                                                                                    InterruptedException {
        final Connection conn = getConnectionAndInitInvokeContext(url, invokeContext);
        this.connectionManager.check(conn);
        this.invokeWithCallback(conn, request, invokeContext, invokeCallback, timeoutMillis);
    }

    /**
     * @see RpcRemoting#preProcessInvokeContext(InvokeContext, RemotingCommand, Connection)
     */
    @Override
    protected void preProcessInvokeContext(InvokeContext invokeContext, RemotingCommand cmd,
                                           Connection connection) {
        if (null != invokeContext) {
            invokeContext.putIfAbsent(InvokeContext.CLIENT_LOCAL_IP,
                RemotingUtil.parseLocalIP(connection.getChannel()));
            invokeContext.putIfAbsent(InvokeContext.CLIENT_LOCAL_PORT,
                RemotingUtil.parseLocalPort(connection.getChannel()));
            invokeContext.putIfAbsent(InvokeContext.CLIENT_REMOTE_IP,
                RemotingUtil.parseRemoteIP(connection.getChannel()));
            invokeContext.putIfAbsent(InvokeContext.CLIENT_REMOTE_PORT,
                RemotingUtil.parseRemotePort(connection.getChannel()));
            invokeContext.putIfAbsent(InvokeContext.BOLT_INVOKE_REQUEST_ID, cmd.getId());
        }
    }

    /**
     * Get connection and set init invokeContext if invokeContext not {@code null}
     *
     * @param url target url
     * @param invokeContext invoke context to set
     * @return connection
     */
    protected Connection getConnectionAndInitInvokeContext(Url url, InvokeContext invokeContext)
                                                                                                throws RemotingException,
                                                                                                InterruptedException {
        long start = System.currentTimeMillis();
        Connection conn;
        try {
            conn = this.connectionManager.getAndCreateIfAbsent(url);
        } finally {
            if (null != invokeContext) {
                invokeContext.putIfAbsent(InvokeContext.CLIENT_CONN_CREATETIME,
                    (System.currentTimeMillis() - start));
            }
        }
        return conn;
    }
}
