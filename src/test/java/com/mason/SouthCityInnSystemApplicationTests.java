package com.mason;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dypnsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dypnsapi20170525.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mason.utils.JwtUtils;
import com.mason.utils.SHA256;
import com.mason.utils.UniqueNo;
import com.mason.utils.ZFBUtils;
import darabonba.core.client.ClientOverrideConfiguration;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Slf4j
@SpringBootTest
class SouthCityInnSystemApplicationTests {

    @Test
    void contextLoads() {
        String phone = "13888123456";
        String last6 = phone.substring(phone.length()-6);//获取手机号的后6位
        String result = SHA256.getSHA256(last6);//进行SHA256加密
        System.out.println(result);
    }
    @Test
    void createJwtTest() {
        Map<String, Object> map = new HashMap<>();
        map.put("id",1);
        map.put("username","admin");
        String jwt = JwtUtils.generateJwt(map);
        System.out.println(jwt);
    }
    @Test
    void parseJwtTest() {
        Claims claims = JwtUtils.parseJWT("eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJhZG1pbiIsImV4cCI6MTc3Nzg5Nzg3MH0.ACnfVKG-vSnfu_6PLMhbaqaPb4wescI-DIlkzeSxyy8");
        System.out.println(claims.get("id"));
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Test
    void redisTest() {
//        stringRedisTemplate.opsForValue().set("name","马辰");
//        String name1 = stringRedisTemplate.opsForValue().get("name1");
//        if (!StringUtils.hasLength(name1)){
//            System.out.println("name1不存在");
//        }
        Long increment = stringRedisTemplate.opsForValue().increment("uniqueNo");
        System.out.println( increment);
    }

    @Autowired
    private ObjectMapper objectMapper;
    @Test
    void jsonTest() throws JsonProcessingException {
        List<Integer> myList = List.of(1,2,3,4,5,6,7,8,9,10);
        String json = objectMapper.writeValueAsString(myList);
        System.out.println(json);
    }
    @Autowired
    private UniqueNo uniqueNo;
    @Test
    void uniqueNoTest() {
        String no = uniqueNo.getUniqueNo("PA");
        System.out.println(no);
    }

    @Value("${aliyun.sms.access-key-id}")
    private String accessKeyId;
    @Value("${aliyun.sms.access-key-secret}")
    private String accessKeySecret;

    @Test
    public void smsTest() throws Exception {

        StaticCredentialProvider provider = StaticCredentialProvider.create(
                Credential.builder()
                        .accessKeyId(accessKeyId)
                        .accessKeySecret(accessKeySecret)
                        .build()
        );

        // Configure the Client
        try (AsyncClient client = AsyncClient.builder()
                .region("cn-shenzhen")
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dypnsapi.aliyuncs.com")
                )
                .build()) {

            // Parameter settings for API request
            SendSmsVerifyCodeRequest sendSmsVerifyCodeRequest = SendSmsVerifyCodeRequest.builder()
                    .templateParam("{\"code\":\"##code##\",\"min\":\"5\"}")
                    .templateCode("100001")
                    .phoneNumber("13432288803")
                    .signName("速通互联验证码")
                    .build();

            // Asynchronously get the return value of the API request
            CompletableFuture<SendSmsVerifyCodeResponse> response = client.sendSmsVerifyCode(sendSmsVerifyCodeRequest);
            // Synchronously get the return value of the API request
            SendSmsVerifyCodeResponse resp = response.get();
            System.out.println(new Gson().toJson(resp));
        }
    }

    @Autowired
    private ZFBUtils zfbUtils;
    @Test
    void payTest() throws Exception {
        String result = zfbUtils.pay("测试订单", BigDecimal.valueOf(0.1));
        System.out.println(result);
    }
}