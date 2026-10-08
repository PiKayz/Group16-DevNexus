package org.example.models;

/**
 * A country code or complete country name supplied by the report user.
 */
public record CountryFilter(String value)
{
    public CountryFilter
    {
        if (value == null || value.isBlank())
        {
            throw new IllegalArgumentException("Country must not be blank");
        }
        value = value.strip();
    }
}
