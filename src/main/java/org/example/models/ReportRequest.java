package org.example.models;

/**
 * Validated CLI input. No arguments preserves report 1 as the default.
 */
public record ReportRequest(int number, Continent continent, Region region, TopN topN, CountryFilter country, DistrictFilter district)
{
    public ReportRequest(int number, Continent continent)
    {
        this(number, continent, null, null, null, null);
    }

    public ReportRequest(int number, Continent continent, Region region)
    {
        this(number, continent, region, null, null, null);
    }

    public ReportRequest(int number, Continent continent, Region region, TopN topN)
    {
        this(number, continent, region, topN, null, null);
    }

    public ReportRequest(int number, Continent continent, Region region, TopN topN, CountryFilter country)
    {
        this(number, continent, region, topN, country, null);
    }

    public ReportRequest
    {
        if (number < 1 || number > 16)
        {
            throw new IllegalArgumentException("Supported reports: report01-report16");
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
        boolean needsDistrict = number == 11 || number == 16;
        if (needsDistrict && district == null)
        {
            throw new IllegalArgumentException("Report " + number + " requires a district");
        }
        if (!needsDistrict && district != null)
        {
            throw new IllegalArgumentException("This report does not use a district");
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
                yield new ReportRequest(1, null, null, null, null, null);
            }
            case "2", "report02" ->
            {
                requireLength(arguments, 2, "Report 2 requires a continent");
                yield new ReportRequest(2, Continent.parse(arguments[1]), null, null, null, null);
            }
            case "3", "report03" ->
            {
                requireLength(arguments, 2, "Report 3 requires a region");
                yield new ReportRequest(3, null, new Region(arguments[1]), null, null, null);
            }
            case "4", "report04" ->
            {
                requireLength(arguments, 2, "Report 4 requires N");
                yield new ReportRequest(4, null, null, TopN.parse(arguments[1]), null, null);
            }
            case "5", "report05" ->
            {
                requireLength(arguments, 3, "Report 5 requires a continent and N");
                yield new ReportRequest(5, Continent.parse(arguments[1]), null, TopN.parse(arguments[2]), null, null);
            }
            case "6", "report06" ->
            {
                requireLength(arguments, 3, "Report 6 requires a region and N");
                yield new ReportRequest(6, null, new Region(arguments[1]), TopN.parse(arguments[2]), null, null);
            }
            case "7", "report07" ->
            {
                requireLength(arguments, 1, "Report 7 requires no filter arguments");
                yield new ReportRequest(7, null, null, null, null, null);
            }
            case "8", "report08" ->
            {
                requireLength(arguments, 2, "Report 8 requires a continent");
                yield new ReportRequest(8, Continent.parse(arguments[1]), null, null, null, null);
            }
            case "9", "report09" ->
            {
                requireLength(arguments, 2, "Report 9 requires a region");
                yield new ReportRequest(9, null, new Region(arguments[1]), null, null, null);
            }
            case "10", "report10" ->
            {
                requireLength(arguments, 2, "Report 10 requires a country");
                yield new ReportRequest(10, null, null, null, new CountryFilter(arguments[1]), null);
            }
            case "11", "report11" ->
            {
                if (arguments.length < 2 || arguments.length > 3)
                {
                    throw new IllegalArgumentException("Report 11 requires a district; optional country is last");
                }
                yield new ReportRequest(11, null, null, null, null, new DistrictFilter(arguments[1], arguments.length == 3 ? new CountryFilter(arguments[2]) : null));
            }
            case "12", "report12" ->
            {
                requireLength(arguments, 2, "Report 12 requires N");
                yield new ReportRequest(12, null, null, TopN.parse(arguments[1]), null, null);
            }
            case "13", "report13" ->
            {
                requireLength(arguments, 3, "Report 13 requires a continent and N");
                yield new ReportRequest(13, Continent.parse(arguments[1]), null, TopN.parse(arguments[2]), null, null);
            }
            case "14", "report14" ->
            {
                requireLength(arguments, 3, "Report 14 requires a region and N");
                yield new ReportRequest(14, null, new Region(arguments[1]), TopN.parse(arguments[2]), null, null);
            }
            case "15", "report15" ->
            {
                requireLength(arguments, 3, "Report 15 requires a country and N");
                yield new ReportRequest(15, null, null, TopN.parse(arguments[2]), new CountryFilter(arguments[1]), null);
            }
            case "16", "report16" ->
            {
                if (arguments.length < 3 || arguments.length > 4)
                {
                    throw new IllegalArgumentException("Report 16 requires a district and N; optional country is last");
                }
                yield new ReportRequest(16, null, null, TopN.parse(arguments[2]), null, new DistrictFilter(arguments[1], arguments.length == 4 ? new CountryFilter(arguments[3]) : null));
            }
            default -> throw new IllegalArgumentException("Unknown report: " + arguments[0]
                    + ". Choose report01-report16");
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
