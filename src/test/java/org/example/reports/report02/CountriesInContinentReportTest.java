package org.example.reports.report02;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.example.reports.common.Continent;
import org.example.reports.common.Country;
import org.example.reports.common.CountryRepository;
import org.example.support.WorldDatabaseFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CountriesInContinentReportTest
{
    @Test
    void includesOnlySelectedContinentInDescendingPopulationOrder() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<Country> countries = report(connection).generate(Continent.ASIA);

            assertEquals(List.of("CHN", "IND", "NCP"),
                    countries.stream().map(Country::code).toList());
            assertEquals("Peking", countries.getFirst().capital());
            assertEquals(0, countries.getLast().population());
            assertNull(countries.getLast().capital());
        }
    }

    @Test
    void ordersEqualPopulationsByCountryCode() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            assertEquals(List.of("DEU", "GBR"), report(connection)
                    .generate(Continent.EUROPE).stream().map(Country::code).toList());
        }
    }

    @ParameterizedTest
    @EnumSource(Continent.class)
    void supportsEveryContinentInTheWorldDatabase(Continent continent) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<Country> countries = report(connection).generate(continent);

            assertFalse(countries.isEmpty());
            assertTrue(countries.stream().allMatch(
                    country -> country.continent().equals(continent.databaseName())));
        }
    }

    @Test
    void displaysAnEmptyContinentReportWithoutInventingRows() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open();
             Statement statement = connection.createStatement())
        {
            statement.executeUpdate("DELETE FROM country WHERE Continent = 'Asia'");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            try (PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8))
            {
                report(connection).print(Continent.ASIA, output);
            }

            String text = bytes.toString(StandardCharsets.UTF_8);
            assertTrue(text.contains("All countries in Asia"));
            assertTrue(text.contains("No matching records found"));
            assertFalse(text.contains("Total countries:"));
        }
    }

    private CountriesInContinentReport report(Connection connection)
    {
        return new CountriesInContinentReport(new CountryRepository(connection));
    }
}
