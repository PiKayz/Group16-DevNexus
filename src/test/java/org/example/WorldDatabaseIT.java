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
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class WorldDatabaseIT
{
    private static Connection connection;

    @BeforeAll
    static void connectToWorldDatabase() throws SQLException
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
    static void disconnect() throws SQLException
    {
        if (connection != null)
        {
            connection.close();
        }
    }

    @Test
    void worldDatabaseContainsAllRequiredTables() throws SQLException
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
    void knownCityIsLoadedFromWorldSql() throws SQLException
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
    void populationReportReadsActualCityData() throws SQLException
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
    void everyReportMethodReadsWorldDatabase() throws SQLException
    {
        CountryReports country = new CountryReports();
        CityReports city = new CityReports(connection);
        CapitalReports capital = new CapitalReports(connection);
        PopulationBreakdowns breakdowns = new PopulationBreakdowns(connection);
        PopulationTotals totals = new PopulationTotals();
        LanguageReports languages = new LanguageReports();

        String output = captureOutput(() ->
        {
            country.printAllCountriesInWorld(connection);             // 1
            country.printAllCountriesInContinent(connection, "Asia"); // 2
            country.printAllCountriesInRegion(connection, "Southeast Asia"); // 3
            country.printTopNCountriesInWorld(connection, 10);       // 4
            country.printTopNCountriesInContinent(connection, "Asia", 5); // 5
            country.printTopNCountriesInRegion(connection, "Southeast Asia", 5); // 6

            city.allCitiesInWorld();                                   // 7
            city.allCitiesInContinent("Asia");                         // 8
            city.allCitiesInRegion("Western Europe");                  // 9
            city.allCitiesInCountry("Myanmar");                        // 10
            city.allCitiesInDistrict("California");                    // 11
            city.topNCitiesInWorld(connection, 5);                     // 12
            city.topNCitiesInContinent(connection, "Asia", 5);         // 13
            city.topNCitiesInRegion(connection, "Southeast Asia", 5);  // 14
            city.topNCitiesInCountry(connection, "Myanmar", 5);       // 15
            city.topNCitiesInDistrict(connection, "Mandalay", 5);     // 16

            capital.report17();
            capital.report18("Asia");
            capital.report19("Southeast Asia");
            capital.report20(10);
            capital.report21("Europe", 5);
            capital.report22("Caribbean", 5);

            breakdowns.report23();
            breakdowns.report24();
            breakdowns.report25();

            totals.printWorldPopulation(connection);                  // 26
            totals.printPopulationByContinent(connection, "Asia");    // 27
            totals.printPopulationByRegion(connection, "Western Europe"); // 28
            totals.printPopulationByCountry(connection, "United Kingdom"); // 29
            totals.printPopulationByDistrict(connection, "California"); // 30
            totals.printPopulationByCity(connection, "Rangoon (Yangon)"); // 31

            languages.printLanguageSpeakerReport(connection);         // 32
        });

        for (int reportNumber = 1; reportNumber <= 32; reportNumber++)
        {
            String heading = "Report " + reportNumber + ":";
            assertTrue(output.contains(heading),
                    () -> "Missing report output: " + heading);
        }

        assertTrue(output.contains("Myanmar"));
        assertTrue(output.contains("Chinese"));

        // Show the real result rows for all 32 reports in the CI test log.
        System.out.print(output);
    }

    @Test
    void mainConnectsAndDelegatesReportGroups() throws SQLException
    {
        Main application = new Main();
        String output = captureOutput(() ->
        {
            assertTrue(application.connect());
            try
            {
                application.checkWorldDatabase();
                application.runCountryReports();
                application.runCityReports();
                application.runCapitalReports();
                application.runPopulationBreakdowns();
                application.runPopulationTotals();
            }
            finally
            {
                application.disconnect();
            }
        });

        assertTrue(output.contains("World database check passed"));
        for (int reportNumber : new int[] {1, 7, 17, 23, 26, 31})
        {
            assertTrue(output.contains("Report " + reportNumber + ":"));
        }
        assertTrue(output.contains("Disconnected from database"));
    }

    @Test
    void countryReportReadsActualRegionData() throws SQLException
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

    private static String captureOutput(SqlAction action) throws SQLException
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
        void run() throws SQLException;
    }
}
