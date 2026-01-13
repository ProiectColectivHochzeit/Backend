package com.proiectcolectiv.demo.service;

import com.proiectcolectiv.demo.model.Invitation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExcelImportService {
    /**
     * Parses an Excel or CSV file and returns a list of email addresses.
     * @param file the Excel (.xlsx, .xls) or CSV (.csv) file to parse
     * @return list of email addresses from the file
     * @throws Exception if file parsing fails
     */
    List<String> parseEmailsFromFile(MultipartFile file) throws Exception;
}
