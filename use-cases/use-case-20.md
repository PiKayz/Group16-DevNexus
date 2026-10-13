# UC-20 — Top N Populated Capital Cities in the World

## Goal

View the N most populated capital cities in the world.

## Primary Actor

Report User.

## Supporting Actor

World Database.

## Preconditions

- The application can connect to the World database.
- The city and country tables are available.
- N is a positive integer.

## Main Flow

1. The user requests the "Top N Populated Capital Cities in the World" report.
2. The user supplies N (for example, 10).
3. The application validates that N is a positive integer.
4. The application identifies capital cities using `country.Capital = city.ID`.
5. The application sorts the capitals by city population in descending order.
6. The application limits the result to the first N capitals.
7. The application displays the report.

## Report Columns

Name, Country, Population.

## Alternative Flows

- If N is zero, negative or not a number, explain the problem and request a positive integer.
- If N exceeds the number of capitals, display all capitals.
- If the database connection or query fails, display an error and do not display a result.

## Postconditions

The report is displayed, sorted by population from largest to smallest, and the database is unchanged.

## Requirement Coverage

| Requirement | Report |
|---|---|
| 20 | Top N populated capital cities in the world |
