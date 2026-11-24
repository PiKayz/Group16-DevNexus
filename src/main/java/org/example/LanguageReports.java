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
    /** Languages included in the speaker population report. */
    private static final List<String> LANGUAGES =
            List.of("Chinese", "English", "Hindi", "Spanish", "Arabic");

    /** Scale factor for converting speaker ratios to percentages. */
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    /** Prints estimated speakers and world-population percentages. */
    public void printLanguageSpeakerReport(Connection con) throws SQLException
    {
        Map<String, BigDecimal> speakers = new LinkedHashMap<>();
        Map<String, Boolean> hasData = new LinkedHashMap<>();

        for (String language : LANGUAGES)
        {
            speakers.put(language, BigDecimal.ZERO);
            hasData.put(language, false);
        }

        String sql = "SELECT countrylanguage.Language, country.Population, "
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
                    String language = result.getString("Language");
                    BigDecimal population =
                            BigDecimal.valueOf(result.getLong("Population"));
                    BigDecimal percentage = result.getBigDecimal("Percentage");

                    BigDecimal estimate = population.multiply(percentage)
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
            if (result.next())
            {
                BigDecimal total = result.getBigDecimal(1);
                if (total != null)
                {
                    worldPopulation = total;
                }
            }
        }

        List<String> sorted = new ArrayList<>(LANGUAGES);
        sorted.sort(Comparator.comparing((String language) -> speakers.get(language))
                .reversed()
                .thenComparing(Comparator.naturalOrder()));

        System.out.println("\nReport 32: Language Speaker Populations");
        System.out.printf(Locale.ROOT, "%-12s %20s %30s%n",
                "Language", "Estimated Speakers", "Percentage of World Population");

        for (String language : sorted)
        {
            BigDecimal count = speakers.get(language);

            String percentage = worldPopulation.signum() == 0
                    ? "N/A"
                    : count.multiply(HUNDRED)
                    .divide(worldPopulation, 2, RoundingMode.HALF_UP)
                    .toPlainString() + "%";

            String line = String.format(Locale.ROOT, "%-12s %,20d %30s",
                    language,
                    count.setScale(0, RoundingMode.HALF_UP).toBigInteger(),
                    percentage);

            if (!hasData.get(language))
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
