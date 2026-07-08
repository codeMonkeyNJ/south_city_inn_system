package com.mason.webSocket;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.mason.domain.dto.RoleAuthSimDTO;
import com.mason.domain.dto.WebSocketDTO;
import com.mason.domain.po.PurchaseApply;
import com.mason.domain.vo.WebSocketNotifyVO;
import com.mason.service.DeptService;
import com.mason.service.InventoryService;
import com.mason.service.PurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Component
public class BaseWebSocketHandler extends TextWebSocketHandler {

    // 会话对象集合（key: userId）
    private final Map<Integer, WebSocketDTO> webSocketMap = new ConcurrentHashMap<>();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    @Autowired
    private DeptService deptService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private PurchaseService purchaseService;

    // ─── 连接建立后 ───
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        Map<String, Object> attributes = session.getAttributes();
        Integer userId = (Integer) attributes.get("userId");
        List<RoleAuthSimDTO> listenAuthList = (List<RoleAuthSimDTO>) attributes.get("listenAuthList");
        log.info("[连接建立] userId={}", userId);
        addWebSocketDTO(userId,listenAuthList, session);

    }

    // ─── 收到文本消息 ───
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        Integer userId = (Integer) session.getAttributes().get("userId");
        log.info("[文本消息] userId={}, sessionId={}, payload={}", userId,session.getId(), message.getPayload());
    }

    // ─── 收到二进制消息 ───
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        log.info("[二进制消息] sessionId={}, length={}", session.getId(), message.getPayloadLength());
    }

    // ─── 收到 Pong 心跳响应 ───
    @Override
    protected void handlePongMessage(WebSocketSession session, @NonNull PongMessage message) {
        log.debug("[Pong] sessionId={}", session.getId());
    }

    // ─── 连接异常 ───
    @Override
    public void handleTransportError(WebSocketSession session, @NonNull Throwable exception) {
        Integer userId = (Integer) session.getAttributes().get("userId");
        log.error("[连接异常] userId={}, sessionId={}", userId, session.getId());
    }

    // ─── 连接关闭后 ───
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Integer userId = (Integer) session.getAttributes().get("userId");
        log.info("[连接关闭] userId={}, sessionId={}, code={}, reason={}",
                userId,session.getId(), status.getCode(), status.getReason());

        writeLock.lock();//获取写锁
        try{
            WebSocketDTO webSocketDTO = webSocketMap.get(userId);
            if (webSocketDTO == null) return;
            for (int i = 0; i < webSocketDTO.getSessionList().size(); i++) {
                if(webSocketDTO.getSessionList().get(i).getId().equals(session.getId())){
                    webSocketDTO.getSessionList().remove(i);
                }
            }
            if (webSocketDTO.getSessionList().isEmpty()) {
                webSocketMap.remove(userId);
            }
        }finally {
            writeLock.unlock();//释放写锁
        }
    }

    /**
     * 对外暴露：向指定的客户端发送消息
     */
    public void sendMessage(Integer userId, String message) throws IOException {
        readLock.lock();//获取读锁
        try{
            WebSocketDTO webSocketDTO = webSocketMap.get(userId);
            if (webSocketDTO == null) { return; }
            for (WebSocketSession session : webSocketDTO.getSessionList()) {
                if(session.isOpen()){
                    session.sendMessage(new TextMessage(message));
                }
            }
        }finally {
            readLock.unlock();//释放读锁
        }
    }

    /**
     * 发送采购申请通知
     * @param orderId 采购申请id
     */
    public void sendPAMessage(Integer orderId){
        PurchaseApply purchaseApply = purchaseService.selectBasePurchaseApplyById(orderId);//查询采购申请
        webSocketMap.values().stream()
                .filter(webSocketDTO -> {//筛选出有获取采购申请消息权限的客户端
                    for (RoleAuthSimDTO roleAuthSimDTO : webSocketDTO.getListenAuthList()) {
                        if ("purchase-apply-listen".equals(roleAuthSimDTO.getCode())){//用户有获取采购申请消息的权限
                            switch (roleAuthSimDTO.getDataCoverage()){//判断权限范围
                                case 0://所有采购申请
                                    return true;
                                case 1://用户所属部门及子部门采购申请
                                    List<Integer> underDept = webSocketDTO.getUnderDept();
                                    List<Integer> sonDept = webSocketDTO.getSonDept();
                                    if (underDept.contains(purchaseApply.getDeptId()) || sonDept.contains(purchaseApply.getDeptId())){
                                        return true;
                                    }
                                    break;
                                case 2://用户所属部门采购申请
                                    if (webSocketDTO.getUnderDept().contains(purchaseApply.getDeptId())){
                                        return true;
                                    }
                                    break;
                            }
                        }
                    }
                    return false;
                })
                .forEach(webSocketDTO -> {//向每个客户端发送采购申请通知
                    try {
                        WebSocketNotifyVO webSocketNotifyVO = new WebSocketNotifyVO(orderId,WebSocketNotifyVO.PURCHASE_APPLY);
                        sendMessage(webSocketDTO.getId(), JSON.toJSONString(webSocketNotifyVO));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * 构建 WebSocketDTO
     */
    private void addWebSocketDTO(Integer userId, List<RoleAuthSimDTO> listenAuthList, WebSocketSession session) {
        writeLock.lock();//获取写锁
        try {
            WebSocketDTO existing = webSocketMap.get(userId);
            if (existing != null) {//该用户已存在 其他WebSocketDTO，直接添加会话对象
                existing.getSessionList().add(session);
                return;
            }
        }finally {
            writeLock.unlock();//释放写锁
        }
        //该用户首次连接，构建 WebSocketDTO 并添加会话对象
        WebSocketDTO webSocketDTO = new WebSocketDTO();
        webSocketDTO.setId(userId);
        webSocketDTO.setListenAuthList(listenAuthList);

        // 获取部门信息
        List<Integer> underDeptIds = deptService.getDeptIdsByUserId(userId);
        List<Integer> allDeptIds = deptService.getAllDeptIdsByUserId(userId);
        List<Integer> sonDeptIds = (List<Integer>) CollUtil.subtract(allDeptIds, underDeptIds);
        webSocketDTO.setUnderDept(underDeptIds);
        webSocketDTO.setSonDept(sonDeptIds);

        // 获取仓库信息
        List<Integer> underStoreIds = inventoryService.selectStoreIdsByUserId(userId);
        List<Integer> allStoreIds = inventoryService.selectAllStoreIdsByUserId(userId);
        List<Integer> sonStoreIds = (List<Integer>) CollUtil.subtract(allStoreIds, underStoreIds);
        webSocketDTO.setUnderStore(underStoreIds);
        webSocketDTO.setSonStore(sonStoreIds);
        webSocketDTO.setSessionList(new ArrayList<>(Collections.singletonList(session)));
        writeLock.lock();//获取写锁
        try{
            //二次检查，避免两个线程同时走到这里导致覆盖
            if(webSocketMap.get(userId) == null){
                webSocketMap.put(userId, webSocketDTO);
            }else{
                webSocketMap.get(userId).getSessionList().add(session);
            }
        }finally {
            writeLock.unlock();//释放写锁
        }
    }
}