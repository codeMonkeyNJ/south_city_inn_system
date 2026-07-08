package com.mason.service.impl;

import com.mason.domain.PageResult;
import com.mason.domain.dto.DeptDTO;
import com.mason.domain.dto.DeptStateDTO;
import com.mason.domain.po.Dept;
import com.mason.domain.vo.DeptSimpleVO;
import com.mason.domain.vo.DeptSimplerVO;
import com.mason.domain.vo.DeptVO;
import com.mason.exception.AuthorityException;
import com.mason.exception.BusinessException;
import com.mason.mapper.DeptMapper;
import com.mason.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {
    @Autowired
    private DeptMapper deptMapper;

    @Override
    public PageResult<DeptSimpleVO> getAllDept(Integer loginUserId, Integer dataCoverage, Integer page, Integer pageSize, String name, Integer type) {
        Integer total;//总记录数
        List<DeptSimpleVO> items;//部门信息列表
        Integer skip = (page-1)*pageSize;//跳过记录数
        List<Integer> deptIds = null;//部门id列表(用于限定范围)
        switch (dataCoverage){
            case 2://仅能获取本部门
                deptIds = deptMapper.getDeptIdByUserId(loginUserId);
                break;
            case 1://仅能获取本部门及下属部门
                deptIds = deptMapper.getAllDeptIdsByUserId(loginUserId);
                break;
            case 0://获取所有部门
                break;
        }
        total = deptMapper.countDept(name, type, deptIds);//统计总记录数
        if (total == 0){
            return new PageResult<>(0, null);
        }
        items = deptMapper.getAllDept(skip, pageSize, name, type, deptIds);//获取部门信息
        return new PageResult<>(total, items);
    }

    @Override
    public DeptVO getDeptById(Integer loginUserId, Integer dataCoverage, Integer id) {
        List<Integer> deptIds;//部门id列表(用于限定范围)
        switch (dataCoverage){
            case 2://仅能获取本部门
                deptIds = deptMapper.getDeptIdByUserId(loginUserId);
                if (!deptIds.contains(id)){throw new AuthorityException("权限不足");}
                break;
            case 1://仅能获取本部门及下属部门
                deptIds = deptMapper.getAllDeptIdsByUserId(loginUserId);
                if (!deptIds.contains(id)){throw new AuthorityException("权限不足");}
                break;
            case 0://获取所有部门
                break;
        }
        return deptMapper.getDeptById(id);
    }

    @Override
    public void addDept(DeptDTO deptDTO) {
        deptMapper.addDept(deptDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDept(Integer loginUserId, Integer dataCoverage, DeptDTO deptDTO) {
        List<Integer> deptIds;//部门id列表(用于限定范围)
        switch (dataCoverage){
            case 2://不能修改部门信息
                throw new AuthorityException("权限不足");
            case 1://仅能修改本部门及下属部门
                deptIds = deptMapper.getAllDeptIdsByUserId(loginUserId);
                if (!deptIds.contains(deptDTO.getId()) || !deptIds.contains(deptDTO.getFatherId()) || deptDTO.getFatherId() == null){
                    //当前修改的部门或父部门id不在当前用户权限内
                    throw new AuthorityException("权限不足");
                }
                break;
            case 0:
                break;

        }
        deptMapper.updateDept(deptDTO);
        //检查父部门是否合法
        Integer fatherId = deptDTO.getId();
        while (true){
            fatherId = deptMapper.selectDeptFatherIdById(fatherId);
            if (fatherId == null){//当前部门直接或间接继承总部部门，合法
                break;
            } else if (fatherId.equals(deptDTO.getId())) {//当前部门直接或间接继承自己，非法
                throw new BusinessException("父部门非法");
            }
        }
    }

    @Override
    public void updateDeptState(Integer loginUserId, Integer dataCoverage, DeptStateDTO deptStateDTO) {
        List<Integer> deptIds;//部门id列表(用于限定范围)
        switch (dataCoverage){
            case 2://不能修改部门信息
                throw new AuthorityException("权限不足");
            case 1://仅能修改本部门及下属部门
                deptIds = deptMapper.getAllDeptIdsByUserId(loginUserId);
                if (!deptIds.contains(deptStateDTO.getId())){
                    //当前修改的部门id不在当前用户权限内
                    throw new AuthorityException("权限不足");
                }
                break;
            case 0:
                break;
        }
        deptMapper.updateDeptState(deptStateDTO);//更新部门状态
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteDept(List<Integer> ids) {
        deptMapper.batchUpdateDeptFather(ids);//批量更新部门的父部门id
        deptMapper.batchDeleteDept(ids);//批量删除部门
    }

    @Override
    public List<Integer> getAllDeptIdsByUserId(Integer userId) {
        return deptMapper.getAllDeptIdsByUserId(userId);//获取用户所属部门以及下属部门id
    }

    @Override
    public List<Integer> getSonDeptIdsByUserId(Integer userId) {
        return deptMapper.getSonDeptIdsByUserId(userId);//根据用户id获取其所属部门及其直属子部门id
    }

    @Override
    public List<Integer> getDeptIdsByUserId(Integer userId) {
        return deptMapper.getDeptIdByUserId(userId);//获取用户所属部门id
    }

    @Override
    public List<String> selectAllDeptNameByUserId(Integer userId) {
        return deptMapper.selectAllDeptByUserId(userId);
    }

    @Override
    public List<DeptDTO> selectDeptNameByUserId(Integer userId) {
        return deptMapper.selectDeptByUserId(userId);
    }

    @Override
    public Integer selectDeptTypeById(Integer deptId) {
        return deptMapper.selectDeptTypeById(deptId);
    }

    @Override
    public Integer selectDeptFatherIdById(Integer deptId) {
        return deptMapper.selectDeptFatherIdById(deptId);
    }

    @Override
    public Dept selectDeptByDId(Integer deptId) {
        return deptMapper.selectDeptByDId(deptId);
    }

    @Override
    public PageResult<DeptSimplerVO> selectDeptItemByUserId(Integer userId) {
        List<DeptSimplerVO> deptSimplerVOS = deptMapper.selectDeptItemByUserId(userId);
        return new PageResult<>(deptSimplerVOS.size(), deptSimplerVOS);
    }

}
