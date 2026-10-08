# Report 7: All cities in the world

Columns: Name, Country, District, Population. Country is the full country name.
Results are ordered by population descending, then city ID ascending for ties.
Cities with zero population and cities sharing a name remain in the report.


After mvn package and docker compose build app:

```sh
docker compose run --rm app report07
```

Implementation: CityReportService in this package; matching tests are under
src/test/java/org/example/reports/city.
