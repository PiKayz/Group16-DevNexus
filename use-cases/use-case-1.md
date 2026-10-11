# UC-01 — View All Countries in the World (Requirement 1) — implemented first

| Field | Description |
|---|---|
| Goal | List every country in the world, largest population first. |
| Primary actor | Report User |
| Supporting actor | World Database |
| Trigger | The user requests the "all countries in the world" report. |
| Preconditions | Common preconditions (section 4). |

**Main flow**

1. The user requests the all-countries-in-the-world report.
2. The application retrieves every country and its capital city name.
3. The application sorts the countries by population, descending.
4. The application displays the report.

**Report columns:** Code, Name, Continent, Region, Population, Capital.

**Alternative flows**

- A1: A country has no recorded capital — it stays in the report and its
  capital is shown as `N/A`.
- A2: The query fails — apply common error handling.

**Postconditions:** The report is displayed; the database is unchanged.

## Related Information

- Project: Group16-DevNexus (World database population reports)
- Developer: Htet Aung Shine
- Branch: `feature/country-reports`
- Requirement: 1
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
ORDER BY c.Population DESC, c.Name;
```

`LEFT JOIN` keeps countries that have no capital (shown as `N/A`).
