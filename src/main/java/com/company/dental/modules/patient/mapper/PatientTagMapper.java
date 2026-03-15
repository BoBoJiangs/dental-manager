package com.company.dental.modules.patient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.patient.entity.PatientTagEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PatientTagMapper extends BaseMapper<PatientTagEntity> {

    @Select("""
            SELECT t.id,
                   t.org_id,
                   t.tag_code,
                   t.tag_name,
                   t.tag_color,
                   t.status,
                   t.created_at,
                   t.updated_at
            FROM patient_tag t
            JOIN patient_tag_rel tr ON tr.tag_id = t.id
            WHERE tr.patient_id = #{patientId}
              AND t.status = 1
            ORDER BY t.id
            """)
    List<PatientTagEntity> selectByPatientId(@Param("patientId") Long patientId);
}
