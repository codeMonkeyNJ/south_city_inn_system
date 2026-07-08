package com.mason.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Data
@Component
@ConfigurationProperties(prefix = "zfb")
public class ZFBConfig {
    private String url;
    private String appid;
    private String privateKey;
    private String charset;
    private String alipayPublicKey;
    private String signType;
    private String tradeswomen;
}
