package org.example.reports.common;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Console output shared by country reports, independent of database access.
 */
public final class CountryReportFormatter
{
    private CountryReportFormatter()
    {
    }

    /**
     * Display the required columns without truncating database values.
     */
    public static void print(String title, List<Country> countries, PrintStream output)
    {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(countries, "countries");
        Objects.requireNonNull(output, "output");
        output.println(title);

        if (countries.isEmpty())
        {
            output.println("No matching records found");
            return;
        }

        String rowFormat = "%-4s | %-52s | %-13s | %-26s | %13s | %-35s%n";
        output.printf(Locale.ROOT, rowFormat,
                "Code", "Name", "Continent", "Region", "Population", "Capital");

        for (Country country : countries)
        {
            String capital = country.capital() == null ? "N/A" : country.capital();
            String population = String.format(Locale.ROOT, "%,d", country.population());

            output.printf(Locale.ROOT, rowFormat,
                    country.code(), country.name(), country.continent(),
                    country.region(), population, capital);
        }

        output.println("Total countries: " + countries.size());
    }
}
