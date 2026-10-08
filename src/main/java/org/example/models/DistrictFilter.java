package org.example.models;

/**
 * A district name, optionally qualified by country. Without a qualifier,
 * matching districts in every country are included and explicitly labelled.
 */
public record DistrictFilter(String name, CountryFilter country)
{
    public DistrictFilter(String name)
    {
        this(name, null);
    }

    public DistrictFilter
    {
        if (name == null || name.isBlank())
        {
            throw new IllegalArgumentException("District must not be blank");
        }
        name = name.strip();
    }

    public String label()
    {
        return name + (country == null ? " (all countries)" : " (" + country.value() + ")");
    }
}
