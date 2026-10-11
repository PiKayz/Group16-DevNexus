package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CityReports
{

    public void topNCitiesInWorld(Connection con, int n) throws SQLException
    {
        if (n <= 0)
        {
            throw new IllegalArgumentException("N must be a positive integer");
        }

        String sql =
                "SELECT city.Name, country.Name AS Country, city.District, city.Population "
                        + "FROM city JOIN country ON city.CountryCode = country.Code "
                        + "ORDER BY city.Population DESC, city.Name ASC LIMIT ?";

        System.out.println();
        System.out.println("Top " + n + " cities in the world");
        System.out.println(String.format("%-35s %-52s %-20s %12s",
                "Name", "Country", "District", "Population"));

        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            stmt.setInt(1, n);

            try (ResultSet result = stmt.executeQuery())
            {
                boolean found = false;

                while (result.next())
                {
                    found = true;
                    System.out.println(String.format("%-35s %-52s %-20s %,12d",
                            result.getString("Name"),
                            result.getString("Country"),
                            result.getString("District"),
                            result.getInt("Population")));
                }

                if (!found)
                {
                    System.out.println("No matching records found");
                }
            }
        }
    }
}