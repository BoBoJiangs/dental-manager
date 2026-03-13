package com.company.dental.integration.file;

import java.io.InputStream;

public interface FileStorageService {

    String upload(String objectName, InputStream inputStream, String contentType);
}
