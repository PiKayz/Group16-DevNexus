package org.example.reports.country;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.example.database.CountryRepository;
import org.example.models.Country;
import org.example.models.Region;
import org.example.support.WorldDatabaseFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CountriesInRegionReportTest
{
    @ParameterizedTest
    @ValueSource(strings = {"Eastern Asia", "eAsTeRn aSiA", " Eastern Asia "})
    void filtersRegionInPopulationOrderAndKeepsCountriesWithoutCapitals(String name)
            throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<Country> countries = service(connection).getCountriesInRegion(new Region(name));

            assertEquals(List.of("CHN", "NCP"),
                    countries.stream().map(Country::code).toList());
            assertEquals("Peking", countries.getFirst().capital());
            assertNull(countries.getLast().capital());
            assertEquals(0, countries.getLast().population());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Unknown Region", "Eastern Asia' OR 1=1 --"})
    void treatsTheRegionAsDataRatherThanSql(String name) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            assertTrue(service(connection).getCountriesInRegion(new Region(name)).isEmpty());
        }
    }

    @Test
    void supportsLiteralApostrophesInRegionNames() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE country SET Region = ? WHERE Code = 'NCP'"))
        {
            statement.setString(1, "O'Region");
            statement.executeUpdate();

            assertEquals(List.of("NCP"), service(connection)
                    .getCountriesInRegion(new Region("O'Region"))
                    .stream().map(Country::code).toList());
        }
    }

    private CountryReportService service(Connection connection)
    {
        return new CountryReportService(new CountryRepository(connection));
    }
}
