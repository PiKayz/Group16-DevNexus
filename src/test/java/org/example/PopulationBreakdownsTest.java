package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Verifies reports 23-25 with mocked database results.
 * The shared fixture also tests inconsistent source data.
 */
@SuppressWarnings("PMD.TooManyMethods")
class PopulationBreakdownsTest
{
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private PopulationBreakdowns reports;

    private ByteArrayOutputStream capturedOutput;
    private PrintStream originalOutput;
    private PrintStream testOutput;

    @BeforeEach
    void setUp() throws SQLException
    {
        // Mock JDBC interfaces: no real database is used.
        connection = mock(Connection.class);
        statement = mock(Statement.class);
        resultSet = mock(ResultSet.class);

        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);

        reports = new PopulationBreakdowns(connection);

        // Capture the report's console output.
        originalOutput = System.out;
        capturedOutput = new ByteArrayOutputStream();
        testOutput = new PrintStream(
                capturedOutput,
                true,
                StandardCharsets.UTF_8
        );
        System.setOut(testOutput);
    }

    @AfterEach
    void tearDown()
    {
        System.setOut(originalOutput);
        testOutput.close();
    }

    @Test
    void constructorRejectsNullConnection()
    {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new PopulationBreakdowns(null)
        );

        assertEquals(
                "Database connection is required",
                exception.getMessage()
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {23, 24, 25})
    void normalPopulationDisplaysCorrectValues(int reportNumber)
            throws SQLException
    {
        prepareSingleRow("Test Area", 1000L, 400L);

        runReport(reportNumber);

        String output = outputText();

        assertTrue(output.contains("Report " + reportNumber + ":"));
        assertTrue(output.contains(
                "Total population In cities City % Outside cities Outside %"
        ));
        assertTrue(output.contains(
                "Test Area 1000 400 40.00% 600 60.00%"
        ));
        assertTrue(output.contains("Total rows: 1"));
        assertFalse(output.contains("Data inconsistency:"));

        verify(resultSet).close();
        verify(statement).close();

        // The shared connection belongs to Main, not the report.
        verify(connection, never()).close();
    }

    @ParameterizedTest
    @ValueSource(ints = {23, 24, 25})
    void emptyResultDisplaysNoData(int reportNumber)
            throws SQLException
    {
        when(resultSet.next()).thenReturn(false);

        runReport(reportNumber);

        String output = outputText();

        assertTrue(output.contains("No data found"));
        assertTrue(output.contains("Total rows: 0"));

        verify(resultSet).close();
        verify(statement).close();
    }

    @ParameterizedTest
    @ValueSource(ints = {23, 24, 25})
    void zeroPopulationDisplaysZeroPercentages(int reportNumber)
            throws SQLException
    {
        prepareSingleRow("Empty Area", 0L, 0L);

        runReport(reportNumber);

        String output = outputText();

        assertTrue(output.contains(
                "Empty Area 0 0 0.00% 0 0.00%"
        ));
        assertFalse(output.contains("NaN"));
        assertFalse(output.contains("Infinity"));
        assertTrue(output.contains("Total rows: 1"));
    }

    @ParameterizedTest
    @ValueSource(ints = {23, 24, 25})
    void cityPopulationAboveTotalDisplaysWarning(int reportNumber)
            throws SQLException
    {
        prepareSingleRow("Inconsistent Area", 100L, 150L);

        runReport(reportNumber);

        String output = outputText();

        assertTrue(output.contains(
                "Inconsistent Area 100 150 N/A N/A N/A"
        ));
        assertTrue(output.contains(
                "Data inconsistency: recorded city population "
                        + "exceeds the reported total for Inconsistent Area."
        ));
        assertFalse(output.contains("150.00%"));
        assertTrue(output.contains("Total rows: 1"));
    }

    @ParameterizedTest
    @ValueSource(ints = {23, 24, 25})
    void queryFailurePropagatesAndClosesStatement(int reportNumber)
            throws SQLException
    {
        SQLException failure = new SQLException("Query failed");

        when(statement.executeQuery(anyString())).thenThrow(failure);

        SQLException actual = assertThrows(
                SQLException.class,
                () -> runReport(reportNumber)
        );

        assertSame(failure, actual);
        verify(statement).close();
        verify(connection, never()).close();
    }

    private void prepareSingleRow(String name, long total, long city)
            throws SQLException
    {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("area_name")).thenReturn(name);
        when(resultSet.getLong("total_population")).thenReturn(total);
        when(resultSet.getLong("city_population")).thenReturn(city);
    }

    private void runReport(int reportNumber) throws SQLException
    {
        switch (reportNumber)
        {
            case 23 -> reports.report23();
            case 24 -> reports.report24();
            case 25 -> reports.report25();
            default -> throw new IllegalArgumentException(
                    "Unknown report: " + reportNumber
            );
        }
    }

    private String outputText()
    {
        testOutput.flush();

        // Ignore column spacing and operating-system line endings.
        return capturedOutput.toString(StandardCharsets.UTF_8)
                .replaceAll("\\s+", " ")
                .trim();
    }
}