# CLAUDE.md - AI Assistant Guide for ETL Pipeline Project

This document provides comprehensive guidance for AI assistants working with this codebase. It covers project structure, conventions, workflows, and best practices.

---

## Project Overview

**Project Name**: ETL Pipeline - Gradle Multi-Module Project
**Repository**: gradle-codecov-report
**Purpose**: Educational Gradle project demonstrating multi-module architecture, task graph execution, dependency management, and build caching through a complete ETL (Extract, Transform, Load) pipeline.

**Technology Stack**:
- **Language**: Java 11
- **Build System**: Gradle 9.2.0
- **Testing Framework**: JUnit 4.13.2 (configured but not yet implemented)
- **Architecture**: Multi-module ETL pipeline pattern
- **License**: MIT License (Copyright 2025 Nipuna Fernando)

---

## Repository Structure

```
/home/user/gradle-codecov-report/
├── .git/                                  # Git repository metadata
├── .gitignore                             # Git ignore configuration
├── LICENSE                                # MIT License file
├── README.md                              # User-facing project documentation
├── CLAUDE.md                              # This file - AI assistant guide
├── settings.gradle                        # Gradle settings defining modules
├── build.gradle                           # Root Gradle build configuration
├── gradlew                                # Gradle wrapper script (Unix)
├── gradlew.bat                            # Gradle wrapper script (Windows)
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar             # Gradle wrapper JAR
│       └── gradle-wrapper.properties      # Gradle version: 9.2.0
│
├── extract/                               # Extract Module
│   ├── build.gradle                       # Module build configuration
│   └── src/main/java/com/etl/extract/
│       └── FileExtractor.java             # CSV file reading (50 lines)
│
├── transform/                             # Transform Module
│   ├── build.gradle                       # Module build configuration
│   └── src/main/java/com/etl/transform/
│       └── DataTransformer.java           # Data processing (60 lines)
│
├── load/                                  # Load Module
│   ├── build.gradle                       # Module build configuration
│   └── src/main/java/com/etl/load/
│       ├── ConsoleLoader.java             # Console output (45 lines)
│       └── LocalDBLoader.java             # In-memory database (68 lines)
│
└── runner/                                # Runner Module
    ├── build.gradle                       # Module build configuration
    ├── src/main/java/com/etl/runner/
    │   └── ETLRunner.java                 # Main application (65 lines)
    └── src/main/resources/
        └── sample-data.csv                # Sample test data (10 records)
```

**Total Java Code**: 288 lines across 5 classes
**Total Modules**: 4 (extract, transform, load, runner)

---

## Module Architecture

### Module Dependency Chain

```
extract (base module - no dependencies)
   ↓
transform (depends on extract)
   ↓
load (depends on transform)
   ↓
runner (depends on all three: extract, transform, load)
```

### Module Details

| Module | Plugin | Package | Purpose | Dependencies |
|--------|--------|---------|---------|--------------|
| **extract** | `java-library` | `com.etl.extract` | Reads CSV files | None |
| **transform** | `java-library` | `com.etl.transform` | Processes/transforms data | `project(':extract')` |
| **load** | `java-library` | `com.etl.load` | Outputs data (console, DB) | `project(':transform')` |
| **runner** | `application` | `com.etl.runner` | Orchestrates ETL workflow | All modules |

### Data Flow

1. **Extract**: `FileExtractor` reads CSV files → Returns `List<String[]>`
2. **Transform**: `DataTransformer` processes records → Returns transformed `List<String[]>`
3. **Load**:
   - `ConsoleLoader` displays formatted output to console
   - `LocalDBLoader` stores data in in-memory HashMap database
4. **Runner**: `ETLRunner` orchestrates all phases in sequence

---

## Key Classes and Their Responsibilities

### 1. FileExtractor (`extract/src/main/java/com/etl/extract/FileExtractor.java`)

**Location**: extract/src/main/java/com/etl/extract/FileExtractor.java:1
**Purpose**: Reads and parses CSV files
**Methods**:
- `extractFromFile(String filePath)`: Reads CSV, returns `List<String[]>` of records
- `getRecordCount(String filePath)`: Returns count of records in file

**Implementation Details**:
- Uses `BufferedReader` with try-with-resources
- Splits CSV lines by comma delimiter
- Skips empty lines automatically
- Throws `IOException` on file errors

### 2. DataTransformer (`transform/src/main/java/com/etl/transform/DataTransformer.java`)

**Location**: transform/src/main/java/com/etl/transform/DataTransformer.java:1
**Purpose**: Transforms and filters extracted data
**Methods**:
- `transform(List<String[]> records)`: Trims whitespace and converts to uppercase
- `filterByFieldCount(List<String[]> records, int minFields)`: Filters by minimum field count
- `aggregateCount(List<String[]> records)`: Returns record count

**Implementation Details**:
- Processes each field individually (trim + uppercase)
- Returns new lists (doesn't modify input)
- Uses streams for filtering operations

### 3. ConsoleLoader (`load/src/main/java/com/etl/load/ConsoleLoader.java`)

**Location**: load/src/main/java/com/etl/load/ConsoleLoader.java:1
**Purpose**: Outputs data to console with formatting
**Methods**:
- `load(List<String[]> records)`: Prints formatted records to stdout
- `loadSummary(List<String[]> records)`: Prints summary statistics

**Implementation Details**:
- Formats output with headers and separators
- Uses pipe delimiter for display (`JOHN | DOE | 30 | ENGINEER`)
- Returns count of loaded records

### 4. LocalDBLoader (`load/src/main/java/com/etl/load/LocalDBLoader.java`)

**Location**: load/src/main/java/com/etl/load/LocalDBLoader.java:1
**Purpose**: In-memory database simulation
**State**:
- `HashMap<Integer, String[]> database`: Stores records with auto-increment IDs
- `int nextId`: Tracks next available ID (starts at 1)

**Methods**:
- `load(List<String[]> records)`: Inserts records, returns count
- `getAllRecords()`: Returns all records as Map
- `getRecord(int id)`: Retrieves specific record by ID
- `getRecordCount()`: Returns total record count
- `clear()`: Resets database and ID counter

**Implementation Details**:
- Not thread-safe (single-threaded use only)
- IDs auto-increment starting from 1
- Data stored only in memory (not persistent)

### 5. ETLRunner (`runner/src/main/java/com/etl/runner/ETLRunner.java`)

**Location**: runner/src/main/java/com/etl/runner/ETLRunner.java:1
**Purpose**: Main application entry point, orchestrates ETL workflow
**Main Method**: `public static void main(String[] args)`

**Execution Flow**:
1. Validates command-line arguments (requires file path)
2. **Phase 1 - EXTRACT**: Reads CSV using `FileExtractor`
3. **Phase 2 - TRANSFORM**: Transforms and filters using `DataTransformer`
4. **Phase 3 - LOAD (Console)**: Outputs to console using `ConsoleLoader`
5. **Phase 4 - LOAD (Local DB)**: Stores in memory using `LocalDBLoader`
6. Prints completion message or error

**Error Handling**:
- Validates arguments, exits with code 1 if missing
- Catches `IOException`, prints error to stderr, exits with code 1

---

## Build Configuration

### Root Build File (`build.gradle`)

**Location**: build.gradle:1

```gradle
plugins {
    id 'java'
}

allprojects {
    group = 'com.etl.pipeline'
    version = '1.0-SNAPSHOT'

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply plugin: 'java'

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        testImplementation 'junit:junit:4.13.2'
    }
}
```

**Key Configuration**:
- **Group**: `com.etl.pipeline`
- **Version**: `1.0-SNAPSHOT`
- **Java Version**: 11 (source and target compatibility)
- **Repository**: Maven Central
- **Test Framework**: JUnit 4.13.2 (not yet used)

### Module Build Files

#### Extract Module (`extract/build.gradle`)
```gradle
plugins {
    id 'java-library'
}

description = 'Extract module - File reader'
```

#### Transform Module (`transform/build.gradle`)
```gradle
plugins {
    id 'java-library'
}

dependencies {
    implementation project(':extract')
}

description = 'Transform module - Data processors'
```

#### Load Module (`load/build.gradle`)
```gradle
plugins {
    id 'java-library'
}

dependencies {
    implementation project(':transform')
}

description = 'Load module - Console and local DB output'
```

#### Runner Module (`runner/build.gradle`)
```gradle
plugins {
    id 'application'
}

dependencies {
    implementation project(':extract')
    implementation project(':transform')
    implementation project(':load')
}

application {
    mainClass = 'com.etl.runner.ETLRunner'
}

// Custom task to run with sample data
task runETL(type: JavaExec) {
    group = 'application'
    description = 'Runs the ETL pipeline with sample data'
    classpath = sourceSets.main.runtimeClasspath
    mainClass = 'com.etl.runner.ETLRunner'
    args 'src/main/resources/sample-data.csv'
}

description = 'Runner module - Main application'
```

---

## Development Workflows

### Building the Project

```bash
# Build all modules
gradle build

# Build specific module
gradle :extract:build

# Clean and build
gradle clean build

# Build with info logging
gradle build --info

# Enable configuration cache (Gradle optimization)
gradle build --configuration-cache
```

**Build Order** (handled automatically by Gradle):
1. extract → 2. transform → 3. load → 4. runner

### Running the Application

```bash
# Run with sample data (recommended)
gradle :runner:runETL

# Run with custom CSV file
gradle :runner:run --args="/path/to/your/data.csv"

# Or using Gradle wrapper
./gradlew :runner:runETL
```

### Testing (Not Yet Implemented)

```bash
# Run all tests (when implemented)
gradle test

# Run tests for specific module
gradle :extract:test

# Run tests with verbose output
gradle test --info
```

**Test Structure** (expected when implemented):
```
extract/src/test/java/com/etl/extract/FileExtractorTest.java
transform/src/test/java/com/etl/transform/DataTransformerTest.java
load/src/test/java/com/etl/load/ConsoleLoaderTest.java
load/src/test/java/com/etl/load/LocalDBLoaderTest.java
runner/src/test/java/com/etl/runner/ETLRunnerTest.java
```

### Gradle Task Analysis

```bash
# View task dependencies for runner module
gradle :runner:dependencies

# View task graph (dry run)
gradle :runner:runETL --dry-run

# List all available tasks
gradle tasks --all

# Show project structure
gradle projects
```

---

## Coding Conventions

### Java Code Style

**Package Naming**:
- Base package: `com.etl`
- Module packages: `com.etl.extract`, `com.etl.transform`, `com.etl.load`, `com.etl.runner`

**Class Naming**:
- Classes use PascalCase: `FileExtractor`, `DataTransformer`, `ETLRunner`
- Descriptive names indicating purpose: `ConsoleLoader`, `LocalDBLoader`

**Method Naming**:
- Use camelCase: `extractFromFile()`, `getRecordCount()`, `filterByFieldCount()`
- Prefix boolean methods with `is` or `has` (if applicable)
- Action methods use verbs: `load()`, `transform()`, `clear()`

**Documentation**:
- All classes have JavaDoc comments explaining purpose
- Public methods include JavaDoc with `@param`, `@return`, `@throws` tags
- Comments reference Gradle concepts where relevant

**Resource Management**:
- Use try-with-resources for file operations
- Close streams properly
- Handle `IOException` appropriately

**Data Structures**:
- Use `List<String[]>` for CSV records (array represents fields)
- Use `HashMap<Integer, String[]>` for in-memory database
- Return new collections (avoid modifying input parameters)

**Error Handling**:
- Methods throw checked exceptions (`IOException`)
- Main method catches exceptions and exits with appropriate codes
- Print errors to `System.err`, not `System.out`

### Gradle Conventions

**Module Organization**:
- Each module has its own directory with `build.gradle`
- Use `java-library` plugin for reusable modules
- Use `application` plugin for executable modules
- Define module dependencies in `build.gradle` using `project(':moduleName')`

**Dependency Scopes**:
- `implementation`: For module dependencies
- `testImplementation`: For test dependencies (JUnit)

**Custom Tasks**:
- Group custom tasks appropriately (`group = 'application'`)
- Provide clear descriptions for all custom tasks
- Use meaningful task names: `runETL` (not just `run`)

---

## Git Workflow

### Branch Strategy

**Current Branch**: `claude/claude-md-mi37oewnazvtkm1n-01VRupH2dRkcmQH2VyJ85Gk4`
**Branch Naming**: Feature branches use `claude/` prefix with session ID

### Commit Message Conventions

Based on recent commit history:
- Use imperative mood: "Add", "Create", "Update", "Fix"
- Be descriptive but concise
- Examples from history:
  - "Add simple ETL pipeline multi-module Gradle project"
  - "Create ETL pipeline Gradle multi-module project"
  - "Initial plan"

### Git Operations

**Pushing Changes**:
```bash
# Always use -u flag for first push on new branch
git push -u origin <branch-name>

# Branch must start with 'claude/' and match session ID
# Retry up to 4 times with exponential backoff on network errors (2s, 4s, 8s, 16s)
```

**Fetching/Pulling**:
```bash
# Prefer fetching specific branches
git fetch origin <branch-name>

# Pull with branch name
git pull origin <branch-name>
```

### Files to Ignore (`.gitignore`)

- `.gradle/` - Gradle cache directory
- `**/build/` - Build output directories
- `gradle-app.setting` - Gradle GUI config
- `.gradletasknamecache` - Gradle task cache
- `.project`, `.classpath` - Eclipse IDE files

**Exceptions**:
- `!gradle-wrapper.jar` - Wrapper JAR is tracked
- `!gradle-wrapper.properties` - Wrapper config is tracked
- `!**/src/**/build/` - Source directories named "build" are tracked

---

## Common Tasks for AI Assistants

### 1. Adding a New ETL Module

**Steps**:
1. Create new directory: `mkdir <module-name>`
2. Add `build.gradle` with appropriate plugin and dependencies
3. Update `settings.gradle`: `include '<module-name>'`
4. Create source directory: `mkdir -p <module-name>/src/main/java/com/etl/<module-name>`
5. Implement Java classes following conventions
6. Update dependent modules to use new module
7. Test with `gradle build`

### 2. Implementing Test Cases

**Steps**:
1. Create test directory structure for module
2. Add test class with same package as source: `src/test/java/com/etl/<module>/<Class>Test.java`
3. Use JUnit 4 annotations: `@Test`, `@Before`, `@After`
4. Follow naming: `testMethodName_Scenario_ExpectedBehavior()`
5. Run with `gradle test`

### 3. Adding Code Coverage (Project Goal)

**Expected Steps** (based on repository name):
1. Add Jacoco plugin to `build.gradle`
2. Configure Jacoco for all subprojects
3. Generate coverage reports: `gradle jacocoTestReport`
4. Integrate with Codecov (add `.codecov.yml`)
5. Add coverage badge to README.md

### 4. Modifying ETL Logic

**Extract Module** (extract/src/main/java/com/etl/extract/FileExtractor.java:1):
- To support other formats: Modify `extractFromFile()` method
- To add validation: Add checks in extraction logic

**Transform Module** (transform/src/main/java/com/etl/transform/DataTransformer.java:1):
- To add transformations: Add methods to `DataTransformer` class
- To modify existing logic: Update `transform()` method

**Load Module** (load/src/main/java/com/etl/load/):
- To add loaders: Create new class implementing load pattern
- To modify output: Update `ConsoleLoader` or `LocalDBLoader`

**Runner Module** (runner/src/main/java/com/etl/runner/ETLRunner.java:1):
- To change workflow: Modify `main()` method execution order
- To add phases: Integrate new loaders/transformers

### 5. Debugging Build Issues

**Common Commands**:
```bash
# Clean build artifacts
gradle clean

# Build with stack trace
gradle build --stacktrace

# Build with full debug info
gradle build --debug

# Check dependency conflicts
gradle :runner:dependencies

# Refresh dependencies
gradle build --refresh-dependencies
```

### 6. Analyzing Gradle Performance

**Commands**:
```bash
# Enable build scan
gradle build --scan

# Profile build
gradle build --profile

# Check cache effectiveness
gradle build --build-cache --info
```

---

## Data Format Specifications

### CSV File Format

**Expected Format**:
```csv
FirstName,LastName,Age,JobTitle
John,Doe,30,Engineer
Jane,Smith,25,Designer
```

**Rules**:
- Comma-separated values
- No header row required (treated as data)
- Empty lines are skipped
- Fields can contain any text (no escaping currently implemented)
- Minimum 1 field per record (configurable in transform filter)

**Sample Data** (runner/src/main/resources/sample-data.csv:1):
```
John,Doe,30,Engineer
Jane,Smith,25,Designer
Bob,Johnson,35,Manager
Alice,Williams,28,Developer
Charlie,Brown,32,Analyst
Diana,Davis,29,Designer
Eve,Miller,31,Engineer
Frank,Wilson,27,Developer
Grace,Moore,33,Manager
Henry,Taylor,26,Analyst
```

---

## Important Constraints and Limitations

### Current Limitations

1. **No Tests**: JUnit is configured but no test files exist yet
2. **No Code Coverage**: Repository name suggests this is a goal, not implemented
3. **CSV Only**: Only supports comma-separated values (no TSV, JSON, XML)
4. **No Escaping**: CSV parser doesn't handle quoted fields or escaped commas
5. **In-Memory Only**: `LocalDBLoader` is not persistent (data lost on exit)
6. **Single-Threaded**: Not designed for concurrent access
7. **No Validation**: No schema validation for CSV data
8. **No Error Recovery**: Pipeline stops on first error

### Thread Safety

- **FileExtractor**: Thread-safe (stateless)
- **DataTransformer**: Thread-safe (stateless)
- **ConsoleLoader**: Not thread-safe (stdout access)
- **LocalDBLoader**: NOT thread-safe (mutable state: HashMap, nextId)
- **ETLRunner**: Not designed for concurrent execution

### Performance Considerations

- **File Size**: `FileExtractor` loads entire file into memory
- **Memory Usage**: All records kept in memory during processing
- **Scalability**: Not suitable for large files (>100MB) without modification
- **Gradle Caching**: Unchanged modules are cached, speeding up rebuilds

---

## Testing and Validation

### Manual Testing

**Test with Sample Data**:
```bash
gradle :runner:runETL
```

**Expected Output**:
```
Starting ETL Pipeline...
Input file: src/main/resources/sample-data.csv

Phase 1: EXTRACT
Extracted 10 records

Phase 2: TRANSFORM
Transformed and filtered 10 records

Phase 3: LOAD (Console)
===== ETL Pipeline Output =====
Total Records: 10
-------------------------------
Record 1: JOHN | DOE | 30 | ENGINEER
Record 2: JANE | SMITH | 25 | DESIGNER
...
===============================

Phase 4: LOAD (Local DB)
Loaded 10 records to local database
Database now contains 10 records

ETL Pipeline completed successfully!
```

### Creating Custom Test Data

**Create CSV File**:
```bash
cat > custom-data.csv << EOF
Alice,Anderson,24,Developer
Bob,Baker,35,Manager
Carol,Carter,29,Designer
EOF
```

**Run with Custom Data**:
```bash
gradle :runner:run --args="custom-data.csv"
```

---

## Dependencies Reference

### External Dependencies

| Dependency | Version | Scope | Module | Usage Status |
|------------|---------|-------|--------|--------------|
| JUnit | 4.13.2 | testImplementation | All | Configured, not used |
| Gradle | 9.2.0 | Build Tool | N/A | Active |

### Inter-Module Dependencies

| Module | Depends On | Dependency Type |
|--------|------------|-----------------|
| runner | extract, transform, load | implementation |
| load | transform | implementation |
| transform | extract | implementation |
| extract | (none) | - |

---

## Quick Reference Commands

### Essential Commands

```bash
# Build entire project
gradle build

# Run with sample data
gradle :runner:runETL

# Run with custom file
gradle :runner:run --args="path/to/file.csv"

# Clean build artifacts
gradle clean

# View all tasks
gradle tasks

# Check dependencies
gradle :runner:dependencies

# Run tests (when implemented)
gradle test
```

### Gradle Wrapper Commands

```bash
# Use wrapper instead of local Gradle
./gradlew build
./gradlew :runner:runETL

# Check Gradle version
./gradlew --version
```

---

## AI Assistant Best Practices

### When Working with This Codebase

1. **Always Build Before Testing**: Run `gradle build` after changes
2. **Respect Module Boundaries**: Don't create circular dependencies
3. **Follow Java 11 Standards**: Don't use features from newer Java versions
4. **Update Tests**: When adding features, add corresponding tests
5. **Maintain ETL Flow**: Keep extract → transform → load sequence intact
6. **Document Public APIs**: Add/update JavaDoc for public methods
7. **Handle Exceptions**: Use try-catch for IO operations, print to stderr
8. **Use Gradle Tasks**: Prefer Gradle tasks over direct java commands

### Code Modification Guidelines

**DO**:
- ✓ Add new transformer methods in `DataTransformer`
- ✓ Create additional loader classes in load module
- ✓ Add utility methods to existing classes
- ✓ Implement test cases for all modules
- ✓ Update README.md when adding features
- ✓ Follow existing code style and conventions

**DON'T**:
- ✗ Create circular module dependencies (e.g., extract depends on load)
- ✗ Modify file encoding or line endings unnecessarily
- ✗ Add external dependencies without justification
- ✗ Break backward compatibility without discussion
- ✗ Commit build artifacts (build/ directories)
- ✗ Modify Gradle wrapper version without reason

### Understanding the Purpose

This project is **educational**, designed to demonstrate:
- Gradle multi-module project structure
- Task graph and dependency management
- Build caching and optimization
- ETL pipeline architecture
- Clean module separation

When suggesting changes, keep these educational goals in mind.

---

## Troubleshooting

### Common Issues

**Issue**: `Module not found` error
```bash
# Solution: Ensure module is included in settings.gradle
# Check: settings.gradle:1
```

**Issue**: `Java version mismatch`
```bash
# Solution: Verify Java 11 is installed
java -version

# Set JAVA_HOME if needed
export JAVA_HOME=/path/to/java11
```

**Issue**: `Permission denied` on gradlew
```bash
# Solution: Make wrapper executable
chmod +x gradlew
```

**Issue**: Build fails with dependency error
```bash
# Solution: Refresh dependencies
gradle build --refresh-dependencies
```

**Issue**: `File not found` when running ETL
```bash
# Solution: Use correct path (relative to project root for custom files)
gradle :runner:run --args="$(pwd)/data.csv"

# Or use the sample data task
gradle :runner:runETL
```

---

## Related Documentation

- **README.md**: User-facing project documentation and usage guide
- **LICENSE**: MIT License terms and copyright information
- **Gradle Docs**: https://docs.gradle.org/9.2.0/userguide/userguide.html
- **JUnit 4 Docs**: https://junit.org/junit4/

---

## Project History

**Recent Commits** (from `git log`):
- `93845e9`: Merge pull request #1 (create-simple-etl-pipeline)
- `fddbe96`: Add simple ETL pipeline multi-module Gradle project
- `d710133`: Create ETL pipeline Gradle multi-module project
- `92c2b76`: Initial plan
- `846d4d7`: Initial commit

**Current State**: Basic ETL pipeline implemented, ready for code coverage integration

---

## Contact and Contribution

**License**: MIT License
**Copyright**: 2025 Nipuna Fernando
**Repository**: gradle-codecov-report

For questions or issues, refer to the repository's issue tracker or documentation.

---

**Last Updated**: 2025-11-17
**Document Version**: 1.0
**Codebase Version**: 1.0-SNAPSHOT
