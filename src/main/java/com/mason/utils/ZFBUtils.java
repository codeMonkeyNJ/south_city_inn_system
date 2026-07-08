package com.mason.utils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.mason.config.ZFBConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class ZFBUtils {

    private final AlipayConfig alipayConfig = new AlipayConfig();
    private AlipayClient alipayClient = null;
    @Autowired
    private ZFBConfig zFBConfig;
    @PostConstruct
    public void init() throws AlipayApiException {
        //设置网关地址
        alipayConfig.setServerUrl(zFBConfig.getUrl());
        //设置应用APPID
        alipayConfig.setAppId(zFBConfig.getAppid());
        //设置应用私钥
        alipayConfig.setPrivateKey(zFBConfig.getPrivateKey());
        //设置请求格式，固定值json
        alipayConfig.setFormat("json");
        //设置字符集
        alipayConfig.setCharset(zFBConfig.getCharset());
        //设置支付宝公钥
        alipayConfig.setAlipayPublicKey(zFBConfig.getAlipayPublicKey());
        //设置签名类型
        alipayConfig.setSignType(zFBConfig.getSignType());
        //构造client
        alipayClient = new DefaultAlipayClient(alipayConfig);
    }

    /**
     * 支付
     * @param title 订单标题
     * @param amount 订单金额
     */
    public String pay(String title, BigDecimal amount) throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        // 设置商户订单号
        model.setOutTradeNo(zFBConfig.getTradeswomen());
        // 设置订单总金额
        model.setTotalAmount(amount.toString());
        // 设置订单标题
        model.setSubject(title);
        // 设置产品码
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        request.setBizModel(model);
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "POST");
        if (response.isSuccess()) {
            System.out.println("调用成功");
            log.info("调用返回=>{}", response);
        } else {
            System.out.println("调用失败");
            log.error("调用返回=>{}", response);
        }
        return response.getBody();
    }
}
