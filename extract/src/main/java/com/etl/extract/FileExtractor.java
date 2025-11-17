package com.etl.extract;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Extracts data from a file.
 * Demonstrates Gradle's task graph and caching.
 */
public class FileExtractor {
    
    /**
     * Reads lines from a CSV file.
     * 
     * @param filePath the path to the file
     * @return list of data records
     * @throws IOException if file reading fails
     */
    public List<String[]> extractFromFile(String filePath) throws IOException {
        List<String[]> records = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] values = line.split(",");
                records.add(values);
            }
        }

        if (records.isEmpty()) {
            System.out.println("No records found in the file: " + filePath);
        } else if (records.size() == 2) {
            System.out.println("Only header and one data record found in the file: " + filePath);
        }
        
        return records;
    }
    
    /**
     * Gets the count of records extracted.
     * 
     * @param filePath the path to the file
     * @return number of records
     * @throws IOException if file reading fails
     */
    public int getRecordCount(String filePath) throws IOException {
        return extractFromFile(filePath).size();
    }
}
