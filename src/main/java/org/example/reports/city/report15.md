# Report 15: Top N cities in a country

Columns: Name, Country, District, Population. Country is the full country name.
Results are ordered by population descending, then city ID ascending for ties.
Cities with zero population and cities sharing a name remain in the report.

Supply a country code such as USA or its full name such as United States.
Country matching is case insensitive; quote names containing spaces.
N must be a positive integer. If N exceeds the matching city count, all
available matches are displayed. N and all filters are bound as SQL parameters.
Blank filters are rejected before connecting. Unknown filters produce
No matching records found.

After mvn package and docker compose build app:

```sh
docker compose run --rm app report15 USA 10
```

Implementation: CityReportService in this package; matching tests are under
src/test/java/org/example/reports/city.
