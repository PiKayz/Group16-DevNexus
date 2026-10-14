# UC-R12 — Top N Populated Cities in the World (Requirement 12, #50)

Project: Group16-DevNexus — Population Reporting System (World database)
Developer: Win Aung

**Goal:** See the N most populated cities in the world.

**Primary actor:** Report User. **Supporting actor:** World Database.

**Trigger:** The user asks for the "Top N cities in the world" report.

**Preconditions:** Common preconditions.

**Main flow**

1. The user selects the Top N cities in the world report.
2. The user enters N.
3. The application validates that N is a positive integer.
4. The application retrieves all cities with their country names.
5. The application sorts the cities by population, largest first.
6. The application keeps the first N cities.
7. The application displays the report.

**Report columns:** Name, Country, District, Population.

**Alternative flows**

- 3a. N is invalid: the application shows an error and asks for a valid N.
- 6a. N exceeds the number of cities: all cities are displayed.

**Postcondition:** The list of the N largest cities is displayed; the database is unchanged.

## Actors

- **Report User (primary):** Requests and reads the report.
- **World Database (supporting):** Supplies city, country and language data.

## Common Preconditions

- The application can connect to the World database.
- The `city`, `country` and `countrylanguage` tables contain data.
- For Top N reports (UC-R12 to UC-R16), N is a positive integer supplied by the user.

## Common Rules and Error Handling

- Reports are read-only; the database is never changed.
- Figures describe the supplied sample dataset, not current populations.
- Top N cities are sorted by population from largest to smallest; ties are
  ordered by city name so the result is repeatable.
- If N is zero, negative or not a number, the application explains the problem
  and does not run the report.
- If N is larger than the number of matching cities, all matching cities are shown.
- If a filter value (continent, region, country or district) is empty, the
  application asks for a valid value.
- If no city matches the filter, the application displays "No matching records found".
- If the database connection or a query fails, the application reports the
  failure and does not display a partial result as a successful report.
- Database statements and result sets are closed after use.
