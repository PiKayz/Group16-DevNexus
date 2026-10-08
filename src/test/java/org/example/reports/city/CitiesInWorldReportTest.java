package org.example.reports.city;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.example.database.CityRepository;
import org.example.models.City;
import org.example.support.WorldDatabaseFixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CitiesInWorldReportTest
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

    private CityReportService service(Connection connection)
    {
        return new CityReportService(new CityRepository(connection));
    }
}
