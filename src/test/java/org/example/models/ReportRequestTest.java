package org.example.models;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportRequestTest
{
    @Test
    void preservesWorldReportAsDefault()
    {
        assertEquals(new ReportRequest(1, null), ReportRequest.parse(new String[0]));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "report01"})
    void acceptsExplicitWorldReport(String command)
    {
        assertEquals(new ReportRequest(1, null),
                ReportRequest.parse(new String[]{command}));
    }

    @ParameterizedTest
    @EnumSource(Continent.class)
    void acceptsAllContinentNamesIncludingNamesWithSpaces(Continent continent)
    {
        assertEquals(new ReportRequest(2, continent), ReportRequest.parse(
                new String[]{"report02", continent.databaseName()}));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Asia", "ASIA", " asia "})
    void normalisesContinentCaseAndSurroundingWhitespace(String name)
    {
        assertEquals(new ReportRequest(2, Continent.ASIA),
                ReportRequest.parse(new String[]{"2", name}));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "Atlantis", "Asia' OR 1=1 --"})
    void rejectsInvalidContinentNames(String name)
    {
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report02", name}));
    }

    @Test
    void rejectsMissingContinentAndUnexpectedArguments()
    {
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report02"}));
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report02", "South", "America"}));
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report01", "Asia"}));
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report03"}));
    }

    @ParameterizedTest
    @ValueSource(strings = {"3", "report03"})
    void acceptsRegionReports(String command)
    {
        assertEquals(new ReportRequest(3, null, new Region("Eastern Asia")),
                ReportRequest.parse(new String[]{command, " Eastern Asia "}));
    }

    @Test
    void rejectsBlankRegionsAndUnexpectedRegionArguments()
    {
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report03", " "}));
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report03", "Eastern", "Asia"}));
    }

    @ParameterizedTest
    @ValueSource(strings = {"4", "report04"})
    void acceptsWorldTopNReports(String command)
    {
        assertEquals(new ReportRequest(4, null, null, new TopN(5)),
                ReportRequest.parse(new String[]{command, "5"}));
    }

    @Test
    void requiresExactlyOneValidCountForWorldTopNReports()
    {
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report04"}));
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report04", "5", "extra"}));
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report04", "0"}));
    }
}
