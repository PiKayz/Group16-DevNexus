package org.example.reports.city;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.example.models.City;

/**
 * Shared console formatting for city reports; database values are not truncated.
 */
public final class CityReportFormatter
{
    private CityReportFormatter()
    {
    }

    public static void print(String title, List<City> cities, PrintStream output)
    {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(cities, "cities");
        Objects.requireNonNull(output, "output");
        output.println(title);

        if (cities.isEmpty())
        {
            output.println("No matching records found");
            return;
        }

        String rowFormat = "%-35s | %-52s | %-20s | %13s%n";
        output.printf(Locale.ROOT, rowFormat, "Name", "Country", "District", "Population");
        for (City city : cities)
        {
            String district = city.district() == null ? "N/A" : city.district();
            output.printf(Locale.ROOT, rowFormat,
                    city.name(), city.country(), district,
                    String.format(Locale.ROOT, "%,d", city.population()));
        }
        output.println("Total cities: " + cities.size());
    }
}
