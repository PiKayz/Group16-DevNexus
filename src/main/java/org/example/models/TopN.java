package org.example.models;

/**
 * A validated positive row limit shared by Top N reports.
 */
public record TopN(int value)
{
    public TopN
    {
        if (value <= 0)
        {
            throw new IllegalArgumentException("N must be a positive integer");
        }
    }

    public static TopN parse(String value)
    {
        if (value == null || value.isBlank())
        {
            throw new IllegalArgumentException("N must be a positive integer");
        }

        try
        {
            return new TopN(Integer.parseInt(value.strip()));
        }
        catch (NumberFormatException error)
        {
            throw new IllegalArgumentException(
                    "N must be a positive integer no greater than " + Integer.MAX_VALUE,
                    error
            );
        }
    }
}
