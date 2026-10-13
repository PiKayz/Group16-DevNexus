package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

/**
 * City and non-city population breakdowns: requirements 23-25.
 * Assigned developer: Han Htal Sein.
 */
public class PopulationBreakdowns
{
    private final Connection con;

    public PopulationBreakdowns(Connection con)
    {
        if (con == null)
        {
            throw new IllegalArgumentException("Database connection is required");
        }
        this.con = con;
    }

    public void report23() throws SQLException
    {
        displayReport("Report 23: Population Breakdown by Continent", "Continent",
                buildQuery("c.Continent", "c.Continent"));
    }

    public void report24() throws SQLException
    {
        displayReport("Report 24: Population Breakdown by Region", "Region",
                buildQuery("c.Region", "c.Region"));
    }

    public void report25() throws SQLException
    {
        displayReport("Report 25: Population Breakdown by Country", "Country",
                buildQuery("c.Name", "c.Code, c.Name"));
    }

    // The city subquery returns one row per country to avoid counting
    // a country's population multiple times when it has several cities.
    private String buildQuery(String nameColumn, String groupColumns)
    {
        return "SELECT " + nameColumn + " AS area_name, "
                + "SUM(c.Population) AS total_population, "
                + "SUM(COALESCE(cp.city_population, 0)) AS city_population "
                + "FROM country c "
                + "LEFT JOIN ("
                + "SELECT CountryCode, SUM(Population) AS city_population "
                + "FROM city GROUP BY CountryCode"
                + ") cp ON cp.CountryCode = c.Code "
                + "GROUP BY " + groupColumns + " "
                + "ORDER BY total_population DESC, area_name ASC";
    }

    private void displayReport(String title, String areaLabel, String sql)
            throws SQLException
    {
        System.out.println("\n=== " + title + " ===");
        System.out.printf(Locale.ROOT, "%-40s %18s %18s %12s %18s %12s%n",
                areaLabel, "Total population", "In cities", "City %",
                "Outside cities", "Outside %");

        int rows = 0;
        try (Statement statement = con.createStatement();
             ResultSet result = statement.executeQuery(sql))
        {
            while (result.next())
            {
                String name = result.getString("area_name");
                long total = result.getLong("total_population");
                long city = result.getLong("city_population");
                // Keep the source figures visible, but do not present a negative
                // outside-city population or percentages above 100% as valid.
                if (city > total)
                {
                    System.out.printf(Locale.ROOT,
                            "%-40s %18d %18d %12s %18s %12s%n",
                            name, total, city, "N/A", "N/A", "N/A");
                    System.out.println("  Data inconsistency: recorded city population "
                            + "exceeds the reported total for " + name + ".");
                    rows++;
                    continue;
                }
                long outside = total - city;
                double cityPercent = total == 0 ? 0.0 : city * 100.0 / total;
                double outsidePercent = total == 0 ? 0.0 : outside * 100.0 / total;

                System.out.printf(Locale.ROOT,
                        "%-40s %18d %18d %11.2f%% %18d %11.2f%%%n",
                        name, total, city, cityPercent, outside, outsidePercent);
                rows++;
            }
        }

        if (rows == 0)
        {
            System.out.println("No data found");
        }
        System.out.println("Total rows: " + rows);
    }
}
