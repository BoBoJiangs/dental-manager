package com.company.dental.modules.appointment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.appointment.entity.DoctorScheduleEntity;
import com.company.dental.modules.appointment.query.DoctorScheduleQuery;
import com.company.dental.modules.appointment.vo.DoctorScheduleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DoctorScheduleMapper extends BaseMapper<DoctorScheduleEntity> {

    List<DoctorScheduleVO> selectScheduleList(@Param("query") DoctorScheduleQuery query,
                                              @Param("orgId") Long orgId,
                                              @Param("accessScope") String accessScope,
                                              @Param("clinicIds") List<Long> clinicIds,
                                              @Param("staffId") Long staffId);

    @Select("""
            SELECT COUNT(1)
            FROM clinic
            WHERE id = #{clinicId}
              AND org_id = #{orgId}
              AND status = 1
              AND is_deleted = 0
            """)
    Long countValidClinic(@Param("orgId") Long orgId, @Param("clinicId") Long clinicId);

    @Select("""
            SELECT COUNT(1)
            FROM staff
            WHERE id = #{doctorId}
              AND org_id = #{orgId}
              AND status = 1
              AND is_deleted = 0
              AND is_doctor = 1
            """)
    Long countValidDoctor(@Param("orgId") Long orgId, @Param("doctorId") Long doctorId);

    @Select("""
            SELECT COUNT(1)
            FROM room
            WHERE id = #{roomId}
              AND org_id = #{orgId}
              AND status = 1
              AND is_deleted = 0
            """)
    Long countValidRoom(@Param("orgId") Long orgId, @Param("roomId") Long roomId);
}
