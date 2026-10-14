# UC-19 — All Capital Cities in a Region by Population

## Goal

View every capital city in one region from largest to smallest population.

## Primary Actor

Report User.

## Supporting Actor

World Database.

## Preconditions

- The application can connect to the World database.
- The city and country tables are available.
- The user supplies a valid region name.

## Main Flow

1. The user requests the "All Capital Cities in a Region by Population" report.
2. The user supplies a region name (for example, Southeast Asia).
3. The application validates the input.
4. The application identifies capital cities using `country.Capital = city.ID`.
5. The application keeps only capitals whose country belongs to the selected region (`country.Region = ?`).
6. The application sorts the capitals by city population in descending order.
7. The application displays the report.

## Report Columns

Name, Country, Population.

## Alternative Flows

- If the region name is empty or invalid, explain the problem and request valid input.
- If no capitals match the region, display "No matching records found".
- If the database connection or query fails, display an error and do not display a result.

## Postconditions

The report is displayed, sorted by population from largest to smallest, and the database is unchanged.

## Requirement Coverage

| Requirement | Report |
|---|---|
| 19 | All capital cities in a region, largest to smallest population |
