# Use Case 28 — Total Population of a Region

| Field | Description |
|---|---|
| Use Case ID | UC28 |
| Requirement | 28 |
| Responsible Member | Khaine Zaw Hein |
| Primary Actor | Report User |
| Status | Planned |

## Goal
Display the total population of a selected region using the World database.

## User Story
As a report user, I want to view the population of a selected region
so that I can understand the population size of that area.

## Preconditions
- The application is connected to the World database.
- The country table contains the required population data.

## Trigger
The user requests the region population total report.

## Main Success Flow
1. The user selects the population total report.
2. The user supplies the region name.
3. The system sums country populations matching the selected region.
4. The system displays the area name and total population.

## Report Output
- Area name
- Total population

## Alternative and Error Flows
- If the region name is blank, request a valid name.
- If no matching region exists, display "No matching area found".
- If required data is unavailable, display "No data found".
- If a database query fails, display an error message.

## Postconditions
- The population total is displayed, or an explanatory message is shown.
- Database records remain unchanged.

## Acceptance Criteria
- The total equals the sum of country.Population where country.Region matches the selected region.
- Each relevant population record is counted once.
- Population values are handled using a type that supports totals exceeding 32-bit integer limits.
- The result is displayed as a clearly labelled whole number.
- A valid population of zero is distinguished from a missing area.
