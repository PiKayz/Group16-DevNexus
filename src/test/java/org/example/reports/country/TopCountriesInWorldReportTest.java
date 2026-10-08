package org.example.reports.country;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.example.database.CountryRepository;
import org.example.models.Country;
import org.example.models.TopN;
import org.example.support.WorldDatabaseFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TopCountriesInWorldReportTest
{
    @Test
    void returnsOnlyTopNInPopulationOrderWithStableTies() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<Country> countries = service(connection).getTopCountriesInWorld(new TopN(5));

            assertEquals(List.of("CHN", "IND", "USA", "BRA", "DEU"),
                    countries.stream().map(Country::code).toList());
            assertEquals("Peking", countries.getFirst().capital());
        }
    }

    @Test
    void supportsAnNOfOne() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            assertEquals(List.of("CHN"), service(connection)
                    .getTopCountriesInWorld(new TopN(1)).stream().map(Country::code).toList());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {100, Integer.MAX_VALUE})
    void returnsAllMatchesWhenNExceedsTheNumberOfCountries(int count) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<Country> countries = service(connection).getTopCountriesInWorld(new TopN(count));

            assertEquals(10, countries.size());
            assertEquals("NCP", countries.getLast().code());
            assertEquals(0, countries.getLast().population());
            assertNull(countries.getLast().capital());
        }
    }

    @Test
    void returnsAnEmptyListWhenNoCountriesExist() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open();
             Statement statement = connection.createStatement())
        {
            statement.executeUpdate("DELETE FROM country");
            assertTrue(service(connection).getTopCountriesInWorld(new TopN(1)).isEmpty());
        }
    }

    private CountryReportService service(Connection connection)
    {
        return new CountryReportService(new CountryRepository(connection));
    }
}
