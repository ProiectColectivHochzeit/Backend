package com.proiectcolectiv.demo.service.impl;

import com.proiectcolectiv.demo.service.ExcelImportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ExcelImportServiceImpl implements ExcelImportService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Override
    public List<String> parseEmailsFromFile(MultipartFile file) throws Exception {
        List<String> emails = new ArrayList<>();
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls") && !fileName.endsWith(".csv"))) {
            throw new IllegalArgumentException("File must be an Excel file (.xlsx or .xls) or CSV file (.csv)");
        }

        // Handle CSV files
        if (fileName.endsWith(".csv")) {
            return parseEmailsFromCsv(file);
        }

        // Handle Excel files
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook;
            
            if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                workbook = new HSSFWorkbook(inputStream);
            }

            Sheet sheet = workbook.getSheetAt(0); // Get first sheet
            int rowCount = 0;
            
            for (Row row : sheet) {
                if (rowCount == 0) {
                    // Skip header row
                    rowCount++;
                    continue;
                }
                
                for (Cell cell : row) {
                    String cellValue = getCellValueAsString(cell);
                    if (cellValue != null && !cellValue.trim().isEmpty()) {
                        String email = cellValue.trim();
                        // Validate email format
                        if (EMAIL_PATTERN.matcher(email).matches()) {
                            emails.add(email);
                            log.debug("Found email: {}", email);
                        } else {
                            log.warn("Invalid email format skipped: {}", email);
                        }
                    }
                }
                rowCount++;
            }
            
            workbook.close();
        } catch (Exception e) {
            log.error("Error parsing Excel file: {}", e.getMessage(), e);
            throw new Exception("Failed to parse Excel file: " + e.getMessage(), e);
        }

        if (emails.isEmpty()) {
            throw new IllegalArgumentException("No valid email addresses found in the file");
        }

        log.info("Successfully parsed {} email addresses from file", emails.size());
        return emails;
    }

    private List<String> parseEmailsFromCsv(MultipartFile file) throws Exception {
        List<String> emails = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                // Skip header row
                if (lineNumber == 1) {
                    continue;
                }
                
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Split CSV line by comma, handling quoted values
                String[] values = parseCsvLine(line);
                
                for (String value : values) {
                    String trimmedValue = value.trim();
                    if (!trimmedValue.isEmpty()) {
                        // Validate email format
                        if (EMAIL_PATTERN.matcher(trimmedValue).matches()) {
                            emails.add(trimmedValue);
                            log.debug("Found email: {}", trimmedValue);
                        } else {
                            log.warn("Invalid email format skipped: {}", trimmedValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error parsing CSV file: {}", e.getMessage(), e);
            throw new Exception("Failed to parse CSV file: " + e.getMessage(), e);
        }

        if (emails.isEmpty()) {
            throw new IllegalArgumentException("No valid email addresses found in the CSV file");
        }

        log.info("Successfully parsed {} email addresses from CSV file", emails.size());
        return emails;
    }

    /**
     * Parses a CSV line, handling quoted values that may contain commas.
     * Simple implementation - for production, consider using a CSV library like Apache Commons CSV.
     */
    private String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentValue = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Escaped quote
                    currentValue.append('"');
                    i++; // Skip next quote
                } else {
                    // Toggle quote state
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // End of field
                values.add(currentValue.toString());
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }
        
        // Add last field
        values.add(currentValue.toString());
        
        return values.toArray(new String[0]);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Convert numeric to string without decimal if it's a whole number
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}
