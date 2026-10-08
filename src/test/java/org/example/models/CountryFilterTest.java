package org.example.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CountryFilterTest
{
    @Test
    void trimsCodesAndNamesWithoutGuessingAnIdentifier()
    {
        assertEquals(new CountryFilter("usa"), new CountryFilter(" usa "));
        assertEquals("United States", new CountryFilter(" United States ").value());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsMissingCountries(String value)
    {
        assertThrows(IllegalArgumentException.class, () -> new CountryFilter(value));
    }
}
