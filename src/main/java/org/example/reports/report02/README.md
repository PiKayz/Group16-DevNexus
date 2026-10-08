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

Implementation: `CountriesInContinentReport.java` in this folder. Tests are in the
matching `src/test/java/org/example/reports/report02` folder.

The shared country repository binds the continent as a prepared-statement
parameter. The report class uses the shared country model and formatter.
