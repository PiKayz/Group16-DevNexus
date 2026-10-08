package org.example.models;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The continent names supported by the World database.
 */
public enum Continent
{
    ASIA("Asia"),
    EUROPE("Europe"),
    NORTH_AMERICA("North America"),
    AFRICA("Africa"),
    OCEANIA("Oceania"),
    ANTARCTICA("Antarctica"),
    SOUTH_AMERICA("South America");

    private final String databaseName;

    Continent(String databaseName)
    {
        this.databaseName = databaseName;
    }

    public String databaseName()
    {
        return databaseName;
    }

    /**
     * Accept case differences and surrounding whitespace, then use the
     * canonical database name. Reject invalid names before connecting.
     */
    public static Continent parse(String value)
    {
        if (value != null)
        {
            String name = value.strip();

            for (Continent continent : values())
            {
                if (continent.databaseName.equalsIgnoreCase(name))
                {
                    return continent;
                }
            }
        }

        String choices = Arrays.stream(values())
                .map(Continent::databaseName)
                .collect(Collectors.joining(", "));
        throw new IllegalArgumentException("Choose a valid continent: " + choices);
    }
}
