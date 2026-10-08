package org.example.reports.report01;

import java.sql.Connection;
import java.util.List;

import org.example.reports.common.Country;
import org.example.reports.common.CountryRepository;
import org.example.support.WorldDatabaseFixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class CountriesInWorldReportTest
{
    @Test
    void returnsEveryCountryInPopulationOrderWithCapitalNames() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<Country> countries = new CountriesInWorldReport(
                    new CountryRepository(connection)
            ).generate();

            assertEquals(List.of("CHN", "IND", "USA", "BRA", "DEU", "GBR",
                    "EGY", "AUS", "ATA", "NCP"),
                    countries.stream().map(Country::code).toList());
            assertEquals(new Country("CHN", "China", "Asia", "Eastern Asia",
                    1500, "Peking"), countries.getFirst());
            assertNull(countries.getLast().capital());
            assertFalse(connection.isClosed(), "The caller owns the connection");
        }
    }
}
