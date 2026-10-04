# Use Case 4: Produce Salary Report by Role

## Goal in Context
As an HR advisor, I want to produce a report on the salary of
employees of a given role so that I can support financial
reporting of the organisation.

## Scope
Group16-DevNexus HR System.

## Level
User goal.

## Preconditions
- The HR advisor has permission to access salary information.
- The employee database is available.
- Employee roles and salary records exist.

## Success Condition
A report displays all current employees in the selected role,
including employee number, first name, last name and current salary.
Results are ordered by employee number in ascending order.

## Failed Condition
No report is produced, and the system explains the failure.
Existing employee data remains unchanged.

## Primary Actor
HR Advisor.

## Trigger
The HR advisor requests a salary report for a selected role.

## Main Success Scenario
1. The HR advisor selects the salary report by role option.
2. The system requests a role title.
3. The HR advisor enters a role, such as Engineer.
4. The system retrieves employees with that current role
   and their current salaries.
5. The system orders the results by employee number.
6. The system displays the salary report.

## Extensions
- 3a. The role is blank: the system requests a valid role.
- 4a. No matching employees exist: the system displays
  a no-results message.
- 4b. The database is unavailable: the system reports
  that the report could not be generated.

## Sub-variations
- The HR advisor can select another role and request another report.

## Schedule
Deliver during the Lab 3b sprint.