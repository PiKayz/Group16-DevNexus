package org.example.models;

/**
 * Validated CLI input. No arguments preserves the original report 1 default.
 */
public record ReportRequest(int number, Continent continent)
{
    public ReportRequest
    {
        if (number != 1 && number != 2)
        {
            throw new IllegalArgumentException("Supported reports: report01, report02");
        }
        if (number == 2 && continent == null)
        {
            throw new IllegalArgumentException("Report 2 requires a continent");
        }
        if (number == 1 && continent != null)
        {
            throw new IllegalArgumentException("Report 1 does not use a continent");
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
            default -> throw new IllegalArgumentException(
                    "Unknown report: " + arguments[0] + ". Choose report01 or report02"
            );
        };
    }
}
