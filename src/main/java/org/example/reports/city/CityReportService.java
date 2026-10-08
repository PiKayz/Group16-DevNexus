package org.example.reports.city;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.example.database.CityRepository;
import org.example.models.City;
import org.example.models.CountryFilter;
import org.example.models.Region;
import org.example.models.Continent;
import org.example.models.ReportRequest;

/**
 * City reporting, requirements 7-16. Requirements 7-10 are implemented.
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
            default -> throw new IllegalArgumentException("Unsupported city report");
        }
    }
}
