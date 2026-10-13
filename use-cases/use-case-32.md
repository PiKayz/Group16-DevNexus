# UC-R32 — Language Speaker Report (Requirement 32, #70)

Project: Group16-DevNexus — Population Reporting System (World database)
Developer: Win Aung

**Goal:** Estimate how many people speak Chinese, English, Hindi, Spanish and
Arabic, and what percentage of the world population they represent.

**Primary actor:** Report User. **Supporting actor:** World Database.

**Trigger:** The user asks for the language speaker report.

**Preconditions:** Common preconditions (no user input is needed).

**Main flow**

1. The user selects the language speaker report.
2. The application retrieves country populations and the language percentages
   recorded for each country.
3. The application selects the languages Chinese, English, Hindi, Spanish and Arabic.
4. For each country and language, the application estimates speakers as
   country population × language percentage ÷ 100.
5. The application adds the estimates across all countries for each language.
6. The application calculates the world population as the sum of all country populations.
7. The application calculates each language's percentage of the world population
   as estimated speakers ÷ world population × 100.
8. The application sorts the languages by estimated speakers, largest first.
9. The application displays all five languages.

**Report columns:** Language, Estimated Speakers, Percentage of World Population.

**Calculation rules**

- Decimal arithmetic is used for calculation; values are rounded only for display
  (speakers to whole numbers, percentages to two decimal places).
- All recorded speakers are counted, not only official languages.
- Language groups can overlap because one person may speak several languages,
  so the percentages must not be added together.

**Alternative flows**

- 3a. A language has no recorded entries: it is shown with zero estimated speakers
  and a note that data is missing.
- 6a. The world population is zero: percentages are displayed as "N/A".
- 2a. The query fails: the application reports the failure and shows no report.

**Postcondition:** The five-language report is displayed; the database is unchanged.

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
