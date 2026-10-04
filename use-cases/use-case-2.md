# Use Case 2: Produce Salary Report by Department

## Goal in Context
As an HR advisor, I want to produce a report on the salary
of employees in a department so that I can support financial
reporting of the organisation.

## Scope
Group16-DevNexus HR System.

## Level
User goal.

## Preconditions
- The HR advisor has permission to access salary information.
- The employee database is available.
- Department records exist.

## Success Condition
The report displays current employees in the selected department
with their employee number, first name, last name and current salary.
Results are ordered by employee number in ascending order.

## Failed Condition
No report is produced, and the system explains the failure.
Existing employee data remains unchanged.

## Primary Actor
HR Advisor.

## Trigger
The HR advisor requests a salary report for a department.

## Main Success Scenario
1. The HR advisor selects the salary report by department option.
2. The system displays available departments.
3. The HR advisor selects a department.
4. The system retrieves employees currently assigned to that
   department and their current salaries.
5. The system orders the results by employee number.
6. The system displays the salary report.

## Extensions
- 3a. No valid department is selected: the system requests
  a valid selection.
- 4a. No matching employees exist: the system displays
  a no-results message.
- 4b. The database is unavailable: the system displays
  an error message.

## Sub-variations
- The HR advisor can select another department
  and request another report.

## Schedule
Agree the implementation sprint with the team.