package com.LR.openRana.utils.bean;

import com.LR.openRana.utils.JSONUtils;
import com.LR.openRana.utils.MapUtils;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.google.gson.Gson;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component// 标示一个Spring框架的服务组件 // 使用Lombok的日志注解，方便记录日志
public class SMSUtils {

    @Value("${aliyun.accessKeyId}") // 从配置文件中注入阿里云Access Key ID
    private String accessKeyId;
    @Value("${aliyun.accessKeySecret}") // 从配置文件中注入阿里云Access Key Secret
    private String accessKeySecret;

    @Autowired
    private EnvironmentUtils environmentUtils;

    /**
     * 发送短信验证码
     *
     * @param phone 目标手机号码
     * @param code  验证码内容
     * @throws ExecutionException   异常处理，抛出执行异常
     * @throws InterruptedException 异常处理，抛出中断异常
     */
    public boolean sendSMS(String phone, String code) {
        if (environmentUtils.isDevEnvironment()) {
            log.info("手机号" + phone + "，短信验证码：" + code);
            return true;
        }
        // 使用静态凭证提供者初始化凭证
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build());

        // 配置阿里云SMS客户端
        AsyncClient client = AsyncClient.builder()
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dysmsapi.aliyuncs.com") // 设置端点覆盖
                )
                .build();

        // 准备发送的短信参数
        var templateParam = MapUtils.putS("code", code); // 设置模板参数，如验证码
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .phoneNumbers(phone) // 目标手机号
                .signName("开发小助手") // 短信签名
                .templateCode("SMS_466435067") // 短信模板代码，此处为空，需根据实际情况填写
                .templateParam(JSONUtils.toJSONString(templateParam)) // 模板参数序列化
                .build();

        // 异步发送短信并处理响应
        try {
            CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
            SendSmsResponse resp = response.get(); // 等待异步任务完成获取响应
            log.info(new Gson().toJson(resp)); // 记录发送结果到日志
            client.close(); // 关闭客户端资源
            return true;
        } catch (Exception e) {
            client.close();
            return false;
        }
    }

    public boolean sendSMS(String phone, String templateCode, String templateParam) {
        if (environmentUtils.isDevEnvironment()) {
            log.info("手机号：" + phone + "，内容：" + templateParam);
            return true;
        }
        // 使用静态凭证提供者初始化凭证
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build());

        // 配置阿里云SMS客户端
        AsyncClient client = AsyncClient.builder()
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dysmsapi.aliyuncs.com") // 设置端点覆盖
                )
                .build();
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .phoneNumbers(phone) // 目标手机号
                .signName("开发小助手") // 短信签名
                .templateCode(templateCode) // 短信模板代码，此处为空，需根据实际情况填写
                .templateParam(templateParam) // 模板参数序列化
                .build();

        // 异步发送短信并处理响应
        try {
            CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
            SendSmsResponse resp = response.get(); // 等待异步任务完成获取响应
            log.info(new Gson().toJson(resp)); // 记录发送结果到日志
            client.close(); // 关闭客户端资源
            return true;
        } catch (Exception e) {
            client.close();
            return false;
        }
    }

}

