package org.example.reports.country;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.example.database.CountryRepository;
import org.example.models.Continent;
import org.example.models.Country;
import org.example.models.Region;

/**
 * Country reporting, requirements 1-6. World, continent and region reports
 * (requirements 1-3) are implemented.
 */
public final class CountryReportService
{
    private final CountryRepository repository;

    public CountryReportService(CountryRepository repository)
    {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    /**
     * Requirement 1: every country, largest population first.
     */
    public List<Country> getCountriesInWorld() throws SQLException
    {
        return repository.findAll();
    }

    /**
     * Requirement 2: countries in the selected continent, largest first.
     */
    public List<Country> getCountriesInContinent(Continent continent) throws SQLException
    {
        return repository.findByContinent(continent);
    }

    /**
     * Requirement 3: countries in the selected region, largest first.
     */
    public List<Country> getCountriesInRegion(Region region) throws SQLException
    {
        return repository.findByRegion(region);
    }

    public void printCountriesInWorld(PrintStream output) throws SQLException
    {
        CountryReportFormatter.print(
                "All countries in the world by population (largest to smallest)",
                getCountriesInWorld(), output
        );
    }

    public void printCountriesInContinent(Continent continent, PrintStream output)
            throws SQLException
    {
        Objects.requireNonNull(continent, "continent");
        CountryReportFormatter.print(
                "All countries in " + continent.databaseName()
                        + " by population (largest to smallest)",
                getCountriesInContinent(continent), output
        );
    }

    public void printCountriesInRegion(Region region, PrintStream output)
            throws SQLException
    {
        Objects.requireNonNull(region, "region");
        CountryReportFormatter.print(
                "All countries in " + region.name()
                        + " by population (largest to smallest)",
                getCountriesInRegion(region), output
        );
    }
}
