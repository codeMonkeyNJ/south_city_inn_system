package com.mason.controller;

import com.mason.anno.AuthCode;
import com.mason.domain.PageResult;
import com.mason.domain.Result;
import com.mason.domain.dto.UserDTO;
import com.mason.domain.dto.UserUpdatePasswordDTO;
import com.mason.domain.vo.UserSimpleVO;
import com.mason.domain.vo.UserVO;
import com.mason.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

@RestController
@RequestMapping("/sys/user")
public class UserController {
    @Autowired
    private UserService userService;
    /**
     * 查询所有用户信息
     * @param page 页码
     * @param pageSize 每页显示数量
     * @param username 用户名
     * @param nickname 昵称
     * @param roleName 角色名称
     * @param deptName 部门名称
     * @param state 状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户列表
     */
    @GetMapping
    @AuthCode("user-select")
    public Result getAllUser(Integer page,
                             Integer pageSize,
                             String username,
                             String nickname,
                             String roleName,
                             String deptName,
                             Boolean state,
                             LocalDate startTime,
                             LocalDate endTime,
                             HttpServletRequest request) {
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        PageResult<UserSimpleVO> pageResult = userService.getAllUser(page, pageSize, username, nickname, roleName, deptName, state, startTime, endTime,dataCoverage,loginUserId);

        return Result.success(pageResult);
    }
    /**
     * 添加用户
     */
    @PostMapping
    @AuthCode("user-insert")
    public Result addUser(@RequestBody UserDTO userDTO,HttpServletRequest request) throws NoSuchAlgorithmException {
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        userService.addUser(userDTO,loginUserId,dataCoverage);
        return Result.success();
    }
    /**
     * 修改用户
     */
    @PutMapping
    @AuthCode("user-update")
    public Result updateUser(@RequestBody UserDTO userDTO,HttpServletRequest request) {
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        userService.updateUser(userDTO,loginUserId,dataCoverage);
        return Result.success();
    }
    /**
     * 根据用户id查询用户
     */
    @GetMapping("/{id}")
    @AuthCode("user-select-id")
    public Result getUserById(@PathVariable Integer id,HttpServletRequest request) {
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        UserVO userVO = userService.getUserById(id,loginUserId,dataCoverage);
        return Result.success(userVO);
    }

    /**
     * 修改用户密码
     */
    @PutMapping("/password")
    @AuthCode("user-update-password")
    public Result updatePassword(@RequestBody UserUpdatePasswordDTO userUpdatePasswordDTO,HttpServletRequest request){
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        userService.updatePassword(userUpdatePasswordDTO,loginUserId,dataCoverage);
        return Result.success();
    }
}
