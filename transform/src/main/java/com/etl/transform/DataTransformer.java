package com.etl.transform;

import java.util.ArrayList;
import java.util.List;

/**
 * Transforms data records.
 * Demonstrates Gradle's task graph and caching with processing logic.
 */
public class DataTransformer {
    
    /**
     * Transforms raw records by trimming whitespace and converting to uppercase.
     * 
     * @param records list of raw data records
     * @return list of transformed records
     */
    public List<String[]> transform(List<String[]> records) {
        List<String[]> transformed = new ArrayList<>();
        
        for (String[] record : records) {
            String[] transformedRecord = new String[record.length];
            for (int i = 0; i < record.length; i++) {
                transformedRecord[i] = record[i].trim().toUpperCase();
            }
            transformed.add(transformedRecord);
        }
        
        return transformed;
    }
    
    /**
     * Filters records based on minimum field count.
     * 
     * @param records list of data records
     * @param minFields minimum number of fields required
     * @return filtered list of records
     */
    public List<String[]> filterByFieldCount(List<String[]> records, int minFields) {
        List<String[]> filtered = new ArrayList<>();
        
        for (String[] record : records) {
            if (record.length >= minFields) {
                filtered.add(record);
            }
        }
        
        return filtered;
    }
    
    /**
     * Aggregates data by counting records.
     * 
     * @param records list of data records
     * @return count of records
     */
    public int aggregateCount(List<String[]> records) {
        return records.size();
    }
}
