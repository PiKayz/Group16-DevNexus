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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

class CapitalReportsTest
{
    private Connection connection;
    private PreparedStatement statement;
    private ResultSet rows;
    private CapitalReports reports;

    private PrintStream originalOut;
    private PrintStream testOut;
    private ByteArrayOutputStream capturedOutput;

    @BeforeEach
    void setUp() throws SQLException
    {
        connection = mock(Connection.class);
        statement = mock(PreparedStatement.class);
        rows = mock(ResultSet.class);
        reports = new CapitalReports(connection);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(rows);

        originalOut = System.out;
        capturedOutput = new ByteArrayOutputStream();
        testOut = new PrintStream(
                capturedOutput, true, StandardCharsets.UTF_8
        );
        System.setOut(testOut);
    }

    @AfterEach
    void tearDown()
    {
        System.setOut(originalOut);
        testOut.close();
    }

    @ParameterizedTest
    @ValueSource(ints = {17, 18, 19, 20, 21, 22})
    void capitalReportsShowResultsWithCorrectFilters(int number)
            throws SQLException
    {
        when(rows.next()).thenReturn(true, false);
        when(rows.getString("Name")).thenReturn("Naypyidaw");
        when(rows.getString("Country")).thenReturn("Myanmar");
        when(rows.getInt("Population")).thenReturn(925_000);

        switch (number)
        {
            case 17 -> reports.report17();
            case 18 -> reports.report18("Asia");
            case 19 -> reports.report19("Southeast Asia");
            case 20 -> reports.report20(5);
            case 21 -> reports.report21("Asia", 5);
            case 22 -> reports.report22("Southeast Asia", 5);
            default -> fail("Unexpected report number");
        }

        assertTrue(output().contains("Report " + number + ":"));
        assertTrue(output().contains("Naypyidaw"));
        assertTrue(output().contains("Myanmar"));
        assertTrue(output().contains("925000"));

        switch (number)
        {
            case 17 -> verify(connection).prepareStatement(
                    contains("ORDER BY city.Population DESC")
            );
            case 18 -> {
                verify(connection).prepareStatement(
                        contains("country.Continent = ?")
                );
                verify(statement).setObject(1, "Asia");
            }
            case 19 -> {
                verify(connection).prepareStatement(
                        contains("country.Region = ?")
                );
                verify(statement).setObject(1, "Southeast Asia");
            }
            case 20 -> {
                verify(connection).prepareStatement(contains("LIMIT ?"));
                verify(statement).setObject(1, 5);
            }
            case 21 -> {
                verify(connection).prepareStatement(
                        contains("country.Continent = ?")
                );
                verify(statement).setObject(1, "Asia");
                verify(statement).setObject(2, 5);
            }
            case 22 -> {
                verify(connection).prepareStatement(
                        contains("country.Region = ?")
                );
                verify(statement).setObject(1, "Southeast Asia");
                verify(statement).setObject(2, 5);
            }
            default -> fail("Unexpected report number");
        }

        verify(rows).close();
        verify(statement).close();
        verify(connection, never()).close();
    }

    @Test
    void noCapitalMatchesShowsMessage() throws SQLException
    {
        when(rows.next()).thenReturn(false);

        reports.report17();

        assertTrue(output().contains("No matching records found"));
    }

    @Test
    void invalidTopNDoesNotQueryDatabase() throws SQLException
    {
        reports.report20(0);

        assertTrue(output().contains(
                "Invalid input: N must be a positive integer."
        ));
        verify(connection, never()).prepareStatement(anyString());
    }

    @Test
    void blankContinentDoesNotQueryDatabase() throws SQLException
    {
        reports.report18("  ");

        assertTrue(output().contains(
                "Invalid input: Continent must not be empty."
        ));
        verify(connection, never()).prepareStatement(anyString());
    }

    @Test
    void sqlFailureDisplaysErrorAndClosesStatement() throws SQLException
    {
        when(statement.executeQuery())
                .thenThrow(new SQLException("Database unavailable"));

        reports.report17();

        assertTrue(output().contains(
                "Error executing report: Database unavailable"
        ));
        verify(statement).close();
        verify(connection, never()).close();
    }

    private String output()
    {
        testOut.flush();
        return capturedOutput.toString(StandardCharsets.UTF_8);
    }
}