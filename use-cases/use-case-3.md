# Use Case 3: Produce Salary Report for My Department

## Goal in Context
As a department manager, I want to produce a report on the
salary of employees in my department so that I can support
financial reporting for my department.

## Scope
Group16-DevNexus HR System.

## Level
User goal.

## Preconditions
- The manager is identified and authorised to access
  salary information for their department.
- The manager is assigned to a department.
- The employee database is available.

## Success Condition
The report displays current employees in the manager's department
with their employee number, first name, last name and current salary.
Results are ordered by employee number in ascending order.

## Failed Condition
No report is produced, and the system explains the failure.
Salary information from unauthorised departments is not disclosed.
Existing employee data remains unchanged.

## Primary Actor
Department Manager.

## Trigger
The manager requests a salary report for their department.

## Main Success Scenario
1. The manager selects the salary report for their department.
2. The system identifies the manager's department.
3. The system checks the manager's access permission.
4. The system retrieves employees currently assigned to that
   department and their current salaries.
5. The system orders the results by employee number.
6. The system displays the salary report.

## Extensions
- 2a. No department is assigned: the system explains
  that a department assignment is required.
- 3a. Access is denied: the system displays an access-denied message.
- 4a. No matching employees exist: the system displays
  a no-results message.
- 4b. The database is unavailable: the system displays
  an error message.

## Sub-variations
None.

## Schedule
Agree the implementation sprint with the team.