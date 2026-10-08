package org.example.reports.country;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.example.database.CountryRepository;
import org.example.models.Continent;
import org.example.models.Country;
import org.example.models.TopN;
import org.example.support.WorldDatabaseFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TopCountriesInContinentReportTest
{
    @Test
    void limitsTheSelectedContinentRatherThanTheWorld() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            assertEquals(List.of("CHN", "IND"), service(connection)
                    .getTopCountriesInContinent(Continent.ASIA, new TopN(2))
                    .stream().map(Country::code).toList());
            assertEquals(List.of("DEU"), service(connection)
                    .getTopCountriesInContinent(Continent.EUROPE, new TopN(1))
                    .stream().map(Country::code).toList());
        }
    }

    @ParameterizedTest
    @EnumSource(Continent.class)
    void supportsAllContinentsWithAnNOfOne(Continent continent) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<Country> countries = service(connection)
                    .getTopCountriesInContinent(continent, new TopN(1));
            assertEquals(1, countries.size());
            assertEquals(continent.databaseName(), countries.getFirst().continent());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {100, Integer.MAX_VALUE})
    void retainsAllMatchesAndMissingCapitalsWhenNExceedsContinentCount(int count)
            throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<Country> countries = service(connection)
                    .getTopCountriesInContinent(Continent.ASIA, new TopN(count));
            assertEquals(List.of("CHN", "IND", "NCP"),
                    countries.stream().map(Country::code).toList());
            assertNull(countries.getLast().capital());
        }
    }

    @Test
    void returnsNoRowsForAnEmptyContinent() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open();
             Statement statement = connection.createStatement())
        {
            statement.executeUpdate("DELETE FROM country WHERE Continent = 'Asia'");
            assertTrue(service(connection)
                    .getTopCountriesInContinent(Continent.ASIA, new TopN(5)).isEmpty());
        }
    }

    private CountryReportService service(Connection connection)
    {
        return new CountryReportService(new CountryRepository(connection));
    }
}
