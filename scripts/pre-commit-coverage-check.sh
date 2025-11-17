#!/bin/bash
# Pre-commit hook to check coverage before committing
# Usage: Copy to .git/hooks/pre-commit and make executable

echo "🔍 Running pre-commit coverage check..."

# Run tests and generate coverage
./gradlew test jacocoTestReport --no-daemon > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo "❌ Tests failed. Please fix failing tests before committing."
    exit 1
fi

# Check each module for 100% coverage
FAILED_MODULES=""
for module in extract transform load runner; do
    if [ -f "${module}/build/reports/jacoco/test/jacocoTestReport.xml" ]; then
        # Extract line coverage from JaCoCo XML
        COVERAGE=$(grep -o 'line-rate="[^"]*"' "${module}/build/reports/jacoco/test/jacocoTestReport.xml" | head -1 | cut -d'"' -f2)
        COVERAGE_PCT=$(echo "$COVERAGE * 100" | bc -l 2>/dev/null || echo "0")
        
        # Round to 2 decimal places
        COVERAGE_PCT=$(printf "%.2f" "$COVERAGE_PCT")
        
        if (( $(echo "$COVERAGE_PCT < 100.00" | bc -l) )); then
            echo "❌ ${module}: ${COVERAGE_PCT}% coverage (expected 100%)"
            FAILED_MODULES="$FAILED_MODULES $module"
        else
            echo "✅ ${module}: ${COVERAGE_PCT}% coverage"
        fi
    fi
done

if [ ! -z "$FAILED_MODULES" ]; then
    echo ""
    echo "❌ Coverage check failed for modules:$FAILED_MODULES"
    echo "Please add tests to achieve 100% coverage before committing."
    echo ""
    echo "💡 To see uncovered lines:"
    for module in $FAILED_MODULES; do
        echo "   open ${module}/build/reports/jacoco/test/html/com.etl.${module}/FileExtractor.java.html"
    done
    exit 1
else
    echo "✅ All modules have 100% coverage!"
    exit 0
fi