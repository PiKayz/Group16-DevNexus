# Report 2: countries in a continent

Lists countries in the continent selected by the user, from largest population
to smallest. Country code determines the order when populations are equal.
Countries with no recorded capital remain in the output with `N/A` as their capital.

Columns: Code, Name, Continent, Region, Population, Capital.

Supported continents: Asia, Europe, North America, Africa, Oceania, Antarctica,
and South America. Input is case insensitive and ignores surrounding whitespace.
Invalid or missing continent names produce an error and exit code 2 before the
application connects to the database.

From the repository root, after `mvn package`:

```sh
docker compose up -d devnexus-db
docker compose build app
docker compose run --rm app report02 Asia
docker compose run --rm app report02 "South America"
```

Implementation: `CountryReportService.getCountriesInContinent()` in this folder.
Tests are in `src/test/java/org/example/reports/country`.

The shared country repository in `org.example.database` binds the continent as a
prepared-statement parameter. The service uses the country model in
`org.example.models` and the country formatter in this package.
