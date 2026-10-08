[![workflow](https://github.com/PiKayz/Group16-DevNexus/actions/workflows/main.yml/badge.svg)](https://github.com/PiKayz/Group16-DevNexus/actions)
* Develop Build Status [![Develop Build](https://img.shields.io/github/actions/workflow/status/PiKayz/Group16-DevNexus/main.yml?branch=develop&label=build)](https://github.com/PiKayz/Group16-DevNexus/actions/workflows/main.yml)
License [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=flat-square)](https://opensource.org/licenses/Apache-2.0)
Release [![Releases](https://img.shields.io/github/release/PiKayz/Group16-DevNexus/all.svg?style=flat-square)](https://github.com/PiKayz/Group16-DevNexus/releases)

# Population Reporting System

The application reads the supplied MySQL World sample database. Its population
figures describe that dataset rather than current populations.

## Country report: requirement 1

On the `feature/country-population-report` branch, the application displays all
countries in the world from largest population to smallest. The report contains
Code, Name, Continent, Region, Population, and Capital. Countries without a
recorded capital remain in the report with `N/A` in the Capital column.

This branch implements 1 requirement of 32, which is 3.125%. The other 31
reporting requirements remain to be implemented.

The report was verified against the supplied World database: 239 unique country
rows, all six columns, descending population order, and countries with no
recorded capital. Five unit tests cover report output and error handling.

## Build and run

```sh
mvn package
docker compose up --build --abort-on-container-exit --exit-code-from app
```

The application checks the database, prints the country report, and exits.
Maven runs the country-report unit tests during `package`.

```sh
docker compose down
```

Report user stories and their checklists are tracked in
[GitHub issues](https://github.com/PiKayz/Group16-DevNexus/issues).
