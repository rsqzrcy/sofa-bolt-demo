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

import com.mfma.sofaboltdemo.sofabolt.CommonCommandCode;
import com.mfma.sofaboltdemo.sofabolt.ResponseStatus;

/**
 * Heartbeat ack.
 * 
 * @author jiangping
 * @version $Id: HeartbeatAckCommand.java, v 0.1 2015-9-29 AM11:46:11 tao Exp $
 */
public class HeartbeatAckCommand extends ResponseCommand {
    /** For serialization */
    private static final long serialVersionUID = 2584912495844320855L;

    /**
     * Constructor.
     */
    public HeartbeatAckCommand() {
        super(CommonCommandCode.HEARTBEAT);
        this.setResponseStatus(ResponseStatus.SUCCESS);
    }
}
