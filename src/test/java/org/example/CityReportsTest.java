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

// PMD TooManyMethods: One fixture verifies the city reports and their shared SQL/output behavior.
// PMD CheckResultSet: Mockito stubs and assertions already control the next() result.
@SuppressWarnings({"PMD.TooManyMethods", "PMD.CheckResultSet"})
class CityReportsTest
{
    private Connection connection;
    private PreparedStatement statement;
    private ResultSet rows;
    private CityReports reports;

    private PrintStream originalOut;
    private PrintStream testOut;
    private ByteArrayOutputStream capturedOutput;

    @BeforeEach
    void setUp() throws SQLException
    {
        connection = mock(Connection.class);
        statement = mock(PreparedStatement.class);
        rows = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(rows);

        reports = new CityReports(connection);

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
    @ValueSource(ints = {7, 8, 9, 10, 11})
    void allCityReportsShowCityAndApplyCorrectFilter(int number)
            throws SQLException
    {
        oneCity();

        switch (number)
        {
            case 7 -> reports.allCitiesInWorld();
            case 8 -> reports.allCitiesInContinent(" Asia ");
            case 9 -> reports.allCitiesInRegion(" Southeast Asia ");
            case 10 -> reports.allCitiesInCountry(" Myanmar ");
            case 11 -> reports.allCitiesInDistrict(" Rangoon [Yangon] ");
            default -> fail("Unexpected report number");
        }

        assertTrue(output().contains("Report " + number + ":"));
        assertTrue(output().contains("Rangoon (Yangon)"));
        assertTrue(output().contains("Myanmar"));
        assertTrue(output().contains("3361700"));

        verifyAllCityFilter(number);

        verify(rows).close();
        verify(statement).close();
        verify(connection, never()).close();
    }

    @ParameterizedTest
    @ValueSource(ints = {12, 13, 14, 15, 16})
    void topCityReportsUseFilterAndLimit(int number)
            throws SQLException
    {
        oneCity();

        switch (number)
        {
            case 12 -> reports.topNCitiesInWorld(connection, 5);
            case 13 -> reports.topNCitiesInContinent(
                    connection, " Asia ", 5
            );
            case 14 -> reports.topNCitiesInRegion(
                    connection, " Southeast Asia ", 5
            );
            case 15 -> reports.topNCitiesInCountry(
                    connection, " Myanmar ", 5
            );
            case 16 -> reports.topNCitiesInDistrict(
                    connection, " Rangoon [Yangon] ", 5
            );
            default -> fail("Unexpected report number");
        }

        assertTrue(output().contains("Report " + number + ": Top 5"));
        assertTrue(output().contains("Rangoon (Yangon)"));
        assertTrue(output().contains("Myanmar"));
        assertTrue(output().contains("3,361,700"));

        verify(connection).prepareStatement(contains("LIMIT ?"));

        verifyTopCityBindings(number);

        verify(rows).close();
        verify(statement).close();
    }

    private void verifyAllCityFilter(int number) throws SQLException
    {
        switch (number)
        {
            case 7 -> verify(connection).prepareStatement(
                    contains("ORDER BY city.Population DESC")
            );
            case 8 -> {
                verify(connection).prepareStatement(
                        contains("country.Continent = ?")
                );
                verify(statement).setObject(1, "Asia");
            }
            case 9 -> {
                verify(connection).prepareStatement(
                        contains("country.Region = ?")
                );
                verify(statement).setObject(1, "Southeast Asia");
            }
            case 10 -> {
                verify(connection).prepareStatement(
                        contains("country.Name = ?")
                );
                verify(statement).setObject(1, "Myanmar");
            }
            case 11 -> {
                verify(connection).prepareStatement(
                        contains("city.District = ?")
                );
                verify(statement).setObject(1, "Rangoon [Yangon]");
            }
            default -> fail("Unexpected report number");
        }
    }

    private void verifyTopCityBindings(int number) throws SQLException
    {
        if (number == 12)
        {
            verify(statement).setInt(1, 5);
        }
        else
        {
            verify(statement).setInt(2, 5);

            switch (number)
            {
                case 13 -> verify(statement).setString(1, "Asia");
                case 14 -> verify(statement).setString(
                        1, "Southeast Asia"
                );
                case 15 -> verify(statement).setString(1, "Myanmar");
                case 16 -> verify(statement).setString(
                        1, "Rangoon [Yangon]"
                );
                default -> fail("Unexpected report number");
            }
        }
    }

    @Test
    void cityReportsPrintsHeading()
    {
        reports.cityReports();

        assertTrue(output().contains("CITY REPORTS"));
    }

    @Test
    void emptyCityResultShowsMessage() throws SQLException
    {
        when(rows.next()).thenReturn(false);

        reports.allCitiesInWorld();

        assertTrue(output().contains("No cities found."));
    }

    @Test
    void topCityReportShowsMessageWhenNoCitiesMatch()
            throws SQLException
    {
        when(rows.next()).thenReturn(false);

        reports.topNCitiesInWorld(connection, 5);

        assertTrue(output().contains("No matching records found"));
        verify(rows).close();
        verify(statement).close();
    }

    @Test
    void invalidLimitIsRejectedBeforeQuery()
    {
        assertThrows(
                IllegalArgumentException.class,
                () -> reports.topNCitiesInWorld(connection, 0)
        );

        verifyNoInteractions(statement);
    }

    @Test
    void nullAndBlankFiltersAreRejectedBeforeQuery()
            throws SQLException
    {
        assertThrows(
                IllegalArgumentException.class,
                () -> reports.allCitiesInCountry(null)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> reports.topNCitiesInDistrict(connection, " ", 5)
        );

        verify(connection, never()).prepareStatement(anyString());
    }

    @Test
    void nullConnectionIsRejected()
    {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CityReports(null)
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
                () -> reports.allCitiesInWorld()
        );

        assertSame(failure, actual);
        verify(statement).close();
        verify(connection, never()).close();
    }

    private void oneCity() throws SQLException
    {
        when(rows.next()).thenReturn(true, false);
        when(rows.getString("City"))
                .thenReturn("Rangoon (Yangon)");
        when(rows.getString("Name"))
                .thenReturn("Rangoon (Yangon)");
        when(rows.getString("Country")).thenReturn("Myanmar");
        when(rows.getString("District"))
                .thenReturn("Rangoon [Yangon]");
        when(rows.getLong("Population")).thenReturn(3_361_700L);
    }

    private String output()
    {
        testOut.flush();
        return capturedOutput.toString(StandardCharsets.UTF_8);
    }
}