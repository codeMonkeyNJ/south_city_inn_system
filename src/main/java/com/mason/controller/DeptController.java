package com.mason.controller;

import com.mason.anno.AuthCode;
import com.mason.domain.PageResult;
import com.mason.domain.Result;
import com.mason.domain.dto.DeptDTO;
import com.mason.domain.dto.DeptStateDTO;
import com.mason.domain.vo.DeptSimpleVO;
import com.mason.domain.vo.DeptSimplerVO;
import com.mason.domain.vo.DeptVO;
import com.mason.service.DeptService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/dept")
public class DeptController {
    @Autowired
    private DeptService deptService;
    /**
     * 查询所有部门
     */
    @AuthCode("dept-select")
    @GetMapping
    public Result getAllDept(HttpServletRequest request, Integer page, Integer pageSize, String name, Integer type) {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        PageResult<DeptSimpleVO> pageResult = deptService.getAllDept(loginUserId, dataCoverage, page, pageSize, name, type);
        return Result.success(pageResult);
    }
    /**
     * 根据部门id查询部门
     */
    @AuthCode("dept-select-id")
    @GetMapping("/{id}")
    public Result getDeptById(HttpServletRequest request, @PathVariable Integer id) {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        DeptVO deptSimpleVO = deptService.getDeptById(loginUserId, dataCoverage, id);
        return Result.success(deptSimpleVO);
    }

    /**
     * 添加部门
     */
    @AuthCode("dept-insert")
    @PostMapping
    public Result addDept(@RequestBody DeptDTO deptDTO) {
        deptService.addDept(deptDTO);
        return Result.success();
    }

    /**
     * 修改部门
     */
    @AuthCode("dept-update")
    @PutMapping
    public Result updateDept(HttpServletRequest request, @RequestBody DeptDTO deptDTO) {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        deptService.updateDept(loginUserId, dataCoverage, deptDTO);
        return Result.success();
    }

    /**
     * 修改部门状态
     */
    @AuthCode("dept-state-update")
    @PutMapping("/state")
    public Result updateDeptState(HttpServletRequest request, @RequestBody DeptStateDTO deptStateDTO) {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        deptService.updateDeptState(loginUserId, dataCoverage, deptStateDTO);
        return Result.success();
    }

    /**
     * 批量删除部门
     */
    @AuthCode("dept-delete")
    @DeleteMapping
    public Result deleteDept(@RequestParam List<Integer> ids) {
        deptService.batchDeleteDept(ids);
        return Result.success();
    }

    /**
     * 获取登录用户所属部门
     */
    @GetMapping("/user")
    public Result selectDeptByUserId(HttpServletRequest request) {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        PageResult<DeptSimplerVO> pageResult = deptService.selectDeptItemByUserId(loginUserId);
        return Result.success(pageResult);
    }
}
