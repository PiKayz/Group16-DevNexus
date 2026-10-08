package org.example.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.example.models.City;
import org.example.models.TopN;

/**
 * Read-only city queries. The caller owns the connection; query resources
 * are closed here and country names are resolved from the World country table.
 */
public final class CityRepository
{
    private static final String SELECT_CITIES = """
            SELECT city.Name, country.Name AS Country, city.District, city.Population
            FROM city
            JOIN country ON city.CountryCode = country.Code
            """;
    private static final String ORDER_BY_POPULATION =
            "ORDER BY city.Population DESC, city.ID ASC";

    private final Connection connection;

    public CityRepository(Connection connection)
    {
        this.connection = Objects.requireNonNull(connection, "connection");
    }

    public List<City> findAll() throws SQLException
    {
        return query("", List.of(), null);
    }

    private List<City> query(String filter, List<String> parameters, TopN topN)
            throws SQLException
    {
        if (connection.isClosed())
        {
            throw new SQLException("Not connected to the database");
        }

        String sql = SELECT_CITIES + filter + ORDER_BY_POPULATION
                + (topN == null ? "" : " LIMIT ?");
        List<City> cities = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql))
        {
            int index = 1;
            for (String parameter : parameters)
            {
                statement.setString(index++, parameter);
            }
            if (topN != null)
            {
                statement.setInt(index, topN.value());
            }

            try (ResultSet result = statement.executeQuery())
            {
                while (result.next())
                {
                    cities.add(new City(
                            result.getString("Name"), result.getString("Country"),
                            result.getString("District"), result.getLong("Population")
                    ));
                }
            }
        }

        return List.copyOf(cities);
    }
}
