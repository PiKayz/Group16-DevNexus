package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

// PMD TooManyMethods: This entry point coordinates report groups and database lifecycle.
// PMD ShortClassName: "Main" is the conventional entry-point name referenced by pom.xml and the tests.
@SuppressWarnings({"PMD.TooManyMethods", "PMD.ShortClassName"})
public class Main
{
    private Connection con;
    private CityReports cityReports;
    private CapitalReports capitalReports;
    private PopulationBreakdowns breakdowns;

    private final CountryReports countryReports = new CountryReports();
    private final PopulationTotals populationTotals = new PopulationTotals();
    private final LanguageReports languageReports = new LanguageReports();

    /** Creates the application; the database connection is opened by {@link #connect()}. */
    public Main()
    {
        // Report helpers are created lazily once the connection is open.
    }

    public boolean connect()
    {
        final String baseUrl = getSetting(
                "DB_URL",
                "jdbc:mysql://localhost:33060/world"
        );
        final String url = baseUrl + (baseUrl.contains("?") ? "&" : "?")
                + "allowPublicKeyRetrieval=true&useSSL=false"
                + "&connectTimeout=5000&socketTimeout=30000";

        final String user = getSetting("DB_USER", "devnexus");
        final String password = System.getenv("DB_PASSWORD");
        boolean connected = false;

        if (password == null || password.isBlank())
        {
            System.err.println("DB_PASSWORD must be set");
        }
        else
        {
            connected = connectWithRetries(url, user, password);
        }

        return connected;
    }

    private boolean connectWithRetries(
            final String url, final String user, final String password)
    {
        final int retries = 30;
        boolean connected = false;
        boolean interrupted = false;

        for (int attempt = 1; attempt <= retries && !connected && !interrupted; attempt++)
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
                breakdowns = new PopulationBreakdowns(con);
                connected = true;
            }
            catch (SQLException e)
            {
                System.err.println(
                        "Connection failed: " + e.getMessage()
                );

                if (attempt < retries)
                {
                    interrupted = !pauseBeforeRetry();
                }
            }
        }

        return connected;
    }

    /** Waits before the next connection attempt; returns false if interrupted. */
    private boolean pauseBeforeRetry()
    {
        boolean resumed = true;

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
            resumed = false;
        }

        return resumed;
    }

    private static String getSetting(
            final String name, final String fallback)
    {
        return getSetting(System.getenv(), name, fallback);
    }

    /* default */ static String getSetting(
            final Map<String, String> settings,
            final String name,
            final String fallback)
    {
        final String value = settings.get(name);
        return value == null || value.isBlank()
                ? fallback
                : value;
    }

    public void checkWorldDatabase() throws SQLException
    {
        if (con == null)
        {
            throw new SQLException(
                    "Not connected to the database"
            );
        }

        final String sql = "SELECT "
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

            final int countries = result.getInt("country_count");
            final int cities = result.getInt("city_count");
            final int languages = result.getInt("language_count");

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

    public void runPopulationBreakdowns()
            throws SQLException
    {
        // Reports 23–25
        breakdowns.report23();
        breakdowns.report24();
        breakdowns.report25();
    }

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

    /* default */ int run()
    {
        int exitCode = 1;

        if (connect())
        {
            exitCode = runAllReports();
        }
        else
        {
            System.err.println(
                    "Could not connect to World database"
            );
        }

        return exitCode;
    }

    private int runAllReports()
    {
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

    public static void main(String[] args)
    {
        System.exit(new Main().run());
    }
}