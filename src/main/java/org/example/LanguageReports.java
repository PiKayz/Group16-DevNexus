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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Language speaker populations: requirement 32.
 * Assigned developer: Win Aung.
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class LanguageReports
{
    /** Languages required by the report. */
    private static final List<String> LANGUAGES =
            List.of("Chinese", "English", "Hindi", "Spanish", "Arabic");

    /** Used to convert percentages into fractions. */
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    /**
     * Report 32: estimated speakers of Chinese, English, Hindi, Spanish and
     * Arabic, and each language's percentage of the world population.
     * Speakers = country population x language percentage / 100, summed over
     * all countries. Values are rounded only when printed.
     *
     * @param con open database connection
     * @throws SQLException if a query fails
     */
    public void printLanguageSpeakerReport(final Connection con) throws SQLException
    {
        final Map<String, BigDecimal> speakers = new ConcurrentHashMap<>();
        final Map<String, Boolean> hasData = new ConcurrentHashMap<>();

        for (final String language : LANGUAGES)
        {
            speakers.put(language, BigDecimal.ZERO);
            hasData.put(language, false);
        }

        final String sql =
                "SELECT countrylanguage.Language, country.Population, countrylanguage.Percentage "
                        + "FROM countrylanguage JOIN country "
                        + "ON countrylanguage.CountryCode = country.Code "
                        + "WHERE countrylanguage.Language IN (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            for (int index = 0; index < LANGUAGES.size(); index++)
            {
                stmt.setString(index + 1, LANGUAGES.get(index));
            }

            try (ResultSet result = stmt.executeQuery())
            {
                while (result.next())
                {
                    final String language = result.getString("Language");
                    final BigDecimal population = BigDecimal.valueOf(result.getLong("Population"));
                    final BigDecimal percentage = result.getBigDecimal("Percentage");

                    final BigDecimal estimate = population.multiply(percentage)
                            .divide(HUNDRED);

                    speakers.merge(language, estimate, BigDecimal::add);
                    hasData.put(language, true);
                }
            }
        }

        BigDecimal worldPopulation = BigDecimal.ZERO;

        try (Statement stmt = con.createStatement();
             ResultSet result = stmt.executeQuery("SELECT SUM(Population) FROM country"))
        {
            if (result.next() && result.getBigDecimal(1) != null)
            {
                worldPopulation = result.getBigDecimal(1);
            }
        }

        final List<String> sorted = new ArrayList<>(LANGUAGES);
        sorted.sort(Comparator.comparing((String lang) -> speakers.get(lang)).reversed()
                .thenComparing(Comparator.naturalOrder()));

        System.out.println();
        System.out.println("Report 32 - Language speaker report");
        System.out.println(String.format("%-12s %20s %28s",
                "Language", "Estimated Speakers", "Percentage of World Population"));

        for (final String language : sorted)
        {
            final BigDecimal count = speakers.get(language);

            final String percentage = worldPopulation.signum() == 0
                    ? "N/A"
                    : count.multiply(HUNDRED)
                    .divide(worldPopulation, 2, RoundingMode.HALF_UP)
                    .toPlainString() + "%";

            final String line = String.format("%-12s %,20d %28s",
                    language,
                    count.setScale(0, RoundingMode.HALF_UP).toBigInteger(),
                    percentage);

            final String missing = hasData.get(language) ? "" : "  (data missing)";

            System.out.println(line + missing);
        }

        System.out.println("Note: speaker groups overlap, so the percentages must not be added together.");
    }
}