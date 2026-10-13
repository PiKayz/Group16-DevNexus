package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    private Connection con;

    // CityReports requires a Connection in its constructor,
    // so we initialize it after the database connection is established.
    private CityReports cityReports;

    // The other report classes are kept as they were.
    // Note: If they also require a Connection in their constructors,
    // they should be initialized in the connect() method just like CityReports.
    private final CountryReports countryReports = new CountryReports();
    private final CapitalReports capitalReports = new CapitalReports();
    private final PopulationBreakdowns populationBreakdowns = new PopulationBreakdowns();
    private final PopulationTotals populationTotals = new PopulationTotals();
    private final LanguageReports languageReports = new LanguageReports();

    /**
     * Connect to the World database.
     */
    public boolean connect() {
        String url =
                "jdbc:mysql://devnexus-db:3306/world"
                        + "?allowPublicKeyRetrieval=true&useSSL=false"
                        + "&connectTimeout=5000&socketTimeout=30000";
        int retries = 30;
        for (int attempt = 1; attempt <= retries; attempt++) {
            System.out.println(
                    "Connecting to database... Attempt " + attempt
            );
            try {
                con = DriverManager.getConnection(
                        url,
                        "devnexus",
                        "DevNexusApp2026"
                );
                System.out.println("Successfully connected to World database");

                // Initialize CityReports with the active connection
                cityReports = new CityReports(con);

                return true;
            } catch (SQLException e) {
                System.out.println("Connection failed: " + e.getMessage());
                if (attempt == retries) {
                    break;
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    System.out.println("Connection interrupted");
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Check that all three World database tables contain data.
     */
    public void checkWorldDatabase() throws SQLException {
        if (con == null) {
            throw new SQLException("Not connected to the database");
        }
        String sql =
                "SELECT "
                        + "(SELECT COUNT(*) FROM country) AS country_count, "
                        + "(SELECT COUNT(*) FROM city) AS city_count, "
                        + "(SELECT COUNT(*) FROM countrylanguage) AS language_count";
        try (Statement stmt = con.createStatement();
             ResultSet result = stmt.executeQuery(sql)) {
            if (!result.next()) {
                throw new SQLException("Database check returned no result");
            }
            int countries = result.getInt("country_count");
            int cities = result.getInt("city_count");
            int languages = result.getInt("language_count");
            System.out.println("Countries: " + countries);
            System.out.println("Cities: " + cities);
            System.out.println("Country-language records: " + languages);
            if (countries == 0 || cities == 0 || languages == 0) {
                throw new SQLException(
                        "World database contains an empty required table"
                );
            }
            System.out.println("World database check passed");
        }
    }

    /**
     * Executes the City Reports.
     * Calls the methods defined in CityReports.java.
     */
    public void runCityReports() {
        if (cityReports == null) {
            System.err.println("CityReports is not initialized. Ensure connect() was successful.");
            return;
        }
        try {
            System.out.println("\n--- Generating City Reports ---");

            // Requirement 7: All cities in the world
            System.out.println("\n[Report 7] All cities in the world ordered by population:");
            cityReports.allCitiesInWorld();

            // Example for Requirement 8: All cities in a continent (Uncomment to test)
            // System.out.println("\n[Report 8] All cities in Europe:");
            // cityReports.allCitiesInContinent("Europe");

            // Example for Requirement 9: All cities in a region (Uncomment to test)
            // System.out.println("\n[Report 9] All cities in Western Europe:");
            // cityReports.allCitiesInRegion("Western Europe");

        } catch (SQLException e) {
            System.err.println("Error executing city reports: " + e.getMessage());
        }
    }

    /**
     * Close the database connection.
     */
    public void disconnect() {
        if (con != null) {
            try {
                con.close();
                con = null;
                System.out.println("Disconnected from database");
            } catch (SQLException e) {
                System.err.println(
                        "Error closing database connection: " + e.getMessage()
                );
            }
        }
    }

    public static void main(String[] args) {
        Main app = new Main();
        if (!app.connect()) {
            System.err.println("Could not connect to World database");
            System.exit(1);
            return;
        }

        int exitCode = 0;
        try {
            app.checkWorldDatabase();

            // Call the new method to run City Reports
            app.runCityReports();

        } catch (SQLException e) {
            System.err.println("World database check failed");
            System.err.println(e.getMessage());
            exitCode = 1;
        } finally {
            app.disconnect();
        }
        System.exit(exitCode);
    }
}