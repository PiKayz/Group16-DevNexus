package org.example.reports.city;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.example.database.CityRepository;
import org.example.models.City;
import org.example.models.ReportRequest;

/**
 * City reporting, requirements 7-16. Requirement 7 is implemented.
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

    public void print(ReportRequest request, PrintStream output) throws SQLException
    {
        Objects.requireNonNull(request, "request");
        switch (request.number())
        {
            case 7 -> CityReportFormatter.print(
                    "All cities in the world by population (largest to smallest)",
                    getCitiesInWorld(), output);
            default -> throw new IllegalArgumentException("Unsupported city report");
        }
    }
}
