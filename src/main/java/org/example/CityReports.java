package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * City Reports
 *
 * Requirement 7 - All cities in the world
 * Requirement 8 - All cities in a continent
 * Requirement 9 - All cities in a region
 */
public class CityReports
{
    private final Connection con;

    public CityReports(Connection con)
    {
        if (con == null)
        {
            throw new IllegalArgumentException(
                    "Database connection cannot be null"
            );
        }

        this.con = con;
    }


    // ============================================================
    // CITY REPORTS
    // ============================================================

    /**
     * City Reports
     */
    public void cityReports()
    {
        System.out.println();
        System.out.println("==============================");
        System.out.println("        CITY REPORTS");
        System.out.println("==============================");
    }


    // ============================================================
    // ALL WORLD
    // Requirement 7
    // ============================================================

    /**
     * Requirement 7:
     * All cities in the world ordered by largest population
     * to smallest.
     */
    public void allCitiesInWorld() throws SQLException
    {
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("          ALL WORLD");
        System.out.println("------------------------------");

        String sql =
                "SELECT city.Name AS City, " +
                        "country.Name AS Country, " +
                        "city.District, " +
                        "city.Population " +
                        "FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "ORDER BY city.Population DESC, city.Name ASC";

        runReport(sql);
    }


    // ============================================================
    // ALL CONTINENT
    // Requirement 8
    // ============================================================

    /**
     * Requirement 8:
     * All cities in a continent ordered by largest population
     * to smallest.
     */
    public void allCitiesInContinent(String continent)
            throws SQLException
    {
        if (continent == null || continent.trim().isEmpty())
        {
            throw new IllegalArgumentException(
                    "Continent cannot be empty"
            );
        }

        System.out.println();
        System.out.println("------------------------------");
        System.out.println("       ALL CONTINENT");
        System.out.println("------------------------------");

        String sql =
                "SELECT city.Name AS City, " +
                        "country.Name AS Country, " +
                        "city.District, " +
                        "city.Population " +
                        "FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "WHERE country.Continent = ? " +
                        "ORDER BY city.Population DESC, city.Name ASC";

        runReport(sql, continent);
    }


    // ============================================================
    // ALL REGION
    // Requirement 9
    // ============================================================

    /**
     * Requirement 9:
     * All cities in a region ordered by largest population
     * to smallest.
     */
    public void allCitiesInRegion(String region)
            throws SQLException
    {
        if (region == null || region.trim().isEmpty())
        {
            throw new IllegalArgumentException(
                    "Region cannot be empty"
            );
        }

        System.out.println();
        System.out.println("------------------------------");
        System.out.println("          ALL REGION");
        System.out.println("------------------------------");

        String sql =
                "SELECT city.Name AS City, " +
                        "country.Name AS Country, " +
                        "city.District, " +
                        "city.Population " +
                        "FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "WHERE country.Region = ? " +
                        "ORDER BY city.Population DESC, city.Name ASC";

        runReport(sql, region);
    }


    // ============================================================
    // REPORT OUTPUT
    // ============================================================

    /**
     * Runs the report query and prints the results.
     */
    private void runReport(
            String sql,
            Object... parameters
    ) throws SQLException
    {
        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            for (int i = 0; i < parameters.length; i++)
            {
                stmt.setObject(
                        i + 1,
                        parameters[i]
                );
            }

            try (ResultSet rs = stmt.executeQuery())
            {
                System.out.printf(
                        "%-30s %-35s %-30s %15s%n",
                        "City",
                        "Country",
                        "District",
                        "Population"
                );

                System.out.println(
                        "----------------------------------------------------------------------------------------------------"
                );

                boolean found = false;

                while (rs.next())
                {
                    found = true;

                    System.out.printf(
                            "%-30s %-35s %-30s %15d%n",
                            rs.getString("City"),
                            rs.getString("Country"),
                            rs.getString("District"),
                            rs.getLong("Population")
                    );
                }

                if (!found)
                {
                    System.out.println(
                            "No cities found."
                    );
                }
            }
        }
    }
}
