package com.etl.load;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads data to a simple in-memory database simulation.
 * Demonstrates Gradle's task graph with state management.
 */
public class LocalDBLoader {
    
    private final Map<Integer, String[]> database = new HashMap<>();
    private int nextId = 1;
    
    /**
     * Loads records to the local database.
     * 
     * @param records list of data records
     * @return number of records loaded
     */
    public int load(List<String[]> records) {
        int count = 0;
        for (String[] record : records) {
            database.put(nextId++, record);
            count++;
        }
        System.out.println("Loaded " + count + " records to local database");
        return count;
    }
    
    /**
     * Gets all records from the database.
     * 
     * @return list of all records
     */
    public List<String[]> getAllRecords() {
        return new ArrayList<>(database.values());
    }
    
    /**
     * Gets a record by ID.
     * 
     * @param id the record ID
     * @return the record or null if not found
     */
    public String[] getRecord(int id) {
        return database.get(id);
    }
    
    /**
     * Gets the count of records in the database.
     * 
     * @return number of records
     */
    public int getRecordCount() {
        return database.size();
    }
    
    /**
     * Clears all records from the database.
     */
    public void clear() {
        database.clear();
        nextId = 1;
    }
}
