# Use Case 26 — Total Population of the World

| Field | Description |
|---|---|
| Use Case ID | UC26 |
| Requirement | 26 |
| Responsible Member | Khaine Zaw Hein |
| Primary Actor | Report User |
| Status | Planned |

## Goal
Display the total population of the world using the World database.

## User Story
As a report user, I want to view the population of the world
so that I can understand the population size of that area.

## Preconditions
- The application is connected to the World database.
- The country table contains the required population data.

## Trigger
The user requests the world population total report.

## Main Success Flow
1. The user selects the population total report.
2. The system sums the population of all countries.
3. The system displays the area name and total population.

## Report Output
- Area name
- Total population

## Alternative and Error Flows
- If required data is unavailable, display "No data found".
- If a database query fails, display an error message.

## Postconditions
- The population total is displayed, or an explanatory message is shown.
- Database records remain unchanged.

## Acceptance Criteria
- The total equals the sum of country.Population for all countries.
- Each relevant population record is counted once.
- Population values are handled using a type that supports totals exceeding 32-bit integer limits.
- The result is displayed as a clearly labelled whole number.
- A valid population of zero is distinguished from a missing area.
