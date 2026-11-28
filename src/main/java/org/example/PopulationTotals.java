package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Population totals for requirements 26–31.
 * Assigned developer: Khaine Zaw Hein.
 */
public class PopulationTotals
{
    /** Creates a population totals report generator. */
    public PopulationTotals()
    {
        // Stateless: the connection is passed to each report method.
    }

    /** Report 26: World population. */
    public void printWorldPopulation(final Connection con) throws SQLException
    {
        final String sql = "SELECT SUM(Population) AS total FROM country";

        System.out.println("\nReport 26: World Population");

        try (Statement stmt = con.createStatement();
             ResultSet result = stmt.executeQuery(sql))
        {
            boolean printed = false;

            if (result.next())
            {
                final long total = result.getLong("total");
                if (!result.wasNull())
                {
                    System.out.println("World Population: " + total);
                    printed = true;
                }
            }

            if (!printed)
            {
                System.out.println("No population data found.");
            }
        }
    }

    /** Report 27: List population totals for all continents. */
    public void printPopulationByContinent(final Connection con) throws SQLException
    {
        final String sql = "SELECT Continent AS name, SUM(Population) AS total "
                + "FROM country GROUP BY Continent ORDER BY total DESC";

        System.out.println("\nReport 27: Population by Continent");
        System.out.printf("%-15s | %s%n", "Continent", "Total Population");

        try (Statement stmt = con.createStatement();
             ResultSet result = stmt.executeQuery(sql))
        {
            while (result.next())
            {
                System.out.printf("%-15s | %d%n",
                        result.getString("name"), result.getLong("total"));
            }
        }
    }

    /** Report 27: Population of a selected continent. */
    public void printPopulationByContinent(final Connection con, final String continent)
            throws SQLException
    {
        final String name = requireValue(continent, "Continent");
        final String sql = "SELECT SUM(Population) AS total "
                + "FROM country WHERE Continent = ?";

        System.out.println("\nReport 27: Population by Continent (" + name + ")");
        printTotal(con, sql, "Continent", name);
    }

    /** Report 28: Population of a selected region. */
    public void printPopulationByRegion(final Connection con, final String region)
            throws SQLException
    {
        final String name = requireValue(region, "Region");
        final String sql = "SELECT SUM(Population) AS total "
                + "FROM country WHERE Region = ?";

        System.out.println("\nReport 28: Population by Region (" + name + ")");
        printTotal(con, sql, "Region", name);
    }

    /** Report 29: Population of a selected country. */
    public void printPopulationByCountry(final Connection con, final String countryName)
            throws SQLException
    {
        final String name = requireValue(countryName, "Country");
        final String sql = "SELECT Population AS total FROM country WHERE Name = ?";

        System.out.println("\nReport 29: Population by Country (" + name + ")");
        printTotal(con, sql, "Country", name);
    }

    /** Report 30: Sum of recorded city populations in a district. */
    public void printPopulationByDistrict(final Connection con, final String district)
            throws SQLException
    {
        final String name = requireValue(district, "District");
        final String sql = "SELECT SUM(Population) AS total "
                + "FROM city WHERE District = ?";

        System.out.println("\nReport 30: Population by District (" + name + ")");
        printTotal(con, sql, "District", name);
    }

    /** Report 31: Population of a selected city. */
    public void printPopulationByCity(final Connection con, final String cityName)
            throws SQLException
    {
        final String name = requireValue(cityName, "City");
        final String sql = "SELECT Population AS total FROM city WHERE Name = ?";

        System.out.println("\nReport 31: Population by City (" + name + ")");
        printTotal(con, sql, "City", name);
    }

    private void printTotal(final Connection con, final String sql,
                            final String label, final String name)
            throws SQLException
    {
        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            stmt.setString(1, name);

            try (ResultSet result = stmt.executeQuery())
            {
                System.out.println(describeTotal(result, label, name));
            }
        }
    }

    /**
     * Builds the message for a single-value total query result.
     * Reads at most two rows so that duplicate matches can be detected.
     */
    private String describeTotal(final ResultSet result, final String label,
                                 final String name)
            throws SQLException
    {
        final String message;

        if (result.next())
        {
            final long total = result.getLong("total");

            if (result.wasNull())
            {
                message = "No data found for " + label + ": " + name;
            }
            else if (result.next())
            {
                message = "Multiple matches found for " + label + ": " + name;
            }
            else
            {
                message = label + " Population (" + name + "): " + total;
            }
        }
        else
        {
            message = "No data found for " + label + ": " + name;
        }

        return message;
    }

    private String requireValue(final String value, final String label)
    {
        if (value == null || value.isBlank())
        {
            throw new IllegalArgumentException(label + " cannot be empty");
        }

        return value.trim();
    }
}
