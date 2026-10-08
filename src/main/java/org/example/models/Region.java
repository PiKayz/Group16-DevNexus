package org.example.models;

/**
 * A nonblank region filter supplied by the user. Available names come from
 * the World database; a name with no matching countries produces an empty report.
 */
public record Region(String name)
{
    public Region
    {
        if (name == null || name.isBlank())
        {
            throw new IllegalArgumentException("Region must not be blank");
        }

        name = name.strip();
    }
}
