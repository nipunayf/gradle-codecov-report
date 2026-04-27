package com.etl.transform;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DataTransformerTest {

    private DataTransformer transformer;

    @Before
    public void setUp() {
        transformer = new DataTransformer();
    }

    @Test
    public void testAggregateCount_WithRecords_ReturnsRecordCount() {
        List<String[]> records = Arrays.asList(
                new String[]{"John", "Doe"},
                new String[]{"Jane", "Smith"}
        );

        int count = transformer.aggregateCount(records);

        assertEquals("Should count all records", 2, count);
    }

    @Test
    public void testAggregateCount_WithNoRecords_ReturnsZero() {
        int count = transformer.aggregateCount(Collections.emptyList());

        assertEquals("Should return zero for empty records", 0, count);
    }
}
