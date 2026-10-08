package org.example.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Small synthetic data set for running the production queries against SQL.
 * Every test receives an independent in-memory database.
 */
public final class WorldDatabaseFixture
{
    private WorldDatabaseFixture()
    {
    }

    public static Connection open() throws SQLException
    {
        Connection connection = DriverManager.getConnection(
                "jdbc:h2:mem:world_" + UUID.randomUUID() + ";MODE=MySQL"
        );

        try (Statement statement = connection.createStatement())
        {
            statement.execute("""
                    CREATE TABLE city (
                        ID INT PRIMARY KEY, Name VARCHAR(35), CountryCode VARCHAR(3),
                        District VARCHAR(20), Population BIGINT
                    )
                    """);
            statement.execute("""
                    CREATE TABLE country (
                        Code VARCHAR(3) PRIMARY KEY, Name VARCHAR(52),
                        Continent VARCHAR(13), Region VARCHAR(26),
                        Population BIGINT, Capital INT
                    )
                    """);
            statement.execute("""
                    INSERT INTO city VALUES
                        (1, 'Peking', 'CHN', 'Beijing', 300),
                        (2, 'New Delhi', 'IND', 'Delhi', 200),
                        (3, 'Berlin', 'DEU', 'Berlin', 400),
                        (4, 'London', 'GBR', 'England', 400),
                        (5, 'Brasilia', 'BRA', 'Distrito Federal', 250),
                        (6, 'Washington', 'USA', 'District of Columbia', 150),
                        (7, 'Cairo', 'EGY', 'Cairo', 400),
                        (8, 'Canberra', 'AUS', 'Capital Region', 350),
                        (9, 'Shanghai', 'CHN', 'Shared District', 600),
                        (10, 'Mumbai', 'IND', 'Shared District', 500),
                        (11, 'Springfield', 'USA', 'Central', 0),
                        (12, 'Springfield', 'BRA', 'Central', 100)
                    """);
            statement.execute("""
                    INSERT INTO country VALUES
                        ('CHN', 'China', 'Asia', 'Eastern Asia', 1500, 1),
                        ('IND', 'India', 'Asia', 'Southern and Central Asia', 1200, 2),
                        ('NCP', 'Country without a capital', 'Asia', 'Eastern Asia', 0, NULL),
                        ('DEU', 'Germany', 'Europe', 'Western Europe', 800, 3),
                        ('GBR', 'United Kingdom', 'Europe', 'British Islands', 800, 4),
                        ('BRA', 'Brazil', 'South America', 'South America', 900, 5),
                        ('USA', 'United States', 'North America', 'North America', 1000, 6),
                        ('EGY', 'Egypt', 'Africa', 'Northern Africa', 700, 7),
                        ('AUS', 'Australia', 'Oceania', 'Australia and New Zealand', 600, 8),
                        ('ATA', 'Antarctica', 'Antarctica', 'Antarctica', 0, NULL)
                    """);
            return connection;
        }
        catch (SQLException error)
        {
            connection.close();
            throw error;
        }
    }
}
