package org.example;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Country reports: requirements 1-6.
 * Assigned developer: Htet Aung Shine.
 * Every report shows: Code, Name, Continent, Region, Population, Capital.
 */
public class CountryReports
{
    /** Shared SELECT part. LEFT JOIN keeps countries without a capital. */
    private static final String BASE_SQL =
            "SELECT c.Code, c.Name, c.Continent, c.Region, c.Population, "
                    + "COALESCE(ci.Name, 'N/A') AS Capital "
                    + "FROM country c "
                    + "LEFT JOIN city ci ON c.Capital = ci.ID ";

    /** Shared ORDER BY part. Name breaks ties so the order is stable. */
    private static final String ORDER_SQL = "ORDER BY c.Population DESC, c.Name ";

    /** Where the validation messages of reports 2-5 are printed. */
    private final PrintStream out;

    /**
     * Create a report printer that writes to standard output.
     */
    public CountryReports()
    {
        this.out = System.out;
    }

    /**
     * Requirement 1: print all countries in the world, largest population first.
     */
    public void printAllCountriesInWorld(final Connection con) throws SQLException
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
    public void printAllCountriesInContinent(final Connection con, final String continent) throws SQLException
    {
        if (continent == null || continent.isBlank())
        {
            out.println();
            out.println("Report 2: Invalid input - please enter a continent name");
            return;
        }

        final String name = continent.trim();

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
    public void printAllCountriesInRegion(final Connection con, final String region) throws SQLException
    {
        if (region == null || region.isBlank())
        {
            out.println();
            out.println("Report 3: Invalid input - please enter a region name");
            return;
        }

        final String name = region.trim();

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
     * @param topN number of countries to show, must be greater than 0
     */
    public void printTopNCountriesInWorld(final Connection con, final int topN) throws SQLException
    {
        if (topN <= 0)
        {
            out.println();
            out.println("Report 4: Invalid input - N must be greater than 0");
            return;
        }

        printReport(con,
                "Report 4: Top " + topN + " populated countries in the world"
                        + " (largest to smallest)",
                BASE_SQL + ORDER_SQL + "LIMIT ?",
                null,
                topN);
    }

    /**
     * Requirement 5: print the top N populated countries in one continent.
     *
     * @param topN number of countries to show, must be greater than 0
     */
    public void printTopNCountriesInContinent(final Connection con, final String continent, final int topN)
            throws SQLException
    {
        if (continent == null || continent.isBlank())
        {
            out.println();
            out.println("Report 5: Invalid input - please enter a continent name");
        }
        else if (topN <= 0)
        {
            out.println();
            out.println("Report 5: Invalid input - N must be greater than 0");
        }
        else
        {
            final String name = continent.trim();

            printReport(con,
                    "Report 5: Top " + topN + " populated countries in the continent " + name
                            + " (largest to smallest)",
                    BASE_SQL + "WHERE c.Continent = ? " + ORDER_SQL + "LIMIT ?",
                    name,
                    topN);
        }
    }

    /**
     * Requirement 6: print the top N populated countries in one region.
     *
     * @param topN number of countries to show, must be greater than 0
     */
    public void printTopNCountriesInRegion(Connection con, String region, int topN)
            throws SQLException
    {
        if (region == null || region.isBlank())
        {
            System.out.println();
            System.out.println("Report 6: Invalid input - please enter a region name");
        }
        else if (topN <= 0)
        {
            System.out.println();
            System.out.println("Report 6: Invalid input - N must be greater than 0");
        }
        else
        {
            final String name = region.trim();

            printReport(con,
                    "Report 6: Top " + topN + " populated countries in the region " + name
                            + " (largest to smallest)",
                    BASE_SQL + "WHERE c.Region = ? " + ORDER_SQL + "LIMIT ?",
                    name,
                    topN);
        }
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

        final String format = "%-5s %-45s %-15s %-26s %13s  %s%n";
        int count = 0;

        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            int index = 1;

            if (param != null)
            {
                stmt.setString(index, param);
                index++;
            }

            if (limit != null)
            {
                stmt.setInt(index, limit);
            }

            try (ResultSet resultSet = stmt.executeQuery())
            {
                while (resultSet.next())
                {
                    if (count == 0)
                    {
                        System.out.printf(format, "Code", "Name", "Continent",
                                "Region", "Population", "Capital");
                    }

                    System.out.printf(format,
                            resultSet.getString("Code"),
                            resultSet.getString("Name"),
                            resultSet.getString("Continent"),
                            resultSet.getString("Region"),
                            String.format("%,d", resultSet.getInt("Population")),
                            resultSet.getString("Capital"));
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
