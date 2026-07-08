package com.mason.mapper;

import com.mason.domain.dto.InvLogDTO;
import com.mason.domain.dto.InvMaterialNumDTO;
import com.mason.domain.dto.InventoryRepertoryDTO;
import com.mason.domain.dto.InventoryStoreDTO;
import com.mason.domain.po.*;
import com.mason.domain.vo.*;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Mapper
public interface InventoryMapper {

    /**
     * 添加仓库
     * @param inventoryStoreDTO 仓库信息
     */
    @Insert("insert into store(dept_id,name,address) values(#{deptId},#{name},#{address})")
    void insertStore(InventoryStoreDTO inventoryStoreDTO);

    /**
     * 修改仓库
     * @param inventoryStoreDTO 仓库信息
     */
    @Insert("update store set dept_id=#{deptId},name=#{name},address=#{address} where id=#{id}")
    Integer updateStore(InventoryStoreDTO inventoryStoreDTO);

    /**
     * 统计仓库数量
     */
    Integer countStore(String name, List<Integer> validDeptIds);

    /**
     * 获取仓库列表
     */
    List<InventoryStoreSimpleVO> selectStoreList(Integer skip, Integer pageSize, String name, List<Integer> validDeptIds);

    /**
     * 根据仓库id获取仓库部门id
     */
    @Select("select dept_id from store where id=#{id}")
    Integer getDeptIdByStoreId(Integer id);

    /**
     * 根据仓库id获取其所属部门id
     **/
    @Select("select dept_id from store where id= #{id}")
    Integer selectDeptIdByStoreId(Integer id);

    /**
     * 根据仓库统计其库存物料数量
     */
    Integer countSMByStoreId(Integer id, String name, String className);

    /**
     * 根据仓库id获取其库存列表
     */
    List<InventoryMaterialVO> selectSMByStoreId(Integer id, Integer skip, Integer pageSize, String name, String className);


    /**
     * 根据用户id获取其所属部门的仓库id列表
     */
    @Select("""
    select store.id from store
    join user_dept on store.dept_id = user_dept.dept_id
    where user_dept.user_id = #{loginUserId};
    """)
    List<Integer> selectStoreIdsByUserId(Integer loginUserId);

    /**
     * 根据库存id获取其所属仓库id
     */
    @Select("select store_id from store_material where id = #{id}")
    Integer selectStoreIdByRTId(Integer id);

    /**
     * 根据用户id获取其所属部门及子部门的仓库id列表
     */
    @Select("""
        WITH RECURSIVE dept_tree AS (
            SELECT dept.id as drptId,store.id as storeId
            FROM dept
            join store on dept.id = store.dept_id
            WHERE dept.id IN (SELECT dept_id FROM user_dept ud WHERE ud.user_id = #{userId})
            UNION
            SELECT dept.id as drptId,store.id as storeId
            FROM dept
            join store on dept.id = store.dept_id
            JOIN dept_tree ON dept.father_id = dept_tree.drptId
        )
        SELECT storeId FROM dept_tree;
    """)
    List<Integer> selectAllStoreIdsByUserId(Integer loginUserId);

    /**
     * 根据用户id获取其所属部门的子部门的仓库id
     */
    @Select("""
        select id from store where dept_id in(
            SELECT d.id
            FROM dept d
            WHERE EXISTS (
                SELECT 1 FROM user_dept ud
                WHERE ud.user_id = #{userId} AND ud.dept_id = d.father_id
            )
              AND NOT EXISTS (
                SELECT 1 FROM user_dept ud
                WHERE ud.user_id = #{userId}  AND ud.dept_id = d.id
            )
        );
    """)
    List<Integer> selectSonStoreIdsByUserId(Integer userId);

    /**
     * 根据库存id获取其库存日志列表
     */
    List<StoreMaterialLog> selectLogByRepertoryId(Integer id, Integer pageSize, Integer lastId, Integer operation, Date startDate, Date endDate);

    /**
     * 获取库存最后日志id
     */
    Integer selectLastLogIdByRepertoryId(Integer id, Integer operation, Date startDate, Date endDate);

      /**
     * 获取出入库日志信息
     * @param sourceType 来源类型(0:采购订单,1:出库单,2:商品订单)
     * @param sourceIds 来源id(采购订单id/出库单id)列表
     * @param operation 操作类型(0:入库,1:出库)
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 日志列表
     */
      List<InvLogDTO> selectLogListByIds(Integer sourceType, List<Integer> sourceIds, Integer operation, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 根据仓库id和物料id获取库存信息
     */
    @Select("select id, store_id, material_id, sum, update_time, create_time from store_material where store_id = #{storeId} and material_id = #{materialId}")
    StoreMaterial selectRepertoryBySIdAndMId(Integer storeId, Integer materialId);

    /**
     * 添加库存
     */
    @Insert("insert into store_material(store_id,material_id,sum) values(#{storeId},#{materialId},#{num})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insertRepertory(InventoryRepertoryDTO inventoryRepertoryDTO);

    /**
     * 添加库存日志
     */
    @Insert("insert into store_material_log(repertory_id,operation,num,source_id,source_type) values(#{repertoryId},#{operation},#{num},#{sourceId},#{sourceType})")
    void insertRepertoryLog(Integer repertoryId, Integer operation, Integer num, Integer sourceId,Integer sourceType);

    /**
     * 批量添加库存日志
     */
    void batchInsertLog(List<InvLogDTO> logs);

    /**
     * 修改库存
     */
    @Insert("update store_material set sum = #{sum} where id = #{id}")
    void updateRepertory(Integer id, Integer sum);

    /**
     * 批量修改库存
     */
    void batchUpdateRepertory(List<InvMaterialNumDTO> materialNumList);

    /**
     * 根据部门id获取其仓库id列表
     */
    @Select("select id from store where dept_id = #{deptId}")
    List<Integer> selectStoreIdsByDeptId(Integer deptId);

    /**
     * 添加要货单基本信息
     */
    @Insert("insert into enquiry(applicant_id, dept_id, store_id, no,amount, pay_type, state, remark) values(#{applicantId},#{deptId},#{storeId},#{no},#{amount},#{payType},#{state},#{remark})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insertEnquiry(Enquiry enquiry);

    /**
     * 批量添加要货单详情信息
     * @param fields 要插入的字段
     * @param enquiryDetails 要货单详情信息
     */
    void batchInsertED(List<String> fields, List<EnquiryDetail> enquiryDetails);

    /**
     * 根据要货单id获取要货单信息(详细详细)
     */
    @Select("""
        select o.id as outboundId,
            e.applicant_id as applicantId,
            ui.name as applicant,
            e.dept_id as deptId,
            d.name as dept,
            e.store_id as storeId,
            s.name as store,
            e.no,
            e.amount,
            e.state,
            e.remark,
            e.req_cause,
            e.resp_cause,
            e.attachment
        from enquiry e
            left join outbound o on e.id = o.enquiry_id
            left join user_info ui on e.applicant_id = ui.user_id
            left join dept d on e.dept_id = d.id
            left join store s on e.store_id = s.id
        where e.id = #{id}
    """)
    InvEnqVO selectFullEnquiryByEId(Integer id);

    /**
     * 根据要货单id获取要货单信息（单表信息）
     */
    @Select("select id, applicant_id, dept_id, store_id, no, amount, pay_type, state, req_cause, resp_cause, remark, update_time, create_time from enquiry where id = #{id}")
    Enquiry selectEnquiryByEId(Integer id);

    /**
     * 修改要货单信息
     */
    void updateEnquiry(Enquiry enquiry);

    /**
     * 根据要货单id获取出库单信息
     */
    @Select("select id, enquiry_id, no, stocker_id, store_out_id, store_in_id, state, attachment, update_time, create_time from outbound where enquiry_id = #{id}")
    Outbound selectOutboundByEId(Integer id);

    /**
     * 统计要货单数量
     */
    Integer countEnquiry(Integer applicantId, List<Integer> validDeptIds, Integer no, Integer state, String dept, Integer minMoney, Integer maxMoney);

    /**
     * 获取要货单列表
     */
    List<InvEnqSimVO> selectEnquiryList(Integer skip, Integer pageSize, Integer applicantId, List<Integer> validDeptIds, Integer no, Integer state, String dept, Integer minMoney, Integer maxMoney);

    /**
     * 根据要货单id获取要货单详情列表
     */
    @Select("""
        select m.id, m.name,ed.num,ed.money,ed.remark,m.pack as unit
        from enquiry_detail ed
        join material m on ed.material_id = m.id
        where source_type = 1 and source_id = #{sourceId}
    """)
    List<InvEnqDetailVO> selectEDByEId(Integer sourceId);

    /**
     * 根据出库单id/要货单id获取要货单详情列表
     */
    @Select("""
        select m.id, m.name,ed.num,ed.remark,m.pack as unit
        from enquiry_detail ed
        join material m on ed.material_id = m.id
        where source_type = #{sourceType} and source_id = #{sourceId}
    """)
    List<InvOutDetailVO> selectODByTypeAndId(Integer sourceType, Integer sourceId);

    /**
     * 添加出库单基本信息
     */
    @Insert("""
        insert into outbound(enquiry_id, no, stocker_id, store_out_id, store_in_id, state, attachment)
        values (#{enquiryId},#{no},#{stockerId},#{storeOutId},#{storeInId},#{state},#{attachment})
     """)
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insertOutbound(Outbound outbound);

    /**
     * 根据出库单id获取出库单信息（详细信息）
     */
    @Select("""
        select o.no,e.id as enquiryId,e.no as enquiryNo,ui.name as stocker,s1.name as storeOut,s2.name as storeIn,
               d.name as dept,o.state,o.attachment
        from outbound o
        left join enquiry e on o.enquiry_id = e.id
        left join user_info ui on o.stocker_id = ui.user_id
        left join store s1 on o.store_out_id = s1.id
        left join store s2 on o.store_in_id = s2.id
        left join dept d on s1.dept_id = d.id
        where o.id = #{id}
    """)
    InvOutVO selectFullOutboundByOId(Integer id);

    /**
     * 根据出库单id获取出库单信息（单表信息）
     */
    @Select("select id, enquiry_id, no, stocker_id, store_out_id, store_in_id, state, attachment from outbound where id = #{id}")
    Outbound selectOutboundByOId(Integer id);

    /**
     * 根据来源类型和来源id删除要货单/出库单明细
     */
    @Delete("delete from enquiry_detail where source_type = #{sourceType} and source_id = #{sourceId}")
    void deleteEDByTypeAndId(Integer sourceType, Integer sourceId);

    /**
     * 修改出库单信息
     */
    @Update("update outbound set enquiry_id = #{enquiryId}, no = #{no}, stocker_id = #{stockerId}, store_out_id = #{storeOutId}, store_in_id = #{storeInId}, state = #{state}, attachment = #{attachment} where id = #{id}")
    void updateOutbound(Outbound outbound);

    /**
     * 统计出库单数量
     */
    Integer countOutbound(List<Integer> validStoreIds, String no, String stocker, String store, String dept, Integer state);

    /**
     * 获取出库单列表
     */
    List<InvOutSimVO> selectOutboundList(List<Integer> validStoreIds, Integer skip, Integer pageSize, String no, String stocker, String store, String dept, Integer state);

    /**
     * 添加库存修正单
     */
    @Insert("insert into store_amend(store_id, applicant_id,remark) values(#{storeId},#{applicantId},#{remark})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insertStoreAmend(StoreAmend storeAmend);

    /**
     * 添加库存修正单明细
     **/
    void insertStoreAmendDetail(List<StoreAmendDetail> repertoryList);

    /**
     * 修改库存修正单状态
     */
    void updateStoreAmendState(StoreAmend storeAmend);

    /**
     * 根据库存修正单id获取库存修正单明细(基础信息)
     */
    @Select("select amend_id,repertory_id,num from store_amend_detail where amend_id = #{id}")
    List<StoreAmendDetail> selectBaseAmendDetailByOId(Integer id);

    /**
     * 批量修改库存
     */
    Integer batchUpdateRepertoryByRID(List<StoreAmendDetail> details);

    /**
     * 统计库存修正单数量
     */
    Integer countStoreAmendList(List<Integer> validStoreIds, Integer state, String dept);

    /**
     * 获取库存修正单列表
     */
    List<InvAmendSimpleVO> selectStoreAmendList(List<Integer> validStoreIds, Integer state, String dept, Integer skip, Integer pageSize);

    /**
     * 根据库存修正单id获取库存修正单信息(详细详细，列表展示)
     */
    @Select("""
        SELECT d.id as deptId,d.name as deptName, sa.store_id,s.name as storeName,
               sa.applicant_id,ui1.name as applicant,sa.auditor_id,ui2.name as auditor,
               sa.state,sa.remark,sa.cause,sa.create_time
        from store_amend sa
        join store s on sa.store_id = s.id
        join dept d on s.dept_id = d.id
        join user_info ui1 on sa.applicant_id = ui1.user_id
        left join user_info ui2 on sa.auditor_id = ui2.user_id
        where sa.id = #{id}
    """)
    InvAmendVO selectStoreAmendById(Integer id);

    /**
     * 根据库存修正单id获取库存修正单信息(详细信息)
     */
    @Select("""
        select asd.repertory_id,m.name as materialName,asd.num
        from store_amend_detail asd
        join store_material sm on asd.repertory_id = sm.id
        join material m on sm.material_id = m.id
        where asd.amend_id =#{id}
    """)
    List<InvAmendDetailVO> selectAmendDetailByOId(Integer id);

    /**
     * 根据仓库id列表查询仓库信息
     */
    List<InventoryStoreSimplerVO> selectStoreByIds(List<Integer> storeIds);
}
