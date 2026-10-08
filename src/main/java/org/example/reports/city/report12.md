# Report 12: Top N cities in the world

Columns: Name, Country, District, Population. Country is the full country name.
Results are ordered by population descending, then city ID ascending for ties.
Cities with zero population and cities sharing a name remain in the report.

N must be a positive integer. If N exceeds the matching city count, all
available matches are displayed. N and all filters are bound as SQL parameters.

After mvn package and docker compose build app:

```sh
docker compose run --rm app report12 10
```

Implementation: CityReportService in this package; matching tests are under
src/test/java/org/example/reports/city.
