package org.example.reports.report02;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.example.reports.common.Continent;
import org.example.reports.common.Country;
import org.example.reports.common.CountryReportFormatter;
import org.example.reports.common.CountryRepository;

/**
 * Report 2: all countries in a selected continent, largest population first.
 */
public final class CountriesInContinentReport
{
    private final CountryRepository repository;

    public CountriesInContinentReport(CountryRepository repository)
    {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Country> generate(Continent continent) throws SQLException
    {
        return repository.findByContinent(continent);
    }

    public void print(Continent continent, PrintStream output) throws SQLException
    {
        Objects.requireNonNull(continent, "continent");
        CountryReportFormatter.print(
                "All countries in " + continent.databaseName()
                        + " by population (largest to smallest)",
                generate(continent), output
        );
    }
}
