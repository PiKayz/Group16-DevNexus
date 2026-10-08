# Report 1: countries in the world

Lists every country from largest population to smallest. Country code determines
the order when populations are equal. Countries with no recorded capital remain
in the output with `N/A` as their capital.

Columns: Code, Name, Continent, Region, Population, Capital.

From the repository root, after `mvn package`:

```sh
docker compose up -d devnexus-db
docker compose build app
docker compose run --rm app report01
```

Implementation: `CountriesInWorldReport.java` in this folder. Tests are in the
matching `src/test/java/org/example/reports/report01` folder.
