package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Language speaker populations: requirement 32.
 * Assigned developer: Win Aung.
 */
public class LanguageReports
{
    private static final List<String> LANGUAGES =
            List.of("Chinese", "English", "Hindi", "Spanish", "Arabic");

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    /** Creates a language report generator. */
    public LanguageReports()
    {
        // Stateless: the connection is passed to each report method.
    }

    public void printLanguageSpeakerReport(final Connection con) throws SQLException
    {
        final Map<String, BigDecimal> speakers = new LinkedHashMap<>();
        final Map<String, Boolean> dataFound = new LinkedHashMap<>();

        for (final String language : LANGUAGES)
        {
            speakers.put(language, BigDecimal.ZERO);
            dataFound.put(language, false);
        }

        final String sql = "SELECT countrylanguage.Language, country.Population, "
                + "countrylanguage.Percentage "
                + "FROM countrylanguage JOIN country "
                + "ON countrylanguage.CountryCode = country.Code "
                + "WHERE countrylanguage.Language IN (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            for (int i = 0; i < LANGUAGES.size(); i++)
            {
                stmt.setString(i + 1, LANGUAGES.get(i));
            }

            try (ResultSet result = stmt.executeQuery())
            {
                while (result.next())
                {
                    final String language = result.getString("Language");
                    final BigDecimal population =
                            BigDecimal.valueOf(result.getLong("Population"));
                    final BigDecimal percentage = result.getBigDecimal("Percentage");

                    final BigDecimal estimate = population.multiply(percentage)
                            .divide(HUNDRED);

                    speakers.merge(language, estimate, BigDecimal::add);
                    dataFound.put(language, true);
                }
            }
        }

        BigDecimal worldPopulation = BigDecimal.ZERO;

        try (Statement stmt = con.createStatement();
             ResultSet result = stmt.executeQuery("SELECT SUM(Population) FROM country"))
        {
            if (result.next())
            {
                final BigDecimal total = result.getBigDecimal(1);
                if (total != null)
                {
                    worldPopulation = total;
                }
            }
        }

        final List<String> sorted = new ArrayList<>(LANGUAGES);
        sorted.sort(Comparator.<String, BigDecimal>comparing(speakers::get)
                .reversed()
                .thenComparing(Comparator.naturalOrder()));

        System.out.println("\nReport 32: Language Speaker Populations");
        System.out.printf(Locale.ROOT, "%-12s %20s %30s%n",
                "Language", "Estimated Speakers", "Percentage of World Population");

        for (final String language : sorted)
        {
            final BigDecimal count = speakers.get(language);

            final String percentage = worldPopulation.signum() == 0
                    ? "N/A"
                    : count.multiply(HUNDRED)
                    .divide(worldPopulation, 2, RoundingMode.HALF_UP)
                    .toPlainString() + "%";

            String line = String.format(Locale.ROOT, "%-12s %,20d %30s",
                    language,
                    count.setScale(0, RoundingMode.HALF_UP).toBigInteger(),
                    percentage);

            if (!dataFound.get(language))
            {
                line += "  (data missing)";
            }

            System.out.println(line);
        }

        System.out.println(
                "Note: speaker groups overlap, so the percentages must not be added together."
        );
    }
}
