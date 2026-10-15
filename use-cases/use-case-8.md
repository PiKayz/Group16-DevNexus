# Use Case 8 — All Cities in a Continent by Population

| Field | Description |
|---|---|
| Use Case ID | UC08 |
| Requirement | 8 |
| Responsible Member | Htet Aung Lin |
| Primary Actor | Report User |
| Status | Planned |

## Goal
Display all cities in a selected continent, ordered by population from largest to smallest.

## User Story
As a report user, I want to view all cities in a continent ordered by population
so that I can compare city populations and identify the largest cities.

## Preconditions
- The application is connected to the World database.
- The city and country tables are available and contain report data.

## Trigger
The user requests the continent city report.

## Main Success Flow
1. The user selects the report.
2. The user supplies the continent name.
3. The system retrieves cities matching the selected continent.
4. The system retrieves each city's name, country, district and population.
5. The system orders cities by population in descending order.
6. The system displays all matching cities.

## Report Output
- City name
- Country name
- District
- Population

## Alternative and Error Flows
- If the continent name is blank, request a valid name.
- If the supplied continent has no matching cities, display "No cities found".
- If no report data is available, display "No cities found".
- If the database query fails, display an error message.

## Postconditions
- The requested report is displayed, or an explanatory message is shown.
- Database records remain unchanged.

## Acceptance Criteria
- Cities are filtered by country.Continent.
- Cities are matched to countries using city.CountryCode and country.Code.
- Each matching city appears once, including cities with zero population.
- Population is sorted from largest to smallest.
- Cities with equal populations are ordered by city name, then city ID.
- All matching cities are displayed without a Top N limit.
- Every row contains city name, country name, district and population.
