package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Population Totals reports.
 * Assigned developer: Khaine Zaw Hein
 */
public class PopulationTotals
{

    /**
     * Requirement #64: The population of the world.
     */
    public void printWorldPopulation(Connection con)
    {
        String query = "SELECT SUM(Population) AS total FROM country";

        System.out.println("Population Totals — World:");
        System.out.println("--------------------------");

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query))
        {
            if (rs.next())
            {
                long totalPopulation = rs.getLong("total");
                System.out.println("World Population: " + totalPopulation);
            }
            else
            {
                System.out.println("No population data found.");
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error fetching world population: " + e.getMessage());
        }
    }

    /**
     * Requirement #65: The population of a continent.
     */
    public void printPopulationByContinent(Connection con)
    {
        String query = "SELECT Continent AS name, SUM(Population) AS total " +
                "FROM country GROUP BY Continent ORDER BY total DESC";

        System.out.println("\nPopulation Totals — By Continent:");
        System.out.println("---------------------------------");
        System.out.println("Continent       | Total Population");
        System.out.println("----------------------------------");

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query))
        {
            while (rs.next())
            {
                System.out.printf("%-15s | %-15s%n",
                        rs.getString("name"),
                        rs.getLong("total"));
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error fetching continent population: " + e.getMessage());
        }
    }

    /**
     * Requirement #66: The population of a region.
     */
    public void printPopulationByRegion(Connection con, String region)
    {
        String query = "SELECT Region AS name, SUM(Population) AS total " +
                "FROM country WHERE Region = ? GROUP BY Region";

        System.out.println("\nPopulation Totals — Region (" + region + "):");
        System.out.println("--------------------------------------------");

        try (PreparedStatement pstmt = con.prepareStatement(query))
        {
            pstmt.setString(1, region);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                System.out.println("Region Population (" + region + "): " + rs.getLong("total"));
            }
            else
            {
                System.out.println("No data found for region: " + region);
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error fetching region population: " + e.getMessage());
        }
    }

    /**
     * Requirement #67: The population of a country.
     */
    public void printPopulationByCountry(Connection con, String countryName)
    {
        String query = "SELECT Name, Population FROM country WHERE Name = ?";

        System.out.println("\nPopulation Totals — Country (" + countryName + "):");
        System.out.println("--------------------------------------------------");

        try (PreparedStatement pstmt = con.prepareStatement(query))
        {
            pstmt.setString(1, countryName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                System.out.println("Country Population (" + countryName + "): " + rs.getLong("Population"));
            }
            else
            {
                System.out.println("No data found for country: " + countryName);
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error fetching country population: " + e.getMessage());
        }
    }
}