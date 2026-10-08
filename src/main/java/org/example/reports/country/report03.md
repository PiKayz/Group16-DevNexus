# Report 3: countries in a region

Lists countries in the region supplied by the user, from largest population to
smallest. Country code determines the order when populations are equal.

Columns: Code, Name, Continent, Region, Population, Capital. Countries with no
recorded capital remain in the output with `N/A` as their capital.

Region names are case insensitive and surrounding whitespace is ignored. Quote
names containing spaces. A blank region is rejected before connecting; an unknown
region returns `No matching records found`. SQL parameters bind the region as data.

After building with `mvn package` and `docker compose build app`:

```sh
docker compose run --rm app report03 "Eastern Asia"
```

Implementation: `CountryReportService.getCountriesInRegion(Region)`.
