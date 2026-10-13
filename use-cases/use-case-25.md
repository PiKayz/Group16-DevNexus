# Use Case 25 — Population Breakdown by Country

| Field | Description |
|---|---|
| Use Case ID | UC25 |
| Requirement | 25 |
| Responsible Member | Han Htal Sein |
| Primary Actor | Report User |
| Status | Planned |

## Goal
Display the total population, population living in cities, and
population living outside cities for each country, including
the percentages of both groups.

## User Story
As a report user, I want to view population breakdowns by
country so that I can compare populations living in cities
and outside cities.

## Preconditions
- The application is connected to the World database.
- The country and city tables contain data.

## Trigger
The user requests the population breakdown by country.

## Main Success Flow
1. The user requests the report.
2. The system retrieves each country's total population.
3. The system sums the populations of recorded cities belonging
   to each country.
4. The system subtracts city population from total population
   to calculate the population living outside cities.
5. The system calculates the percentages of both groups.
6. The system displays one row per country.

## Report Output
- Country name
- Total population
- Population living in cities
- Percentage living in cities
- Population living outside cities
- Percentage living outside cities

## Alternative and Error Flows
- If a country has no city records, treat its city population as zero.
- If no data is available, display "No data found".
- If total population is zero, display both percentages as 0%.
- If the database query fails, display an error message.

## Calculations
- Outside-city population = Total population - City population
- City percentage = (City population / Total population) × 100
- Outside-city percentage =
  (Outside-city population / Total population) × 100

## Postconditions
- The report is displayed.
- Database records remain unchanged.

## Acceptance Criteria
- Each country appears once, including countries without city records.
- Cities are matched using city.CountryCode and country.Code.
- Each city's population is counted once.
- Each country's total population is counted once.
- City population uses the cities recorded in the World database.
- Percentages are displayed to two decimal places.