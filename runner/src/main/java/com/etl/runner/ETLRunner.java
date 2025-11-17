package com.etl.runner;

import com.etl.extract.FileExtractor;
import com.etl.transform.DataTransformer;
import com.etl.load.ConsoleLoader;
import com.etl.load.LocalDBLoader;

import java.io.IOException;
import java.util.List;

/**
 * Main ETL Pipeline Runner.
 * Demonstrates Gradle's task graph, caching, and module dependencies.
 */
public class ETLRunner {
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: ETLRunner <file-path>");
            System.err.println("Example: ETLRunner data.csv");
            System.exit(1);
        }
        
        String filePath = args[0];
        System.out.println("Starting ETL Pipeline...");
        System.out.println("Input file: " + filePath);
        System.out.println();
        
        try {
            // Extract phase
            System.out.println("Phase 1: EXTRACT");
            FileExtractor extractor = new FileExtractor();
            List<String[]> rawData = extractor.extractFromFile(filePath);
            System.out.println("Extracted " + rawData.size() + " records");
            System.out.println();
            
            // Transform phase
            System.out.println("Phase 2: TRANSFORM");
            DataTransformer transformer = new DataTransformer();
            List<String[]> transformedData = transformer.transform(rawData);
            List<String[]> filteredData = transformer.filterByFieldCount(transformedData, 1);
            System.out.println("Transformed and filtered " + filteredData.size() + " records");
            System.out.println();
            
            // Load phase - Console
            System.out.println("Phase 3: LOAD (Console)");
            ConsoleLoader consoleLoader = new ConsoleLoader();
            consoleLoader.load(filteredData);
            System.out.println();
            
            // Load phase - Local DB
            System.out.println("Phase 4: LOAD (Local DB)");
            LocalDBLoader dbLoader = new LocalDBLoader();
            int loadedCount = dbLoader.load(filteredData);
            System.out.println("Database now contains " + dbLoader.getRecordCount() + " records");
            System.out.println();

            if (loadedCount > 100) {
                System.out.println("Warning: Loaded record count exceeds 100!");
            } else if (loadedCount < 10) {
                System.out.println("Warning: Loaded record count is below 10!");
            } else if (loadedCount == 0) {
                System.out.println("Error: No records were loaded into the database!");
            } else {
                System.out.println("Loaded record count is within the expected range.");
            }
            
            System.out.println("ETL Pipeline completed successfully!");
            
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
