[![workflow](https://github.com/PiKayz/Group16-DevNexus/actions/workflows/main.yml/badge.svg)](https://github.com/PiKayz/Group16-DevNexus/actions)
* Develop Build Status [![Develop Build](https://img.shields.io/github/actions/workflow/status/PiKayz/Group16-DevNexus/main.yml?branch=develop&label=build)](https://github.com/PiKayz/Group16-DevNexus/actions/workflows/main.yml)
License [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=flat-square)](https://opensource.org/licenses/Apache-2.0)
Release [![Releases](https://img.shields.io/github/release/PiKayz/Group16-DevNexus/all.svg?style=flat-square)](https://github.com/PiKayz/Group16-DevNexus/releases)

# Population Reporting System

The application reads the supplied MySQL World sample database. Its population
figures describe that dataset rather than current populations.

## Implemented reports

This branch implements 16 requirements of 32, which is 50%. The other 16
reporting requirements remain to be implemented. Output screenshots for the
final submission still need to be captured.

| Report | Requirement | Documentation |
|---|---|---|
| 1 | All countries in the world, largest population first | [Report 1](src/main/java/org/example/reports/country/report01.md) |
| 2 | All countries in a selected continent, largest population first | [Report 2](src/main/java/org/example/reports/country/report02.md) |
| 3 | All countries in a selected region, largest population first | [Report 3](src/main/java/org/example/reports/country/report03.md) |
| 4 | Top N countries in the world | [Report 4](src/main/java/org/example/reports/country/report04.md) |
| 5 | Top N countries in a selected continent | [Report 5](src/main/java/org/example/reports/country/report05.md) |
| 6 | Top N countries in a selected region | [Report 6](src/main/java/org/example/reports/country/report06.md) |
| 7 | All cities in the world | [Report 7](src/main/java/org/example/reports/city/report07.md) |
| 8 | All cities in a continent | [Report 8](src/main/java/org/example/reports/city/report08.md) |
| 9 | All cities in a region | [Report 9](src/main/java/org/example/reports/city/report09.md) |
| 10 | All cities in a country | [Report 10](src/main/java/org/example/reports/city/report10.md) |
| 11 | All cities in a district | [Report 11](src/main/java/org/example/reports/city/report11.md) |
| 12 | Top N cities in the world | [Report 12](src/main/java/org/example/reports/city/report12.md) |
| 13 | Top N cities in a continent | [Report 13](src/main/java/org/example/reports/city/report13.md) |
| 14 | Top N cities in a region | [Report 14](src/main/java/org/example/reports/city/report14.md) |
| 15 | Top N cities in a country | [Report 15](src/main/java/org/example/reports/city/report15.md) |
| 16 | Top N cities in a district | [Report 16](src/main/java/org/example/reports/city/report16.md) |

Reports are grouped by category. A single `CountryReportService` supplies the
country reports as separate methods, with shared queries
and formatting. All country reports display Code, Name, Continent,
Region, Population, and Capital. Countries without a recorded capital remain
in the output with `N/A` in the Capital column.

City reports display Name, Country, District, and Population. `CityReportService`
uses shared queries and formatting for every implemented city requirement.

## Java package structure

```text
src/main/java/org/example/
  Main.java
  database/
    WorldDatabase.java
    CountryRepository.java
    CityRepository.java
  models/
    Country.java
    City.java
    CountryFilter.java
    DistrictFilter.java
    Continent.java
    Region.java
    TopN.java
    ReportRequest.java
  reports/
    country/
      CountryReportService.java
      CountryReportFormatter.java
      package-info.java
      report01.md
      report02.md
      report03.md
      report04.md
      report05.md
      report06.md
    city/
      CityReportService.java
      CityReportFormatter.java
      package-info.java
      report07.md
      report08.md
      report09.md
      report10.md
      report11.md
      report12.md
      report13.md
      report14.md
      report15.md
      report16.md
    capital/package-info.java
    breakdown/package-info.java
    population/package-info.java
    language/package-info.java
```

`Main` validates the request and selects the report. `WorldDatabase` owns the
existing MySQL connection, retry logic, database checks, and shutdown.
`CountryRepository` and `CityRepository` share parameterised queries and result
mapping within their categories. City queries join country names and apply
population ordering and Top N limits in SQL. The model
package contains immutable report data and validated request values.

The category packages containing only `package-info.java` document future
work; they provide no report service or stub results. As their reports are
implemented, each category should have one cohesive service with separate
methods for its requirements. Tests follow the same category packages.

| Category | Requirements still unimplemented |
|---|---|
| Country | None: requirements 1-6 implemented |
| City | None: requirements 7-16 implemented |
| Capital | 17-22 |
| Breakdown | 23-25 |
| Population | 26-31 |
| Language | 32: all five requested languages together |

## Build and run

```sh
mvn package
docker compose up --build --abort-on-container-exit --exit-code-from app
```

The default command checks the database, prints report 1, and exits. Select a
report and supply its filters and optional N value:

```sh
docker compose up -d devnexus-db
docker compose build app
docker compose run --rm app report02 Asia
docker compose run --rm app report02 "South America"
docker compose run --rm app report03 "Eastern Asia"
docker compose run --rm app report04 10
docker compose run --rm app report05 Asia 10
docker compose run --rm app report06 "Eastern Asia" 10
docker compose run --rm app report07
docker compose run --rm app report08 Asia
docker compose run --rm app report09 "Eastern Asia"
docker compose run --rm app report10 USA
docker compose run --rm app report11 California USA
docker compose run --rm app report12 10
docker compose run --rm app report13 Asia 10
docker compose run --rm app report14 "Eastern Asia" 10
docker compose run --rm app report15 USA 10
docker compose run --rm app report16 California 10 USA
```

Use `report01` to explicitly select report 1. Continent names are case
insensitive; names containing spaces must be quoted. Missing or invalid input
exits with code 2 before connecting to the database. Database/report failures
exit with code 1; successful reports exit with code 0.

Region names are also case insensitive and ignore surrounding whitespace. An
unknown region produces `No matching records found`. Top N reports require N to
be a positive integer; when it exceeds the available count, all matches are
displayed. All filters and SQL row limits use bound parameters. Country filters
accept a country code or complete country name. District reports include matches
across all countries by default; an optional final country argument qualifies the
district, and the report title identifies the scope.

Reports 3-6 have separate local commits on `feature/country-reports/all-region`,
`feature/country-reports/top-n-world`, `feature/country-reports/top-n-continent`,
and `feature/country-reports/top-n-region`. Each report branch includes the
preceding implemented country reports. City reports 7-16 also have a separate
commit on each exact `feature/city-reports/` branch. Each city branch builds on
the preceding city report; `feature/city-reports/top-n-district` contains all
16 implemented country and city requirements.

`mvn package` runs formatter/input tests and tests of the production SQL queries
against a small, isolated H2 database in MySQL compatibility mode. Docker checks
verify the reports against the actual World dataset and MySQL. City SQL tests
cover ties, duplicate city names, zero populations, unknown/literal filters,
country code/name matching, shared districts, qualified districts, and N limits.

Validation: 186 automated tests passed. MySQL verification returned all 239
countries for report 1 and the correct filtered rows for all seven continents
in report 2 (Asia 51, Europe 46, North America 37, Africa 58, Oceania 28,
Antarctica 5, South America 14).

Reports 3-6 were also verified against MySQL: region filtering (Eastern Asia 8
countries, Western Europe 9), exact Top N rows in each scope, counts exceeding
the available matches, the maximum supported integer count, unknown regions,
and parameterised region inputs. Ten invalid CLI cases were rejected before
connecting. Example outputs are saved in `target/report03-output.txt` through
`target/report06-output.txt` after verification.

All ten city reports were verified row by row against a read-only MySQL snapshot
of 4,079 cities. Checked scopes included Asia (1,766), Eastern Asia (740), USA
(274), California (68) and shared district name Central (11). Top N variants,
country names/codes, optional district country qualifiers, very large N values,
and literal parameter inputs passed. Fourteen malformed CLI cases were rejected
before connecting; the saved outputs of country reports 1-6 were unchanged.
Example city outputs are saved in `target/report07-output.txt` through
`target/report16-output.txt` after verification.

```sh
docker compose down
```

Report user stories and their checklists are tracked in
[GitHub issues](https://github.com/PiKayz/Group16-DevNexus/issues).
