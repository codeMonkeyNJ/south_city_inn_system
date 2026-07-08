package com.mason.service;

import com.mason.domain.PageResult;
import com.mason.domain.dto.DeptDTO;
import com.mason.domain.dto.DeptStateDTO;
import com.mason.domain.po.Dept;
import com.mason.domain.vo.DeptSimpleVO;
import com.mason.domain.vo.DeptSimplerVO;
import com.mason.domain.vo.DeptVO;

import java.util.List;

public interface DeptService {
    /**
     * 获取所有部门
     */
    PageResult<DeptSimpleVO> getAllDept(Integer loginUserId, Integer dataCoverage, Integer page, Integer pageSize, String name, Integer type);

    /**
     * 根据部门id查询部门
     */
    DeptVO getDeptById(Integer loginUserId, Integer dataCoverage, Integer id);

    /**
     * 添加部门
     */
    void addDept(DeptDTO deptDTO);

    /**
     * 修改部门
     */
    void updateDept(Integer loginUserId, Integer dataCoverage, DeptDTO deptDTO);

    /**
     * 更新部门状态
     */
    void updateDeptState(Integer loginUserId, Integer dataCoverage, DeptStateDTO deptStateDTO);

    /**
     * 批量删除部门
     */
    void batchDeleteDept(List<Integer> ids);

    /**
     * 根据用户id获取其所属部门及下属部门id
     * @param userId 用户id
     * @return 部门id列表
     */
    List<Integer> getAllDeptIdsByUserId(Integer userId);

    /**
     * 根据用户id获取其所属部门及其直属子部门id
     * @param userId 用户id
     * @return 部门id列表
     */
    List<Integer> getSonDeptIdsByUserId(Integer userId);

    /**
     * 根据用户id获取其所属部门ids
     * @param userId 用户id
     * @return 部门id列表
     */
    List<Integer> getDeptIdsByUserId(Integer userId);

    /**
     * 根据用户id获取其所属部门及下属部门名称
     * @param userId 用户id
     * @return 部门名称列表
     */
    List<String> selectAllDeptNameByUserId(Integer userId);

    /**
     * 根据用户id获取其所属部门名称
     * @param userId 用户id
     * @return 部门名称列表
     */
    List<DeptDTO> selectDeptNameByUserId(Integer userId);

    /**
     * 根据部门id获取其部门类型
     * @param deptId 部门id
     * @return 部门类型，0:部门,1:门店
     */
    Integer selectDeptTypeById(Integer deptId);

    /**
     * 根据部门id获取其父部门id
     * @param deptId 部门id
     * @return 夫部门id
     */
    Integer selectDeptFatherIdById(Integer deptId);

    /**
     * 根据部门id获取部门信息
     * @param deptId 部门id
     * @return 夫部门id
     */
    Dept selectDeptByDId(Integer deptId);

    /**
     * 根据用户id获取其所属部门的简单信息
     */
    PageResult<DeptSimplerVO> selectDeptItemByUserId(Integer userId);
}
