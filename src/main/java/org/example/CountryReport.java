package org.example;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Requirement 1: all countries in descending population order.
 */
public final class CountryReport
{
    private CountryReport()
    {
    }

    /**
     * Retrieve every country, including countries without a capital.
     * Country code gives countries with equal populations a stable order.
     */
    public static List<Country> getAllCountries(Connection connection)
            throws SQLException
    {
        if (connection == null || connection.isClosed())
        {
            throw new SQLException("Not connected to the database");
        }

        String sql = """
                SELECT country.Code, country.Name, country.Continent,
                       country.Region, country.Population,
                       capital.Name AS Capital
                FROM country
                LEFT JOIN city AS capital ON country.Capital = capital.ID
                ORDER BY country.Population DESC, country.Code ASC
                """;

        List<Country> countries = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql))
        {
            while (result.next())
            {
                countries.add(new Country(
                        result.getString("Code"),
                        result.getString("Name"),
                        result.getString("Continent"),
                        result.getString("Region"),
                        result.getLong("Population"),
                        result.getString("Capital")
                ));
            }
        }

        return countries;
    }

    /**
     * Display the required columns without truncating database values.
     */
    public static void printCountries(List<Country> countries, PrintStream output)
    {
        output.println("All countries in the world by population (largest to smallest)");

        if (countries.isEmpty())
        {
            output.println("No matching records found");
            return;
        }

        String rowFormat = "%-4s | %-52s | %-13s | %-26s | %13s | %-35s%n";
        output.printf(Locale.ROOT, rowFormat,
                "Code", "Name", "Continent", "Region", "Population", "Capital");

        for (Country country : countries)
        {
            String capital = country.capital() == null ? "N/A" : country.capital();
            String population = String.format(Locale.ROOT, "%,d", country.population());

            output.printf(Locale.ROOT, rowFormat,
                    country.code(), country.name(), country.continent(),
                    country.region(), population, capital);
        }

        output.println("Total countries: " + countries.size());
    }
}
