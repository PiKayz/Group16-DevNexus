package org.example.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TopNTest
{
    @ParameterizedTest
    @ValueSource(ints = {1, 10, Integer.MAX_VALUE})
    void acceptsPositiveIntegers(int value)
    {
        assertEquals(value, TopN.parse(Integer.toString(value)).value());
    }

    @Test
    void trimsSurroundingWhitespace()
    {
        assertEquals(new TopN(5), TopN.parse(" 5 "));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "abc", "1.5", "0", "-1", "2147483648"})
    void rejectsInvalidCounts(String value)
    {
        assertThrows(IllegalArgumentException.class, () -> TopN.parse(value));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    void rejectsInvalidCountsPassedDirectly(int value)
    {
        assertThrows(IllegalArgumentException.class, () -> new TopN(value));
    }
}
