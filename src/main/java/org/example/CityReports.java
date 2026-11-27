package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Reports 7–11: All cities.
 * Reports 12–16: Top N cities.
 * These methods share a city-report API used by Main and integration tests.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class CityReports
{
    /** Connection used by the all-cities reports. */
    private final Connection con;

    /** Creates city reports using the supplied database connection. */
    public CityReports(Connection con)
    {
        if (con == null)
        {
            throw new IllegalArgumentException("Database connection cannot be null");
        }

        this.con = con;
    }

    /** Prints the heading for the city reports. */
    public void cityReports()
    {
        System.out.println();
        System.out.println("==============================");
        System.out.println("        CITY REPORTS");
        System.out.println("==============================");
    }

    /** Report 7: All cities in the world. */
    public void allCitiesInWorld() throws SQLException
    {
        System.out.println("\nReport 7: All cities in the world");
        runReport(allCitiesQuery(""));
    }

    /** Report 8: All cities in a continent. */
    public void allCitiesInContinent(String continent) throws SQLException
    {
        requireValue(continent, "Continent");
        System.out.println("\nReport 8: All cities in continent: " + continent);
        runReport(allCitiesQuery("WHERE country.Continent = ? "), continent.trim());
    }

    /** Report 9: All cities in a region. */
    public void allCitiesInRegion(String region) throws SQLException
    {
        requireValue(region, "Region");
        System.out.println("\nReport 9: All cities in region: " + region);
        runReport(allCitiesQuery("WHERE country.Region = ? "), region.trim());
    }

    /** Report 10: All cities in a country. */
    public void allCitiesInCountry(String country) throws SQLException
    {
        requireValue(country, "Country");
        System.out.println("\nReport 10: All cities in country: " + country);
        runReport(allCitiesQuery("WHERE country.Name = ? "), country.trim());
    }

    /** Report 11: All cities in a district. */
    public void allCitiesInDistrict(String district) throws SQLException
    {
        requireValue(district, "District");
        System.out.println("\nReport 11: All cities in district: " + district);
        runReport(allCitiesQuery("WHERE city.District = ? "), district.trim());
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
            throw new IllegalArgumentException(label + " cannot be empty");
        }
    }

    private void runReport(String sql, Object... parameters) throws SQLException
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
                        "City", "Country", "District", "Population"
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

    /** Report 12: Top N cities in the world. */
    public void topNCitiesInWorld(Connection con, int n) throws SQLException
    {
        printTopN(con, "Report 12: Top " + n + " cities in the world", "", null, n);
    }

    /** Report 13: Top N cities in a continent. */
    public void topNCitiesInContinent(Connection con, String continent, int n)
            throws SQLException
    {
        printTopN(
                con,
                "Report 13: Top " + n + " cities in continent: " + continent,
                "WHERE country.Continent = ? ",
                continent,
                n
        );
    }

    /** Report 14: Top N cities in a region. */
    public void topNCitiesInRegion(Connection con, String region, int n)
            throws SQLException
    {
        printTopN(
                con,
                "Report 14: Top " + n + " cities in region: " + region,
                "WHERE country.Region = ? ",
                region,
                n
        );
    }

    /** Report 15: Top N cities in a country. */
    public void topNCitiesInCountry(Connection con, String country, int n)
            throws SQLException
    {
        printTopN(
                con,
                "Report 15: Top " + n + " cities in country: " + country,
                "WHERE country.Name = ? ",
                country,
                n
        );
    }

    /** Report 16: Top N cities in a district. */
    public void topNCitiesInDistrict(Connection con, String district, int n)
            throws SQLException
    {
        printTopN(
                con,
                "Report 16: Top " + n + " cities in district: " + district,
                "WHERE city.District = ? ",
                district,
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
            throw new IllegalArgumentException("N must be a positive integer");
        }

        if (!filter.isEmpty())
        {
            requireValue(value, "Filter value");
        }

        String sql = "SELECT city.Name, country.Name AS Country, "
                + "city.District, city.Population "
                + "FROM city "
                + "JOIN country ON city.CountryCode = country.Code "
                + filter
                + "ORDER BY city.Population DESC, city.Name ASC LIMIT ?";

        System.out.println("\n" + title);
        System.out.printf(
                "%-35s %-52s %-20s %12s%n",
                "Name", "Country", "District", "Population"
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
