package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Reports 7–11: All cities.
 * Reports 12–16: Top N cities.
 */
// PMD TooManyMethods: Reports 7-16 share one city-report API used by Main and the integration tests.
@SuppressWarnings("PMD.TooManyMethods")
public class CityReports
{
    private final Connection con;

    public CityReports(final Connection con)
    {
        if (con == null)
        {
            throw new IllegalArgumentException("Database connection cannot be null");
        }

        this.con = con;
    }

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
    public void allCitiesInContinent(final String continent) throws SQLException
    {
        requireValue(continent, "Continent");
        System.out.println("\nReport 8: All cities in continent: " + continent);
        runReport(allCitiesQuery("WHERE country.Continent = ? "), continent.trim());
    }

    /** Report 9: All cities in a region. */
    public void allCitiesInRegion(final String region) throws SQLException
    {
        requireValue(region, "Region");
        System.out.println("\nReport 9: All cities in region: " + region);
        runReport(allCitiesQuery("WHERE country.Region = ? "), region.trim());
    }

    /** Report 10: All cities in a country. */
    public void allCitiesInCountry(final String country) throws SQLException
    {
        requireValue(country, "Country");
        System.out.println("\nReport 10: All cities in country: " + country);
        runReport(allCitiesQuery("WHERE country.Name = ? "), country.trim());
    }

    /** Report 11: All cities in a district. */
    public void allCitiesInDistrict(final String district) throws SQLException
    {
        requireValue(district, "District");
        System.out.println("\nReport 11: All cities in district: " + district);
        runReport(allCitiesQuery("WHERE city.District = ? "), district.trim());
    }

    private String allCitiesQuery(final String filter)
    {
        return "SELECT city.Name AS City, "
                + "country.Name AS Country, "
                + "city.District, city.Population "
                + "FROM city "
                + "JOIN country ON city.CountryCode = country.Code "
                + filter
                + "ORDER BY city.Population DESC, city.Name ASC";
    }

    private void requireValue(final String value, final String label)
    {
        if (value == null || value.trim().isEmpty())
        {
            throw new IllegalArgumentException(label + " cannot be empty");
        }
    }

    private void runReport(final String sql, final Object... parameters) throws SQLException
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
    public void topNCitiesInWorld(final Connection con, final int limit) throws SQLException
    {
        printTopN(con, "Report 12: Top " + limit + " cities in the world", "", null, limit);
    }

    /** Report 13: Top N cities in a continent. */
    public void topNCitiesInContinent(final Connection con, final String continent, final int limit)
            throws SQLException
    {
        printTopN(
                con,
                "Report 13: Top " + limit + " cities in continent: " + continent,
                "WHERE country.Continent = ? ",
                continent,
                limit
        );
    }

    /** Report 14: Top N cities in a region. */
    public void topNCitiesInRegion(final Connection con, final String region, final int limit)
            throws SQLException
    {
        printTopN(
                con,
                "Report 14: Top " + limit + " cities in region: " + region,
                "WHERE country.Region = ? ",
                region,
                limit
        );
    }

    /** Report 15: Top N cities in a country. */
    public void topNCitiesInCountry(final Connection con, final String country, final int limit)
            throws SQLException
    {
        printTopN(
                con,
                "Report 15: Top " + limit + " cities in country: " + country,
                "WHERE country.Name = ? ",
                country,
                limit
        );
    }

    /** Report 16: Top N cities in a district. */
    public void topNCitiesInDistrict(final Connection con, final String district, final int limit)
            throws SQLException
    {
        printTopN(
                con,
                "Report 16: Top " + limit + " cities in district: " + district,
                "WHERE city.District = ? ",
                district,
                limit
        );
    }

    private void printTopN(
            final Connection con,
            final String title,
            final String filter,
            final String value,
            final int limit
    ) throws SQLException
    {
        if (limit <= 0)
        {
            throw new IllegalArgumentException("N must be a positive integer");
        }

        if (!filter.isEmpty())
        {
            requireValue(value, "Filter value");
        }

        final String sql = "SELECT city.Name, country.Name AS Country, "
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

            stmt.setInt(index, limit);

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