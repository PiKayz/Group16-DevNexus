package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * City reports: requirements 12-16 (top N populated cities).
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class CityReports
{

    /**
     * Report 12: top N populated cities in the world.
     *
     * @param con   open database connection
     * @param limit number of cities to show
     * @throws SQLException if the query fails
     */
    public void topNCitiesInWorld(final Connection con, final int limit) throws SQLException
    {
        printTopN(con, "Report 12 - Top " + limit + " cities in the world", "", null, limit);
    }


    /**
     * Report 13: top N populated cities in a continent.
     *
     * @param con       open database connection
     * @param continent continent name
     * @param limit     number of cities to show
     * @throws SQLException if the query fails
     */
    public void topNCitiesInContinent(final Connection con, final String continent, final int limit)
            throws SQLException
    {
        printTopN(con, "Report 13 - Top " + limit + " cities in " + continent,
                "WHERE country.Continent = ? ", continent, limit);
    }


    /**
     * Report 14: top N populated cities in a region.
     *
     * @param con    open database connection
     * @param region region name
     * @param limit  number of cities to show
     * @throws SQLException if the query fails
     */
    public void topNCitiesInRegion(final Connection con, final String region, final int limit)
            throws SQLException
    {
        printTopN(con, "Report 14 - Top " + limit + " cities in " + region,
                "WHERE country.Region = ? ", region, limit);
    }


    /** Report 15: top N populated cities in a country. */
    public void topNCitiesInCountry(final Connection con, final String country, final int limit)
            throws SQLException
    {
        printTopN(con, "Report 15 - Top " + limit + " cities in " + country,
                "WHERE country.Name = ? ", country, limit);
    }


    /** Report 16: top N populated cities in a district. */
    public void topNCitiesInDistrict(final Connection con, final String district, final int limit)
            throws SQLException
    {
        printTopN(con, "Report 16 - Top " + limit + " cities in " + district + " district",
                "WHERE city.District = ? ", district, limit);
    }

    /**
     * Run a Top N query (largest population first) and print the result.
     * filter is an empty string for the world report, otherwise a WHERE clause
     * with one "?" that is set to value.
     */
    private void printTopN(final Connection con, final String title, final String filter,
                           final String value, final int limit)
            throws SQLException
    {
        if (limit <= 0)
        {
            throw new IllegalArgumentException("N must be a positive integer");
        }

        if (!filter.isEmpty() && (value == null || value.isBlank()))
        {
            throw new IllegalArgumentException("A filter value is required");
        }

        final String sql =
                "SELECT city.Name, country.Name AS Country, city.District, city.Population "
                        + "FROM city JOIN country ON city.CountryCode = country.Code "
                        + filter
                        + "ORDER BY city.Population DESC, city.Name ASC LIMIT ?";

        System.out.println();
        System.out.println(title);
        System.out.println(String.format("%-35s %-52s %-20s %12s",
                "Name", "Country", "District", "Population"));

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
                    System.out.println(String.format("%-35s %-52s %-20s %,12d",
                            result.getString("Name"),
                            result.getString("Country"),
                            result.getString("District"),
                            result.getInt("Population")));
                }

                if (!found)
                {
                    System.out.println("No matching records found");
                }
            }
        }
    }
}