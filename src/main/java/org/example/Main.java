package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Coordinates the World database connection and all report groups.
 * Its methods remain together because they form the application entry point.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class Main
{
    /** Active World database connection. */
    private Connection con;
    /** City reports that use the active connection. */
    private CityReports cityReports;
    /** Capital city reports that use the active connection. */
    private CapitalReports capitalReports;
    /** Population breakdown reports that use the active connection. */
    private PopulationBreakdowns populationBreakdowns;

    /** Country report implementation. */
    private final CountryReports countryReports = new CountryReports();
    /** Population total report implementation. */
    private final PopulationTotals populationTotals = new PopulationTotals();
    /** Language speaker report implementation. */
    private final LanguageReports languageReports = new LanguageReports();

    /** Connects to the World database and prepares connection-bound reports. */
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
        String password = System.getenv("DB_PASSWORD");
        if (password == null || password.isBlank())
        {
            System.err.println("DB_PASSWORD must be set");
            return false;
        }
        int retries = 30;

        for (int attempt = 1; attempt <= retries; attempt++)
        {
            System.out.println(
                    "Connecting to database... Attempt " + attempt
            );

            try
            {
                con = DriverManager.getConnection(
                        url, user, password
                );
                System.out.println(
                        "Successfully connected to World database"
                );

                cityReports = new CityReports(con);
                capitalReports = new CapitalReports(con);
                populationBreakdowns =
                        new PopulationBreakdowns(con);
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
                    System.err.println(
                            "Connection interrupted"
                    );
                    return false;
                }
            }
        }

        return false;
    }

    private static String getSetting(
            String name, String fallback)
    {
        return getSetting(System.getenv(), name, fallback);
    }

    static String getSetting(
            Map<String, String> settings,
            String name,
            String fallback)
    {
        String value = settings.get(name);
        return value == null || value.isBlank()
                ? fallback
                : value;
    }

    /** Checks that all required World database tables contain data. */
    public void checkWorldDatabase() throws SQLException
    {
        if (con == null)
        {
            throw new SQLException(
                    "Not connected to the database"
            );
        }

        String sql = "SELECT "
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

            if (countries == 0 || cities == 0
                    || languages == 0)
            {
                throw new SQLException(
                        "World database contains an empty required table"
                );
            }

            System.out.println(
                    "World database check passed"
            );
        }
    }

    /** Prints country reports 1-6. */
    public void runCountryReports() throws SQLException
    {
        // Reports 1–6
        countryReports.printAllCountriesInWorld(con);
        countryReports.printAllCountriesInContinent(
                con, "Asia"
        );
        countryReports.printAllCountriesInRegion(
                con, "Southeast Asia"
        );

        countryReports.printTopNCountriesInWorld(con, 10);
        countryReports.printTopNCountriesInContinent(
                con, "Asia", 5
        );
        countryReports.printTopNCountriesInRegion(
                con, "Southeast Asia", 5
        );
    }

    /** Prints city reports 7-16. */
    public void runCityReports() throws SQLException
    {
        cityReports.cityReports();

        // Reports 7–11
        cityReports.allCitiesInWorld();
        cityReports.allCitiesInContinent("Asia");
        cityReports.allCitiesInRegion("Western Europe");
        cityReports.allCitiesInCountry("Myanmar");
        cityReports.allCitiesInDistrict("California");

        // Reports 12–16
        cityReports.topNCitiesInWorld(con, 5);
        cityReports.topNCitiesInContinent(
                con, "Asia", 5
        );
        cityReports.topNCitiesInRegion(
                con, "Southeast Asia", 5
        );
        cityReports.topNCitiesInCountry(
                con, "Myanmar", 5
        );
        cityReports.topNCitiesInDistrict(
                con, "Mandalay", 5
        );
    }

    /** Prints capital city reports 17-22. */
    public void runCapitalReports()
    {
        // Reports 17–22
        capitalReports.report17();
        capitalReports.report18("Asia");
        capitalReports.report19("Southeast Asia");
        capitalReports.report20(10);
        capitalReports.report21("Europe", 5);
        capitalReports.report22("Caribbean", 5);
    }

    /** Prints population breakdown reports 23-25. */
    public void runPopulationBreakdowns()
            throws SQLException
    {
        // Reports 23–25
        populationBreakdowns.report23();
        populationBreakdowns.report24();
        populationBreakdowns.report25();
    }

    /** Prints population total reports 26-31. */
    public void runPopulationTotals() throws SQLException
    {
        System.out.println(
                "\n--- Running Population Totals Reports ---"
        );

        // Reports 26–31
        populationTotals.printWorldPopulation(con);
        populationTotals.printPopulationByContinent(
                con, "Asia"
        );
        populationTotals.printPopulationByRegion(
                con, "Western Europe"
        );
        populationTotals.printPopulationByCountry(
                con, "United Kingdom"
        );
        populationTotals.printPopulationByDistrict(
                con, "California"
        );
        populationTotals.printPopulationByCity(
                con, "Rangoon (Yangon)"
        );
    }

    /** Closes the active World database connection, if present. */
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

    int run()
    {
        if (!connect())
        {
            System.err.println(
                    "Could not connect to World database"
            );
            return 1;
        }

        int exitCode = 0;

        try
        {
            checkWorldDatabase();
            runCountryReports();
            runCityReports();
            runCapitalReports();
            runPopulationBreakdowns();
            runPopulationTotals();
            languageReports.printLanguageSpeakerReport(con);
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
            disconnect();
        }

        return exitCode;
    }

    /** Starts the application and exits with the report run's status. */
    public static void main(String[] args)
    {
        System.exit(new Main().run());
    }
}
