# Report 11: All cities in a district

Columns: Name, Country, District, Population. Country is the full country name.
Results are ordered by population descending, then city ID ascending for ties.
Cities with zero population and cities sharing a name remain in the report.

District names are case insensitive. By default all matching districts across
countries are included. An optional country code/name narrows the district.
Blank filters are rejected before connecting. Unknown filters produce
No matching records found.

After mvn package and docker compose build app:

```sh
docker compose run --rm app report11 California USA
```

Implementation: CityReportService in this package; matching tests are under
src/test/java/org/example/reports/city.
