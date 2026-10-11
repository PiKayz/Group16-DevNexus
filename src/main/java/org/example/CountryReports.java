package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Country reports: requirements 1-6.
 * Assigned developer: Htet Aung Shine.
 *
 * Implemented so far:
 *  1. All countries in the world, largest population first (UC-01).
 */
public class CountryReports
{
    /**
     * Requirement 1: print all countries in the world, largest population first.
     * A country without a capital is kept, with its capital shown as "N/A".
     *
     * @param con an open connection to the World database
     * @throws SQLException if the query fails
     */
    public void printAllCountriesInWorld(Connection con) throws SQLException
    {
        if (con == null)
        {
            throw new SQLException("Not connected to the database");
        }

        String sql =
                "SELECT c.Code, c.Name, c.Continent, c.Region, c.Population, "
                        + "COALESCE(ci.Name, 'N/A') AS Capital "
                        + "FROM country c "
                        + "LEFT JOIN city ci ON c.Capital = ci.ID "
                        + "ORDER BY c.Population DESC, c.Name";

        System.out.println();
        System.out.println("Report 1: All countries in the world by population (largest to smallest)");

        String format = "%-5s %-45s %-15s %-26s %13s  %s%n";
        int count = 0;

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery())
        {
            System.out.printf(format, "Code", "Name", "Continent", "Region", "Population", "Capital");

            while (rs.next())
            {
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