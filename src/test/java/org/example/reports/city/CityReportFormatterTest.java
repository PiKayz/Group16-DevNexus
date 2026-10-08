package org.example.reports.city;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.example.models.City;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CityReportFormatterTest
{
    @Test
    void displaysAllFourColumnsAndFullValues()
    {
        String name = "An exceptionally long city name that remains visible";
        String country = "South Georgia and the South Sandwich Islands";
        String text = render(List.of(new City(name, country, "A long district name", 1234567890L)));
        String[] headings = text.lines().skip(1).findFirst().orElseThrow().split("\\|");
        assertEquals(4, headings.length);
        assertEquals("Name", headings[0].trim());
        assertEquals("Country", headings[1].trim());
        assertEquals("District", headings[2].trim());
        assertEquals("Population", headings[3].trim());
        assertTrue(text.contains(name));
        assertTrue(text.contains(country));
        assertTrue(text.contains("1,234,567,890"));
        assertTrue(text.contains("Total cities: 1"));
    }

    @Test
    void retainsZeroPopulationAndBlankDistricts()
    {
        String text = render(List.of(new City("Springfield", "United States", "", 0)));
        String[] values = text.lines().skip(2).findFirst().orElseThrow().split("\\|");
        assertEquals("", values[2].trim());
        assertEquals("0", values[3].trim());
        assertTrue(text.contains("Springfield"));
    }

    @Test
    void explainsAnEmptyReport()
    {
        String text = render(List.of());
        assertTrue(text.contains("No matching records found"));
        assertFalse(text.contains("Total cities:"));
    }

    private String render(List<City> cities)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8))
        {
            CityReportFormatter.print("City population report", cities, output);
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }
}
