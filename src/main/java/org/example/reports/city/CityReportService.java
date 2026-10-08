package org.example.reports.city;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.example.database.CityRepository;
import org.example.models.City;
import org.example.models.Continent;
import org.example.models.CountryFilter;
import org.example.models.DistrictFilter;
import org.example.models.Region;
import org.example.models.ReportRequest;
import org.example.models.TopN;

/**
 * All ten city reports, requirements 7-16, using shared queries and formatting.
 */
public final class CityReportService
{
    private final CityRepository repository;

    public CityReportService(CityRepository repository)
    {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    /** Requirement 7: all cities in the world, largest population first. */
    public List<City> getCitiesInWorld() throws SQLException
    {
        return repository.findAll();
    }

    /** Requirement 8: all cities in a continent. */
    public List<City> getCitiesInContinent(Continent continent) throws SQLException
    {
        return repository.findByContinent(continent);
    }

    /** Requirement 9: all cities in a region. */
    public List<City> getCitiesInRegion(Region region) throws SQLException
    {
        return repository.findByRegion(region);
    }

    /** Requirement 10: all cities in a country. */
    public List<City> getCitiesInCountry(CountryFilter country) throws SQLException
    {
        return repository.findByCountry(country);
    }

    /** Requirement 11: all cities in a district. */
    public List<City> getCitiesInDistrict(DistrictFilter district) throws SQLException
    {
        return repository.findByDistrict(district);
    }

    /** Requirement 12: Top N cities in the world. */
    public List<City> getTopCitiesInWorld(TopN topN) throws SQLException
    {
        return repository.findTopInWorld(topN);
    }

    /** Requirement 13: Top N cities in a continent. */
    public List<City> getTopCitiesInContinent(Continent continent, TopN topN) throws SQLException
    {
        return repository.findTopByContinent(continent, topN);
    }

    /** Requirement 14: Top N cities in a region. */
    public List<City> getTopCitiesInRegion(Region region, TopN topN) throws SQLException
    {
        return repository.findTopByRegion(region, topN);
    }

    /** Requirement 15: Top N cities in a country. */
    public List<City> getTopCitiesInCountry(CountryFilter country, TopN topN) throws SQLException
    {
        return repository.findTopByCountry(country, topN);
    }

    /** Requirement 16: Top N cities in a district. */
    public List<City> getTopCitiesInDistrict(DistrictFilter district, TopN topN) throws SQLException
    {
        return repository.findTopByDistrict(district, topN);
    }

    public void print(ReportRequest request, PrintStream output) throws SQLException
    {
        Objects.requireNonNull(request, "request");
        switch (request.number())
        {
            case 7 -> CityReportFormatter.print(
                    "All cities in the world by population (largest to smallest)",
                    getCitiesInWorld(), output);
            case 8 -> CityReportFormatter.print(
                    "All cities in " + request.continent().databaseName() + " by population (largest to smallest)",
                    getCitiesInContinent(request.continent()), output);
            case 9 -> CityReportFormatter.print(
                    "All cities in " + request.region().name() + " by population (largest to smallest)",
                    getCitiesInRegion(request.region()), output);
            case 10 -> CityReportFormatter.print(
                    "All cities in " + request.country().value() + " by population (largest to smallest)",
                    getCitiesInCountry(request.country()), output);
            case 11 -> CityReportFormatter.print(
                    "All cities in district " + request.district().label() + " by population (largest to smallest)",
                    getCitiesInDistrict(request.district()), output);
            case 12 -> CityReportFormatter.print(
                    "Top " + request.topN().value() + " cities in the world by population (largest to smallest)",
                    getTopCitiesInWorld(request.topN()), output);
            case 13 -> CityReportFormatter.print(
                    "Top " + request.topN().value() + " cities in " + request.continent().databaseName() + " by population (largest to smallest)",
                    getTopCitiesInContinent(request.continent(), request.topN()), output);
            case 14 -> CityReportFormatter.print(
                    "Top " + request.topN().value() + " cities in " + request.region().name() + " by population (largest to smallest)",
                    getTopCitiesInRegion(request.region(), request.topN()), output);
            case 15 -> CityReportFormatter.print(
                    "Top " + request.topN().value() + " cities in " + request.country().value() + " by population (largest to smallest)",
                    getTopCitiesInCountry(request.country(), request.topN()), output);
            case 16 -> CityReportFormatter.print(
                    "Top " + request.topN().value() + " cities in district " + request.district().label() + " by population (largest to smallest)",
                    getTopCitiesInDistrict(request.district(), request.topN()), output);
            default -> throw new IllegalArgumentException("Unsupported city report");
        }
    }
}
