package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implemented city reports:
 * Requirements 7-9: All cities.
 * Requirements 12-14: Top N cities.
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

    public void cityReports()
    {
        System.out.println();
        System.out.println("==============================");
        System.out.println("        CITY REPORTS");
        System.out.println("==============================");
    }

    /**
     * Requirement 7: All cities in the world.
     */
    public void allCitiesInWorld() throws SQLException
    {
        System.out.println("\nAll cities in the world");
        runReport(allCitiesQuery(""));
    }

    /**
     * Requirement 8: All cities in a continent.
     */
    public void allCitiesInContinent(String continent)
            throws SQLException
    {
        requireValue(continent, "Continent");

        System.out.println(
                "\nAll cities in continent: " + continent
        );

        runReport(
                allCitiesQuery("WHERE country.Continent = ? "),
                continent.trim()
        );
    }

    /**
     * Requirement 9: All cities in a region.
     */
    public void allCitiesInRegion(String region)
            throws SQLException
    {
        requireValue(region, "Region");

        System.out.println(
                "\nAll cities in region: " + region
        );

        runReport(
                allCitiesQuery("WHERE country.Region = ? "),
                region.trim()
        );
    }

    private String allCitiesQuery(String filter)
    {
        return "SELECT city.Name AS City, "
                + "country.Name AS Country, "
                + "city.District, city.Population "
                + "FROM city "
                + "JOIN country ON city.CountryCode = country.Code "
                + filter
                + "ORDER BY city.Population DESC, city.Name ASC";
    }

    private void requireValue(String value, String label)
    {
        if (value == null || value.trim().isEmpty())
        {
            throw new IllegalArgumentException(
                    label + " cannot be empty"
            );
        }
    }

    private void runReport(String sql, Object... parameters)
            throws SQLException
    {
        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            for (int i = 0; i < parameters.length; i++)
            {
                stmt.setObject(i + 1, parameters[i]);
            }

            try (ResultSet result = stmt.executeQuery())
            {
                System.out.printf(
                        "%-30s %-35s %-30s %15s%n",
                        "City",
                        "Country",
                        "District",
                        "Population"
                );

                boolean found = false;

                while (result.next())
                {
                    found = true;

                    System.out.printf(
                            "%-30s %-35s %-30s %15d%n",
                            result.getString("City"),
                            result.getString("Country"),
                            result.getString("District"),
                            result.getLong("Population")
                    );
                }

                if (!found)
                {
                    System.out.println("No cities found.");
                }
            }
        }
    }

    /**
     * Requirement 12: Top N cities in the world.
     */
    public void topNCitiesInWorld(Connection con, int n)
            throws SQLException
    {
        printTopN(
                con,
                "Top " + n + " cities in the world",
                "",
                null,
                n
        );
    }

    /**
     * Requirement 13: Top N cities in a continent.
     */
    public void topNCitiesInContinent(
            Connection con,
            String continent,
            int n
    ) throws SQLException
    {
        printTopN(
                con,
                "Top " + n + " cities in " + continent,
                "WHERE country.Continent = ? ",
                continent,
                n
        );
    }

    /**
     * Requirement 14: Top N cities in a region.
     */
    public void topNCitiesInRegion(
            Connection con,
            String region,
            int n
    ) throws SQLException
    {
        printTopN(
                con,
                "Top " + n + " cities in " + region,
                "WHERE country.Region = ? ",
                region,
                n
        );
    }

    private void printTopN(
            Connection con,
            String title,
            String filter,
            String value,
            int n
    ) throws SQLException
    {
        if (n <= 0)
        {
            throw new IllegalArgumentException(
                    "N must be a positive integer"
            );
        }

        if (!filter.isEmpty())
        {
            requireValue(value, "Filter value");
        }

        String sql =
                "SELECT city.Name, country.Name AS Country, "
                        + "city.District, city.Population "
                        + "FROM city "
                        + "JOIN country ON city.CountryCode = country.Code "
                        + filter
                        + "ORDER BY city.Population DESC, city.Name ASC LIMIT ?";

        System.out.println("\n" + title);

        System.out.printf(
                "%-35s %-52s %-20s %12s%n",
                "Name",
                "Country",
                "District",
                "Population"
        );

        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            int index = 1;

            if (!filter.isEmpty())
            {
                stmt.setString(index++, value.trim());
            }

            stmt.setInt(index, n);

            try (ResultSet result = stmt.executeQuery())
            {
                boolean found = false;

                while (result.next())
                {
                    found = true;

                    System.out.printf(
                            "%-35s %-52s %-20s %,12d%n",
                            result.getString("Name"),
                            result.getString("Country"),
                            result.getString("District"),
                            result.getLong("Population")
                    );
                }

                if (!found)
                {
                    System.out.println("No matching records found");
                }
            }
        }
    }
}