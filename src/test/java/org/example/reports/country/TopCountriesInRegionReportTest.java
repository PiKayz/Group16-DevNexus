package org.example.reports.country;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.example.database.CountryRepository;
import org.example.models.Country;
import org.example.models.Region;
import org.example.models.TopN;
import org.example.support.WorldDatabaseFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TopCountriesInRegionReportTest
{
    @Test
    void returnsTheRequestedTopCountriesInTheRegion() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<Country> countries = service(connection)
                    .getTopCountriesInRegion(new Region(" eastern asia "), new TopN(1));
            assertEquals(List.of("CHN"), countries.stream().map(Country::code).toList());
            assertEquals("Peking", countries.getFirst().capital());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 100, Integer.MAX_VALUE})
    void keepsAllAvailableMatchesAndMissingCapitalsForLargerLimits(int count) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<Country> countries = service(connection)
                    .getTopCountriesInRegion(new Region("Eastern Asia"), new TopN(count));
            assertEquals(List.of("CHN", "NCP"),
                    countries.stream().map(Country::code).toList());
            assertEquals(0, countries.getLast().population());
            assertNull(countries.getLast().capital());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Unknown Region", "Eastern Asia' OR 1=1 --"})
    void returnsNoMatchesForUnknownOrSqlShapedRegionText(String name) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            assertTrue(service(connection)
                    .getTopCountriesInRegion(new Region(name), new TopN(10)).isEmpty());
        }
    }

    @Test
    void bindsAnApostropheInTheRegionSeparatelyFromTheLimit() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE country SET Region = ? WHERE Code = 'NCP'"))
        {
            statement.setString(1, "O'Region");
            statement.executeUpdate();
            assertEquals(List.of("NCP"), service(connection)
                    .getTopCountriesInRegion(new Region("O'Region"), new TopN(1))
                    .stream().map(Country::code).toList());
        }
    }

    @Test
    void breaksEqualPopulationTiesBeforeApplyingTheLimit() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE country SET Region = ? WHERE Code = 'GBR'"))
        {
            statement.setString(1, "Western Europe");
            statement.executeUpdate();
            assertEquals(List.of("DEU"), service(connection)
                    .getTopCountriesInRegion(new Region("Western Europe"), new TopN(1))
                    .stream().map(Country::code).toList());
        }
    }

    @Test
    void displaysTheScopeCountAndRequiredColumns() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8))
            {
                service(connection).printTopCountriesInRegion(
                        new Region("Eastern Asia"), new TopN(1), output);
            }
            String text = bytes.toString(StandardCharsets.UTF_8);
            assertTrue(text.contains("Top 1 countries in Eastern Asia"));
            assertTrue(text.contains("Population"));
            assertTrue(text.contains("Capital"));
            assertTrue(text.contains("Total countries: 1"));
        }
    }

    private CountryReportService service(Connection connection)
    {
        return new CountryReportService(new CountryRepository(connection));
    }
}
