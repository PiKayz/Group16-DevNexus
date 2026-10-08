package org.example.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.example.models.City;
import org.example.models.DistrictFilter;
import org.example.models.CountryFilter;
import org.example.models.Region;
import org.example.models.Continent;
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

    private static final String FILTER_CONTINENT = "WHERE country.Continent = ? ";

    private static final String FILTER_REGION = "WHERE LOWER(country.Region) = LOWER(?) ";

    private static final String MATCH_COUNTRY = "(UPPER(country.Code) = UPPER(?) OR LOWER(country.Name) = LOWER(?))";

    private static final String FILTER_DISTRICT = "WHERE LOWER(city.District) = LOWER(?) ";

    private final Connection connection;

    public CityRepository(Connection connection)
    {
        this.connection = Objects.requireNonNull(connection, "connection");
    }

    public List<City> findAll() throws SQLException
    {
        return query("", List.of(), null);
    }

    public List<City> findByContinent(Continent continent) throws SQLException
    {
        Objects.requireNonNull(continent, "continent");
        return query(FILTER_CONTINENT, List.of(continent.databaseName()), null);
    }

    public List<City> findByRegion(Region region) throws SQLException
    {
        Objects.requireNonNull(region, "region");
        return query(FILTER_REGION, List.of(region.name()), null);
    }

    public List<City> findByCountry(CountryFilter country) throws SQLException
    {
        Objects.requireNonNull(country, "country");
        return query("WHERE " + MATCH_COUNTRY + " ", List.of(country.value(), country.value()), null);
    }

    public List<City> findByDistrict(DistrictFilter district) throws SQLException
    {
        Objects.requireNonNull(district, "district");
        String filter = FILTER_DISTRICT;
        List<String> parameters = new ArrayList<>();
        parameters.add(district.name());
        if (district.country() != null)
        {
            filter += "AND " + MATCH_COUNTRY + " ";
            parameters.add(district.country().value());
            parameters.add(district.country().value());
        }
        return query(filter, parameters, null);
    }

    public List<City> findTopInWorld(TopN topN) throws SQLException
    {
        Objects.requireNonNull(topN, "topN");
        return query("", List.of(), topN);
    }

    public List<City> findTopByContinent(Continent continent, TopN topN) throws SQLException
    {
        Objects.requireNonNull(continent, "continent");
        Objects.requireNonNull(topN, "topN");
        return query(FILTER_CONTINENT, List.of(continent.databaseName()), topN);
    }

    public List<City> findTopByRegion(Region region, TopN topN) throws SQLException
    {
        Objects.requireNonNull(region, "region");
        Objects.requireNonNull(topN, "topN");
        return query(FILTER_REGION, List.of(region.name()), topN);
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
