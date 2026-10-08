# Report 4: Top N countries in the world

Lists the N most populated countries in the world. N is supplied by the user
and must be a positive integer within the Java integer range. Missing, zero,
negative, fractional or overflowing counts are rejected before connecting.

Columns: Code, Name, Continent, Region, Population, Capital. Countries are sorted
by population descending, then country code ascending for equal populations.
Countries without capitals remain present with `N/A`; if N exceeds the number
of matching countries, all matches are displayed.

After building with `mvn package` and `docker compose build app`:

```sh
docker compose run --rm app report04 10
```

Implementation: `CountryReportService.getTopCountriesInWorld(TopN)`. The database
binds N as a `LIMIT` parameter rather than retrieving every country first.
