package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketDTO {
    private Integer id;//用户id
    private List<RoleAuthSimDTO> listenAuthList;//用户监听权限列表
    private List<Integer> underDept;//用户所属部门id列表
    private List<Integer> SonDept;//用户所属部门的子部门id列表
    private List<Integer> underStore;//用户所属部门仓库id列表
    private List<Integer> SonStore;//用户所属部门的子部门仓库id列表
    private List<WebSocketSession> sessionList;//会话对象列表
}
