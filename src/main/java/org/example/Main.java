package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main
{
    private Connection con;

    private CityReports cityReports;
    private CapitalReports capitalReports;
    private PopulationBreakdowns populationBreakdowns;

    private final CountryReports countryReports =
            new CountryReports();

    private final PopulationTotals populationTotals =
            new PopulationTotals();

    private final LanguageReports languageReports =
            new LanguageReports();

    /**
     * Connect using Docker environment settings,
     * or localhost when running directly from IntelliJ.
     */
    public boolean connect()
    {
        String url = getSetting(
                "DB_URL",
                "jdbc:mysql://localhost:33060/world"
        );

        url += (url.contains("?") ? "&" : "?")
                + "allowPublicKeyRetrieval=true&useSSL=false"
                + "&connectTimeout=5000&socketTimeout=30000";

        String user = getSetting("DB_USER", "devnexus");
        String password = getSetting(
                "DB_PASSWORD",
                "DevNexusApp2026"
        );

        int retries = 30;

        for (int attempt = 1; attempt <= retries; attempt++)
        {
            System.out.println(
                    "Connecting to database... Attempt " + attempt
            );

            try
            {
                con = DriverManager.getConnection(
                        url,
                        user,
                        password
                );

                System.out.println(
                        "Successfully connected to World database"
                );

                cityReports = new CityReports(con);
                capitalReports = new CapitalReports(con);
                populationBreakdowns = new PopulationBreakdowns(con);

                return true;
            }
            catch (SQLException e)
            {
                System.err.println(
                        "Connection failed: " + e.getMessage()
                );

                if (attempt == retries)
                {
                    break;
                }

                try
                {
                    Thread.sleep(5000);
                }
                catch (InterruptedException interrupted)
                {
                    Thread.currentThread().interrupt();
                    System.err.println("Connection interrupted");
                    return false;
                }
            }
        }

        return false;
    }

    private static String getSetting(
            String name,
            String fallback
    )
    {
        String value = System.getenv(name);

        return value == null || value.isBlank()
                ? fallback
                : value;
    }

    /**
     * Check the required database tables.
     */
    public void checkWorldDatabase() throws SQLException
    {
        if (con == null)
        {
            throw new SQLException(
                    "Not connected to the database"
            );
        }

        String sql =
                "SELECT "
                        + "(SELECT COUNT(*) FROM country) AS country_count, "
                        + "(SELECT COUNT(*) FROM city) AS city_count, "
                        + "(SELECT COUNT(*) FROM countrylanguage) AS language_count";

        try (Statement stmt = con.createStatement();
             ResultSet result = stmt.executeQuery(sql))
        {
            if (!result.next())
            {
                throw new SQLException(
                        "Database check returned no result"
                );
            }

            int countries = result.getInt("country_count");
            int cities = result.getInt("city_count");
            int languages = result.getInt("language_count");

            System.out.println("Countries: " + countries);
            System.out.println("Cities: " + cities);
            System.out.println(
                    "Country-language records: " + languages
            );

            if (countries == 0 || cities == 0 || languages == 0)
            {
                throw new SQLException(
                        "World database contains an empty required table"
                );
            }

            System.out.println("World database check passed");
        }
    }

    /**
     * Run the country reports implemented so far.
     */
    public void runCountryReports() throws SQLException
    {
        countryReports.printAllCountriesInWorld(con);
        countryReports.printAllCountriesInContinent(con, "Asia");
        countryReports.printAllCountriesInRegion(
                con,
                "Southeast Asia"
        );
    }

    /**
     * Run implemented city reports from both members.
     */
    public void runCityReports() throws SQLException
    {
        cityReports.cityReports();

        // Htet Aung Lin: Requirements 7-9
        cityReports.allCitiesInWorld();
        cityReports.allCitiesInContinent("Asia");
        cityReports.allCitiesInRegion("Western Europe");

        // Win Aung: Requirements 12-14
        cityReports.topNCitiesInWorld(con, 5);
        cityReports.topNCitiesInContinent(con, "Asia", 5);
        cityReports.topNCitiesInRegion(
                con,
                "Southeast Asia",
                5
        );
    }

    /**
     * Hein Phyo: Capital reports 17-22.
     */
    public void runCapitalReports()
    {
        capitalReports.report17();
        capitalReports.report18("Asia");
        capitalReports.report19("Southeast Asia");
        capitalReports.report20(10);
        capitalReports.report21("Europe", 5);
        capitalReports.report22("Caribbean", 5);
    }

    /**
     * Han Htal Sein: Population breakdown reports 23-25.
     */
    public void runPopulationBreakdowns() throws SQLException
    {
        populationBreakdowns.report23();
        populationBreakdowns.report24();
        populationBreakdowns.report25();
    }

    /**
     * Close the database connection.
     */
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                con.close();
                con = null;

                System.out.println(
                        "Disconnected from database"
                );
            }
            catch (SQLException e)
            {
                System.err.println(
                        "Error closing database connection: "
                                + e.getMessage()
                );
            }
        }
    }

    public static void main(String[] args)
    {
        Main app = new Main();

        if (!app.connect())
        {
            System.err.println(
                    "Could not connect to World database"
            );

            System.exit(1);
            return;
        }

        int exitCode = 0;

        try
        {
            app.checkWorldDatabase();

            app.runCountryReports();
            app.runCityReports();
            app.runCapitalReports();
            app.runPopulationBreakdowns();
        }
        catch (SQLException e)
        {
            System.err.println(
                    "Database check or report query failed"
            );
            System.err.println(e.getMessage());

            exitCode = 1;
        }
        finally
        {
            app.disconnect();
        }

        System.exit(exitCode);
    }
}