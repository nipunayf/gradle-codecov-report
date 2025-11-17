package com.etl.load;

import java.util.List;

/**
 * Loads data to console output.
 * Demonstrates Gradle's task graph with simple output.
 */
public class ConsoleLoader {
    
    /**
     * Loads records to console with formatted output.
     * 
     * @param records list of data records
     */
    public void load(List<String[]> records) {
        System.out.println("===== ETL Pipeline Output =====");
        System.out.println("Total Records: " + records.size());
        System.out.println("-------------------------------");
        
        int count = 1;
        for (String[] record : records) {
            System.out.print("Record " + count++ + ": ");
            System.out.println(String.join(" | ", record));
        }
        
        System.out.println("===============================");
    }
    
    /**
     * Loads a summary to console.
     * 
     * @param records list of data records
     */
    public void loadSummary(List<String[]> records) {
        System.out.println("===== ETL Pipeline Summary =====");
        System.out.println("Total Records Processed: " + records.size());
        
        if (!records.isEmpty()) {
            System.out.println("Fields per Record: " + records.get(0).length);
        } else {
            System.out.println("No records to summarize.");
        }
        
        System.out.println("================================");
    }
}
