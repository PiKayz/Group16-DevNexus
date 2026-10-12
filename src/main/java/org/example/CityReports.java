package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * City reports for coursework requirements 7-16.
 *
 * This class is connected to the application's database through the
 * Connection supplied by Main.
 */
public class CityReports
{
    private final Connection con;

    /**
     * Create CityReports using the database connection created by Main.
     */
    public CityReports(Connection con)
    {
        if (con == null)
        {
            throw new IllegalArgumentException("Database connection cannot be null");
        }

        this.con = con;
    }

    /**
     * Requirement 7:
     * All cities in the world ordered by population, largest first.
     */
    public void allCitiesInWorld() throws SQLException
    {
        String sql =
                "SELECT city.Name, country.Name AS Country, city.District, city.Population " +
                        "FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "ORDER BY city.Population DESC, city.Name ASC";

        runReport(sql);
    }

    /**
     * Requirement 8:
     * All cities in a continent ordered by population, largest first.
     */
    public void allCitiesInContinent(String continent) throws SQLException
    {
        validateText(continent, "Continent");

        String sql =
                "SELECT city.Name, country.Name AS Country, city.District, city.Population " +
                        "FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "WHERE country.Continent = ? " +
                        "ORDER BY city.Population DESC, city.Name ASC";

        runReport(sql, continent);
    }

    /**
     * Requirement 9:
     * All cities in a region ordered by population, largest first.
     */
    public void allCitiesInRegion(String region) throws SQLException
    {
        validateText(region, "Region");

        String sql =
                "SELECT city.Name, country.Name AS Country, city.District, city.Population " +
                        "FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "WHERE country.Region = ? " +
                        "ORDER BY city.Population DESC, city.Name ASC";

        runReport(sql, region);
    }

    /**
     * Requirement 10:
     * All cities in a country ordered by population, largest first.
     */
    public void allCitiesInCountry(String country) throws SQLException
    {
        validateText(country, "Country");

        String sql =
                "SELECT city.Name, country.Name AS Country, city.District, city.Population " +
                        "FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "WHERE country.Name = ? " +
                        "ORDER BY city.Population DESC, city.Name ASC";

        runReport(sql, country);
    }

    /**
     * Requirement 11:
     * All cities in a district ordered by population, largest first.
     */
    public void allCitiesInDistrict(String district) throws SQLException
    {
        validateText(district, "District");

        String sql =
                "SELECT city.Name, country.Name AS Country, city.District, city.Population " +
                        "FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "WHERE city.District = ? " +
                        "ORDER BY city.Population DESC, city.Name ASC";

        runReport(sql, district);
    }

    /**
     * Requirement 12:
     * Top N populated cities in the world.
     */
    public void topNCitiesInWorld(int n) throws SQLException
    {
        validateN(n);

        String sql =
                "SELECT city.Name, country.Name AS Country, city.District, city.Population " +
                        "FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "ORDER BY city.Population DESC, city.Name ASC " +
                        "LIMIT ?";

        runReport(sql, n);
    }

    /**
     * Requirement 13:
     * Top N populated cities in a continent.
     */
    public void topNCitiesInContinent(String continent, int n) throws SQLException
    {
        validateText(continent, "Continent");
        validateN(n);

        String sql =
                "SELECT city.Name, country.Name AS Country, city.District, city.Population " +
                        "FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "WHERE country.Continent = ? " +
                        "ORDER BY city.Population DESC, city.Name ASC " +
                        "LIMIT ?";

        runReport(sql, continent, n);
    }

    /**
     * Requirement 14:
     * Top N populated cities in a region.
     */
    public void topNCitiesInRegion(String region, int n) throws SQLException
    {
        validateText(region, "Region");
        validateN(n);

        String sql =
                "SELECT city.Name, country.Name AS Country, city.District, city.Population " +
                        "FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "WHERE country.Region = ? " +
                        "ORDER BY city.Population DESC, city.Name ASC " +
                        "LIMIT ?";

        runReport(sql, region, n);
    }

    /**
     * Requirement 15:
     * Top N populated cities in a country.
     */
    public void topNCitiesInCountry(String country, int n) throws SQLException
    {
        validateText(country, "Country");
        validateN(n);

        String sql =
                "SELECT city.Name, country.Name AS Country, city.District, city.Population " +
                        "FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "WHERE country.Name = ? " +
                        "ORDER BY city.Population DESC, city.Name ASC " +
                        "LIMIT ?";

        runReport(sql, country, n);
    }

    /**
     * Requirement 16:
     * Top N populated cities in a district.
     */
    public void topNCitiesInDistrict(String district, int n) throws SQLException
    {
        validateText(district, "District");
        validateN(n);

        String sql =
                "SELECT city.Name, country.Name AS Country, city.District, city.Population " +
                        "FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "WHERE city.District = ? " +
                        "ORDER BY city.Population DESC, city.Name ASC " +
                        "LIMIT ?";

        runReport(sql, district, n);
    }

    /**
     * Execute a report with parameters.
     */
    private void runReport(String sql, Object... parameters) throws SQLException
    {
        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            for (int i = 0; i < parameters.length; i++)
            {
                stmt.setObject(i + 1, parameters[i]);
            }

            try (ResultSet rs = stmt.executeQuery())
            {
                printHeader();

                boolean found = false;

                while (rs.next())
                {
                    found = true;

                    System.out.printf(
                            "%-30s %-35s %-25s %12d%n",
                            rs.getString("Name"),
                            rs.getString("Country"),
                            rs.getString("District"),
                            rs.getInt("Population")
                    );
                }

                if (!found)
                {
                    System.out.println("No cities found.");
                }

                System.out.println();
            }
        }
    }

    private void printHeader()
    {
        System.out.println(
                "City                          Country                             District                     Population"
        );
        System.out.println(
                "------------------------------------------------------------------------------------------------------------"
        );
    }

    private void validateText(String value, String fieldName)
    {
        if (value == null || value.trim().isEmpty())
        {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }

    private void validateN(int n)
    {
        if (n <= 0)
        {
            throw new IllegalArgumentException("N must be greater than 0");
        }
    }
}
