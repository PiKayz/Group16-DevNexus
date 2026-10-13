# Use Case 29 — Total Population of a Country

| Field | Description |
|---|---|
| Use Case ID | UC29 |
| Requirement | 29 |
| Responsible Member | Khaine Zaw Hein |
| Primary Actor | Report User |
| Status | Planned |

## Goal
Display the total population of a selected country using the World database.

## User Story
As a report user, I want to view the population of a selected country
so that I can understand the population size of that area.

## Preconditions
- The application is connected to the World database.
- The country table contains the required population data.

## Trigger
The user requests the country population total report.

## Main Success Flow
1. The user selects the population total report.
2. The user supplies the country name.
3. The system retrieves the selected country's population.
4. The system displays the area name and total population.

## Report Output
- Area name
- Total population

## Alternative and Error Flows
- If the country name is blank, request a valid name.
- If no matching country exists, display "No matching area found".
- If required data is unavailable, display "No data found".
- If a database query fails, display an error message.

## Postconditions
- The population total is displayed, or an explanatory message is shown.
- Database records remain unchanged.

## Acceptance Criteria
- The result equals country.Population for the selected country.
- Each relevant population record is counted once.
- Population values are handled using a type that supports totals exceeding 32-bit integer limits.
- The result is displayed as a clearly labelled whole number.
- A valid population of zero is distinguished from a missing area.
