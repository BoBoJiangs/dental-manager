package com.company.dental.modules.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.sms.entity.SmsTemplateEntity;
import com.company.dental.modules.sms.vo.SmsTemplateVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SmsTemplateMapper extends BaseMapper<SmsTemplateEntity> {

    @Select("""
            SELECT id,
                   clinic_id,
                   template_code,
                   template_name,
                   biz_type,
                   content,
                   sign_name,
                   enabled_flag,
                   remark
            FROM sms_template
            WHERE org_id = #{orgId}
              AND enabled_flag = 1
            ORDER BY clinic_id, id
            """)
    List<SmsTemplateVO> selectEnabledTemplates(@Param("orgId") Long orgId);
}
