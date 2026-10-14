# UC-17 — All Capital Cities in the World by Population

## Goal

View every capital city in the world from largest to smallest population.

## Primary Actor

Report User.

## Supporting Actor

World Database.

## Preconditions

- The application can connect to the World database.
- The city and country tables are available.

## Main Flow

1. The user requests the "All Capital Cities in the World by Population" report.
2. The application identifies capital cities using `country.Capital = city.ID`.
3. The application sorts the capitals by city population in descending order.
4. The application displays the report.

## Report Columns

Name, Country, Population.

## Alternative Flows

- If no capital cities exist in the database, display "No matching records found".
- If the database connection or query fails, display an error and do not display a result.

## Postconditions

The report is displayed, sorted by population from largest to smallest, and the database is unchanged.

## Requirement Coverage

| Requirement | Report |
|---|---|
| 17 | All capital cities in the world, largest to smallest population |
