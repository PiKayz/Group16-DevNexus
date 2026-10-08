package org.example;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CountryReportTest
{
    @Test
    void displaysAllRequiredColumnsAndFullCountryAndCapitalNames()
    {
        String name = "South Georgia and the South Sandwich Islands";
        String capital = "A capital city with a long name";
        String report = render(List.of(new Country(
                "SGS", name, "Antarctica", "Antarctica", 1234567890L, capital
        )));

        String[] columns = report.lines().skip(1).findFirst().orElseThrow().split("\\|");
        assertEquals(6, columns.length);
        assertEquals("Code", columns[0].trim());
        assertEquals("Name", columns[1].trim());
        assertEquals("Continent", columns[2].trim());
        assertEquals("Region", columns[3].trim());
        assertEquals("Population", columns[4].trim());
        assertEquals("Capital", columns[5].trim());
        assertTrue(report.contains(name));
        assertTrue(report.contains(capital));
        assertTrue(report.contains("1,234,567,890"));
        assertTrue(report.contains("Total countries: 1"));
    }

    @Test
    void keepsCountriesWithoutCapitalsAndWithZeroPopulation()
    {
        String report = render(List.of(new Country(
                "ATA", "Antarctica", "Antarctica", "Antarctica", 0, null
        )));

        String[] columns = report.lines().skip(2).findFirst().orElseThrow().split("\\|");
        assertEquals("ATA", columns[0].trim());
        assertEquals("0", columns[4].trim());
        assertEquals("N/A", columns[5].trim());
        assertFalse(report.contains("null"));
    }

    @Test
    void explainsWhenNoCountriesAreReturned()
    {
        String report = render(List.of());

        assertTrue(report.contains("No matching records found"));
        assertFalse(report.contains("Total countries:"));
    }

    @Test
    void usesConsistentPopulationFormattingAcrossComputerLocales()
    {
        Locale original = Locale.getDefault();

        try
        {
            Locale.setDefault(Locale.GERMANY);
            String report = render(List.of(new Country(
                    "CHN", "China", "Asia", "Eastern Asia", 1277558000L, "Peking"
            )));
            assertTrue(report.contains("1,277,558,000"));
        }
        finally
        {
            Locale.setDefault(original);
        }
    }

    @Test
    void refusesToQueryWithoutADatabaseConnection()
    {
        SQLException error = assertThrows(SQLException.class,
                () -> CountryReport.getAllCountries(null));

        assertEquals("Not connected to the database", error.getMessage());
    }

    private String render(List<Country> countries)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        try (PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8))
        {
            CountryReport.printCountries(countries, output);
        }

        return bytes.toString(StandardCharsets.UTF_8);
    }
}
