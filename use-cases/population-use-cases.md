\# Group16-DevNexus — Population Reporting Use Cases



\## 1. Purpose



The Population Reporting System provides reports using the MySQL

World sample database. Users can compare populations of countries,

cities and capital cities, view population totals and distributions,

and estimate the number of speakers of selected languages.



\## 2. Actors



\- \*\*Report User:\*\* Requests and views population reports.

\- \*\*World Database:\*\* Provides country, city and language data.



\## 3. System Scope



The application reads the existing World database and displays reports.

It does not add, update or delete population records.



Reports describe the supplied sample dataset, not current population figures.



\## 4. Common Preconditions



\- The application can connect to the World database.

\- The country, city and countrylanguage tables are available.

\- The user supplies the required report scope and any filter values.

\- For a Top N report, N must be a positive integer.



\## 5. Common Error Handling



\- If the database connection fails, display an error and stop the report.

\- If input is invalid, explain the problem and request valid input.

\- If no matching records exist, display "No matching records found".

\- If a query fails, report the failure without displaying a successful result.

\- Close database query resources after use.



\## UC-01 — View Country Population Reports



\### Goal



Compare countries from largest population to smallest population.



\### Primary Actor



Report User.



\### Supporting Actor



World Database.



\### Main Flow



1\. The user requests a country population report.

2\. The user selects world, continent or region as the scope.

3\. For a continent or region, the user supplies its name.

4\. The user chooses all matching countries or the Top N countries.

5\. For Top N, the user supplies a positive integer.

6\. The application validates the input.

7\. The application retrieves matching countries and their capital names.

8\. The application sorts countries by population in descending order.

9\. For Top N, the application limits the result to N countries.

10\. The application displays the report.



\### Report Columns



Code, Name, Continent, Region, Population, Capital.



\### Alternative Flows



\- A country without a recorded capital remains in the report,

&#x20; with its capital displayed as "N/A".

\- If N exceeds the number of matching countries, display all matches.

\- Apply the common error handling rules.



\### Postconditions



The requested country report is displayed and the database is unchanged.



\### Requirement Coverage



| Requirement | Report |

|---|---|

| 1 | All countries in the world |

| 2 | All countries in a continent |

| 3 | All countries in a region |

| 4 | Top N countries in the world |

| 5 | Top N countries in a continent |

| 6 | Top N countries in a region |



All reports are sorted by population from largest to smallest.



\## UC-02 — View City Population Reports



\### Goal



Compare cities from largest population to smallest population.



\### Primary Actor



Report User.



\### Supporting Actor



World Database.



\### Main Flow



1\. The user requests a city population report.

2\. The user selects world, continent, region, country or district.

3\. The user supplies the filter required for the selected scope.

4\. The user chooses all matching cities or the Top N cities.

5\. For Top N, the user supplies a positive integer.

6\. The application validates the input.

7\. The application retrieves matching cities and their country names.

8\. The application sorts cities by population in descending order.

9\. For Top N, the application limits the result to N cities.

10\. The application displays the report.



\### Report Columns



Name, Country, District, Population.



\### Alternative Flows



\- If N exceeds the number of matching cities, display all matches.

\- Where a district name occurs in multiple countries, clarify the

&#x20; intended country or explicitly report all matching districts.

\- Apply the common error handling rules.



\### Postconditions



The requested city report is displayed and the database is unchanged.



\### Requirement Coverage



| Requirement | Report |

|---|---|

| 7 | All cities in the world |

| 8 | All cities in a continent |

| 9 | All cities in a region |

| 10 | All cities in a country |

| 11 | All cities in a district |

| 12 | Top N cities in the world |

| 13 | Top N cities in a continent |

| 14 | Top N cities in a region |

| 15 | Top N cities in a country |

| 16 | Top N cities in a district |



All reports are sorted by population from largest to smallest.



\## UC-03 — View Capital City Population Reports



\### Goal



Compare capital cities from largest population to smallest population.



\### Primary Actor



Report User.



\### Supporting Actor



World Database.



\### Main Flow



1\. The user requests a capital city population report.

2\. The user selects world, continent or region as the scope.

3\. For a continent or region, the user supplies its name.

4\. The user chooses all matching capitals or the Top N capitals.

5\. For Top N, the user supplies a positive integer.

6\. The application validates the input.

7\. The application identifies capitals using country.Capital and city.ID.

8\. The application sorts capitals by city population in descending order.

9\. For Top N, the application limits the result to N capitals.

10\. The application displays the report.



\### Report Columns



Name, Country, Population.



\### Alternative Flows



\- Countries without a recorded capital do not produce a capital city row.

\- If N exceeds the number of matching capitals, display all matches.

\- Apply the common error handling rules.



\### Postconditions



The requested capital city report is displayed and the database is unchanged.



\### Requirement Coverage



| Requirement | Report |

|---|---|

| 17 | All capital cities in the world |

| 18 | All capital cities in a continent |

| 19 | All capital cities in a region |

| 20 | Top N capital cities in the world |

| 21 | Top N capital cities in a continent |

| 22 | Top N capital cities in a region |



All reports are sorted by population from largest to smallest.



\## UC-04 — View Population Distribution Reports



\### Goal



View total population, population living in cities and population

not living in cities for each continent, region or country.



\### Primary Actor



Report User.



\### Supporting Actor



World Database.



\### Main Flow



1\. The user requests a population distribution report.

2\. The user selects grouping by continent, region or country.

3\. The application calculates total population from country records.

4\. The application calculates city population from associated city records.

5\. The application calculates non-city population:

&#x20;  non-city population = total population - city population.

6\. The application calculates city and non-city percentages.

7\. The application displays one row for each group.



\### Report Columns



Name, Total Population, City Population, City Percentage,

Non-City Population, Non-City Percentage.



\### Calculation Rules



\- City percentage = city population / total population × 100.

\- Non-city percentage = non-city population / total population × 100.

\- Aggregate country totals separately from city totals to avoid

&#x20; counting a country's population repeatedly for each city.

\- If total population is zero, display percentages as "N/A".

\- City population refers to cities recorded in the sample database.

\- If city population exceeds total population, flag the dataset

&#x20; inconsistency instead of silently replacing the calculated values.



\### Alternative Flows



Apply the common error handling rules.



\### Postconditions



The distribution report is displayed and the database is unchanged.



\### Requirement Coverage



| Requirement | Report |

|---|---|

| 23 | Population distribution for each continent |

| 24 | Population distribution for each region |

| 25 | Population distribution for each country |



\## UC-05 — View Population Totals



\### Goal



View the population of the world or a selected geographical area.



\### Primary Actor



Report User.



\### Supporting Actor



World Database.



\### Main Flow



1\. The user requests a population total.

2\. The user selects world, continent, region, country, district or city.

3\. The user supplies the filter required for the selected scope.

4\. The application validates the input.

5\. For world, continent, region or country, the application uses

&#x20;  population values from the country table.

6\. For a district, the application sums populations of matching

&#x20;  cities recorded in the city table.

7\. For a city, the application retrieves the selected city's population.

8\. The application displays the selected area and its population.



\### Report Columns



Area Type, Area Name, Population.



\### Alternative Flows



\- If a city name matches multiple records, request a country,

&#x20; district or city ID to identify the intended city.

\- If a district name occurs in multiple countries, clarify the

&#x20; intended country or explicitly report all matching districts.

\- Apply the common error handling rules.



\### Postconditions



The requested population total is displayed and the database is unchanged.



\### Requirement Coverage



| Requirement | Report |

|---|---|

| 26 | Population of the world |

| 27 | Population of a continent |

| 28 | Population of a region |

| 29 | Population of a country |

| 30 | Population of a district |

| 31 | Population of a city |



\## UC-06 — View Language Population Report



\### Goal



Estimate the number of people who speak Chinese, English, Hindi,

Spanish and Arabic, and their percentage of the world population.



\### Primary Actor



Report User.



\### Supporting Actor



World Database.



\### Main Flow



1\. The user requests the language population report.

2\. The application retrieves country populations and language percentages.

3\. The application selects Chinese, English, Hindi, Spanish and Arabic.

4\. For each language, the application estimates speakers in each country:

&#x20;  estimated speakers = country population × language percentage / 100.

5\. The application sums the estimates across countries for each language.

6\. The application calculates world population from the country table.

7\. The application calculates each language's percentage of world population.

8\. The application sorts languages by estimated speakers in descending order.

9\. The application displays all five languages.



\### Report Columns



Language, Estimated Speakers, Percentage of World Population.



\### Calculation Rules



\- World percentage = estimated speakers / world population × 100.

\- Use decimal arithmetic during calculation and round for display.

\- The report includes recorded speakers, not only official languages.

\- Language groups may overlap because a person may speak multiple languages.

\- If world population is zero, display percentages as "N/A".



\### Alternative Flows



\- If a requested language has no recorded entries, show zero

&#x20; estimated speakers and identify the missing data.

\- Apply the common error handling rules.



\### Postconditions



The language report is displayed and the database is unchanged.



\### Requirement Coverage



| Requirement | Report |

|---|---|

| 32 | Chinese, English, Hindi, Spanish and Arabic speaker estimates |



\## 6. Implementation Status



This document specifies planned behaviour.



The World database connection and table checks have been implemented

and tested. The use cases must be implemented and verified separately

before being marked complete.



\## 7. Use Case Diagram



The use case diagram will be maintained as:



use-cases/population-system-use-case.drawio



The diagram must show:



\- Report User.

\- Population Reporting System boundary.

\- UC-01 through UC-06 inside the system boundary.

\- World Database outside the system boundary.

\- Associations between Report User and each use case.

\- Associations between World Database and each use case.

