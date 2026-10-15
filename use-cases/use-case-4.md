# UC-04 — View Top N Countries in the World (Requirement 4)

| Field | Description |
|---|---|
| Goal | List the N most populated countries in the world. |
| Primary actor | Report User |
| Supporting actor | World Database |
| Trigger | The user requests the Top N countries report. |
| Preconditions | Common preconditions; the user supplies N. |

**Main flow**

1. The user requests the Top N countries in the world.
2. The user supplies N.
3. The application validates that N is a positive integer.
4. The application retrieves countries with capital names, sorted by
   population descending.
5. The application limits the result to N countries and displays it.

**Report columns:** Code, Name, Continent, Region, Population, Capital.

**Alternative flows**

- A1: N is not a positive integer — explain and ask again.
- A2: N exceeds the number of countries — display all countries.
- A3: Capital missing — display `N/A`.

**Postconditions:** The report is displayed; the database is unchanged.

## Related Information

- Project: Group16-DevNexus (World database population reports)
- Developer: Htet Aung Shine
- Branch: `feature/country-reports`
- Requirement: 4
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
ORDER BY c.Population DESC, c.Name
LIMIT ?;
```

`LEFT JOIN` keeps countries that have no capital (shown as `N/A`).
