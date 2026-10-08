package org.example;

import java.sql.SQLException;

import org.example.database.CountryRepository;
import org.example.database.CityRepository;
import org.example.database.WorldDatabase;
import org.example.models.ReportRequest;
import org.example.reports.country.CountryReportService;
import org.example.reports.city.CityReportService;

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
            System.err.println("       java -jar app.jar report04 10");
            System.err.println("       java -jar app.jar report05 \"South America\" 10");
            System.err.println("       java -jar app.jar report06 \"Eastern Asia\" 10");
            System.err.println("       java -jar app.jar report07");
            System.err.println("       java -jar app.jar report08 Asia");
            System.err.println("       java -jar app.jar report09 \"Eastern Asia\"");
            System.err.println("       java -jar app.jar report10 USA");
            System.err.println("       java -jar app.jar report11 California USA");
            System.err.println("       java -jar app.jar report12 10");
            System.err.println("       java -jar app.jar report13 Asia 10");
            System.err.println("       java -jar app.jar report14 \"Eastern Asia\" 10");
            System.err.println("       java -jar app.jar report15 USA 10");
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
            if (request.number() <= 6)
            {
                CountryReportService reports = new CountryReportService(
                        new CountryRepository(database.getConnection()));
                switch (request.number())
                {
                    case 1 -> reports.printCountriesInWorld(System.out);
                    case 2 -> reports.printCountriesInContinent(request.continent(), System.out);
                    case 3 -> reports.printCountriesInRegion(request.region(), System.out);
                    case 4 -> reports.printTopCountriesInWorld(request.topN(), System.out);
                    case 5 -> reports.printTopCountriesInContinent(
                            request.continent(), request.topN(), System.out);
                    case 6 -> reports.printTopCountriesInRegion(
                            request.region(), request.topN(), System.out);
                    default -> throw new IllegalStateException("Unsupported validated report");
                }
            }
            else
            {
                new CityReportService(new CityRepository(database.getConnection()))
                        .print(request, System.out);
            }
        }
        catch (SQLException e)
        {
            System.err.println("World database check or population report failed");
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
