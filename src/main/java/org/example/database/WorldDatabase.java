package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Owns the application's connection to the existing MySQL World database.
 * Preserves the configured Docker hostname, credentials, timeouts and retries.
 */
public final class WorldDatabase implements AutoCloseable
{
    private Connection con;

    /**
     * Connect to the World database.
     */
    public boolean connect()
    {
        String url =
                "jdbc:mysql://devnexus-db:3306/world"
                        + "?allowPublicKeyRetrieval=true&useSSL=false"
                        + "&connectTimeout=5000&socketTimeout=30000";

        int retries = 30;

        for (int attempt = 1; attempt <= retries; attempt++)
        {
            System.out.println(
                    "Connecting to database... Attempt " + attempt
            );

            try
            {
                con = DriverManager.getConnection(
                        url,
                        "devnexus",
                        "DevNexusApp2026"
                );

                System.out.println("Successfully connected to World database");
                return true;
            }
            catch (SQLException e)
            {
                System.out.println("Connection failed: " + e.getMessage());

                if (attempt == retries)
                {
                    break;
                }

                try
                {
                    Thread.sleep(5000);
                }
                catch (InterruptedException interrupted)
                {
                    Thread.currentThread().interrupt();
                    System.out.println("Connection interrupted");
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * Provide the connection to shared query code without transferring ownership.
     */
    public Connection getConnection() throws SQLException
    {
        if (con == null || con.isClosed())
        {
            throw new SQLException("Not connected to the database");
        }

        return con;
    }

    /**
     * Check that all three World database tables contain data.
     */
    public void checkWorldDatabase() throws SQLException
    {
        if (con == null)
        {
            throw new SQLException("Not connected to the database");
        }

        String sql =
                "SELECT "
                        + "(SELECT COUNT(*) FROM country) AS country_count, "
                        + "(SELECT COUNT(*) FROM city) AS city_count, "
                        + "(SELECT COUNT(*) FROM countrylanguage) AS language_count";

        try (Statement stmt = con.createStatement();
             ResultSet result = stmt.executeQuery(sql))
        {
            if (!result.next())
            {
                throw new SQLException("Database check returned no result");
            }

            int countries = result.getInt("country_count");
            int cities = result.getInt("city_count");
            int languages = result.getInt("language_count");

            System.out.println("Countries: " + countries);
            System.out.println("Cities: " + cities);
            System.out.println("Country-language records: " + languages);

            if (countries == 0 || cities == 0 || languages == 0)
            {
                throw new SQLException(
                        "World database contains an empty required table"
                );
            }

            System.out.println("World database check passed");
        }
    }

    /**
     * Close the owned connection, retaining the original shutdown messages.
     */
    @Override
    public void close()
    {
        if (con != null)
        {
            try
            {
                con.close();
                con = null;
                System.out.println("Disconnected from database");
            }
            catch (SQLException e)
            {
                System.err.println(
                        "Error closing database connection: " + e.getMessage()
                );
            }
        }
    }
}
