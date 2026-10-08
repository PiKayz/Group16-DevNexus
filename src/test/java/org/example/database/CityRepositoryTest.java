package org.example.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.example.support.WorldDatabaseFixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CityRepositoryTest
{
    @Test
    void rejectsAMissingConnection()
    {
        assertThrows(NullPointerException.class, () -> new CityRepository(null));
    }

    @Test
    void reportsAClosedConnectionClearly() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            CityRepository repository = new CityRepository(connection);
            connection.close();
            SQLException error = assertThrows(SQLException.class, repository::findAll);
            assertEquals("Not connected to the database", error.getMessage());
        }
    }
}
