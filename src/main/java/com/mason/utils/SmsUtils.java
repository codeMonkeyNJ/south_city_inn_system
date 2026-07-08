package com.mason.utils;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dypnsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dypnsapi20170525.models.CheckSmsVerifyCodeRequest;
import com.aliyun.sdk.service.dypnsapi20170525.models.CheckSmsVerifyCodeResponse;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import darabonba.core.client.ClientOverrideConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class SmsUtils {
    @Value("${aliyun.sms.access-key-id}")
    private String accessKeyId;
    @Value("${aliyun.sms.access-key-secret}")
    private String accessKeySecret;
    private StaticCredentialProvider provider;
    @PostConstruct
    public void init() {
        provider = StaticCredentialProvider.create(
                Credential.builder()
                        .accessKeyId(accessKeyId)
                        .accessKeySecret(accessKeySecret)
                        .build()
        );
    }
    /**
     * 发送验证码
     * @param phoneNum 手机号
     */
    public void sendSms(String phoneNum) throws Exception {
        try (AsyncClient client = AsyncClient.builder()
                .region("cn-shenzhen")
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dypnsapi.aliyuncs.com")
                )
                .build()) {

            SendSmsVerifyCodeRequest sendSmsVerifyCodeRequest = SendSmsVerifyCodeRequest.builder()
                    .templateParam("{\"code\":\"##code##\",\"min\":\"5\"}")
                    .templateCode("100001")
                    .phoneNumber(phoneNum)
                    .signName("速通互联验证码")
                    .interval(60L)
                    .build();
            CompletableFuture<SendSmsVerifyCodeResponse> response = client.sendSmsVerifyCode(sendSmsVerifyCodeRequest);
            SendSmsVerifyCodeResponse resp = response.get();
            log.info("发送验证码结果：{}", resp.getBody().getCode());
        }
    }
    /**
     * 验证码验证
     * @param phoneNum 手机号
     * @param code 验证码
     * @return 校验结果
     */
    public Boolean checkSms(String phoneNum, String code) throws ExecutionException, InterruptedException {
        try (AsyncClient client = AsyncClient.builder()
                .region("cn-shenzhen") // Region ID
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dypnsapi.aliyuncs.com")
                )
                .build()) {

            // Parameter settings for API request
            CheckSmsVerifyCodeRequest checkSmsVerifyCodeRequest = CheckSmsVerifyCodeRequest.builder()
                    .phoneNumber(phoneNum)
                    .verifyCode(code)
                    .build();
            CompletableFuture<CheckSmsVerifyCodeResponse> response = client.checkSmsVerifyCode(checkSmsVerifyCodeRequest);
            CheckSmsVerifyCodeResponse resp = response.get();
            return resp.getBody().getCode().equals("OK");
        }
    }
}
