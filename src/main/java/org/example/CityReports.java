package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CityReports
{

    public void topNCitiesInWorld(Connection con, int n) throws SQLException
    {
        printTopN(con, "Top " + n + " cities in the world", "", null, n);
    }


    public void topNCitiesInContinent(Connection con, String continent, int n) throws SQLException
    {
        printTopN(con, "Top " + n + " cities in " + continent,
                "WHERE country.Continent = ? ", continent, n);
    }


    public void topNCitiesInRegion(Connection con, String region, int n) throws SQLException
    {
        printTopN(con, "Top " + n + " cities in " + region,
                "WHERE country.Region = ? ", region, n);
    }

    /**
     * Run a Top N query (largest population first) and print the result.
     * filter is an empty string for the world report, otherwise a WHERE clause
     * with one "?" that is set to value.
     */
    private void printTopN(Connection con, String title, String filter, String value, int n)
            throws SQLException
    {
        if (n <= 0)
        {
            throw new IllegalArgumentException("N must be a positive integer");
        }

        if (!filter.isEmpty() && (value == null || value.trim().isEmpty()))
        {
            throw new IllegalArgumentException("A filter value is required");
        }

        String sql =
                "SELECT city.Name, country.Name AS Country, city.District, city.Population "
                        + "FROM city JOIN country ON city.CountryCode = country.Code "
                        + filter
                        + "ORDER BY city.Population DESC, city.Name ASC LIMIT ?";

        System.out.println();
        System.out.println(title);
        System.out.println(String.format("%-35s %-52s %-20s %12s",
                "Name", "Country", "District", "Population"));

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
                    System.out.println(String.format("%-35s %-52s %-20s %,12d",
                            result.getString("Name"),
                            result.getString("Country"),
                            result.getString("District"),
                            result.getInt("Population")));
                }

                if (!found)
                {
                    System.out.println("No matching records found");
                }
            }
        }
    }
}