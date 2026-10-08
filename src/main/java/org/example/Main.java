package org.example;

import java.sql.SQLException;

import org.example.database.CountryRepository;
import org.example.database.WorldDatabase;
import org.example.models.ReportRequest;
import org.example.reports.country.CountryReportService;

public class Main
{
    public static void main(String[] args)
    {
        ReportRequest request;

        try
        {
            request = ReportRequest.parse(args);
        }
        catch (IllegalArgumentException error)
        {
            System.err.println(error.getMessage());
            System.err.println("Usage: java -jar app.jar report01");
            System.err.println("       java -jar app.jar report02 \"South America\"");
            System.err.println("       java -jar app.jar report03 \"Eastern Asia\"");
            System.exit(2);
            return;
        }

        WorldDatabase database = new WorldDatabase();

        if (!database.connect())
        {
            System.err.println("Could not connect to World database");
            System.exit(1);
            return;
        }

        int exitCode = 0;

        try
        {
            database.checkWorldDatabase();
            System.out.println();
            CountryReportService reports = new CountryReportService(
                    new CountryRepository(database.getConnection())
            );

            switch (request.number())
            {
                case 1 -> reports.printCountriesInWorld(System.out);
                case 2 -> reports.printCountriesInContinent(request.continent(), System.out);
                case 3 -> reports.printCountriesInRegion(request.region(), System.out);
                default -> throw new IllegalStateException("Unsupported validated report");
            }
        }
        catch (SQLException e)
        {
            System.err.println("World database check or country report failed");
            System.err.println(e.getMessage());
            exitCode = 1;
        }
        finally
        {
            database.close();
        }

        System.exit(exitCode);
    }
}
