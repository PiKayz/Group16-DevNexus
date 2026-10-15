# Use Case 24 — Population Breakdown by Region

| Field | Description |
|---|---|
| Use Case ID | UC24 |
| Requirement | 24 |
| Responsible Member | Han Htal Sein |
| Primary Actor | Report User |
| Status | Planned |

## Goal
Display the total population, population living in cities, and
population living outside cities for each region, including
the percentages of both groups.

## User Story
As a report user, I want to view population breakdowns by
region so that I can compare populations living in cities
and outside cities.

## Preconditions
- The application is connected to the World database.
- The country and city tables contain data.

## Trigger
The user requests the population breakdown by region.

## Main Success Flow
1. The user requests the report.
2. The system groups countries by region.
3. The system sums country populations for each region.
4. The system sums the populations of recorded cities for each region.
5. The system subtracts city population from total population
   to calculate the population living outside cities.
6. The system calculates the percentages of both groups.
7. The system displays one row per region.

## Report Output
- Region name
- Total population
- Population living in cities
- Percentage living in cities
- Population living outside cities
- Percentage living outside cities

## Alternative and Error Flows
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
- Each region appears once.
- Country populations are counted once, even when a country
  has multiple cities.
- Countries without city records contribute to total population.
- City population uses the cities recorded in the World database.
- Percentages are displayed to two decimal places.