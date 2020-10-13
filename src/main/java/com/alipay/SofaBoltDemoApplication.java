package com.alipay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.mfma.protocol.ProtocolDecoder;
import com.alipay.remoting.ProtocolCode;
import com.alipay.remoting.rpc.RpcServer;
import com.alipay.remoting.rpc.protocol.RpcProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author mfma
 */
@Slf4j
@SpringBootApplication
public class SofaBoltDemoApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(SofaBoltDemoApplication.class, args);
		JSONObject protocolParams = initProtocols();
		RpcServer rpcServer = new RpcServer(9090, protocolParams);
		try {
			rpcServer.startup();
			log.info("RpcServer启动成功");
		} catch (Exception e) {
			log.error("RpcServer启动失败");
		}
	}
	
	/**
	 * 初始化所有需要支持的协议
	 *
	 * @return
	 */
	private static JSONObject initProtocols() {
		JSONObject protocolParams = new JSONObject();
		JSONObject protocolCode = new JSONObject().fluentPut("protocolHeader", RpcProtocol.PROTOCOL_HEADER).fluentPut("protocolVersion", RpcProtocol.PROTOCOL_VERSION);
		protocolParams.fluentPut("ProtocolCode", ProtocolCode.fromBytes(protocolCode)).fluentPut("ProtocolDecoder", new ProtocolDecoder()).fluentPut("Protocol", new RpcProtocol());
		return protocolParams;
	}
	
}
