package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Capital city reports: requirements 17-22.
 * Assigned developer: Hein Phyo.
 *
 * Every report shows the columns: Name, Country, Population,
 * sorted by population from largest to smallest.
 */
public class CapitalReports {

    private final Connection con;

    // Capital cities are identified through country.Capital = city.ID
    private static final String BASE_QUERY =
            "SELECT city.Name AS Name, country.Name AS Country, city.Population AS Population " +
            "FROM city JOIN country ON country.Capital = city.ID ";
    private static final String ORDER_BY = "ORDER BY city.Population DESC";

    public CapitalReports(final Connection con) {
        this.con = con;
    }

    /**
     * Runs a capital city query and prints the result with the columns
     * Name, Country, Population.
     */
    private void printCapitalReport(final String reportName, final String query,
                                    final List<Object> params) {
        System.out.println("=== " + reportName + " ===");
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet result = pstmt.executeQuery()) {
                System.out.printf("%-30s %-45s %-15s%n", "Name", "Country", "Population");
                System.out.println("-".repeat(92));
                boolean found = false;
                while (result.next()) {
                    found = true;
                    System.out.printf("%-30s %-45s %-15d%n",
                            result.getString("Name"),
                            result.getString("Country"),
                            result.getInt("Population"));
                }
                if (!found) {
                    System.out.println("No matching records found");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error executing report: " + e.getMessage());
        }
        System.out.println();
    }

    private boolean validText(final String value, final String label) {
        final boolean valid = value != null && !value.isBlank();
        if (!valid) {
            System.out.println("Invalid input: " + label + " must not be empty.");
        }
        return valid;
    }

    private boolean validN(final int limit) {
        final boolean valid = limit > 0;
        if (!valid) {
            System.out.println("Invalid input: N must be a positive integer.");
        }
        return valid;
    }

    // 17. All capital cities in the world, largest to smallest population
    public void report17() {
        final String query = BASE_QUERY + ORDER_BY;
        printCapitalReport("Report 17: All Capital Cities in the World by Population",
                query, List.of());
    }

    // 18. All capital cities in a continent, largest to smallest population
    public void report18(final String continent) {
        if (validText(continent, "Continent")) {
            final String query = BASE_QUERY + "WHERE country.Continent = ? " + ORDER_BY;
            printCapitalReport("Report 18: Capital Cities in Continent (" + continent + ") by Population",
                    query, List.of(continent));
        }
    }

    // 19. All capital cities in a region, largest to smallest population
    public void report19(final String region) {
        if (validText(region, "Region")) {
            final String query = BASE_QUERY + "WHERE country.Region = ? " + ORDER_BY;
            printCapitalReport("Report 19: Capital Cities in Region (" + region + ") by Population",
                    query, List.of(region));
        }
    }

    // 20. Top N populated capital cities in the world
    public void report20(final int limit) {
        if (validN(limit)) {
            final String query = BASE_QUERY + ORDER_BY + " LIMIT ?";
            printCapitalReport("Report 20: Top " + limit + " Capital Cities in the World",
                    query, List.of(limit));
        }
    }

    // 21. Top N populated capital cities in a continent
    public void report21(final String continent, final int limit) {
        if (validText(continent, "Continent") && validN(limit)) {
            final String query = BASE_QUERY + "WHERE country.Continent = ? " + ORDER_BY + " LIMIT ?";
            printCapitalReport("Report 21: Top " + limit + " Capital Cities in Continent (" + continent + ")",
                    query, List.of(continent, limit));
        }
    }

    // 22. Top N populated capital cities in a region
    public void report22(final String region, final int limit) {
        if (validText(region, "Region") && validN(limit)) {
            final String query = BASE_QUERY + "WHERE country.Region = ? " + ORDER_BY + " LIMIT ?";
            printCapitalReport("Report 22: Top " + limit + " Capital Cities in Region (" + region + ")",
                    query, List.of(region, limit));
        }
    }
}
