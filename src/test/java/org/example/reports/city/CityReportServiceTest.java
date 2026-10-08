package org.example.reports.city;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.example.database.CityRepository;
import org.example.models.City;
import org.example.models.TopN;
import org.example.models.DistrictFilter;
import org.example.models.CountryFilter;
import org.example.models.Region;
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


    @ParameterizedTest
    @ValueSource(strings = {"Eastern Asia", "eastern ASIA", " Eastern Asia "})
    void filtersCitiesByRegionInPopulationOrder(String value) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            assertEquals(List.of("Shanghai", "Peking"), service(connection)
                    .getCitiesInRegion(new Region(value)).stream().map(City::name).toList());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Unknown Region", "Eastern Asia' OR 1=1 --"})
    void treatsCityRegionFiltersAsLiteralData(String value) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            assertTrue(service(connection).getCitiesInRegion(new Region(value)).isEmpty());
        }
    }


    @ParameterizedTest
    @ValueSource(strings = {"USA", " usa ", "United States", " united states "})
    void findsCitiesByCountryCodeOrCompleteName(String value) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<City> cities = service(connection).getCitiesInCountry(new CountryFilter(value));
            assertEquals(List.of("Washington", "Springfield"), cities.stream().map(City::name).toList());
            assertTrue(cities.stream().allMatch(city -> city.country().equals("United States")));
            assertEquals(0, cities.getLast().population());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Unknown Country", "US", "USA' OR 1=1 --"})
    void matchesTheWholeCountryIdentifierAsLiteralData(String value) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            assertTrue(service(connection).getCitiesInCountry(new CountryFilter(value)).isEmpty());
        }
    }

    @Test
    void supportsApostrophesInCountryNames() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open();
             java.sql.PreparedStatement statement = connection.prepareStatement(
                     "UPDATE country SET Name = ? WHERE Code = 'USA'"))
        {
            statement.setString(1, "O'Country");
            statement.executeUpdate();
            assertEquals(2, service(connection).getCitiesInCountry(new CountryFilter("O'Country")).size());
        }
    }


    @ParameterizedTest
    @ValueSource(strings = {"Shared District", "shared DISTRICT", " Shared District "})
    void includesAllCountriesWhenADistrictNameIsShared(String value) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<City> cities = service(connection).getCitiesInDistrict(new DistrictFilter(value));
            assertEquals(List.of("Shanghai", "Mumbai"), cities.stream().map(City::name).toList());
            assertEquals(List.of("China", "India"), cities.stream().map(City::country).toList());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"USA", "United States"})
    void qualifiesDistrictsByCountryWithoutDroppingZeroPopulationCities(String value) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<City> cities = service(connection).getCitiesInDistrict(
                    new DistrictFilter("Central", new CountryFilter(value)));
            assertEquals(List.of(new City("Springfield", "United States", "Central", 0)), cities);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Unknown District", "Central' OR 1=1 --"})
    void treatsDistrictNamesAsLiteralData(String value) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            assertTrue(service(connection).getCitiesInDistrict(new DistrictFilter(value)).isEmpty());
        }
    }


    @Test
    void limitsWorldCitiesAfterPopulationSortingWithStableTies() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            assertEquals(List.of("Shanghai", "Mumbai", "Berlin"), service(connection)
                    .getTopCitiesInWorld(new TopN(3)).stream().map(City::name).toList());
            assertEquals(List.of("Shanghai"), service(connection)
                    .getTopCitiesInWorld(new TopN(1)).stream().map(City::name).toList());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {100, Integer.MAX_VALUE})
    void retainsAllCitiesWhenNExceedsTheWorldCityCount(int count) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<City> cities = service(connection).getTopCitiesInWorld(new TopN(count));
            assertEquals(12, cities.size());
            assertEquals(0, cities.getLast().population());
            assertEquals(2, cities.stream().filter(city -> city.name().equals("Springfield")).count());
        }
    }


    @Test
    void limitsCitiesWithinTheSelectedContinentRatherThanTheWorld() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            assertEquals(List.of("Shanghai", "Mumbai"), service(connection)
                    .getTopCitiesInContinent(Continent.ASIA, new TopN(2))
                    .stream().map(City::name).toList());
            assertEquals(List.of("Berlin"), service(connection)
                    .getTopCitiesInContinent(Continent.EUROPE, new TopN(1))
                    .stream().map(City::name).toList());
            assertTrue(service(connection)
                    .getTopCitiesInContinent(Continent.ANTARCTICA, new TopN(1)).isEmpty());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {100, Integer.MAX_VALUE})
    void retainsAllMatchingContinentCitiesForLargeN(int count) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<City> cities = service(connection)
                    .getTopCitiesInContinent(Continent.NORTH_AMERICA, new TopN(count));
            assertEquals(List.of("Washington", "Springfield"), cities.stream().map(City::name).toList());
            assertEquals(0, cities.getLast().population());
        }
    }


    @Test
    void limitsCitiesWithinTheSelectedRegion() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            assertEquals(List.of("Shanghai", "Peking"), service(connection)
                    .getTopCitiesInRegion(new Region(" eastern ASIA "), new TopN(2))
                    .stream().map(City::name).toList());
            assertEquals(List.of("Berlin"), service(connection)
                    .getTopCitiesInRegion(new Region("Western Europe"), new TopN(1))
                    .stream().map(City::name).toList());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {100, Integer.MAX_VALUE})
    void retainsAllMatchingRegionCitiesForLargeN(int count) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<City> cities = service(connection)
                    .getTopCitiesInRegion(new Region("North America"), new TopN(count));
            assertEquals(List.of("Washington", "Springfield"), cities.stream().map(City::name).toList());
            assertEquals(0, cities.getLast().population());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Unknown Region", "Eastern Asia' OR 1=1 --"})
    void bindsTheTopCityRegionSeparatelyFromN(String value) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            assertTrue(service(connection).getTopCitiesInRegion(new Region(value), new TopN(5)).isEmpty());
        }
    }


    @ParameterizedTest
    @ValueSource(strings = {"USA", "United States"})
    void limitsCitiesInsideTheSelectedCountry(String value) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            assertEquals(List.of("Washington"), service(connection)
                    .getTopCitiesInCountry(new CountryFilter(value), new TopN(1))
                    .stream().map(City::name).toList());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {100, Integer.MAX_VALUE})
    void retainsAllMatchingCountryCitiesForLargeN(int count) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<City> cities = service(connection)
                    .getTopCitiesInCountry(new CountryFilter("USA"), new TopN(count));
            assertEquals(List.of("Washington", "Springfield"), cities.stream().map(City::name).toList());
            assertEquals(0, cities.getLast().population());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Unknown Country", "USA' OR 1=1 --"})
    void bindsTheTopCityCountrySeparatelyFromN(String value) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            assertTrue(service(connection).getTopCitiesInCountry(new CountryFilter(value), new TopN(5)).isEmpty());
        }
    }


    @Test
    void limitsSharedDistrictsAcrossCountriesOrInsideAQualifiedCountry() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            assertEquals(List.of("Shanghai"), service(connection)
                    .getTopCitiesInDistrict(new DistrictFilter("Shared District"), new TopN(1))
                    .stream().map(City::name).toList());
            assertEquals(List.of("Mumbai"), service(connection)
                    .getTopCitiesInDistrict(new DistrictFilter("Shared District", new CountryFilter("IND")), new TopN(1))
                    .stream().map(City::name).toList());
            assertTrue(service(connection)
                    .getTopCitiesInDistrict(new DistrictFilter("Shared District", new CountryFilter("United States")), new TopN(1))
                    .isEmpty());
            assertEquals(List.of(new City("Springfield", "United States", "Central", 0)), service(connection)
                    .getTopCitiesInDistrict(new DistrictFilter("Central", new CountryFilter("USA")), new TopN(1)));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {100, Integer.MAX_VALUE})
    void retainsAllMatchingDistrictCitiesForLargeN(int count) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            List<City> cities = service(connection)
                    .getTopCitiesInDistrict(new DistrictFilter("Central"), new TopN(count));
            assertEquals(List.of("Brazil", "United States"), cities.stream().map(City::country).toList());
            assertEquals(List.of("Springfield", "Springfield"), cities.stream().map(City::name).toList());
            assertEquals(0, cities.getLast().population());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Unknown District", "Central' OR 1=1 --"})
    void bindsTheTopCityDistrictAsData(String value) throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            assertTrue(service(connection).getTopCitiesInDistrict(new DistrictFilter(value), new TopN(5)).isEmpty());
        }
    }

    @Test
    void sortsEqualDistrictPopulationsByCityIdBeforeTheLimit() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open();
             java.sql.PreparedStatement statement = connection.prepareStatement(
                     "UPDATE city SET District = ? WHERE ID = 4"))
        {
            statement.setString(1, "Berlin");
            statement.executeUpdate();
            assertEquals(List.of("Berlin"), service(connection)
                    .getTopCitiesInDistrict(new DistrictFilter("Berlin"), new TopN(1))
                    .stream().map(City::name).toList());
        }
    }

    private CityReportService service(Connection connection)
    {
        return new CityReportService(new CityRepository(connection));
    }
}
