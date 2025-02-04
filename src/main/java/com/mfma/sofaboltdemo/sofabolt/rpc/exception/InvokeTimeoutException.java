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
package com.mfma.sofaboltdemo.sofabolt.rpc.exception;

import com.mfma.sofaboltdemo.sofabolt.exception.RemotingException;

/**
 * Exception when invoke timeout
 * 
 * @author jiangping
 * @version $Id: InvokeTimeoutException.java, v 0.1 2015-9-28 PM3:35:53 tao Exp $
 */
public class InvokeTimeoutException extends RemotingException {

    /** For serialization  */
    private static final long serialVersionUID = -7772633244795043476L;

    /**
     * Default constructor.
     */
    public InvokeTimeoutException() {
    }

    /**
     * Constructor.
     *
     * @param msg the detail message
     */
    public InvokeTimeoutException(String msg) {
        super(msg);
    }

    /**
     * Constructor.
     *
     * @param msg the detail message
     * @param cause the cause
     */
    public InvokeTimeoutException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
