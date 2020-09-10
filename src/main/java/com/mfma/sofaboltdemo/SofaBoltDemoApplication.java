package com.mfma.sofaboltdemo;

import com.alibaba.fastjson.JSONObject;
import com.mfma.sofaboltdemo.sofabolt.Protocol;
import com.mfma.sofaboltdemo.sofabolt.ProtocolCode;
import com.mfma.sofaboltdemo.sofabolt.ProtocolManager;
import com.mfma.sofaboltdemo.sofabolt.rpc.RpcServer;
import com.mfma.sofaboltdemo.sofabolt.rpc.protocol.RpcProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author mfma
 */
@Slf4j
@SpringBootApplication
public class SofaBoltDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SofaBoltDemoApplication.class, args);
        ConcurrentMap<ProtocolCode, Protocol> protocolsMap= initProtocols();
        RpcServer rpcServer = new RpcServer(9090,protocolsMap);
        try {
            rpcServer.startup();
            log.info("RpcServer启动成功");
        } catch (Exception e) {
            log.error("RpcServer启动失败");
        }
    }

    /**
     * 初始化所有需要支持的协议
     * @return
     */
    private static ConcurrentMap<ProtocolCode, Protocol> initProtocols(){
        ConcurrentMap<ProtocolCode, Protocol> protocolsMap = new ConcurrentHashMap<>();
        JSONObject protocolCode = new JSONObject().fluentPut("protocolHeader", RpcProtocol.PROTOCOL_HEADER)
                .fluentPut("protocolVersion",RpcProtocol.PROTOCOL_VERSION);
        protocolsMap.putIfAbsent(ProtocolCode.fromBytes(protocolCode),  new RpcProtocol());
        return protocolsMap;
    }

}
