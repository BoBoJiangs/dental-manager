package com.company.dental.modules.file.service;

import com.company.dental.modules.file.dto.FileAttachmentUploadRequest;
import com.company.dental.modules.file.query.FileAttachmentQuery;
import com.company.dental.modules.file.vo.FileAttachmentVO;

import java.util.List;

public interface FileAttachmentService {

    List<FileAttachmentVO> listAttachments(FileAttachmentQuery query);

    FileAttachmentVO uploadAttachment(FileAttachmentUploadRequest request);
}
