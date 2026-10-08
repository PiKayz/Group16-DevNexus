package org.example.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DistrictFilterTest
{
    @Test
    void labelsBothUnqualifiedAndCountryQualifiedDistricts()
    {
        assertEquals("Central (all countries)", new DistrictFilter(" Central ").label());
        assertEquals("Central (USA)", new DistrictFilter("Central", new CountryFilter("USA")).label());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsMissingDistricts(String value)
    {
        assertThrows(IllegalArgumentException.class, () -> new DistrictFilter(value));
    }
}
