package com.etl.extract;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for FileExtractor class.
 * Tests CSV file extraction functionality including normal cases,
 * edge cases, and error conditions.
 */
public class FileExtractorTest {

    private FileExtractor extractor;
    private String testResourcesPath;

    @Before
    public void setUp() {
        extractor = new FileExtractor();
        // Get the path to test resources from classpath
        URL resourceUrl = getClass().getClassLoader().getResource("");
        if (resourceUrl != null) {
            testResourcesPath = new File(resourceUrl.getFile()).getAbsolutePath() + File.separator;
        } else {
            testResourcesPath = "";
        }
    }

    /**
     * Helper method to get the full path to a test resource file.
     *
     * @param fileName the name of the resource file
     * @return the absolute path to the resource file
     */
    private String getResourcePath(String fileName) {
        URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new RuntimeException("Resource not found: " + fileName);
        }
        return new File(resource.getFile()).getAbsolutePath();
    }

    /**
     * Tests extraction from a normal CSV file with multiple records and fields.
     */
    @Test
    public void testExtractFromFile_NormalData_ReturnsCorrectRecords() throws IOException {
        String filePath = getResourcePath("normal-data.csv");

        List<String[]> records = extractor.extractFromFile(filePath);

        assertNotNull("Records should not be null", records);
        assertEquals("Should have 3 records", 3, records.size());

        // Verify first record
        String[] firstRecord = records.get(0);
        assertEquals("First record should have 4 fields", 4, firstRecord.length);
        assertEquals("John", firstRecord[0]);
        assertEquals("Doe", firstRecord[1]);
        assertEquals("30", firstRecord[2]);
        assertEquals("Engineer", firstRecord[3]);

        // Verify second record
        String[] secondRecord = records.get(1);
        assertEquals("Jane", secondRecord[0]);
        assertEquals("Smith", secondRecord[1]);

        // Verify third record
        String[] thirdRecord = records.get(2);
        assertEquals("Bob", thirdRecord[0]);
        assertEquals("Manager", thirdRecord[3]);
    }

    /**
     * Tests extraction from an empty file.
     */
    @Test
    public void testExtractFromFile_EmptyFile_ReturnsEmptyList() throws IOException {
        String filePath = getResourcePath("empty-file.csv");

        List<String[]> records = extractor.extractFromFile(filePath);

        assertNotNull("Records should not be null", records);
        assertEquals("Should have 0 records for empty file", 0, records.size());
        assertTrue("Records list should be empty", records.isEmpty());
    }

    /**
     * Tests extraction from a file with empty lines (should skip empty lines).
     */
    @Test
    public void testExtractFromFile_FileWithEmptyLines_SkipsEmptyLines() throws IOException {
        String filePath = getResourcePath("file-with-empty-lines.csv");

        List<String[]> records = extractor.extractFromFile(filePath);

        assertNotNull("Records should not be null", records);
        assertEquals("Should have 3 records (empty lines skipped)", 3, records.size());

        // Verify records are correct
        assertEquals("Alice", records.get(0)[0]);
        assertEquals("Bob", records.get(1)[0]);
        assertEquals("Carol", records.get(2)[0]);
    }

    /**
     * Tests extraction from a file with single field per record.
     */
    @Test
    public void testExtractFromFile_SingleField_ReturnsCorrectRecords() throws IOException {
        String filePath = getResourcePath("single-field.csv");

        List<String[]> records = extractor.extractFromFile(filePath);

        assertNotNull("Records should not be null", records);
        assertEquals("Should have 3 records", 3, records.size());

        // Verify each record has single field
        for (String[] record : records) {
            assertEquals("Each record should have 1 field", 1, record.length);
        }

        assertEquals("SingleValue", records.get(0)[0]);
        assertEquals("AnotherValue", records.get(1)[0]);
        assertEquals("ThirdValue", records.get(2)[0]);
    }

    /**
     * Tests extraction from a file with single record.
     */
    @Test
    public void testExtractFromFile_SingleRecord_ReturnsOneRecord() throws IOException {
        String filePath = getResourcePath("single-record.csv");

        List<String[]> records = extractor.extractFromFile(filePath);

        assertNotNull("Records should not be null", records);
        assertEquals("Should have 1 record", 1, records.size());

        String[] record = records.get(0);
        assertEquals("Record should have 3 fields", 3, record.length);
        assertEquals("OnlyOne", record[0]);
        assertEquals("Record", record[1]);
        assertEquals("Here", record[2]);
    }

    /**
     * Tests extraction from a non-existent file (should throw IOException).
     */
    @Test(expected = IOException.class)
    public void testExtractFromFile_NonExistentFile_ThrowsIOException() throws IOException {
        String filePath = "non-existent-file.csv";

        extractor.extractFromFile(filePath);

        fail("Should have thrown IOException for non-existent file");
    }

    /**
     * Tests extraction with null file path (should throw exception).
     */
    @Test(expected = NullPointerException.class)
    public void testExtractFromFile_NullFilePath_ThrowsException() throws IOException {
        extractor.extractFromFile(null);

        fail("Should have thrown exception for null file path");
    }

    /**
     * Tests getRecordCount with normal data file.
     */
    @Test
    public void testGetRecordCount_NormalData_ReturnsCorrectCount() throws IOException {
        String filePath = getResourcePath("normal-data.csv");

        int count = extractor.getRecordCount(filePath);

        assertEquals("Should return 3 records", 3, count);
    }

    /**
     * Tests getRecordCount with empty file.
     */
    @Test
    public void testGetRecordCount_EmptyFile_ReturnsZero() throws IOException {
        String filePath = getResourcePath("empty-file.csv");

        int count = extractor.getRecordCount(filePath);

        assertEquals("Should return 0 for empty file", 0, count);
    }

    /**
     * Tests getRecordCount with file containing empty lines.
     */
    @Test
    public void testGetRecordCount_FileWithEmptyLines_ReturnsCorrectCount() throws IOException {
        String filePath = getResourcePath("file-with-empty-lines.csv");

        int count = extractor.getRecordCount(filePath);

        assertEquals("Should return 3 (empty lines not counted)", 3, count);
    }

    /**
     * Tests getRecordCount with single record.
     */
    @Test
    public void testGetRecordCount_SingleRecord_ReturnsOne() throws IOException {
        String filePath = getResourcePath("single-record.csv");

        int count = extractor.getRecordCount(filePath);

        assertEquals("Should return 1 for single record", 1, count);
    }

    /**
     * Tests getRecordCount with non-existent file (should throw IOException).
     */
    @Test(expected = IOException.class)
    public void testGetRecordCount_NonExistentFile_ThrowsIOException() throws IOException {
        String filePath = "non-existent-file.csv";

        extractor.getRecordCount(filePath);

        fail("Should have thrown IOException for non-existent file");
    }

    /**
     * Tests that extractFromFile returns a new list each time (not cached).
     */
    @Test
    public void testExtractFromFile_MultipleCalls_ReturnsNewListEachTime() throws IOException {
        String filePath = getResourcePath("normal-data.csv");

        List<String[]> records1 = extractor.extractFromFile(filePath);
        List<String[]> records2 = extractor.extractFromFile(filePath);

        assertNotNull("First result should not be null", records1);
        assertNotNull("Second result should not be null", records2);
        assertNotSame("Should return different list instances", records1, records2);
        assertEquals("Both lists should have same size", records1.size(), records2.size());
    }

    /**
     * Tests that FileExtractor can be instantiated multiple times.
     */
    @Test
    public void testFileExtractor_MultipleInstances_WorkIndependently() throws IOException {
        FileExtractor extractor1 = new FileExtractor();
        FileExtractor extractor2 = new FileExtractor();

        String filePath = getResourcePath("normal-data.csv");

        List<String[]> records1 = extractor1.extractFromFile(filePath);
        List<String[]> records2 = extractor2.extractFromFile(filePath);

        assertEquals("Both extractors should return same number of records",
                     records1.size(), records2.size());
    }
}
