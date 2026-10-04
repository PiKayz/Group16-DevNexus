# Use Case 5: Add a New Employee

## Goal in Context
As an HR advisor, I want to add a new employee's details
so that I can ensure the new employee is paid.

## Scope
Group16-DevNexus HR System.

## Level
User goal.

## Preconditions
- The HR advisor has permission to add employee records.
- The employee database is available.
- The new employee's required details are available.
- The selected department exists.

## Success Condition
The employee record and initial department, role and salary
assignments are saved successfully.

## Failed Condition
The employee is not added, and the system explains the failure.
No partial employee record or assignments are saved.

## Primary Actor
HR Advisor.

## Trigger
The HR advisor selects the add employee option.

## Main Success Scenario
1. The HR advisor selects the add employee option.
2. The system requests the employee's personal details,
   hire date, department, role and initial salary.
3. The HR advisor enters the required information.
4. The system validates the information and checks
   that the employee number is unique.
5. The system displays the details for confirmation.
6. The HR advisor confirms the addition.
7. The system saves the employee and related assignments.
8. The system displays a success message.

## Extensions
- 4a. Required information is missing or invalid:
  the system identifies the errors and requests corrections.
- 4b. The employee number already exists:
  the system requests a unique employee number.
- 6a. The HR advisor cancels: no changes are saved.
- 7a. Saving fails: the system rolls back the changes
  and displays an error message.

## Sub-variations
- The HR advisor may correct the details before confirmation.

## Schedule
Agree the implementation sprint with the team.