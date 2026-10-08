package org.example.reports.report01;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.example.reports.common.Country;
import org.example.reports.common.CountryReportFormatter;
import org.example.reports.common.CountryRepository;

/**
 * Report 1: all countries in the world, largest population first.
 */
public final class CountriesInWorldReport
{
    private final CountryRepository repository;

    public CountriesInWorldReport(CountryRepository repository)
    {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Country> generate() throws SQLException
    {
        return repository.findAll();
    }

    public void print(PrintStream output) throws SQLException
    {
        CountryReportFormatter.print(
                "All countries in the world by population (largest to smallest)",
                generate(), output
        );
    }
}
