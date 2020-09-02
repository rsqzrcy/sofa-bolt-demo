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
package com.mfma.sofaboltdemo.sofabolt;

import java.util.Objects;

/**
 * Protocol code definition, you can define your own protocol code in byte array {@link ProtocolCode#version}
 * We suggest to use just one byte for simplicity.
 *
 * @author tsui
 * @version $Id: ProtocolCode.java, v 0.1 2018-03-27 17:23 tsui Exp $
 */
public class ProtocolCode {

    String header;

    /**
     * bytes to represent protocol code
     */
    String version;

    public ProtocolCode(String header, String version) {
        this.header = header;
        this.version = version;
    }

    public static ProtocolCode fromBytes(String protocolCode,String protocolVersion) {
        return new ProtocolCode(protocolCode, protocolVersion);
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } ;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProtocolCode that = (ProtocolCode) o;
        return Objects.equals(header, that.header) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, version);
    }

    @Override
    public String toString() {
        return "ProtocolCode{"+header +","+ version+"}";
    }

}
