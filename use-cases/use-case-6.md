# Use Case 6: View Employee Details

## Goal in Context
As an HR advisor, I want to view an employee's details
so that the employee's promotion request can be supported.

## Scope
Group16-DevNexus HR System.

## Level
User goal.

## Preconditions
- The HR advisor has permission to view employee details.
- The employee database is available.

## Success Condition
The system displays the selected employee's number, first name,
last name, current job title, current salary, department and manager.

## Failed Condition
Employee details cannot be displayed, and the system explains
the failure. Existing employee data remains unchanged.

## Primary Actor
HR Advisor.

## Trigger
The HR advisor requests an employee's details.

## Main Success Scenario
1. The HR advisor selects the view employee option.
2. The system requests an employee number.
3. The HR advisor enters the employee number.
4. The system validates the employee number.
5. The system retrieves the employee's personal details
   and current employment information.
6. The system displays the employee's details.

## Extensions
- 4a. The employee number is missing or invalid:
  the system requests a valid employee number.
- 5a. The employee does not exist:
  the system displays an employee-not-found message.
- 5b. Some employment information is unavailable:
  the system displays the available details and marks
  missing information as unavailable.
- 5c. The database is unavailable:
  the system displays an error message.

## Sub-variations
- The HR advisor can enter another employee number
  to view a different employee.

## Schedule
Basic employee lookup completed in Lab 3a.
Schedule the remaining employment details with the team.