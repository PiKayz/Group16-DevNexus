# UC-22 — Top N Populated Capital Cities in a Region

## Goal

View the N most populated capital cities in one region.

## Primary Actor

Report User.

## Supporting Actor

World Database.

## Preconditions

- The application can connect to the World database.
- The city and country tables are available.
- The user supplies a valid region name.
- N is a positive integer.

## Main Flow

1. The user requests the "Top N Populated Capital Cities in a Region" report.
2. The user supplies a region name (for example, Caribbean).
3. The user supplies N (for example, 5).
4. The application validates the region name and that N is a positive integer.
5. The application identifies capital cities using `country.Capital = city.ID`.
6. The application keeps only capitals whose country belongs to the selected region (`country.Region = ?`).
7. The application sorts the capitals by city population in descending order.
8. The application limits the result to the first N capitals.
9. The application displays the report.

## Report Columns

Name, Country, Population.

## Alternative Flows

- If the region name is empty or N is not a positive integer, explain the problem and request valid input.
- If N exceeds the number of capitals in the region, display all matching capitals.
- If no capitals match the region, display "No matching records found".
- If the database connection or query fails, display an error and do not display a result.

## Postconditions

The report is displayed, sorted by population from largest to smallest, and the database is unchanged.

## Requirement Coverage

| Requirement | Report |
|---|---|
| 22 | Top N populated capital cities in a region |
