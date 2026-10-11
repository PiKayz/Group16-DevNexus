package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CountryReports
{
    // Shared SELECT part. LEFT JOIN keeps countries without a capital.
    private static final String BASE_SQL =
            "SELECT c.Code, c.Name, c.Continent, c.Region, c.Population, "
                    + "COALESCE(ci.Name, 'N/A') AS Capital "
                    + "FROM country c "
                    + "LEFT JOIN city ci ON c.Capital = ci.ID ";

    /**
     * Requirement 1: print all countries in the world, largest population first.
     *
     * @param con an open connection to the World database
     * @throws SQLException if the query fails
     */
    public void printAllCountriesInWorld(Connection con) throws SQLException
    {
        printReport(con,
                "Report 1: All countries in the world by population (largest to smallest)",
                BASE_SQL + "ORDER BY c.Population DESC, c.Name",
                null);
    }

    /**
     * Requirement 2: print all countries in one continent, largest population first.
     *
     * @param con       an open connection to the World database
     * @param continent the continent name, for example "Asia"
     * @throws SQLException if the query fails
     */
    public void printAllCountriesInContinent(Connection con, String continent) throws SQLException
    {
        if (continent == null || continent.trim().isEmpty())
        {
            System.out.println();
            System.out.println("Report 2: Invalid input - please enter a continent name");
            return;
        }

        String name = continent.trim();

        printReport(con,
                "Report 2: All countries in the continent " + name
                        + " by population (largest to smallest)",
                BASE_SQL + "WHERE c.Continent = ? ORDER BY c.Population DESC, c.Name",
                name);
    }

    /**
     * Run a country report query and print the result as a table.
     *
     * @param param optional single text parameter for the query, or null if none
     */
    private void printReport(Connection con, String title, String sql, String param)
            throws SQLException
    {
        if (con == null)
        {
            throw new SQLException("Not connected to the database");
        }

        System.out.println();
        System.out.println(title);

        String format = "%-5s %-45s %-15s %-26s %13s  %s%n";
        int count = 0;

        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            if (param != null)
            {
                stmt.setString(1, param);
            }

            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    if (count == 0)
                    {
                        System.out.printf(format, "Code", "Name", "Continent",
                                "Region", "Population", "Capital");
                    }

                    System.out.printf(format,
                            rs.getString("Code"),
                            rs.getString("Name"),
                            rs.getString("Continent"),
                            rs.getString("Region"),
                            String.format("%,d", rs.getInt("Population")),
                            rs.getString("Capital"));
                    count++;
                }
            }
        }

        if (count == 0)
        {
            System.out.println("No matching records found");
        }
        else
        {
            System.out.println(count + " countries");
        }
    }
}
