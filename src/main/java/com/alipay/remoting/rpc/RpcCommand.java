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
package com.alipay.remoting.rpc;

import com.alibaba.fastjson.JSONObject;
import com.alipay.remoting.CommandCode;
import com.alipay.remoting.InvokeContext;
import com.alipay.remoting.ProtocolCode;
import com.alipay.remoting.RemotingCommand;
import com.alipay.remoting.config.ConfigManager;
import com.alipay.remoting.config.switches.ProtocolSwitch;
import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.exception.DeserializationException;
import com.alipay.remoting.exception.SerializationException;
import com.alipay.remoting.rpc.protocol.RpcDeserializeLevel;
import com.alipay.remoting.rpc.protocol.RpcProtocol;


/**
 * Remoting command. <br>
 * A remoting command stands for a kind of transfer object in the network communication layer.
 *
 * @author jiangping
 * @version $Id: RpcCommand.java, v 0.1 2015-9-6 PM5:26:31 tao Exp $
 */
public abstract class RpcCommand implements RemotingCommand {
	
	/**
	 * For serialization
	 */
	private static final long serialVersionUID = -3570261012462596503L;
	
	private ProtocolCode protocolCode;
	
	/**
	 * Code which stands for the command.
	 */
	private CommandCode cmdCode;
	
	private byte type;
	
	private JSONObject otherAttributes;
	
	/**
	 * Serializer, see the Configs.SERIALIZER_DEFAULT for the default serializer.
	 * Notice: this can not be changed after initialized at runtime.
	 */
	private byte serializer = ConfigManager.serializer;
	/**
	 * protocol switches
	 */
	private ProtocolSwitch protocolSwitch = new ProtocolSwitch();
	private int id;
	
	/**
	 * invoke context of each rpc command.
	 */
	private InvokeContext invokeContext;
	
	public RpcCommand() {
	}
	
	public RpcCommand(byte type) {
		this();
		this.type = type;
	}
	
	public RpcCommand(CommandCode cmdCode) {
		this();
		this.cmdCode = cmdCode;
	}
	
	public RpcCommand(byte type, CommandCode cmdCode) {
		this(cmdCode);
		this.type = type;
	}
	
	
	/**
	 * Serialize  the class header and content.
	 *
	 * @throws Exception
	 */
	@Override
	public void serialize() throws SerializationException {
		this.serializeClazz();
		this.serializeHeader(this.invokeContext);
		this.serializeContent(this.invokeContext);
	}
	
	/**
	 * Deserialize the class header and content.
	 *
	 * @throws Exception
	 */
	@Override
	public void deserialize() throws DeserializationException {
		this.deserializeClazz();
		this.deserializeHeader(this.invokeContext);
		this.deserializeContent(this.invokeContext);
	}
	
	/**
	 * Deserialize according to mask.
	 * <ol>
	 *     <li>If mask <= {@link RpcDeserializeLevel#DESERIALIZE_CLAZZ}, only deserialize clazz - only one part.</li>
	 *     <li>If mask <= {@link RpcDeserializeLevel#DESERIALIZE_HEADER}, deserialize clazz and header - two parts.</li>
	 *     <li>If mask <= {@link RpcDeserializeLevel#DESERIALIZE_ALL}, deserialize clazz, header and content - all three parts.</li>
	 * </ol>
	 *
	 * @param mask
	 * @throws CodecException
	 */
	public void deserialize(long mask) throws DeserializationException {
		if (mask <= RpcDeserializeLevel.DESERIALIZE_CLAZZ) {
			this.deserializeClazz();
		} else if (mask <= RpcDeserializeLevel.DESERIALIZE_HEADER) {
			this.deserializeClazz();
			this.deserializeHeader(this.getInvokeContext());
		} else if (mask <= RpcDeserializeLevel.DESERIALIZE_ALL) {
			this.deserialize();
		}
	}
	
	/**
	 * Serialize content class.
	 *
	 * @throws Exception
	 */
	public void serializeClazz() throws SerializationException {
	
	}
	
	/**
	 * Deserialize the content class.
	 *
	 * @throws Exception
	 */
	public void deserializeClazz() throws DeserializationException {
	
	}
	
	/**
	 * Serialize the header.
	 *
	 * @throws Exception
	 */
	public void serializeHeader(InvokeContext invokeContext) throws SerializationException {
	}
	
	/**
	 * Serialize the content.
	 *
	 * @throws Exception
	 */
	@Override
	public void serializeContent(InvokeContext invokeContext) throws SerializationException {
	}
	
	/**
	 * Deserialize the header.
	 *
	 * @throws Exception
	 */
	public void deserializeHeader(InvokeContext invokeContext) throws DeserializationException {
	}
	
	/**
	 * Deserialize the content.
	 *
	 * @throws Exception
	 */
	@Override
	public void deserializeContent(InvokeContext invokeContext) throws DeserializationException {
	}
	
	@Override
	public ProtocolCode getProtocolCode() {
		JSONObject protocolCode = new JSONObject().fluentPut("protocolHeader", RpcProtocol.PROTOCOL_HEADER).fluentPut("protocolVersion", RpcProtocol.PROTOCOL_VERSION);
		return ProtocolCode.fromBytes(protocolCode);
	}
	
	@Override
	public CommandCode getCmdCode() {
		return cmdCode;
	}
	
	@Override
	public InvokeContext getInvokeContext() {
		return invokeContext;
	}
	
	@Override
	public byte getSerializer() {
		return serializer;
	}
	
	@Override
	public ProtocolSwitch getProtocolSwitch() {
		return protocolSwitch;
	}
	
	public void setCmdCode(CommandCode cmdCode) {
		this.cmdCode = cmdCode;
	}
	
	public byte getType() {
		return type;
	}
	
	public void setType(byte type) {
		this.type = type;
	}
	
	public void setSerializer(byte serializer) {
		this.serializer = serializer;
	}
	
	public void setProtocolSwitch(ProtocolSwitch protocolSwitch) {
		this.protocolSwitch = protocolSwitch;
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	
	public void setInvokeContext(InvokeContext invokeContext) {
		this.invokeContext = invokeContext;
	}
	
	public void setProtocolCode(ProtocolCode protocolCode) {
		this.protocolCode = protocolCode;
	}
	
	public JSONObject getOtherAttributes() {
		return otherAttributes;
	}
	
	public void setOtherAttributes(JSONObject otherAttributes) {
		this.otherAttributes = otherAttributes;
	}
}
