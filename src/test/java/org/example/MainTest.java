package org.example;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    @Test
    void databaseCheckRequiresConnection()
    {
        SQLException error = assertThrows(
                SQLException.class, new Main()::checkWorldDatabase
        );
        assertEquals("Not connected to the database", error.getMessage());
    }

    @Test
    void databaseCheckRejectsMissingResult() throws Exception
    {
        Main app = new Main();
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ResultSet result = mock(ResultSet.class);
        setConnection(app, connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(result);
        when(result.next()).thenReturn(false);

        SQLException error = assertThrows(
                SQLException.class, app::checkWorldDatabase
        );
        assertEquals("Database check returned no result", error.getMessage());
        verify(result).close();
        verify(statement).close();
    }

    @Test
    void databaseCheckRejectsEmptyTables() throws Exception
    {
        Main app = new Main();
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ResultSet result = mock(ResultSet.class);
        setConnection(app, connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(result);
        when(result.next()).thenReturn(true);
        when(result.getInt("country_count")).thenReturn(1);
        when(result.getInt("city_count")).thenReturn(0);
        when(result.getInt("language_count")).thenReturn(1);

        SQLException error = assertThrows(
                SQLException.class, app::checkWorldDatabase
        );
        assertEquals(
                "World database contains an empty required table",
                error.getMessage()
        );
    }

    @Test
    void databaseCheckAcceptsPopulatedWorldTables() throws Exception
    {
        Main app = new Main();
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ResultSet result = mock(ResultSet.class);
        setConnection(app, connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(result);
        when(result.next()).thenReturn(true);
        when(result.getInt("country_count")).thenReturn(239);
        when(result.getInt("city_count")).thenReturn(4079);
        when(result.getInt("language_count")).thenReturn(984);

        assertDoesNotThrow(app::checkWorldDatabase);

        verify(result).close();
        verify(statement).close();
    }

    @Test
    void disconnectClosesOpenConnection() throws Exception
    {
        Main app = new Main();
        Connection connection = mock(Connection.class);
        setConnection(app, connection);

        app.disconnect();

        verify(connection).close();
        Field field = Main.class.getDeclaredField("con");
        field.setAccessible(true);
        assertNull(field.get(app));
    }

    @Test
    void disconnectReportsCloseFailure() throws Exception
    {
        Main app = new Main();
        Connection connection = mock(Connection.class);
        setConnection(app, connection);
        doThrow(new SQLException("close failed")).when(connection).close();

        assertDoesNotThrow(app::disconnect);
        verify(connection).close();
    }

    @Test
    void applicationReturnsFailureWhenConnectionFails()
    {
        Main app = new Main()
        {
            @Override
            public boolean connect()
            {
                return false;
            }
        };

        assertEquals(1, app.run());
    }

    @Test
    void applicationReturnsFailureWhenDatabaseCheckFails()
    {
        final boolean[] disconnected = {false};
        Main app = new Main()
        {
            @Override
            public boolean connect()
            {
                return true;
            }

            @Override
            public void checkWorldDatabase() throws SQLException
            {
                throw new SQLException("test query failed");
            }

            @Override
            public void disconnect()
            {
                disconnected[0] = true;
            }
        };

        assertEquals(1, app.run());
        assertTrue(disconnected[0]);
    }

    @Test
    void applicationStopsOnReportFailureAndDisconnects()
    {
        List<String> calls = new ArrayList<>();
        Main app = new Main()
        {
            @Override
            public boolean connect()
            {
                calls.add("connect");
                return true;
            }

            @Override
            public void checkWorldDatabase()
            {
                calls.add("database check");
            }

            @Override
            public void runCountryReports()
            {
                calls.add("country reports");
            }

            @Override
            public void runCityReports() throws SQLException
            {
                calls.add("city reports");
                throw new SQLException("report query failed");
            }

            @Override
            public void disconnect()
            {
                calls.add("disconnect");
            }
        };

        assertEquals(1, app.run());
        assertEquals(List.of("connect", "database check",
                "country reports", "city reports", "disconnect"), calls);
    }

    private static void setConnection(Main app, Connection connection)
            throws Exception
    {
        Field field = Main.class.getDeclaredField("con");
        field.setAccessible(true);
        field.set(app, connection);
    }

}