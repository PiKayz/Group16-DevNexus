package org.example;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest
{
    @Test
    void missingSettingUsesFallback()
    {
        String result = Main.getSetting(
                Map.of(),
                "DB_USER",
                "devnexus"
        );

        assertEquals("devnexus", result);
    }

    @Test
    void blankSettingUsesFallback()
    {
        String result = Main.getSetting(
                Map.of("DB_USER", "  "),
                "DB_USER",
                "devnexus"
        );

        assertEquals("devnexus", result);
    }

    @Test
    void configuredSettingIsUsed()
    {
        String result = Main.getSetting(
                Map.of("DB_USER", "testuser"),
                "DB_USER",
                "devnexus"
        );

        assertEquals("testuser", result);
    }
}