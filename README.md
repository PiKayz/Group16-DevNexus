[![workflow](https://github.com/PiKayz/Group16-DevNexus/actions/workflows/main.yml/badge.svg)](https://github.com/PiKayz/Group16-DevNexus/actions)
* Develop Build Status [![Develop Build](https://img.shields.io/github/actions/workflow/status/PiKayz/Group16-DevNexus/main.yml?branch=develop&label=build)](https://github.com/PiKayz/Group16-DevNexus/actions/workflows/main.yml)
License [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=flat-square)](https://opensource.org/licenses/Apache-2.0)
Release [![Releases](https://img.shields.io/github/release/PiKayz/Group16-DevNexus/all.svg?style=flat-square)](https://github.com/PiKayz/Group16-DevNexus/releases)

# Population Reporting System

The application reads the supplied MySQL World sample database. Its population
figures describe that dataset rather than current populations.

## Implemented reports

This branch implements 2 requirements of 32, which is 6.25%. The other 30
reporting requirements remain to be implemented. Output screenshots for the
final submission still need to be captured.

| Report | Requirement | Source folder |
|---|---|---|
| 1 | All countries in the world, largest population first | [report01](src/main/java/org/example/reports/report01) |
| 2 | All countries in a selected continent, largest population first | [report02](src/main/java/org/example/reports/report02) |

Each report has a separate source folder and matching test folder. The common
folder holds the country model, continent validation, parameterised database
queries, and console formatting. Both reports display Code, Name, Continent,
Region, Population, and Capital. Countries without a recorded capital remain
in the output with `N/A` in the Capital column.

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
```

Use `report01` to explicitly select report 1. Continent names are case
insensitive; names containing spaces must be quoted. Missing or invalid input
exits with code 2 before connecting to the database. Database/report failures
exit with code 1; successful reports exit with code 0.

`mvn package` runs formatter/input tests and tests of the production SQL queries
against a small, isolated H2 database in MySQL compatibility mode. Docker checks
verify the reports against the actual World dataset and MySQL.

Validation: 34 automated tests passed. MySQL verification returned all 239
countries for report 1 and the correct filtered rows for all seven continents
in report 2 (Asia 51, Europe 46, North America 37, Africa 58, Oceania 28,
Antarctica 5, South America 14).

```sh
docker compose down
```

Report user stories and their checklists are tracked in
[GitHub issues](https://github.com/PiKayz/Group16-DevNexus/issues).
