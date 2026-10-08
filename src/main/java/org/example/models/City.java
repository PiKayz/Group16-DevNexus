package org.example.models;

/**
 * The four columns required for a city population report.
 */
public record City(String name, String country, String district, long population)
{
}
