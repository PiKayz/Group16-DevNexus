# Group16-DevNexus — Capital City Report Use Cases (Requirements 17–22)

Actors: **Report User** (requests and views reports) and **World Database** (provides city and country data). All six reports read the World database only; none of them adds, updates or deletes records. Capital cities are found with `country.Capital = city.ID`; countries without a recorded capital produce no row.

---

## UC-17 — All Capital Cities in the World by Population

### Goal

View every capital city in the world from largest to smallest population.

### Primary Actor

Report User.

### Supporting Actor

World Database.

### Preconditions

- The application can connect to the World database.
- The city and country tables are available.

### Main Flow

1. The user requests the "All Capital Cities in the World by Population" report.
2. The application identifies capital cities using `country.Capital = city.ID`.
3. The application sorts the capitals by city population in descending order.
4. The application displays the report.

### Report Columns

Name, Country, Population.

### Alternative Flows

- If no capital cities exist in the database, display "No matching records found".
- If the database connection or query fails, display an error and do not display a result.

### Postconditions

The report is displayed, sorted by population from largest to smallest, and the database is unchanged.

### Requirement Coverage

| Requirement | Report |
|---|---|
| 17 | All capital cities in the world, largest to smallest population |

---

## UC-18 — All Capital Cities in a Continent by Population

### Goal

View every capital city in one continent from largest to smallest population.

### Primary Actor

Report User.

### Supporting Actor

World Database.

### Preconditions

- The application can connect to the World database.
- The city and country tables are available.
- The user supplies a valid continent name.

### Main Flow

1. The user requests the "All Capital Cities in a Continent by Population" report.
2. The user supplies a continent name (for example, Asia).
3. The application validates the input.
4. The application identifies capital cities using `country.Capital = city.ID`.
5. The application keeps only capitals whose country belongs to the selected continent (`country.Continent = ?`).
6. The application sorts the capitals by city population in descending order.
7. The application displays the report.

### Report Columns

Name, Country, Population.

### Alternative Flows

- If the continent name is empty or invalid, explain the problem and request valid input.
- If no capitals match the continent, display "No matching records found".
- If the database connection or query fails, display an error and do not display a result.

### Postconditions

The report is displayed, sorted by population from largest to smallest, and the database is unchanged.

### Requirement Coverage

| Requirement | Report |
|---|---|
| 18 | All capital cities in a continent, largest to smallest population |

---

## UC-19 — All Capital Cities in a Region by Population

### Goal

View every capital city in one region from largest to smallest population.

### Primary Actor

Report User.

### Supporting Actor

World Database.

### Preconditions

- The application can connect to the World database.
- The city and country tables are available.
- The user supplies a valid region name.

### Main Flow

1. The user requests the "All Capital Cities in a Region by Population" report.
2. The user supplies a region name (for example, Southeast Asia).
3. The application validates the input.
4. The application identifies capital cities using `country.Capital = city.ID`.
5. The application keeps only capitals whose country belongs to the selected region (`country.Region = ?`).
6. The application sorts the capitals by city population in descending order.
7. The application displays the report.

### Report Columns

Name, Country, Population.

### Alternative Flows

- If the region name is empty or invalid, explain the problem and request valid input.
- If no capitals match the region, display "No matching records found".
- If the database connection or query fails, display an error and do not display a result.

### Postconditions

The report is displayed, sorted by population from largest to smallest, and the database is unchanged.

### Requirement Coverage

| Requirement | Report |
|---|---|
| 19 | All capital cities in a region, largest to smallest population |

---

## UC-20 — Top N Populated Capital Cities in the World

### Goal

View the N most populated capital cities in the world.

### Primary Actor

Report User.

### Supporting Actor

World Database.

### Preconditions

- The application can connect to the World database.
- The city and country tables are available.
- N is a positive integer.

### Main Flow

1. The user requests the "Top N Populated Capital Cities in the World" report.
2. The user supplies N (for example, 10).
3. The application validates that N is a positive integer.
4. The application identifies capital cities using `country.Capital = city.ID`.
5. The application sorts the capitals by city population in descending order.
6. The application limits the result to the first N capitals.
7. The application displays the report.

### Report Columns

Name, Country, Population.

### Alternative Flows

- If N is zero, negative or not a number, explain the problem and request a positive integer.
- If N exceeds the number of capitals, display all capitals.
- If the database connection or query fails, display an error and do not display a result.

### Postconditions

The report is displayed, sorted by population from largest to smallest, and the database is unchanged.

### Requirement Coverage

| Requirement | Report |
|---|---|
| 20 | Top N populated capital cities in the world |

---

## UC-21 — Top N Populated Capital Cities in a Continent

### Goal

View the N most populated capital cities in one continent.

### Primary Actor

Report User.

### Supporting Actor

World Database.

### Preconditions

- The application can connect to the World database.
- The city and country tables are available.
- The user supplies a valid continent name.
- N is a positive integer.

### Main Flow

1. The user requests the "Top N Populated Capital Cities in a Continent" report.
2. The user supplies a continent name (for example, Europe).
3. The user supplies N (for example, 5).
4. The application validates the continent name and that N is a positive integer.
5. The application identifies capital cities using `country.Capital = city.ID`.
6. The application keeps only capitals whose country belongs to the selected continent (`country.Continent = ?`).
7. The application sorts the capitals by city population in descending order.
8. The application limits the result to the first N capitals.
9. The application displays the report.

### Report Columns

Name, Country, Population.

### Alternative Flows

- If the continent name is empty or N is not a positive integer, explain the problem and request valid input.
- If N exceeds the number of capitals in the continent, display all matching capitals.
- If no capitals match the continent, display "No matching records found".
- If the database connection or query fails, display an error and do not display a result.

### Postconditions

The report is displayed, sorted by population from largest to smallest, and the database is unchanged.

### Requirement Coverage

| Requirement | Report |
|---|---|
| 21 | Top N populated capital cities in a continent |

---

## UC-22 — Top N Populated Capital Cities in a Region

### Goal

View the N most populated capital cities in one region.

### Primary Actor

Report User.

### Supporting Actor

World Database.

### Preconditions

- The application can connect to the World database.
- The city and country tables are available.
- The user supplies a valid region name.
- N is a positive integer.

### Main Flow

1. The user requests the "Top N Populated Capital Cities in a Region" report.
2. The user supplies a region name (for example, Caribbean).
3. The user supplies N (for example, 5).
4. The application validates the region name and that N is a positive integer.
5. The application identifies capital cities using `country.Capital = city.ID`.
6. The application keeps only capitals whose country belongs to the selected region (`country.Region = ?`).
7. The application sorts the capitals by city population in descending order.
8. The application limits the result to the first N capitals.
9. The application displays the report.

### Report Columns

Name, Country, Population.

### Alternative Flows

- If the region name is empty or N is not a positive integer, explain the problem and request valid input.
- If N exceeds the number of capitals in the region, display all matching capitals.
- If no capitals match the region, display "No matching records found".
- If the database connection or query fails, display an error and do not display a result.

### Postconditions

The report is displayed, sorted by population from largest to smallest, and the database is unchanged.

### Requirement Coverage

| Requirement | Report |
|---|---|
| 22 | Top N populated capital cities in a region |

---
