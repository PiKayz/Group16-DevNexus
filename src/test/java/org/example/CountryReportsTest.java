package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

// PMD TooManyMethods: One fixture verifies the country reports and their shared SQL/output behavior.
@SuppressWarnings("PMD.TooManyMethods")
class CountryReportsTest
{
    private Connection connection;
    private PreparedStatement statement;
    private ResultSet rows;
    private CountryReports reports;

    private PrintStream originalOut;
    private PrintStream testOut;
    private ByteArrayOutputStream capturedOutput;

    @BeforeEach
    void setUp() throws SQLException
    {
        connection = mock(Connection.class);
        statement = mock(PreparedStatement.class);
        rows = mock(ResultSet.class);
        reports = new CountryReports();

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

    @Test
    void report1ShowsCountryData() throws SQLException
    {
        oneCountry();

        reports.printAllCountriesInWorld(connection);

        assertTrue(output().contains("Report 1:"));
        assertTrue(output().contains("MMR"));
        assertTrue(output().contains("Myanmar"));
        assertTrue(output().contains("Rangoon"));
        assertTrue(output().contains("50,000,000"));
        assertTrue(output().contains("1 countries"));

        verify(connection).prepareStatement(
                contains("ORDER BY c.Population DESC")
        );
        verify(rows).close();
        verify(statement).close();
        verify(connection, never()).close();
    }

    @Test
    void report2FiltersByContinent() throws SQLException
    {
        oneCountry();

        reports.printAllCountriesInContinent(connection, " Asia ");

        assertTrue(output().contains("Report 2:"));
        verify(connection).prepareStatement(
                contains("WHERE c.Continent = ?")
        );
        verify(statement).setString(1, "Asia");
    }

    @Test
    void report3FiltersByRegion() throws SQLException
    {
        oneCountry();

        reports.printAllCountriesInRegion(
                connection, " Southeast Asia "
        );

        assertTrue(output().contains("Report 3:"));
        verify(connection).prepareStatement(
                contains("WHERE c.Region = ?")
        );
        verify(statement).setString(1, "Southeast Asia");
    }

    @Test
    void report4LimitsWorldResults() throws SQLException
    {
        oneCountry();

        reports.printTopNCountriesInWorld(connection, 5);

        assertTrue(output().contains("Report 4: Top 5"));
        verify(connection).prepareStatement(contains("LIMIT ?"));
        verify(statement).setInt(1, 5);
    }

    @Test
    void report5FiltersContinentAndLimitsResults()
            throws SQLException
    {
        oneCountry();

        reports.printTopNCountriesInContinent(
                connection, " Asia ", 5
        );

        assertTrue(output().contains("Report 5: Top 5"));
        verify(connection).prepareStatement(
                contains("WHERE c.Continent = ?")
        );
        verify(statement).setString(1, "Asia");
        verify(statement).setInt(2, 5);
    }

    @Test
    void report6FiltersRegionAndLimitsResults()
            throws SQLException
    {
        oneCountry();

        reports.printTopNCountriesInRegion(
                connection, " Southeast Asia ", 3
        );

        assertTrue(output().contains("Report 6: Top 3"));
        verify(connection).prepareStatement(
                contains("WHERE c.Region = ?")
        );
        verify(statement).setString(1, "Southeast Asia");
        verify(statement).setInt(2, 3);
    }

    @Test
    void emptyResultShowsNoMatchingRecords() throws SQLException
    {
        when(rows.next()).thenReturn(false);

        reports.printAllCountriesInWorld(connection);

        assertTrue(output().contains("No matching records found"));
        verify(rows).close();
        verify(statement).close();
    }

    @Test
    void invalidTopNDoesNotQueryDatabase() throws SQLException
    {
        reports.printTopNCountriesInWorld(connection, 0);

        assertTrue(output().contains("Report 4: Invalid input"));
        verify(connection, never()).prepareStatement(anyString());
    }

    @Test
    void blankContinentAndRegionShowInputMessages()
            throws SQLException
    {
        reports.printAllCountriesInContinent(connection, " ");
        reports.printAllCountriesInRegion(connection, " ");
        reports.printTopNCountriesInContinent(
                connection, " ", 5
        );
        reports.printTopNCountriesInRegion(
                connection, " ", 5
        );

        assertTrue(output().contains(
                "Report 2: Invalid input - please enter a continent name"
        ));
        assertTrue(output().contains(
                "Report 3: Invalid input - please enter a region name"
        ));
        assertTrue(output().contains(
                "Report 5: Invalid input - please enter a continent name"
        ));
        assertTrue(output().contains(
                "Report 6: Invalid input - please enter a region name"
        ));

        verify(connection, never()).prepareStatement(anyString());
    }

    @Test
    void nullContinentAndRegionShowInputMessages()
            throws SQLException
    {
        reports.printAllCountriesInContinent(connection, null);
        reports.printAllCountriesInRegion(connection, null);
        reports.printTopNCountriesInContinent(
                connection, null, 5
        );
        reports.printTopNCountriesInRegion(
                connection, null, 5
        );

        assertTrue(output().contains(
                "Report 2: Invalid input - please enter a continent name"
        ));
        assertTrue(output().contains(
                "Report 3: Invalid input - please enter a region name"
        ));
        assertTrue(output().contains(
                "Report 5: Invalid input - please enter a continent name"
        ));
        assertTrue(output().contains(
                "Report 6: Invalid input - please enter a region name"
        ));

        verify(connection, never()).prepareStatement(anyString());
    }

    @Test
    void reports5And6RejectNonPositiveN() throws SQLException
    {
        reports.printTopNCountriesInContinent(
                connection, "Asia", 0
        );
        reports.printTopNCountriesInRegion(
                connection, "Southeast Asia", 0
        );

        assertTrue(output().contains(
                "Report 5: Invalid input - N must be greater than 0"
        ));
        assertTrue(output().contains(
                "Report 6: Invalid input - N must be greater than 0"
        ));

        verify(connection, never()).prepareStatement(anyString());
    }

    @Test
    void nullConnectionIsRejected() throws SQLException
    {
        SQLException error = assertThrows(
                SQLException.class,
                () -> reports.printAllCountriesInWorld(null)
        );

        assertEquals(
                "Not connected to the database",
                error.getMessage()
        );
    }

    @Test
    void queryFailurePropagatesAndClosesStatement()
            throws SQLException
    {
        SQLException failure = new SQLException("Query failed");
        when(statement.executeQuery()).thenThrow(failure);

        SQLException actual = assertThrows(
                SQLException.class,
                () -> reports.printAllCountriesInWorld(connection)
        );

        assertSame(failure, actual);
        verify(statement).close();
        verify(connection, never()).close();
    }

    private void oneCountry() throws SQLException
    {
        when(rows.next()).thenReturn(true, false);
        when(rows.getString("Code")).thenReturn("MMR");
        when(rows.getString("Name")).thenReturn("Myanmar");
        when(rows.getString("Continent")).thenReturn("Asia");
        when(rows.getString("Region"))
                .thenReturn("Southeast Asia");
        when(rows.getInt("Population")).thenReturn(50_000_000);
        when(rows.getString("Capital")).thenReturn("Rangoon");
    }

    private String output()
    {
        testOut.flush();
        return capturedOutput.toString(StandardCharsets.UTF_8);
    }
}