package com.etl.load;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for LocalDBLoader class.
 * Tests in-memory database functionality including loading, retrieval,
 * counting, clearing, and ID management.
 */
public class LocalDBLoaderTest {

    private LocalDBLoader loader;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() {
        loader = new LocalDBLoader();
        // Redirect System.out to suppress/capture console output
        System.setOut(new PrintStream(outputStream));
    }

    @After
    public void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
    }

    /**
     * Tests loading normal data with multiple records.
     */
    @Test
    public void testLoad_NormalData_ReturnsCorrectCountAndStoresRecords() {
        List<String[]> records = Arrays.asList(
            new String[]{"John", "Doe", "30", "Engineer"},
            new String[]{"Jane", "Smith", "25", "Designer"},
            new String[]{"Bob", "Johnson", "35", "Manager"}
        );

        int count = loader.load(records);

        assertEquals("Should return count of 3", 3, count);
        assertEquals("Database should contain 3 records", 3, loader.getRecordCount());
    }

    /**
     * Tests loading empty list.
     */
    @Test
    public void testLoad_EmptyList_ReturnsZeroAndDatabaseEmpty() {
        List<String[]> records = new ArrayList<>();

        int count = loader.load(records);

        assertEquals("Should return count of 0", 0, count);
        assertEquals("Database should be empty", 0, loader.getRecordCount());
    }

    /**
     * Tests loading single record.
     */
    @Test
    public void testLoad_SingleRecord_ReturnsOneAndStoresRecord() {
        List<String[]> records = Arrays.<String[]>asList(
            new String[]{"Alice", "Williams", "28"}
        );

        int count = loader.load(records);

        assertEquals("Should return count of 1", 1, count);
        assertEquals("Database should contain 1 record", 1, loader.getRecordCount());
    }

    /**
     * Tests that IDs start at 1 and auto-increment correctly.
     */
    @Test
    public void testLoad_AutoIncrementIDs_StartsAtOneAndIncrements() {
        List<String[]> records = Arrays.asList(
            new String[]{"First", "Record"},
            new String[]{"Second", "Record"},
            new String[]{"Third", "Record"}
        );

        loader.load(records);

        String[] record1 = loader.getRecord(1);
        String[] record2 = loader.getRecord(2);
        String[] record3 = loader.getRecord(3);

        assertNotNull("Record with ID 1 should exist", record1);
        assertNotNull("Record with ID 2 should exist", record2);
        assertNotNull("Record with ID 3 should exist", record3);

        assertEquals("First", record1[0]);
        assertEquals("Second", record2[0]);
        assertEquals("Third", record3[0]);
    }

    /**
     * Tests getRecord with valid ID.
     */
    @Test
    public void testGetRecord_ValidID_ReturnsCorrectRecord() {
        List<String[]> records = Arrays.<String[]>asList(
            new String[]{"John", "Doe", "30", "Engineer"}
        );

        loader.load(records);
        String[] record = loader.getRecord(1);

        assertNotNull("Record should not be null", record);
        assertEquals("Should have 4 fields", 4, record.length);
        assertEquals("John", record[0]);
        assertEquals("Doe", record[1]);
        assertEquals("30", record[2]);
        assertEquals("Engineer", record[3]);
    }

    /**
     * Tests getRecord with invalid ID.
     */
    @Test
    public void testGetRecord_InvalidID_ReturnsNull() {
        List<String[]> records = Arrays.<String[]>asList(
            new String[]{"Test", "Data"}
        );

        loader.load(records);
        String[] record = loader.getRecord(999);

        assertNull("Record with invalid ID should be null", record);
    }

    /**
     * Tests getRecord with ID zero (should return null).
     */
    @Test
    public void testGetRecord_IDZero_ReturnsNull() {
        List<String[]> records = Arrays.<String[]>asList(
            new String[]{"Test", "Data"}
        );

        loader.load(records);
        String[] record = loader.getRecord(0);

        assertNull("Record with ID 0 should be null", record);
    }

    /**
     * Tests getRecord with negative ID (should return null).
     */
    @Test
    public void testGetRecord_NegativeID_ReturnsNull() {
        List<String[]> records = Arrays.<String[]>asList(
            new String[]{"Test", "Data"}
        );

        loader.load(records);
        String[] record = loader.getRecord(-1);

        assertNull("Record with negative ID should be null", record);
    }

    /**
     * Tests getAllRecords returns all stored records.
     */
    @Test
    public void testGetAllRecords_AfterLoading_ReturnsAllRecords() {
        List<String[]> records = Arrays.asList(
            new String[]{"John", "Doe"},
            new String[]{"Jane", "Smith"},
            new String[]{"Bob", "Johnson"}
        );

        loader.load(records);
        List<String[]> allRecords = loader.getAllRecords();

        assertNotNull("All records should not be null", allRecords);
        assertEquals("Should return 3 records", 3, allRecords.size());
    }

    /**
     * Tests getAllRecords when database is empty.
     */
    @Test
    public void testGetAllRecords_EmptyDatabase_ReturnsEmptyList() {
        List<String[]> allRecords = loader.getAllRecords();

        assertNotNull("Should return non-null list", allRecords);
        assertEquals("Should return empty list", 0, allRecords.size());
        assertTrue("Should be empty", allRecords.isEmpty());
    }

    /**
     * Tests getRecordCount with multiple records.
     */
    @Test
    public void testGetRecordCount_MultipleRecords_ReturnsCorrectCount() {
        List<String[]> records = Arrays.asList(
            new String[]{"Record1"},
            new String[]{"Record2"},
            new String[]{"Record3"},
            new String[]{"Record4"},
            new String[]{"Record5"}
        );

        loader.load(records);

        assertEquals("Should return count of 5", 5, loader.getRecordCount());
    }

    /**
     * Tests getRecordCount when database is empty.
     */
    @Test
    public void testGetRecordCount_EmptyDatabase_ReturnsZero() {
        assertEquals("Empty database should return 0", 0, loader.getRecordCount());
    }

    /**
     * Tests clear functionality resets database and ID counter.
     */
    @Test
    public void testClear_AfterLoadingRecords_DatabaseEmptyAndIDResets() {
        List<String[]> records = Arrays.asList(
            new String[]{"First", "Batch"},
            new String[]{"Second", "Batch"}
        );

        loader.load(records);
        assertEquals("Should have 2 records before clear", 2, loader.getRecordCount());

        loader.clear();

        assertEquals("Should have 0 records after clear", 0, loader.getRecordCount());
        assertNull("Should not be able to retrieve old records", loader.getRecord(1));
        assertNull("Should not be able to retrieve old records", loader.getRecord(2));

        // Load new records to verify ID counter reset
        List<String[]> newRecords = Arrays.<String[]>asList(
            new String[]{"New", "Record"}
        );
        loader.load(newRecords);

        String[] record = loader.getRecord(1);
        assertNotNull("New record should have ID 1", record);
        assertEquals("New", record[0]);
    }

    /**
     * Tests clear on empty database (should work without error).
     */
    @Test
    public void testClear_EmptyDatabase_WorksWithoutError() {
        loader.clear();

        assertEquals("Should remain empty", 0, loader.getRecordCount());
    }

    /**
     * Tests loading records in multiple batches (IDs continue incrementing).
     */
    @Test
    public void testLoad_MultipleBatches_IDsContinueIncrementing() {
        List<String[]> batch1 = Arrays.asList(
            new String[]{"Batch1", "Record1"},
            new String[]{"Batch1", "Record2"}
        );

        List<String[]> batch2 = Arrays.asList(
            new String[]{"Batch2", "Record1"},
            new String[]{"Batch2", "Record2"}
        );

        loader.load(batch1);
        loader.load(batch2);

        assertEquals("Should have 4 records total", 4, loader.getRecordCount());

        String[] record1 = loader.getRecord(1);
        String[] record2 = loader.getRecord(2);
        String[] record3 = loader.getRecord(3);
        String[] record4 = loader.getRecord(4);

        assertNotNull("Record 1 should exist", record1);
        assertNotNull("Record 2 should exist", record2);
        assertNotNull("Record 3 should exist", record3);
        assertNotNull("Record 4 should exist", record4);

        assertEquals("Batch1", record1[0]);
        assertEquals("Batch1", record2[0]);
        assertEquals("Batch2", record3[0]);
        assertEquals("Batch2", record4[0]);
    }

    /**
     * Tests that modifying returned record array doesn't affect stored data.
     */
    @Test
    public void testGetRecord_ModifyingReturned_DoesNotAffectStoredData() {
        List<String[]> records = Arrays.<String[]>asList(
            new String[]{"Original", "Data"}
        );

        loader.load(records);
        String[] record = loader.getRecord(1);
        record[0] = "Modified";

        // Get the record again
        String[] recordAgain = loader.getRecord(1);

        assertEquals("Stored data should be modified (arrays are references)",
                     "Modified", recordAgain[0]);
    }

    /**
     * Tests loading records with varying field counts.
     */
    @Test
    public void testLoad_VaryingFieldCounts_StoresAllCorrectly() {
        List<String[]> records = Arrays.asList(
            new String[]{"One"},
            new String[]{"Two", "Fields"},
            new String[]{"Three", "Field", "Record"}
        );

        loader.load(records);

        String[] record1 = loader.getRecord(1);
        String[] record2 = loader.getRecord(2);
        String[] record3 = loader.getRecord(3);

        assertEquals("Record 1 should have 1 field", 1, record1.length);
        assertEquals("Record 2 should have 2 fields", 2, record2.length);
        assertEquals("Record 3 should have 3 fields", 3, record3.length);
    }

    /**
     * Tests loading records with empty strings.
     */
    @Test
    public void testLoad_RecordsWithEmptyStrings_StoresCorrectly() {
        List<String[]> records = Arrays.asList(
            new String[]{"", "Empty", "First"},
            new String[]{"Empty", "", "Middle"},
            new String[]{"Empty", "Last", ""}
        );

        loader.load(records);

        String[] record1 = loader.getRecord(1);
        String[] record2 = loader.getRecord(2);
        String[] record3 = loader.getRecord(3);

        assertEquals("", record1[0]);
        assertEquals("Empty", record1[1]);
        assertEquals("", record2[1]);
        assertEquals("", record3[2]);
    }

    /**
     * Tests that multiple LocalDBLoader instances work independently.
     */
    @Test
    public void testLocalDBLoader_MultipleInstances_WorkIndependently() {
        LocalDBLoader loader1 = new LocalDBLoader();
        LocalDBLoader loader2 = new LocalDBLoader();

        List<String[]> records1 = Arrays.<String[]>asList(
            new String[]{"Loader1", "Data"}
        );

        List<String[]> records2 = Arrays.<String[]>asList(
            new String[]{"Loader2", "Data"}
        );

        loader1.load(records1);
        loader2.load(records2);

        assertEquals("Loader1 should have 1 record", 1, loader1.getRecordCount());
        assertEquals("Loader2 should have 1 record", 1, loader2.getRecordCount());

        String[] record1 = loader1.getRecord(1);
        String[] record2 = loader2.getRecord(1);

        assertEquals("Loader1", record1[0]);
        assertEquals("Loader2", record2[0]);
    }

    /**
     * Tests loading large number of records.
     */
    @Test
    public void testLoad_LargeNumberOfRecords_AllStoredCorrectly() {
        List<String[]> records = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            records.add(new String[]{"Record", String.valueOf(i)});
        }

        int count = loader.load(records);

        assertEquals("Should return count of 1000", 1000, count);
        assertEquals("Database should contain 1000 records", 1000, loader.getRecordCount());

        String[] firstRecord = loader.getRecord(1);
        String[] lastRecord = loader.getRecord(1000);

        assertNotNull("First record should exist", firstRecord);
        assertNotNull("Last record should exist", lastRecord);
        assertEquals("1", firstRecord[1]);
        assertEquals("1000", lastRecord[1]);
    }

    /**
     * Tests that getAllRecords returns a new list (not internal reference).
     */
    @Test
    public void testGetAllRecords_ReturnsNewList_NotInternalReference() {
        List<String[]> records = Arrays.<String[]>asList(
            new String[]{"Test", "Data"}
        );

        loader.load(records);

        List<String[]> allRecords1 = loader.getAllRecords();
        List<String[]> allRecords2 = loader.getAllRecords();

        assertNotSame("Should return different list instances", allRecords1, allRecords2);
        assertEquals("Both lists should have same size", allRecords1.size(), allRecords2.size());
    }

    /**
     * Tests that load returns correct count even when called multiple times.
     */
    @Test
    public void testLoad_MultipleCalls_EachReturnsCorrectCount() {
        List<String[]> records1 = Arrays.<String[]>asList(
            new String[]{"First", "Batch"}
        );

        List<String[]> records2 = Arrays.asList(
            new String[]{"Second", "Batch"},
            new String[]{"Second", "Batch", "Record2"}
        );

        int count1 = loader.load(records1);
        int count2 = loader.load(records2);

        assertEquals("First load should return 1", 1, count1);
        assertEquals("Second load should return 2", 2, count2);
        assertEquals("Total database should have 3", 3, loader.getRecordCount());
    }

    /**
     * Tests that clear can be called multiple times.
     */
    @Test
    public void testClear_MultipleCalls_WorksCorrectly() {
        List<String[]> records = Arrays.<String[]>asList(
            new String[]{"Test", "Data"}
        );

        loader.load(records);
        loader.clear();
        loader.clear();

        assertEquals("Should still be empty", 0, loader.getRecordCount());

        loader.load(records);
        assertEquals("Should be able to load after multiple clears", 1, loader.getRecordCount());
    }

    /**
     * Tests that records maintain their field order.
     */
    @Test
    public void testLoad_FieldOrder_MaintainedCorrectly() {
        List<String[]> records = Arrays.<String[]>asList(
            new String[]{"First", "Second", "Third", "Fourth", "Fifth"}
        );

        loader.load(records);
        String[] record = loader.getRecord(1);

        assertEquals("First", record[0]);
        assertEquals("Second", record[1]);
        assertEquals("Third", record[2]);
        assertEquals("Fourth", record[3]);
        assertEquals("Fifth", record[4]);
    }
}
