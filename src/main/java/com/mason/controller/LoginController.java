package com.mason.controller;

import com.mason.domain.Result;
import com.mason.domain.dto.LoginDTO;
import com.mason.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;
    @PostMapping("/sys/user/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        String jwt = loginService.login(loginDTO);
        return Result.success(Map.of("token", jwt));
    }
    @PostMapping("/sys/user/logout")
    public Result logout(HttpServletRequest request) {
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        loginService.logout(loginUserId);
        return Result.success();
    }
}
