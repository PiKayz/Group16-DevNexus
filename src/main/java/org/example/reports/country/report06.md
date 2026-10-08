# Report 6: Top N countries in a region

Lists the N most populated countries in the region supplied by the user.
Region names are case insensitive and surrounding whitespace is ignored; quote
names containing spaces. N must be a positive integer. Missing/invalid N or a
blank region is rejected before connecting. An unknown region produces
`No matching records found`.

Columns: Code, Name, Continent, Region, Population, Capital. Sort order is
population descending, then country code ascending. Countries without capitals
remain present with `N/A`. If N exceeds the region's country count, all matching
countries are displayed.

After building with `mvn package` and `docker compose build app`:

```sh
docker compose run --rm app report06 "Eastern Asia" 10
```

Implementation: `CountryReportService.getTopCountriesInRegion(Region, TopN)`.
Both the region and the SQL row limit are bound as query parameters.
