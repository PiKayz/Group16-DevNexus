package org.example.models;

/**
 * Validated CLI input. No arguments preserves report 1 as the default.
 */
public record ReportRequest(int number, Continent continent, Region region, TopN topN, CountryFilter country)
{
    public ReportRequest(int number, Continent continent)
    {
        this(number, continent, null, null, null);
    }

    public ReportRequest(int number, Continent continent, Region region)
    {
        this(number, continent, region, null, null);
    }

    public ReportRequest(int number, Continent continent, Region region, TopN topN)
    {
        this(number, continent, region, topN, null);
    }

    public ReportRequest
    {
        if (number < 1 || number > 10)
        {
            throw new IllegalArgumentException("Supported reports: report01-report10");
        }
        boolean needsContinent = number == 2 || number == 5 || number == 8 || number == 13;
        if (needsContinent && continent == null)
        {
            throw new IllegalArgumentException("Report " + number + " requires a continent");
        }
        if (!needsContinent && continent != null)
        {
            throw new IllegalArgumentException("This report does not use a continent");
        }
        boolean needsRegion = number == 3 || number == 6 || number == 9 || number == 14;
        if (needsRegion && region == null)
        {
            throw new IllegalArgumentException("Report " + number + " requires a region");
        }
        if (!needsRegion && region != null)
        {
            throw new IllegalArgumentException("This report does not use a region");
        }
        boolean needsTopN = (number >= 4 && number <= 6) || (number >= 12 && number <= 16);
        if (needsTopN && topN == null)
        {
            throw new IllegalArgumentException("Report " + number + " requires N");
        }
        if (!needsTopN && topN != null)
        {
            throw new IllegalArgumentException("This report does not use N");
        }
        boolean needsCountry = number == 10 || number == 15;
        if (needsCountry && country == null)
        {
            throw new IllegalArgumentException("Report " + number + " requires a country");
        }
        if (!needsCountry && country != null)
        {
            throw new IllegalArgumentException("This report does not use a country");
        }
    }

    public static ReportRequest parse(String[] arguments)
    {
        if (arguments.length == 0)
        {
            return new ReportRequest(1, null);
        }

        return switch (arguments[0])
        {
            case "1", "report01" ->
            {
                requireLength(arguments, 1, "Report 1 requires no filter arguments");
                yield new ReportRequest(1, null, null, null, null);
            }
            case "2", "report02" ->
            {
                requireLength(arguments, 2, "Report 2 requires a continent");
                yield new ReportRequest(2, Continent.parse(arguments[1]), null, null, null);
            }
            case "3", "report03" ->
            {
                requireLength(arguments, 2, "Report 3 requires a region");
                yield new ReportRequest(3, null, new Region(arguments[1]), null, null);
            }
            case "4", "report04" ->
            {
                requireLength(arguments, 2, "Report 4 requires N");
                yield new ReportRequest(4, null, null, TopN.parse(arguments[1]), null);
            }
            case "5", "report05" ->
            {
                requireLength(arguments, 3, "Report 5 requires a continent and N");
                yield new ReportRequest(5, Continent.parse(arguments[1]), null, TopN.parse(arguments[2]), null);
            }
            case "6", "report06" ->
            {
                requireLength(arguments, 3, "Report 6 requires a region and N");
                yield new ReportRequest(6, null, new Region(arguments[1]), TopN.parse(arguments[2]), null);
            }
            case "7", "report07" ->
            {
                requireLength(arguments, 1, "Report 7 requires no filter arguments");
                yield new ReportRequest(7, null, null, null, null);
            }
            case "8", "report08" ->
            {
                requireLength(arguments, 2, "Report 8 requires a continent");
                yield new ReportRequest(8, Continent.parse(arguments[1]), null, null, null);
            }
            case "9", "report09" ->
            {
                requireLength(arguments, 2, "Report 9 requires a region");
                yield new ReportRequest(9, null, new Region(arguments[1]), null, null);
            }
            case "10", "report10" ->
            {
                requireLength(arguments, 2, "Report 10 requires a country");
                yield new ReportRequest(10, null, null, null, new CountryFilter(arguments[1]));
            }
            default -> throw new IllegalArgumentException("Unknown report: " + arguments[0]
                    + ". Choose report01-report10");
        };
    }

    private static void requireLength(String[] arguments, int expected, String message)
    {
        if (arguments.length != expected)
        {
            throw new IllegalArgumentException(message + "; quote names containing spaces");
        }
    }
}
