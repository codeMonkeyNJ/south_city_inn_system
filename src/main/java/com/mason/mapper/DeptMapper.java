package com.mason.mapper;

import com.mason.domain.dto.DeptDTO;
import com.mason.domain.dto.DeptStateDTO;
import com.mason.domain.po.Dept;
import com.mason.domain.vo.DeptSimpleVO;
import com.mason.domain.vo.DeptSimplerVO;
import com.mason.domain.vo.DeptVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DeptMapper {
    /**
     * 统计总记录数
     * @param name 部门名称（可选）
     * @param type 部门类型 0表示总部部门，1表示门店部（可选）
     * @param ids 部门id列表(可选)
     * @ return 总记录数
     */
    Integer countDept(String name, Integer type, List<Integer> ids);

    /**
     * 获取所有部门信息
     * @param skip 跳过记录数
     * @param pageSize 每页显示数量
     * @param name 部门名称(可选)
     * @param type 部门类型 0表示总部部门，1表示门店部(可选)
     * @param ids 部门id列表(可选)
     * @return 部门列表
     */
    List<DeptSimpleVO> getAllDept(Integer skip, Integer pageSize, String name, Integer type, List<Integer> ids);

    /**
     * 根据部门id查询部门
     * @param id 部门id
     * @return 部门信息
     */
    DeptVO getDeptById(Integer id);

    /**
     * 添加部门
     * @param deptDTO 部门信息
     */
    void addDept(DeptDTO deptDTO);

    /**
     * 修改部门
     * @param deptDTO 部门信息
     */
    void updateDept(DeptDTO deptDTO);

    /**
     * 更新部门状态
     * @param deptStateDTO 部门状态信息DTO
     */
    @Update("UPDATE dept SET state = #{state} WHERE id = #{id}")
    void updateDeptState(DeptStateDTO deptStateDTO);

    /**
     * 批量删除部门
     * @param ids 部门id列表
     */
    void batchDeleteDept(List<Integer> ids);

    /**
     * 批量更新部门父部门id
     * @param ids 部门id列表
     */
    void batchUpdateDeptFather(List<Integer> ids);

    /**
     * 根据用户id获取用户所属部门以及下属部门id
     * @param userId 用户id
     * @return 部门id列表
     */
    @Select("""
        WITH RECURSIVE dept_tree AS (
            SELECT id
            FROM dept
            WHERE id IN (SELECT dept_id FROM user_dept ud WHERE ud.user_id = #{userId})
            UNION
            SELECT d.id
            FROM dept d
            JOIN dept_tree dt ON d.father_id = dt.id
        )
        SELECT id FROM dept_tree;
    """)
    List<Integer> getAllDeptIdsByUserId(Integer userId);

    /**
     * 根据用户id获取用户所属部门id
     * @param userId 用户id
     * @return 部门id列表
     */
    @Select("SELECT dept_id FROM user_dept where user_id = #{userId}")
    List<Integer> getDeptIdByUserId(Integer userId);


    /**
     * 根据id查询用户所属部门
     * @param userId 用户id
     * @return 部门名称列表
     */
    @Select("select d.name ,ud.dept_id as id from user_dept ud join dept d on ud.dept_id=d.id where ud.user_id= #{id}")
    List<DeptDTO> selectDeptByUserId(Integer userId);

    /**
     * 根据用户id查询用户所属部门及其下属部门名称
     * @param userId 用户id
     * @return 部门名称列表
     */
    @Select("""
    WITH RECURSIVE dept_tree AS (
            SELECT id,name
            FROM dept
            WHERE id IN (SELECT dept_id FROM user_dept ud WHERE ud.user_id = #{userId})
            UNION
            SELECT d.id,d.name
            FROM dept d
            JOIN dept_tree dt ON d.father_id = dt.id
        )
        SELECT name FROM dept_tree;
    """)
    List<String> selectAllDeptByUserId(Integer userId);

    /**
     * 根据部门id查询部门类型
     * @param deptId 部门id
     * @return 部门类型
     */
    @Select("SELECT type FROM dept WHERE id = #{deptId}")
    Integer selectDeptTypeById(Integer deptId);

    /**
     * 根据部门id查询部门父部门id
     * @param deptId 部门id
     * @return 部门父部门id
     */
    @Select("SELECT father_id FROM dept WHERE id = #{deptId}")
    Integer selectDeptFatherIdById(Integer deptId);

    /**
     * 根据用户id获取用户所属部门及下属部门id
     * @param userId 用户id
     * @return 部门id列表
     */
    @Select("""
        SELECT d.id
        FROM dept d
        JOIN user_dept ud ON d.id = ud.dept_id
        WHERE ud.user_id = #{userId}
        UNION
        SELECT d.id
        FROM dept d
        JOIN user_dept ud ON d.father_id = ud.dept_id
        WHERE ud.user_id = #{userId};
    """)
    List<Integer> getSonDeptIdsByUserId(Integer userId);

    @Select("SELECT name FROM dept WHERE id = #{deptId}")
    Dept selectDeptByDId(Integer deptId);

    /**
     * 获取用户所属部门的简单信息
     * @param userId 用户id
     * @return 部门简单信息列表
     */
    @Select("""
        SELECT id,name FROM dept d
        JOIN user_dept ud ON d.id = ud.dept_id
        WHERE ud.user_id = #{userId}
    """)
    List<DeptSimplerVO> selectDeptItemByUserId(Integer userId);
}
