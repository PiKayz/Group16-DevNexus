package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Prints country reports 1-6 with population ranking and optional filters.
 * Each row includes the country code, name, region, population and capital.
 * Assigned developer: Htet Aung Shine.
 */
public class CountryReports
{
    /** Shared select that retains countries without a recorded capital. */
    private static final String BASE_SQL =
            "SELECT c.Code, c.Name, c.Continent, c.Region, c.Population, "
                    + "COALESCE(ci.Name, 'N/A') AS Capital "
                    + "FROM country c "
                    + "LEFT JOIN city ci ON c.Capital = ci.ID ";

    /** Sorts by population, then country name to break ties. */
    private static final String ORDER_SQL = "ORDER BY c.Population DESC, c.Name ";

    /**
     * Requirement 1: print all countries in the world, largest population first.
     */
    public void printAllCountriesInWorld(Connection con) throws SQLException
    {
        printReport(con,
                "Report 1: All countries in the world by population (largest to smallest)",
                BASE_SQL + ORDER_SQL,
                null,
                null);
    }

    /**
     * Requirement 2: print all countries in one continent, largest population first.
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
                BASE_SQL + "WHERE c.Continent = ? " + ORDER_SQL,
                name,
                null);
    }

    /**
     * Requirement 3: print all countries in one region, largest population first.
     */
    public void printAllCountriesInRegion(Connection con, String region) throws SQLException
    {
        if (region == null || region.trim().isEmpty())
        {
            System.out.println();
            System.out.println("Report 3: Invalid input - please enter a region name");
            return;
        }

        String name = region.trim();

        printReport(con,
                "Report 3: All countries in the region " + name
                        + " by population (largest to smallest)",
                BASE_SQL + "WHERE c.Region = ? " + ORDER_SQL,
                name,
                null);
    }

    /**
     * Requirement 4: print the top N populated countries in the world.
     *
     * @param n number of countries to show, must be greater than 0
     */
    public void printTopNCountriesInWorld(Connection con, int n) throws SQLException
    {
        if (n <= 0)
        {
            System.out.println();
            System.out.println("Report 4: Invalid input - N must be greater than 0");
            return;
        }

        printReport(con,
                "Report 4: Top " + n + " populated countries in the world"
                        + " (largest to smallest)",
                BASE_SQL + ORDER_SQL + "LIMIT ?",
                null,
                n);
    }

    /**
     * Requirement 5: print the top N populated countries in one continent.
     *
     * @param n number of countries to show, must be greater than 0
     */
    public void printTopNCountriesInContinent(Connection con, String continent, int n)
            throws SQLException
    {
        if (continent == null || continent.trim().isEmpty())
        {
            System.out.println();
            System.out.println("Report 5: Invalid input - please enter a continent name");
            return;
        }

        if (n <= 0)
        {
            System.out.println();
            System.out.println("Report 5: Invalid input - N must be greater than 0");
            return;
        }

        String name = continent.trim();

        printReport(con,
                "Report 5: Top " + n + " populated countries in the continent " + name
                        + " (largest to smallest)",
                BASE_SQL + "WHERE c.Continent = ? " + ORDER_SQL + "LIMIT ?",
                name,
                n);
    }

    /**
     * Requirement 6: print the top N populated countries in one region.
     *
     * @param n number of countries to show, must be greater than 0
     */
    public void printTopNCountriesInRegion(Connection con, String region, int n)
            throws SQLException
    {
        if (region == null || region.trim().isEmpty())
        {
            System.out.println();
            System.out.println("Report 6: Invalid input - please enter a region name");
            return;
        }

        if (n <= 0)
        {
            System.out.println();
            System.out.println("Report 6: Invalid input - N must be greater than 0");
            return;
        }

        String name = region.trim();

        printReport(con,
                "Report 6: Top " + n + " populated countries in the region " + name
                        + " (largest to smallest)",
                BASE_SQL + "WHERE c.Region = ? " + ORDER_SQL + "LIMIT ?",
                name,
                n);
    }

    /**
     * Run a country report query and print the result as a table.
     *
     * @param param optional single text parameter for the query, or null if none
     * @param limit optional LIMIT value for the query, or null if none
     */
    private void printReport(Connection con, String title, String sql,
                             String param, Integer limit) throws SQLException
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
            int index = 1;

            if (param != null)
            {
                stmt.setString(index++, param);
            }

            if (limit != null)
            {
                stmt.setInt(index, limit);
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
