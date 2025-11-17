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

## CSV File Format

The pipeline expects CSV files with comma-separated values:
```
John,Doe,30,Engineer
Jane,Smith,25,Designer
```

Each line represents a record that will be extracted, transformed, and loaded.