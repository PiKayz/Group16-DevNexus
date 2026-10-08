package org.example.models;

/**
 * Validated CLI input. No arguments preserves the original report 1 default.
 */
public record ReportRequest(int number, Continent continent, Region region, TopN topN)
{
    public ReportRequest(int number, Continent continent)
    {
        this(number, continent, null, null);
    }

    public ReportRequest(int number, Continent continent, Region region)
    {
        this(number, continent, region, null);
    }

    public ReportRequest
    {
        if (number < 1 || number > 4)
        {
            throw new IllegalArgumentException("Supported reports: report01-report04");
        }
        if (number == 2 && continent == null)
        {
            throw new IllegalArgumentException("Report 2 requires a continent");
        }
        if (number != 2 && continent != null)
        {
            throw new IllegalArgumentException("This report does not use a continent");
        }
        if (number == 3 && region == null)
        {
            throw new IllegalArgumentException("Report 3 requires a region");
        }
        if (number != 3 && region != null)
        {
            throw new IllegalArgumentException("This report does not use a region");
        }
        if (number == 4 && topN == null)
        {
            throw new IllegalArgumentException("Report 4 requires N");
        }
        if (number < 4 && topN != null)
        {
            throw new IllegalArgumentException("This report does not use N");
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
                if (arguments.length != 1)
                {
                    throw new IllegalArgumentException("Report 1 takes no filter arguments");
                }
                yield new ReportRequest(1, null);
            }
            case "2", "report02" ->
            {
                if (arguments.length != 2)
                {
                    throw new IllegalArgumentException(
                            "Report 2 requires one continent; quote names containing spaces"
                    );
                }
                yield new ReportRequest(2, Continent.parse(arguments[1]));
            }
            case "3", "report03" ->
            {
                if (arguments.length != 2)
                {
                    throw new IllegalArgumentException(
                            "Report 3 requires one region; quote names containing spaces"
                    );
                }
                yield new ReportRequest(3, null, new Region(arguments[1]));
            }
            case "4", "report04" ->
            {
                if (arguments.length != 2)
                {
                    throw new IllegalArgumentException("Report 4 requires N");
                }
                yield new ReportRequest(4, null, null, TopN.parse(arguments[1]));
            }
            default -> throw new IllegalArgumentException(
                    "Unknown report: " + arguments[0] + ". Choose report01-report04"
            );
        };
    }
}
