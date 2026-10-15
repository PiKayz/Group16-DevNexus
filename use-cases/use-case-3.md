# UC-03 — View All Countries in a Region (Requirement 3)

| Field | Description |
|---|---|
| Goal | List every country in one region, largest population first. |
| Primary actor | Report User |
| Supporting actor | World Database |
| Trigger | The user requests the report for a region. |
| Preconditions | Common preconditions; the user supplies a region name. |

**Main flow**

1. The user requests the all-countries-in-a-region report.
2. The user supplies the region name.
3. The application validates the name.
4. The application retrieves matching countries with capital names.
5. The application sorts them by population, descending, and displays them.

**Report columns:** Code, Name, Continent, Region, Population, Capital.

**Alternative flows**

- A1: Blank or unknown region — explain and ask for a valid region.
- A2: Capital missing — display `N/A`.
- A3: No matches — display "No matching records found".

**Postconditions:** The report is displayed; the database is unchanged.

## Related Information

- Project: Group16-DevNexus (World database population reports)
- Developer: Htet Aung Shine
- Branch: `feature/country-reports`
- Requirement: 3
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
ORDER BY c.Population DESC, c.Name;
```

`LEFT JOIN` keeps countries that have no capital (shown as `N/A`).
