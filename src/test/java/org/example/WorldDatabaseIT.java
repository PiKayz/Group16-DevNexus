package org.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class WorldDatabaseIT
{
    private static Connection connection;

    @BeforeAll
    static void connectToWorldDatabase() throws Exception
    {
        String url = System.getenv().getOrDefault(
                "DB_URL", "jdbc:mysql://localhost:33060/world"
        );
        url += (url.contains("?") ? "&" : "?")
                + "allowPublicKeyRetrieval=true&useSSL=false"
                + "&connectTimeout=5000&socketTimeout=30000";

        connection = DriverManager.getConnection(
                url, requiredSetting("DB_USER"), requiredSetting("DB_PASSWORD")
        );
    }

    private static String requiredSetting(String name)
    {
        String value = System.getenv(name);
        if (value == null || value.isBlank())
        {
            throw new IllegalStateException(name + " must be set for integration tests");
        }
        return value;
    }

    @AfterAll
    static void disconnect() throws Exception
    {
        if (connection != null)
        {
            connection.close();
        }
    }

    @Test
    void worldDatabaseContainsAllRequiredTables() throws Exception
    {
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(
                     "SELECT "
                             + "(SELECT COUNT(*) FROM country) AS countries, "
                             + "(SELECT COUNT(*) FROM city) AS cities, "
                             + "(SELECT COUNT(*) FROM countrylanguage) AS languages"
             ))
        {
            assertTrue(result.next());
            assertTrue(result.getInt("countries") > 0);
            assertTrue(result.getInt("cities") > 0);
            assertTrue(result.getInt("languages") > 0);
        }
    }

    @Test
    void knownCityIsLoadedFromWorldSql() throws Exception
    {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT Population FROM city WHERE Name = ?"
        ))
        {
            statement.setString(1, "Rangoon (Yangon)");

            try (ResultSet result = statement.executeQuery())
            {
                assertTrue(result.next());
                assertEquals(3_361_700, result.getInt("Population"));
            }
        }
    }

    @Test
    void populationReportReadsActualCityData() throws Exception
    {
        String output = captureOutput(() ->
                new PopulationTotals().printPopulationByCity(
                        connection, "Rangoon (Yangon)"
                )
        );

        assertTrue(output.contains("Report 31:"));
        assertTrue(output.contains(
                "City Population (Rangoon (Yangon)): 3361700"
        ));
    }

    @Test
    void countryReportReadsActualRegionData() throws Exception
    {
        String output = captureOutput(() ->
                new CountryReports().printAllCountriesInRegion(
                        connection, "Southeast Asia"
                )
        );

        assertTrue(output.contains("Report 3:"));
        assertTrue(output.contains("Myanmar"));
        assertTrue(output.contains("Southeast Asia"));
    }

    private static String captureOutput(SqlAction action) throws Exception
    {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        try (PrintStream capture = new PrintStream(
                bytes, true, StandardCharsets.UTF_8
        ))
        {
            System.setOut(capture);
            action.run();
        }
        finally
        {
            System.setOut(original);
        }

        return bytes.toString(StandardCharsets.UTF_8);
    }

    @FunctionalInterface
    private interface SqlAction
    {
        void run() throws Exception;
    }
}
