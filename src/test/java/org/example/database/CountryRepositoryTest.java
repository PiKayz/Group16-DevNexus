package org.example.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.example.support.WorldDatabaseFixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CountryRepositoryTest
{
    @Test
    void rejectsAMissingConnection()
    {
        assertThrows(NullPointerException.class, () -> new CountryRepository(null));
    }

    @Test
    void reportsAClosedConnectionClearly() throws Exception
    {
        try (Connection connection = WorldDatabaseFixture.open())
        {
            CountryRepository repository = new CountryRepository(connection);
            connection.close();
            SQLException error = assertThrows(SQLException.class, repository::findAll);

            assertEquals("Not connected to the database", error.getMessage());
        }
    }
}
