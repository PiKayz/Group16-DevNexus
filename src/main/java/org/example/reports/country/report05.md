# Report 5: Top N countries in a continent

Lists the N most populated countries in a continent supplied by the user.
Supported continents are Asia, Europe, North America, Africa, Oceania,
Antarctica and South America. Names are case insensitive and surrounding
whitespace is ignored; quote names containing spaces.

Columns: Code, Name, Continent, Region, Population, Capital. Sort order is
population descending, then country code ascending. Countries without a capital
remain present with `N/A`. N must be a positive integer; if it exceeds the
continent's country count, all matching countries are displayed.

After building with `mvn package` and `docker compose build app`:

```sh
docker compose run --rm app report05 Asia 10
docker compose run --rm app report05 "South America" 5
```

Implementation: `CountryReportService.getTopCountriesInContinent(Continent, TopN)`.
Both the continent and the SQL row limit are bound as query parameters.
