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
 * Unit tests for ConsoleLoader class.
 * Tests console output functionality including normal cases,
 * edge cases, and output formatting.
 */
public class ConsoleLoaderTest {

    private ConsoleLoader loader;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() {
        loader = new ConsoleLoader();
        // Redirect System.out to capture output
        System.setOut(new PrintStream(outputStream));
    }

    @After
    public void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
    }

    /**
     * Helper method to get captured output as a string.
     *
     * @return the captured output
     */
    private String getCapturedOutput() {
        return outputStream.toString();
    }

    /**
     * Helper method to reset captured output.
     */
    private void resetOutput() {
        outputStream.reset();
    }

    /**
     * Tests load with normal data containing multiple records.
     */
    @Test
    public void testLoad_NormalData_PrintsFormattedOutput() {
        List<String[]> records = Arrays.asList(
            new String[]{"John", "Doe", "30", "Engineer"},
            new String[]{"Jane", "Smith", "25", "Designer"},
            new String[]{"Bob", "Johnson", "35", "Manager"}
        );

        loader.load(records);

        String output = getCapturedOutput();
        assertNotNull("Output should not be null", output);
        assertTrue("Output should contain header", output.contains("===== ETL Pipeline Output ====="));
        assertTrue("Output should contain total records", output.contains("Total Records: 3"));
        assertTrue("Output should contain Record 1", output.contains("Record 1: John | Doe | 30 | Engineer"));
        assertTrue("Output should contain Record 2", output.contains("Record 2: Jane | Smith | 25 | Designer"));
        assertTrue("Output should contain Record 3", output.contains("Record 3: Bob | Johnson | 35 | Manager"));
        assertTrue("Output should contain footer", output.contains("==============================="));
    }

    /**
     * Tests load with empty list.
     */
    @Test
    public void testLoad_EmptyList_PrintsHeaderWithZeroRecords() {
        List<String[]> records = new ArrayList<>();

        loader.load(records);

        String output = getCapturedOutput();
        assertNotNull("Output should not be null", output);
        assertTrue("Output should contain header", output.contains("===== ETL Pipeline Output ====="));
        assertTrue("Output should contain zero records", output.contains("Total Records: 0"));
        assertTrue("Output should contain footer", output.contains("==============================="));
        assertFalse("Output should not contain any Record entries", output.contains("Record 1:"));
    }

    /**
     * Tests load with single record.
     */
    @Test
    public void testLoad_SingleRecord_PrintsSingleRecord() {
        List<String[]> records = Arrays.<String[]>asList(
            new String[]{"Alice", "Williams", "28"}
        );

        loader.load(records);

        String output = getCapturedOutput();
        assertNotNull("Output should not be null", output);
        assertTrue("Output should contain total records", output.contains("Total Records: 1"));
        assertTrue("Output should contain Record 1", output.contains("Record 1: Alice | Williams | 28"));
        assertFalse("Output should not contain Record 2", output.contains("Record 2:"));
    }

    /**
     * Tests load with single field per record.
     */
    @Test
    public void testLoad_SingleFieldRecords_PrintsCorrectly() {
        List<String[]> records = Arrays.asList(
            new String[]{"Value1"},
            new String[]{"Value2"},
            new String[]{"Value3"}
        );

        loader.load(records);

        String output = getCapturedOutput();
        assertNotNull("Output should not be null", output);
        assertTrue("Output should contain Record 1", output.contains("Record 1: Value1"));
        assertTrue("Output should contain Record 2", output.contains("Record 2: Value2"));
        assertTrue("Output should contain Record 3", output.contains("Record 3: Value3"));
    }

    /**
     * Tests load with records containing varying field counts.
     */
    @Test
    public void testLoad_VaryingFieldCounts_PrintsAllRecordsCorrectly() {
        List<String[]> records = Arrays.asList(
            new String[]{"One"},
            new String[]{"Two", "Fields"},
            new String[]{"Three", "Field", "Record"}
        );

        loader.load(records);

        String output = getCapturedOutput();
        assertNotNull("Output should not be null", output);
        assertTrue("Output should contain Record 1", output.contains("Record 1: One"));
        assertTrue("Output should contain Record 2", output.contains("Record 2: Two | Fields"));
        assertTrue("Output should contain Record 3", output.contains("Record 3: Three | Field | Record"));
    }

    /**
     * Tests load with records containing empty strings.
     */
    @Test
    public void testLoad_RecordsWithEmptyStrings_PrintsCorrectly() {
        List<String[]> records = Arrays.asList(
            new String[]{"", "Empty", "First"},
            new String[]{"Empty", "", "Middle"},
            new String[]{"Empty", "Last", ""}
        );

        loader.load(records);

        String output = getCapturedOutput();
        assertNotNull("Output should not be null", output);
        assertTrue("Output should contain Record 1", output.contains("Record 1:  | Empty | First"));
        assertTrue("Output should contain Record 2", output.contains("Record 2: Empty |  | Middle"));
        assertTrue("Output should contain Record 3", output.contains("Record 3: Empty | Last | "));
    }

    /**
     * Tests loadSummary with normal data.
     */
    @Test
    public void testLoadSummary_NormalData_PrintsSummary() {
        List<String[]> records = Arrays.asList(
            new String[]{"John", "Doe", "30", "Engineer"},
            new String[]{"Jane", "Smith", "25", "Designer"}
        );

        loader.loadSummary(records);

        String output = getCapturedOutput();
        assertNotNull("Output should not be null", output);
        assertTrue("Output should contain header", output.contains("===== ETL Pipeline Summary ====="));
        assertTrue("Output should contain total records", output.contains("Total Records Processed: 2"));
        assertTrue("Output should contain fields per record", output.contains("Fields per Record: 4"));
        assertTrue("Output should contain footer", output.contains("================================"));
    }

    /**
     * Tests loadSummary with empty list.
     */
    @Test
    public void testLoadSummary_EmptyList_PrintsSummaryWithoutFieldCount() {
        List<String[]> records = new ArrayList<>();

        loader.loadSummary(records);

        String output = getCapturedOutput();
        assertNotNull("Output should not be null", output);
        assertTrue("Output should contain header", output.contains("===== ETL Pipeline Summary ====="));
        assertTrue("Output should contain zero records", output.contains("Total Records Processed: 0"));
        assertFalse("Output should not contain fields per record", output.contains("Fields per Record:"));
        assertTrue("Output should contain footer", output.contains("================================"));
    }

    /**
     * Tests loadSummary with single record.
     */
    @Test
    public void testLoadSummary_SingleRecord_PrintsCorrectSummary() {
        List<String[]> records = Arrays.<String[]>asList(
            new String[]{"Alice", "Williams", "28"}
        );

        loader.loadSummary(records);

        String output = getCapturedOutput();
        assertNotNull("Output should not be null", output);
        assertTrue("Output should contain total records", output.contains("Total Records Processed: 1"));
        assertTrue("Output should contain fields per record", output.contains("Fields per Record: 3"));
    }

    /**
     * Tests loadSummary with single field records.
     */
    @Test
    public void testLoadSummary_SingleFieldRecords_PrintsCorrectFieldCount() {
        List<String[]> records = Arrays.asList(
            new String[]{"Value1"},
            new String[]{"Value2"}
        );

        loader.loadSummary(records);

        String output = getCapturedOutput();
        assertNotNull("Output should not be null", output);
        assertTrue("Output should contain total records", output.contains("Total Records Processed: 2"));
        assertTrue("Output should contain fields per record", output.contains("Fields per Record: 1"));
    }

    /**
     * Tests that multiple calls to load work correctly.
     */
    @Test
    public void testLoad_MultipleCalls_EachProducesCorrectOutput() {
        List<String[]> records1 = Arrays.<String[]>asList(
            new String[]{"First", "Call"}
        );

        loader.load(records1);
        String output1 = getCapturedOutput();
        assertTrue("First output should contain First Call", output1.contains("Record 1: First | Call"));

        resetOutput();

        List<String[]> records2 = Arrays.<String[]>asList(
            new String[]{"Second", "Call"}
        );

        loader.load(records2);
        String output2 = getCapturedOutput();
        assertTrue("Second output should contain Second Call", output2.contains("Record 1: Second | Call"));
        assertFalse("Second output should not contain First Call", output2.contains("First | Call"));
    }

    /**
     * Tests that ConsoleLoader can be instantiated multiple times.
     */
    @Test
    public void testConsoleLoader_MultipleInstances_WorkIndependently() {
        ConsoleLoader loader1 = new ConsoleLoader();
        ConsoleLoader loader2 = new ConsoleLoader();

        List<String[]> records = Arrays.<String[]>asList(
            new String[]{"Test", "Data"}
        );

        loader1.load(records);
        String output1 = getCapturedOutput();

        resetOutput();

        loader2.load(records);
        String output2 = getCapturedOutput();

        assertNotNull("First loader output should not be null", output1);
        assertNotNull("Second loader output should not be null", output2);
        assertTrue("Both outputs should contain the same data",
                   output1.contains("Test | Data") && output2.contains("Test | Data"));
    }

    /**
     * Tests load with large number of records to ensure proper counting.
     */
    @Test
    public void testLoad_LargeNumberOfRecords_PrintsCorrectCounts() {
        List<String[]> records = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            records.add(new String[]{"Record", String.valueOf(i)});
        }

        loader.load(records);

        String output = getCapturedOutput();
        assertNotNull("Output should not be null", output);
        assertTrue("Output should contain total records", output.contains("Total Records: 100"));
        assertTrue("Output should contain Record 1", output.contains("Record 1:"));
        assertTrue("Output should contain Record 50", output.contains("Record 50:"));
        assertTrue("Output should contain Record 100", output.contains("Record 100:"));
    }

    /**
     * Tests loadSummary with varying field counts (uses first record).
     */
    @Test
    public void testLoadSummary_VaryingFieldCounts_UsesFirstRecord() {
        List<String[]> records = Arrays.asList(
            new String[]{"First", "Record", "Has", "Four", "Fields"},
            new String[]{"Second", "Has", "Three"}
        );

        loader.loadSummary(records);

        String output = getCapturedOutput();
        assertNotNull("Output should not be null", output);
        assertTrue("Output should show field count from first record",
                   output.contains("Fields per Record: 5"));
    }

    /**
     * Tests that load doesn't modify the input list.
     */
    @Test
    public void testLoad_DoesNotModifyInputList() {
        List<String[]> records = new ArrayList<>(Arrays.<String[]>asList(
            new String[]{"Test", "Data"}
        ));
        int originalSize = records.size();

        loader.load(records);

        assertEquals("Input list size should not change", originalSize, records.size());
        assertNotNull("Input list should not be modified", records.get(0));
    }
}
