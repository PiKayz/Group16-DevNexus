
package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * City reports for requirements 7-11.
 * Uses the database connection created by Main.
 */
public class CityReports
{
    private final Connection con;

    private static final String HEADER_FORMAT =
            "%-30s %-35s %-30s %15s%n";

    private static final String ROW_FORMAT =
            "%-30s %-35s %-30s %,15d%n";

    private static final String SEPARATOR =
            "---------------------------------------------------------------------------------------------------------------";

    public CityReports(Connection con)
    {
        this.con = con;
    }

    /**
     * Prints the main City Reports heading.
     */
    public void cityReports()
    {
        System.out.println();
        System.out.println("=== City Reports (Requirements 7-11) ===");
    }

    // Requirement 7: Cities in the World
    public void allCitiesInWorld() throws SQLException
    {
        String sql =
                "SELECT ci.Name AS city, co.Name AS country, " +
                        "ci.District AS district, ci.Population AS population " +
                        "FROM city ci JOIN country co ON ci.CountryCode = co.Code " +
                        "ORDER BY ci.Population DESC";

        printReport("Report 7: Cities in the World", sql, null);
    }

    // Requirement 8: Cities in a Continent
    public void allCitiesInContinent(String continent) throws SQLException
    {
        String sql =
                "SELECT ci.Name AS city, co.Name AS country, " +
                        "ci.District AS district, ci.Population AS population " +
                        "FROM city ci JOIN country co ON ci.CountryCode = co.Code " +
                        "WHERE co.Continent = ? ORDER BY ci.Population DESC";

        printReport("Report 8: Cities in " + continent, sql, continent);
    }

    // Requirement 9: Cities in a Region
    public void allCitiesInRegion(String region) throws SQLException
    {
        String sql =
                "SELECT ci.Name AS city, co.Name AS country, " +
                        "ci.District AS district, ci.Population AS population " +
                        "FROM city ci JOIN country co ON ci.CountryCode = co.Code " +
                        "WHERE co.Region = ? ORDER BY ci.Population DESC";

        printReport("Report 9: Cities in " + region, sql, region);
    }

    // Requirement 10: Cities in a Country
    public void allCitiesInCountry(String country) throws SQLException
    {
        String sql =
                "SELECT ci.Name AS city, co.Name AS country, " +
                        "ci.District AS district, ci.Population AS population " +
                        "FROM city ci JOIN country co ON ci.CountryCode = co.Code " +
                        "WHERE co.Name = ? ORDER BY ci.Population DESC";

        printReport("Report 10: Cities in " + country, sql, country);
    }

    // Requirement 11: Cities in a District
    public void allCitiesInDistrict(String district) throws SQLException
    {
        String sql =
                "SELECT ci.Name AS city, co.Name AS country, " +
                        "ci.District AS district, ci.Population AS population " +
                        "FROM city ci JOIN country co ON ci.CountryCode = co.Code " +
                        "WHERE ci.District = ? ORDER BY ci.Population DESC";

        printReport("Report 11: Cities in " + district, sql, district);
    }

    /**
     * Shared method for printing city report results.
     */
    private void printReport(String title, String sql, String filter)
            throws SQLException
    {
        if (con == null || con.isClosed())
        {
            throw new SQLException("Database connection is not available.");
        }

        System.out.println();
        System.out.println("=== " + title + " ===");

        System.out.printf(
                HEADER_FORMAT,
                "City",
                "Country",
                "District",
                "Population"
        );

        System.out.println(SEPARATOR);

        try (PreparedStatement statement = con.prepareStatement(sql))
        {
            if (filter != null)
            {
                statement.setString(1, filter);
            }

            try (ResultSet result = statement.executeQuery())
            {
                boolean found = false;

                while (result.next())
                {
                    found = true;

                    System.out.printf(
                            ROW_FORMAT,
                            result.getString("city"),
                            result.getString("country"),
                            result.getString("district"),
                            result.getLong("population")
                    );
                }

                if (!found)
                {
                    System.out.println("No cities found for this report.");
                }
            }
        }
    }
}
