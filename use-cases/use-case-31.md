# Use Case 31 — Total Population of a City

| Field | Description |
|---|---|
| Use Case ID | UC31 |
| Requirement | 31 |
| Responsible Member | Khaine Zaw Hein |
| Primary Actor | Report User |
| Status | Planned |

## Goal
Display the total population of a selected city using the World database.

## User Story
As a report user, I want to view the population of a selected city
so that I can understand the population size of that area.

## Preconditions
- The application is connected to the World database.
- The city table contains the required population data.

## Trigger
The user requests the city population total report.

## Main Success Flow
1. The user selects the population total report.
2. The user supplies the city name.
3. The system retrieves the selected city's population.
4. The system displays the area name and total population.

## Report Output
- Area name
- Total population
- Country name (to identify the selected area)

## Alternative and Error Flows
- If the city name is blank, request a valid name.
- If no matching city exists, display "No matching area found".
- If the name identifies multiple areas, show their country details and request clarification before calculating the selected area's population.
- If required data is unavailable, display "No data found".
- If a database query fails, display an error message.

## Postconditions
- The population total is displayed, or an explanatory message is shown.
- Database records remain unchanged.

## Acceptance Criteria
- The result equals city.Population for the selected city, identified by its city ID after resolving any duplicate names.
- Each relevant population record is counted once.
- Population values are handled using a type that supports totals exceeding 32-bit integer limits.
- The result is displayed as a clearly labelled whole number.
- A valid population of zero is distinguished from a missing area.
