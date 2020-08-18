package com.mfma.sofaboltdemo;

import com.mfma.sofaboltdemo.sofabolt.rpc.RpcServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SofaBoltDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SofaBoltDemoApplication.class, args);
        RpcServer rpcServer = new RpcServer(9090);
        try {
            rpcServer.startup();
            log.info("RpcServer启动成功");
        } catch (Exception e) {
            log.error("RpcServer启动失败");
        }
    }

}
