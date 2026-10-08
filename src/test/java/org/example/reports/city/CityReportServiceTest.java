package org.example.reports.city;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.example.database.CityRepository;
import org.example.models.City;
import org.example.models.Continent;
import org.example.support.WorldDatabaseFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CityReportServiceTest
{
    @Test
    void returnsEveryCityWithCountryNamesInPopulationAndIdOrder() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<City> cities = service(connection).getCitiesInWorld();
            assertEquals(List.of("Shanghai", "Mumbai", "Berlin", "London", "Cairo",
                    "Canberra", "Peking", "Brasilia", "New Delhi", "Washington",
                    "Springfield", "Springfield"), cities.stream().map(City::name).toList());
            assertEquals(new City("Shanghai", "China", "Shared District", 600), cities.getFirst());
            assertEquals(new City("Springfield", "United States", "Central", 0), cities.getLast());
            assertEquals(2, cities.stream().filter(city -> city.name().equals("Springfield")).count());
            assertFalse(connection.isClosed());
        }
    }

    @Test
    void returnsNoCitiesForAnEmptyCityTable() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open();
             Statement statement = connection.createStatement())
        {
            statement.executeUpdate("DELETE FROM city");
            assertTrue(service(connection).getCitiesInWorld().isEmpty());
        }
    }


    @ParameterizedTest
    @EnumSource(Continent.class)
    void filtersCitiesByContinentIncludingContinentsWithoutCities(Continent continent) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<String> expected = switch (continent)
            {
                case ASIA -> List.of("Shanghai", "Mumbai", "Peking", "New Delhi");
                case EUROPE -> List.of("Berlin", "London");
                case NORTH_AMERICA -> List.of("Washington", "Springfield");
                case AFRICA -> List.of("Cairo");
                case OCEANIA -> List.of("Canberra");
                case ANTARCTICA -> List.of();
                case SOUTH_AMERICA -> List.of("Brasilia", "Springfield");
            };
            assertEquals(expected, service(connection).getCitiesInContinent(continent)
                    .stream().map(City::name).toList());
        }
    }

    private CityReportService service(Connection connection)
    {
        return new CityReportService(new CityRepository(connection));
    }
}
