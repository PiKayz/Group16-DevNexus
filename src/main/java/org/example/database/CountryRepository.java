package org.example.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.example.models.Continent;
import org.example.models.Country;
import org.example.models.Region;

/**
 * Read-only country queries. The caller owns the database connection;
 * this repository closes every statement and result set that it creates.
 */
public final class CountryRepository
{
    private static final String SELECT_COUNTRIES = """
            SELECT country.Code, country.Name, country.Continent,
                   country.Region, country.Population,
                   capital.Name AS Capital
            FROM country
            LEFT JOIN city AS capital ON country.Capital = capital.ID
            """;

    private static final String ORDER_BY_POPULATION =
            "ORDER BY country.Population DESC, country.Code ASC";

    private final Connection connection;

    public CountryRepository(Connection connection)
    {
        this.connection = Objects.requireNonNull(connection, "connection");
    }

    public List<Country> findAll() throws SQLException
    {
        return query(SELECT_COUNTRIES + ORDER_BY_POPULATION, null);
    }

    public List<Country> findByContinent(Continent continent) throws SQLException
    {
        Objects.requireNonNull(continent, "continent");
        return query(SELECT_COUNTRIES + "WHERE country.Continent = ? "
                + ORDER_BY_POPULATION, continent.databaseName());
    }

    public List<Country> findByRegion(Region region) throws SQLException
    {
        Objects.requireNonNull(region, "region");
        return query(SELECT_COUNTRIES + "WHERE LOWER(country.Region) = LOWER(?) "
                + ORDER_BY_POPULATION, region.name());
    }

    private List<Country> query(String sql, String filter) throws SQLException
    {
        if (connection.isClosed())
        {
            throw new SQLException("Not connected to the database");
        }

        List<Country> countries = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql))
        {
            if (filter != null)
            {
                statement.setString(1, filter);
            }

            try (ResultSet result = statement.executeQuery())
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
        }

        return List.copyOf(countries);
    }
}
