# UC-06 — View Top N Countries in a Region (Requirement 6)

| Field | Description |
|---|---|
| Goal | List the N most populated countries in one region. |
| Primary actor | Report User |
| Supporting actor | World Database |
| Trigger | The user requests the Top N countries in a region. |
| Preconditions | Common preconditions; the user supplies a region and N. |

**Main flow**

1. The user requests the Top N countries in a region.
2. The user supplies the region name and N.
3. The application validates both inputs.
4. The application retrieves matching countries, sorted by population
   descending.
5. The application limits the result to N countries and displays it.

**Report columns:** Code, Name, Continent, Region, Population, Capital.

**Alternative flows**

- A1: Invalid region or N — explain and ask again.
- A2: N exceeds the number of matching countries — display all matches.
- A3: Capital missing — display `N/A`.
- A4: No matches — display "No matching records found".

**Postconditions:** The report is displayed; the database is unchanged.

## Related Information

- Project: Group16-DevNexus (World database population reports)
- Developer: Htet Aung Shine
- Branch: `feature/country-reports`
- Requirement: 6
- Sorting: population, largest to smallest

## Common Preconditions

- The application can connect to the World database.
- The `country` and `city` tables contain data.

## Common Error Handling

- Database connection fails: display an error and stop the report.
- Invalid input (blank name, N <= 0): explain the problem and ask again.
- No matching records: display "No matching records found".
- Query fails: report the failure; do not show a partial result as success.
- Database statements and result sets are closed after use.

## Data Source

```sql
SELECT c.Code, c.Name, c.Continent, c.Region, c.Population,
       COALESCE(ci.Name, 'N/A') AS Capital
FROM country c
LEFT JOIN city ci ON c.Capital = ci.ID
WHERE c.Region = ?
ORDER BY c.Population DESC, c.Name
LIMIT ?;
```

`LEFT JOIN` keeps countries that have no capital (shown as `N/A`).
