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
    /** Report 26: World population. */
    public void printWorldPopulation(Connection con) throws SQLException
    {
        String sql = "SELECT SUM(Population) AS total FROM country";

        System.out.println("\nReport 26: World Population");

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            if (rs.next())
            {
                long total = rs.getLong("total");
                if (!rs.wasNull())
                {
                    System.out.println("World Population: " + total);
                    return;
                }
            }

            System.out.println("No population data found.");
        }
    }

    /** Report 27: List population totals for all continents. */
    public void printPopulationByContinent(Connection con) throws SQLException
    {
        String sql = "SELECT Continent AS name, SUM(Population) AS total "
                + "FROM country GROUP BY Continent ORDER BY total DESC";

        System.out.println("\nReport 27: Population by Continent");
        System.out.printf("%-15s | %s%n", "Continent", "Total Population");

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                System.out.printf("%-15s | %d%n",
                        rs.getString("name"), rs.getLong("total"));
            }
        }
    }

    /** Report 27: Population of a selected continent. */
    public void printPopulationByContinent(Connection con, String continent)
            throws SQLException
    {
        String name = requireValue(continent, "Continent");
        String sql = "SELECT SUM(Population) AS total "
                + "FROM country WHERE Continent = ?";

        System.out.println("\nReport 27: Population by Continent (" + name + ")");
        printTotal(con, sql, "Continent", name);
    }

    /** Report 28: Population of a selected region. */
    public void printPopulationByRegion(Connection con, String region)
            throws SQLException
    {
        String name = requireValue(region, "Region");
        String sql = "SELECT SUM(Population) AS total "
                + "FROM country WHERE Region = ?";

        System.out.println("\nReport 28: Population by Region (" + name + ")");
        printTotal(con, sql, "Region", name);
    }

    /** Report 29: Population of a selected country. */
    public void printPopulationByCountry(Connection con, String countryName)
            throws SQLException
    {
        String name = requireValue(countryName, "Country");
        String sql = "SELECT Population AS total FROM country WHERE Name = ?";

        System.out.println("\nReport 29: Population by Country (" + name + ")");
        printTotal(con, sql, "Country", name);
    }

    /** Report 30: Sum of recorded city populations in a district. */
    public void printPopulationByDistrict(Connection con, String district)
            throws SQLException
    {
        String name = requireValue(district, "District");
        String sql = "SELECT SUM(Population) AS total "
                + "FROM city WHERE District = ?";

        System.out.println("\nReport 30: Population by District (" + name + ")");
        printTotal(con, sql, "District", name);
    }

    /** Report 31: Population of a selected city. */
    public void printPopulationByCity(Connection con, String cityName)
            throws SQLException
    {
        String name = requireValue(cityName, "City");
        String sql = "SELECT Population AS total FROM city WHERE Name = ?";

        System.out.println("\nReport 31: Population by City (" + name + ")");
        printTotal(con, sql, "City", name);
    }

    private void printTotal(Connection con, String sql, String label, String name)
            throws SQLException
    {
        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    long total = rs.getLong("total");
                    if (!rs.wasNull())
                    {
                        if (rs.next())
                        {
                            System.out.println(
                                    "Multiple matches found for " + label + ": " + name
                            );
                            return;
                        }

                        System.out.println(
                                label + " Population (" + name + "): " + total
                        );
                        return;
                    }
                }

                System.out.println("No data found for " + label + ": " + name);
            }
        }
    }

    private String requireValue(String value, String label)
    {
        if (value == null || value.isBlank())
        {
            throw new IllegalArgumentException(label + " cannot be empty");
        }

        return value.trim();
    }
}