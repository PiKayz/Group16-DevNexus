package org.example.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegionTest
{
    @Test
    void trimsSurroundingWhitespace()
    {
        assertEquals("Eastern Asia", new Region(" Eastern Asia ").name());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void rejectsMissingRegion(String value)
    {
        assertThrows(IllegalArgumentException.class, () -> new Region(value));
    }
}
