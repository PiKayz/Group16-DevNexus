package org.example.models;

/**
 * The six columns required for a country population report.
 * Capital is null when the database has no recorded capital city.
 */
public record Country(
        String code,
        String name,
        String continent,
        String region,
        long population,
        String capital)
{
}
