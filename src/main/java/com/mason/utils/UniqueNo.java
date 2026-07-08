package com.mason.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
public class UniqueNo {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    public String getUniqueNo(String prefix) {
        String key = "uniqueNo";
        // Lua 脚本
        String script =
                "local key = KEYS[1] " +
                        "local max = 9999999 " +
                        "local current = tonumber(redis.call('GET', key) or 0) " +
                        "if current == max then " +
                        "   redis.call('SET', key, 1) " +
                        "   return 1 " +
                        "else " +
                        "   local next = current +1 " +
                        "   redis.call('SET', key, next) " +
                        "   return next " +
                        "end";

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);

        // 执行（原子）
        String increment = String.valueOf(stringRedisTemplate.execute(redisScript, Collections.singletonList(key))*1102391%10000000);
        String today = String.valueOf(System.currentTimeMillis()/(1000*60*60*24)) ;
        return prefix+today+increment;
    }

    /**
     * 获取取餐码
     * @param deptId 门店id
     * @param type 类型(0:堂食,1:打包,2:外送)
     * @return 取餐码("T":堂食 "D":打包 "W":外送)
     */
    public String getTackNo(Integer deptId, Integer type){
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = switch (type) {
            case 0 -> "T";
            case 1 -> "D";
            case 2 -> "W";
            default -> "";
        };
        String key = "pickupNum:"+deptId+":"+date+":"+prefix;
        Long num = stringRedisTemplate.opsForValue().increment(key, 1);
        stringRedisTemplate.expire(key, 3, TimeUnit.DAYS);//缓存3天
        return prefix + String.format("%04d", num);//固定一位前缀加4位数字
    }
}
