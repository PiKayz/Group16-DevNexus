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

    @ParameterizedTest
    @ValueSource(strings = {"5", "report05"})
    void acceptsContinentTopNReports(String command)
    {
        assertEquals(new ReportRequest(5, Continent.SOUTH_AMERICA, null, new TopN(10)),
                ReportRequest.parse(new String[]{command, "south america", "10"}));
    }

    @Test
    void requiresAValidContinentAndExactlyOneValidCount()
    {
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report05", "Asia"}));
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report05", "Atlantis", "5"}));
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report05", "Asia", "1.5"}));
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report05", "Asia", "5", "extra"}));
    }

    @ParameterizedTest
    @ValueSource(strings = {"6", "report06"})
    void acceptsRegionTopNReports(String command)
    {
        assertEquals(new ReportRequest(6, null, new Region("Eastern Asia"), new TopN(10)),
                ReportRequest.parse(new String[]{command, " Eastern Asia ", "10"}));
    }

    @Test
    void requiresExactlyOneNonblankRegionAndValidCount()
    {
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report06", "Eastern Asia"}));
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report06", " ", "5"}));
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report06", "Eastern Asia", "-1"}));
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{"report06", "Eastern Asia", "5", "extra"}));
    }

    @Test
    void rejectsRequestsWhoseFiltersDoNotBelongToTheSelectedReport()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new ReportRequest(4, Continent.ASIA, null, new TopN(5)));
        assertThrows(IllegalArgumentException.class,
                () -> new ReportRequest(5, Continent.ASIA, new Region("Eastern Asia"), new TopN(5)));
        assertThrows(IllegalArgumentException.class,
                () -> new ReportRequest(6, null, null, new TopN(5)));
        assertThrows(IllegalArgumentException.class,
                () -> new ReportRequest(3, null, new Region("Eastern Asia"), new TopN(5)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"7", "report07"})
    void acceptsWorldCityReportsWithoutFiltersOrLimits(String command)
    {
        assertEquals(new ReportRequest(7, null), ReportRequest.parse(new String[]{command}));
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{command, "5"}));
    }

    @ParameterizedTest
    @ValueSource(strings = {"8", "report08"})
    void acceptsContinentCityReports(String command)
    {
        assertEquals(new ReportRequest(8, Continent.SOUTH_AMERICA),
                ReportRequest.parse(new String[]{command, " south america "}));
        assertThrows(IllegalArgumentException.class,
                () -> ReportRequest.parse(new String[]{command, "Atlantis"}));
    }

}
