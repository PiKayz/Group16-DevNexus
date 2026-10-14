# Use Case 7 — All Cities in the World by Population

| Field | Description |
|---|---|
| Use Case ID | UC07 |
| Requirement | 7 |
| Responsible Member | Htet Aung Lin |
| Primary Actor | Report User |
| Status | Planned |

## Goal
Display all cities in the world, ordered by population from largest to smallest.

## User Story
As a report user, I want to view all cities in the world ordered by population
so that I can compare city populations and identify the largest cities.

## Preconditions
- The application is connected to the World database.
- The city and country tables are available and contain report data.

## Trigger
The user requests the world city report.

## Main Success Flow
1. The user selects the report.
2. The system retrieves all cities in the World database.
3. The system retrieves each city's name, country, district and population.
4. The system orders cities by population in descending order.
5. The system displays all matching cities.

## Report Output
- City name
- Country name
- District
- Population

## Alternative and Error Flows
- If no report data is available, display "No cities found".
- If the database query fails, display an error message.

## Postconditions
- The requested report is displayed, or an explanatory message is shown.
- Database records remain unchanged.

## Acceptance Criteria
- All city records are included.
- Cities are matched to countries using city.CountryCode and country.Code.
- Each matching city appears once, including cities with zero population.
- Population is sorted from largest to smallest.
- Cities with equal populations are ordered by city name, then city ID.
- All matching cities are displayed without a Top N limit.
- Every row contains city name, country name, district and population.
