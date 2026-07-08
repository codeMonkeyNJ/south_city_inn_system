package com.mason.listener;

import com.mason.domain.dto.WebSocketNotifyDTO;
import com.mason.webSocket.BaseWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketMessageListener {
    @Autowired
    private BaseWebSocketHandler baseWebSocketHandler;
    /**
     * webSocket通知监听（异步调用webSocket发送通知）
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "webSocket_notify_queue"),
            exchange = @Exchange(name = "webSocket_notify_exchange"),
            key = "webSocket_notify_key"
    ))
    public void listenOrderNotify(WebSocketNotifyDTO webSocketNotifyDTO) {
        if (webSocketNotifyDTO.getType() == WebSocketNotifyDTO.PURCHASE_APPLY){//采购申请
            baseWebSocketHandler.sendPAMessage(webSocketNotifyDTO.getOrderId());//发送通知
        }
    }
}
