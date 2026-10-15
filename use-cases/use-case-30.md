# Use Case 30 — Total Population of a District

| Field | Description |
|---|---|
| Use Case ID | UC30 |
| Requirement | 30 |
| Responsible Member | Khaine Zaw Hein |
| Primary Actor | Report User |
| Status | Planned |

## Goal
Display the total population of a selected district using the World database.

## User Story
As a report user, I want to view the population of a selected district
so that I can understand the population size of that area.

## Preconditions
- The application is connected to the World database.
- The city table contains the required population data.

## Trigger
The user requests the district population total report.

## Main Success Flow
1. The user selects the population total report.
2. The user supplies the district name.
3. The system sums populations of cities in the selected district.
4. The system displays the area name and total population.

## Report Output
- Area name
- Total population
- Country name (to identify the selected area)

## Alternative and Error Flows
- If the district name is blank, request a valid name.
- If no matching district exists, display "No matching area found".
- If the name identifies multiple areas, show their country details and request clarification before calculating the selected area's population.
- If a selected district has cities with zero population, display a total of zero.
- If required data is unavailable, display "No data found".
- If a database query fails, display an error message.

## Postconditions
- The population total is displayed, or an explanatory message is shown.
- Database records remain unchanged.

## Acceptance Criteria
- The total equals the sum of city.Population for cities in the selected district and country.
- Each relevant population record is counted once.
- Population values are handled using a type that supports totals exceeding 32-bit integer limits.
- The result is displayed as a clearly labelled whole number.
- A valid population of zero is distinguished from a missing area.
