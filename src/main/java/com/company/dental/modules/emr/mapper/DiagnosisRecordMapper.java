package com.company.dental.modules.emr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.emr.entity.DiagnosisRecordEntity;
import com.company.dental.modules.emr.vo.DiagnosisRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DiagnosisRecordMapper extends BaseMapper<DiagnosisRecordEntity> {

    @Select("""
            SELECT id,
                   diagnosis_type,
                   diagnosis_code,
                   diagnosis_name,
                   tooth_position,
                   diagnosis_desc,
                   sort_no
            FROM diagnosis_record
            WHERE medical_record_id = #{medicalRecordId}
            ORDER BY sort_no, id
            """)
    List<DiagnosisRecordVO> selectByMedicalRecordId(@Param("medicalRecordId") Long medicalRecordId);
}
