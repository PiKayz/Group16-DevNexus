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
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

class PopulationTotalsTest
{
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private PopulationTotals reports;

    private PrintStream originalOut;
    private PrintStream capturedPrintStream;
    private ByteArrayOutputStream capturedOutput;

    @BeforeEach
    void setUp() throws SQLException
    {
        connection = mock(Connection.class);
        statement = mock(Statement.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        reports = new PopulationTotals();

        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        originalOut = System.out;
        capturedOutput = new ByteArrayOutputStream();
        capturedPrintStream = new PrintStream(
                capturedOutput, true, StandardCharsets.UTF_8
        );
        System.setOut(capturedPrintStream);
    }

    @AfterEach
    void tearDown()
    {
        System.setOut(originalOut);
        capturedPrintStream.close();
    }

    @Test
    void report26ShowsWorldPopulation() throws SQLException
    {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("total")).thenReturn(6_000_000_000L);

        reports.printWorldPopulation(connection);

        assertTrue(output().contains("Report 26: World Population"));
        assertTrue(output().contains("World Population: 6000000000"));
        verify(statement).executeQuery(
                "SELECT SUM(Population) AS total FROM country"
        );
        verify(resultSet).close();
        verify(statement).close();
        verify(connection, never()).close();
    }

    @Test
    void report26ShowsNoDataWhenSumIsNull() throws SQLException
    {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.wasNull()).thenReturn(true);

        reports.printWorldPopulation(connection);

        assertTrue(output().contains("No population data found."));
    }

    @Test
    void report27ListsContinents() throws SQLException
    {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("name")).thenReturn("Asia", "Europe");
        when(resultSet.getLong("total")).thenReturn(
                3_700_000_000L, 730_000_000L
        );

        reports.printPopulationByContinent(connection);

        assertTrue(output().contains("Report 27: Population by Continent"));
        assertTrue(output().contains("Asia"));
        assertTrue(output().contains("3700000000"));
        assertTrue(output().contains("Europe"));
        assertTrue(output().contains("730000000"));
        verify(statement).executeQuery(contains("GROUP BY Continent"));
        verify(resultSet).close();
    }

    @Test
    void report27FiltersSelectedContinent() throws SQLException
    {
        checkSelectedArea(
                () -> reports.printPopulationByContinent(connection, " Asia "),
                "Continent", "Asia", "FROM country WHERE Continent = ?"
        );
    }

    @Test
    void report28FiltersSelectedRegion() throws SQLException
    {
        checkSelectedArea(
                () -> reports.printPopulationByRegion(connection, " Southeast Asia "),
                "Region", "Southeast Asia",
                "FROM country WHERE Region = ?"
        );
    }

    @Test
    void report29FiltersSelectedCountry() throws SQLException
    {
        checkSelectedArea(
                () -> reports.printPopulationByCountry(connection, " Myanmar "),
                "Country", "Myanmar", "FROM country WHERE Name = ?"
        );
    }

    @Test
    void report30SumsDistrictCityPopulation() throws SQLException
    {
        checkSelectedArea(
                () -> reports.printPopulationByDistrict(connection, " California "),
                "District", "California", "FROM city WHERE District = ?"
        );
    }

    @Test
    void report31FiltersSelectedCity() throws SQLException
    {
        checkSelectedArea(
                () -> reports.printPopulationByCity(connection, " Yangon "),
                "City", "Yangon", "FROM city WHERE Name = ?"
        );
    }

    @Test
    void missingCityShowsNoData() throws SQLException
    {
        when(resultSet.next()).thenReturn(false);

        reports.printPopulationByCity(connection, "Unknown City");

        assertTrue(output().contains("No data found for City: Unknown City"));
        verify(preparedStatement).setString(1, "Unknown City");
        verify(resultSet).close();
        verify(preparedStatement).close();
    }

    @Test
    void duplicateCityNamesShowWarning() throws SQLException
    {
        when(resultSet.next()).thenReturn(true, true);
        when(resultSet.getLong("total")).thenReturn(100L);

        reports.printPopulationByCity(connection, "Springfield");

        assertTrue(output().contains(
                "Multiple matches found for City: Springfield"
        ));
    }

    @Test
    void emptyRegionIsRejectedBeforeQuery() throws SQLException
    {
        assertThrows(
                IllegalArgumentException.class,
                () -> reports.printPopulationByRegion(connection, "  ")
        );

        verify(connection, never()).prepareStatement(anyString());
    }

    private void checkSelectedArea(
            ReportCall call, String label, String name, String sqlPart
    ) throws SQLException
    {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("total")).thenReturn(12345L);

        call.run();

        assertTrue(output().contains(
                label + " Population (" + name + "): 12345"
        ));
        verify(connection).prepareStatement(contains(sqlPart));
        verify(preparedStatement).setString(1, name);
        verify(resultSet).close();
        verify(preparedStatement).close();
        verify(connection, never()).close();
    }

    private String output()
    {
        capturedPrintStream.flush();
        return capturedOutput.toString(StandardCharsets.UTF_8);
    }

    @FunctionalInterface
    private interface ReportCall
    {
        void run() throws SQLException;
    }
}