# ETL Pipeline - Gradle Multi-Module Project

A simple ETL (Extract, Transform, Load) pipeline demonstrating Gradle's task graph and caching capabilities.

## Project Structure

This is a multi-module Gradle project with the following modules:

```
etl-pipeline/
├── extract/          # File reader module
├── transform/        # Data processor module
├── load/             # Console and local DB output module
├── runner/           # Main application runner
└── build.gradle      # Root build configuration
```

## Modules

### 1. Extract Module
- **Purpose**: Reads data from CSV files
- **Key Classes**: `FileExtractor`
- **Functionality**: Extracts records from file and returns them as a list

### 2. Transform Module
- **Purpose**: Processes and transforms data
- **Dependencies**: `extract` module
- **Key Classes**: `DataTransformer`
- **Functionality**: 
  - Transforms data (trim, uppercase)
  - Filters records by field count
  - Aggregates data

### 3. Load Module
- **Purpose**: Outputs processed data
- **Dependencies**: `transform` module
- **Key Classes**: `ConsoleLoader`, `LocalDBLoader`
- **Functionality**:
  - Console output with formatted display
  - In-memory database simulation

### 4. Runner Module
- **Purpose**: Orchestrates the ETL pipeline
- **Dependencies**: `extract`, `transform`, `load` modules
- **Key Classes**: `ETLRunner`
- **Functionality**: Executes the complete ETL workflow

## Building the Project

Build all modules:
```bash
gradle build
```

## Running the Pipeline

Run the ETL pipeline with sample data:
```bash
gradle :runner:runETL
```

Run with a custom data file:
```bash
gradle :runner:run --args="/path/to/your/data.csv"
```

## Sample Output

```
Starting ETL Pipeline...
Input file: sample-data.csv

Phase 1: EXTRACT
Extracted 10 records

Phase 2: TRANSFORM
Transformed and filtered 10 records

Phase 3: LOAD (Console)
===== ETL Pipeline Output =====
Total Records: 10
-------------------------------
Record 1: JOHN | DOE | 30 | ENGINEER
...
===============================

Phase 4: LOAD (Local DB)
Loaded 10 records to local database
Database now contains 10 records

ETL Pipeline completed successfully!
```

## Why This is Good for Gradle Demonstrations

1. **Task Graph**: Each module has clear dependencies, demonstrating Gradle's ability to build modules in the correct order (extract → transform → load → runner)

2. **Caching**: When you run the build multiple times, Gradle caches unchanged modules, showing build optimization

3. **Multi-Module**: Demonstrates proper module separation and dependency management

4. **Incremental Builds**: Changes to one module only rebuild that module and its dependents

## Testing Gradle Features

### View Task Dependencies
```bash
gradle :runner:dependencies
```

### View Task Graph
```bash
gradle :runner:runETL --dry-run
```

### Build with Info
```bash
gradle build --info
```

### Enable Configuration Cache
```bash
gradle build --configuration-cache
```

## Testing

### Running Tests

Run all tests:
```bash
gradle test
```

Run tests for a specific module:
```bash
gradle :extract:test
```

Run tests with coverage report:
```bash
gradle testCoverage
```

### Test Coverage

This project uses JaCoCo for code coverage analysis and integrates with Codecov for:
- **Code Coverage Tracking**: Monitors test coverage across all modules
- **Test Analytics**: Tracks test execution, failures, and flaky tests

## 📊 Coverage Requirements

⚠️ **Strict Coverage Policy**: This project enforces **100% line coverage** for all modules. Any PR with uncovered lines will fail CI checks.

### What gets enforced:
- ✅ **100% line coverage** for all Java source files
- ✅ **Zero uncovered lines** in new patches
- ✅ **Per-module validation** - each module must have 100% coverage
- ✅ **GitHub annotations** show uncovered lines in PR diffs
- ✅ **Automated CI failures** when coverage drops below 100%

### How to check coverage locally:
```bash
# Run tests with coverage
gradle testCoverage

# Validate 100% coverage (fails if any uncovered lines)
gradle validateAllCoverage

# Check specific module coverage
gradle :extract:validateCoverage
```

Coverage reports are generated at:
- **HTML Report**: `build/reports/jacoco/test/html/index.html`
- **XML Report**: `build/reports/jacoco/test/jacocoTestReport.xml`
- **Module Reports**: `<module>/build/reports/jacoco/test/html/`

Test results are generated at:
- `<module>/build/test-results/test/` (JUnit XML format)

### Viewing Uncovered Lines
To see exactly which lines are uncovered:
```bash
# Open the HTML report for a specific module
open extract/build/reports/jacoco/test/html/com.etl.extract/FileExtractor.java.html
```

[![codecov](https://codecov.io/gh/nipunayf/gradle-codecov-report/graph/badge.svg)](https://codecov.io/gh/nipunayf/gradle-codecov-report)

## CSV File Format

The pipeline expects CSV files with comma-separated values:
```
John,Doe,30,Engineer
Jane,Smith,25,Designer
```

Each line represents a record that will be extracted, transformed, and loaded.