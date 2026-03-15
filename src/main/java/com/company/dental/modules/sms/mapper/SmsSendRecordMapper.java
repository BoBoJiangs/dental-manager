package com.company.dental.modules.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.sms.entity.SmsSendRecordEntity;
import com.company.dental.modules.sms.vo.SmsSendRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SmsSendRecordMapper extends BaseMapper<SmsSendRecordEntity> {

    @Select("""
            SELECT id,
                   provider_name AS providerName,
                   provider_msg_id AS providerMsgId,
                   send_status AS sendStatus,
                   response_code AS responseCode,
                   response_msg AS responseMsg,
                   sent_at AS sentAt,
                   created_at AS createdAt
            FROM sms_send_record
            WHERE sms_task_id = #{smsTaskId}
            ORDER BY id DESC
            """)
    List<SmsSendRecordVO> selectBySmsTaskId(@Param("smsTaskId") Long smsTaskId);
}
