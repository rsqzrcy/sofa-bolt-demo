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
package com.mfma.sofaboltdemo.sofabolt.exception;

/**
 * Exception when deserialize failed
 *
 * @author tsui
 * @version $Id: DeserializationException.java, v 0.1 2017-07-26 16:13 tsui Exp $
 */
public class DeserializationException extends CodecException {
    /** For serialization */
    private static final long serialVersionUID = 310446237157256052L;

    private boolean           serverSide       = false;

    /**
     * Constructor.
     */
    public DeserializationException() {

    }

    /**
     * Constructor.
     */
    public DeserializationException(String message) {
        super(message);
    }

    /**
     * Constructor.
     */
    public DeserializationException(String message, boolean serverSide) {
        this(message);
        this.serverSide = serverSide;
    }

    /**
     * Constructor.
     */
    public DeserializationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     */
    public DeserializationException(String message, Throwable cause, boolean serverSide) {
        this(message, cause);
        this.serverSide = serverSide;
    }

    /**
     * Getter method for property <tt>serverSide</tt>.
     *
     * @return property value of serverSide
     */
    public boolean isServerSide() {
        return serverSide;
    }
}