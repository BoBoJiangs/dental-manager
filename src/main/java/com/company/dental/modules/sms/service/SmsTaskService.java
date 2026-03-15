package com.company.dental.modules.sms.service;

import com.company.dental.common.api.PageResult;
import com.company.dental.modules.sms.dto.SmsTaskCreateRequest;
import com.company.dental.modules.sms.query.SmsTaskPageQuery;
import com.company.dental.modules.sms.vo.SmsTaskDetailVO;
import com.company.dental.modules.sms.vo.SmsTaskPageItemVO;
import com.company.dental.modules.sms.vo.SmsTemplateVO;

import java.util.List;

public interface SmsTaskService {

    List<SmsTemplateVO> listTemplates();

    PageResult<SmsTaskPageItemVO> pageTasks(SmsTaskPageQuery query);

    SmsTaskDetailVO getTaskDetail(Long taskId);

    SmsTaskDetailVO createTask(SmsTaskCreateRequest request);
}
