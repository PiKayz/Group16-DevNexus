# Report 9: All cities in a region

Columns: Name, Country, District, Population. Country is the full country name.
Results are ordered by population descending, then city ID ascending for ties.
Cities with zero population and cities sharing a name remain in the report.

Region names are case insensitive and surrounding whitespace is ignored.
Blank filters are rejected before connecting. Unknown filters produce
No matching records found.

After mvn package and docker compose build app:

```sh
docker compose run --rm app report09 "Eastern Asia"
```

Implementation: CityReportService in this package; matching tests are under
src/test/java/org/example/reports/city.
