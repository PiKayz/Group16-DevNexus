# Use Case 25 — Population Breakdown by Country

| Field | Description |
|---|---|
| Use Case ID | UC25 |
| Requirement | 25 |
| Responsible Member | Han Htal Sein |
| Primary Actor | Report User |
| Status | Implemented; integration validation in progress |

## Goal
Display each country's total population, population living in
recorded cities, and population living outside recorded cities,
including the percentages of both groups.

## User Story
As a report user, I want to view population breakdowns by
country so that I can compare populations living in cities
and outside cities.

## Preconditions
- The application is connected to the World database.
- The country and city tables are available.

## Trigger
The user requests the population breakdown by country.

## Main Success Flow
1. The user requests the report.
2. The system retrieves each country's total population.
3. The system sums the populations of recorded cities belonging
   to each country.
4. The system checks whether city population exceeds the
   country's total population.
5. For consistent data, the system calculates the population
   living outside cities and the percentages of both groups.
6. The system displays one row per country, ordered by total
   population from largest to smallest.

## Report Output
- Country name
- Total population
- Population living in cities
- Percentage living in cities
- Population living outside cities
- Percentage living outside cities

## Alternative and Error Flows

### A1 — Country Has No City Records
Treat city population as zero and calculate the remaining
values using the country's total population.

### A2 — Total Population Is Zero
If city population is also zero, display both percentages as 0%.

### A3 — Inconsistent Population Data
If recorded city population exceeds the country's total population:
1. Display the original country and city population values.
2. Display N/A for outside-city population and both percentages.
3. Display a message explaining the data inconsistency.
4. Continue processing the remaining countries.

Do not modify the source data or display the inconsistent
derived values as valid results.

### A4 — No Country Data
Display "No data found".

### A5 — Database Query Fails
Display an error message and finish the application with
a non-zero exit code.

## Calculations
For consistent data:
- Outside-city population = Total population - City population
- City percentage = (City population / Total population) × 100
- Outside-city percentage =
  (Outside-city population / Total population) × 100

## Postconditions
- The report or an explanatory message is displayed.
- Database records remain unchanged.

## Acceptance Criteria
- Each country appears once, including countries without city records.
- Cities are matched using city.CountryCode and country.Code.
- Each city's population is counted once.
- Country population is not duplicated when joining city data.
- Population totals use a type supporting values beyond
  the 32-bit integer limit.
- Valid percentages are displayed to two decimal places.
- Zero population is handled without division by zero.
- Inconsistent derived values are displayed as N/A with an explanation.
- Results are sorted by total population descending, then country name.

## Data Interpretation
"Population living in cities" means the sum of populations
of cities recorded in the World database. It does not
necessarily represent every urban settlement in a country.

## Validation Examples
- A country with total population 1,000 and city population 400
  displays outside-city population 600, city percentage 40.00%,
  and outside-city percentage 60.00%.
- A country without city records displays city population zero.
- A country with both populations zero displays percentages 0.00%.
- Cocos (Keeling) Islands has total population 600 and recorded
  city population 670 in the supplied dataset. The report displays
  N/A for derived values and a data inconsistency message.