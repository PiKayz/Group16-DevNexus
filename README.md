[![workflow](https://github.com/PiKayz/Group16-DevNexus/actions/workflows/main.yml/badge.svg)](https://github.com/PiKayz/Group16-DevNexus/actions)
* Develop Build Status [![Develop Build](https://img.shields.io/github/actions/workflow/status/PiKayz/Group16-DevNexus/main.yml?branch=develop&label=build)](https://github.com/PiKayz/Group16-DevNexus/actions/workflows/main.yml)
License [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=flat-square)](https://opensource.org/licenses/Apache-2.0)
Release [![Releases](https://img.shields.io/github/release/PiKayz/Group16-DevNexus/all.svg?style=flat-square)](https://github.com/PiKayz/Group16-DevNexus/releases)

# Population Reporting System

The application reads the supplied MySQL World sample database. Its population
figures describe that dataset rather than current populations.

## Implemented reports

This branch implements 3 requirements of 32, which is 9.375%. The other 29
reporting requirements remain to be implemented. Output screenshots for the
final submission still need to be captured.

| Report | Requirement | Documentation |
|---|---|---|
| 1 | All countries in the world, largest population first | [Report 1](src/main/java/org/example/reports/country/report01.md) |
| 2 | All countries in a selected continent, largest population first | [Report 2](src/main/java/org/example/reports/country/report02.md) |
| 3 | All countries in a selected region, largest population first | [Report 3](src/main/java/org/example/reports/country/report03.md) |

Reports are grouped by category. A single `CountryReportService` supplies the
world, continent and region reports as separate methods, with shared queries
and formatting. All country reports display Code, Name, Continent,
Region, Population, and Capital. Countries without a recorded capital remain
in the output with `N/A` in the Capital column.

## Java package structure

```text
src/main/java/org/example/
  Main.java
  database/
    WorldDatabase.java
    CountryRepository.java
  models/
    Country.java
    Continent.java
    Region.java
    ReportRequest.java
  reports/
    country/
      CountryReportService.java
      CountryReportFormatter.java
      package-info.java
      report01.md
      report02.md
      report03.md
    city/package-info.java
    capital/package-info.java
    breakdown/package-info.java
    population/package-info.java
    language/package-info.java
```

`Main` validates the request and selects the report. `WorldDatabase` owns the
existing MySQL connection, retry logic, database checks, and shutdown.
`CountryRepository` shares parameterised SQL and result mapping. The model
package contains immutable report data and validated request values.

The category packages containing only `package-info.java` document future
work; they provide no report service or stub results. As their reports are
implemented, each category should have one cohesive service with separate
methods for its requirements. Tests follow the same category packages.

| Category | Requirements still unimplemented |
|---|---|
| Country | 4-6: Top N reports |
| City | 7-16 |
| Capital | 17-22 |
| Breakdown | 23-25 |
| Population | 26-31 |
| Language | 32: all five requested languages together |

## Build and run

```sh
mvn package
docker compose up --build --abort-on-container-exit --exit-code-from app
```

The default command checks the database, prints report 1, and exits. To select
report 2 and supply a continent:

```sh
docker compose up -d devnexus-db
docker compose build app
docker compose run --rm app report02 Asia
docker compose run --rm app report02 "South America"
docker compose run --rm app report03 "Eastern Asia"
```

Use `report01` to explicitly select report 1. Continent names are case
insensitive; names containing spaces must be quoted. Missing or invalid input
exits with code 2 before connecting to the database. Database/report failures
exit with code 1; successful reports exit with code 0.

`mvn package` runs formatter/input tests and tests of the production SQL queries
against a small, isolated H2 database in MySQL compatibility mode. Docker checks
verify the reports against the actual World dataset and MySQL.

Validation: 48 automated tests passed. MySQL verification returned all 239
countries for report 1 and the correct filtered rows for all seven continents
in report 2 (Asia 51, Europe 46, North America 37, Africa 58, Oceania 28,
Antarctica 5, South America 14).

```sh
docker compose down
```

Report user stories and their checklists are tracked in
[GitHub issues](https://github.com/PiKayz/Group16-DevNexus/issues).
