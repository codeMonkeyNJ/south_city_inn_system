package com.mason.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {
    @Bean
    public MessageConverter messageConverter(){
        // 1.定义消息转换器
        JacksonJsonMessageConverter jacksonJsonMessageConverter = new JacksonJsonMessageConverter();
        // 2.配置自动创建消息id，用于识别不同消息，也可以在业务中基于ID判断是否是重复消息
        jacksonJsonMessageConverter.setCreateMessageIds(true);
        return jacksonJsonMessageConverter;
    }
    @Bean
    public MessageRecoverer republishMessageRecoverer(RabbitTemplate rabbitTemplate){
        return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error");
    }

    /**
     * 订单延迟交换机
     * @return DirectExchange
     */
    @Bean
    public DirectExchange orderDelayExchange(){
        return new DirectExchange("order_delay_exchange");
    }

    /**
     * 订单延迟队列
     * @return Queue
     */
    @Bean
    public Queue orderDelayQueue(){
        return QueueBuilder
                .durable("order_delay_queue")
                .deadLetterExchange("order_delay_dlx_exchange")
                .build();
    }

    @Bean
    public Binding orderDelayBinding(Queue orderDelayQueue, DirectExchange orderDelayExchange){
        return BindingBuilder.bind(orderDelayQueue).to(orderDelayExchange).with("order_delay_key");
    }
}
