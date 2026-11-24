package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// PMD CheckResultSet: Mockito stubs and assertions already control the next() result.
@SuppressWarnings("PMD.CheckResultSet")
class LanguageReportsTest
{
    private Connection connection;
    private PreparedStatement languageStatement;
    private ResultSet languageRows;
    private Statement totalStatement;
    private ResultSet totalRows;
    private LanguageReports reports;

    private PrintStream originalOut;
    private PrintStream testOut;
    private ByteArrayOutputStream capturedOutput;

    @BeforeEach
    void setUp() throws SQLException
    {
        connection = mock(Connection.class);
        languageStatement = mock(PreparedStatement.class);
        languageRows = mock(ResultSet.class);
        totalStatement = mock(Statement.class);
        totalRows = mock(ResultSet.class);
        reports = new LanguageReports();

        when(connection.prepareStatement(anyString()))
                .thenReturn(languageStatement);
        when(languageStatement.executeQuery())
                .thenReturn(languageRows);
        when(connection.createStatement())
                .thenReturn(totalStatement);
        when(totalStatement.executeQuery(
                "SELECT SUM(Population) FROM country"
        )).thenReturn(totalRows);

        originalOut = System.out;
        capturedOutput = new ByteArrayOutputStream();
        testOut = new PrintStream(
                capturedOutput, true, StandardCharsets.UTF_8
        );
        System.setOut(testOut);
    }

    @AfterEach
    void tearDown()
    {
        System.setOut(originalOut);
        testOut.close();
    }

    @Test
    void calculatesSpeakersAndSortsLargestFirst() throws SQLException
    {
        // Chinese: 1,000 × 50% = 500 speakers.
        // English: 200 × 25% = 50 speakers.
        when(languageRows.next()).thenReturn(true, true, false);
        when(languageRows.getString("Language"))
                .thenReturn("Chinese", "English");
        when(languageRows.getLong("Population"))
                .thenReturn(1000L, 200L);
        when(languageRows.getBigDecimal("Percentage"))
                .thenReturn(
                        new BigDecimal("50"),
                        new BigDecimal("25")
                );

        when(totalRows.next()).thenReturn(true);
        when(totalRows.getBigDecimal(1))
                .thenReturn(new BigDecimal("1000"));

        reports.printLanguageSpeakerReport(connection);

        String output = output();
        assertTrue(output.contains("Report 32: Language Speaker Populations"));
        assertTrue(output.matches("(?s).*Chinese\\s+500\\s+50\\.00%.*"));
        assertTrue(output.matches("(?s).*English\\s+50\\s+5\\.00%.*"));
        assertTrue(output.indexOf("\nChinese") < output.indexOf("\nEnglish"));

        verify(languageStatement).setString(1, "Chinese");
        verify(languageStatement).setString(2, "English");
        verify(languageStatement).setString(3, "Hindi");
        verify(languageStatement).setString(4, "Spanish");
        verify(languageStatement).setString(5, "Arabic");

        verify(languageRows).close();
        verify(languageStatement).close();
        verify(totalRows).close();
        verify(totalStatement).close();
        verify(connection, never()).close();
    }

    @Test
    void missingLanguagesAreMarkedAndZeroWorldPopulationShowsNA()
            throws SQLException
    {
        when(languageRows.next()).thenReturn(false);
        when(totalRows.next()).thenReturn(true);
        when(totalRows.getBigDecimal(1))
                .thenReturn(BigDecimal.ZERO);

        reports.printLanguageSpeakerReport(connection);

        String output = output();
        assertTrue(output.contains("Report 32:"));
        assertTrue(output.contains("Chinese"));
        assertTrue(output.contains("English"));
        assertTrue(output.contains("Hindi"));
        assertTrue(output.contains("Spanish"));
        assertTrue(output.contains("Arabic"));
        assertTrue(output.contains("N/A  (data missing)"));
        assertFalse(output.contains("Infinity"));
    }

    @Test
    void databaseQueryFailurePropagatesAndClosesStatement()
            throws SQLException
    {
        SQLException failure = new SQLException("Database unavailable");
        when(languageStatement.executeQuery()).thenThrow(failure);

        SQLException actual = assertThrows(
                SQLException.class,
                () -> reports.printLanguageSpeakerReport(connection)
        );

        assertSame(failure, actual);
        verify(languageStatement).close();
        verify(connection, never()).close();
    }

    private String output()
    {
        testOut.flush();
        return capturedOutput.toString(StandardCharsets.UTF_8);
    }
}