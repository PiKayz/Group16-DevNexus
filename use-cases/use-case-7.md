# Use Case 7: Update Employee Details

## Goal in Context
As an HR advisor, I want to update an employee's details
so that employee details are kept up-to-date.

## Scope
Group16-DevNexus HR System.

## Level
User goal.

## Preconditions
- The HR advisor has permission to update employee records.
- The employee database is available.
- The employee record exists.

## Success Condition
The confirmed changes are saved successfully.
Unchanged details and relevant employment history are preserved.

## Failed Condition
The update is not completed, and the system explains the failure.
No partial changes are saved.

## Primary Actor
HR Advisor.

## Trigger
The HR advisor selects the update employee option.

## Main Success Scenario
1. The HR advisor enters an employee number.
2. The system retrieves and displays the employee's details.
3. The HR advisor edits the required details.
4. The system validates the changes.
5. The system displays the proposed changes for confirmation.
6. The HR advisor confirms the update.
7. The system saves the changes and preserves employment
   history where applicable.
8. The system displays a success message.

## Extensions
- 1a. The employee number is invalid:
  the system requests a valid employee number.
- 2a. The employee does not exist:
  the system displays an employee-not-found message.
- 4a. The changes are invalid:
  the system identifies the errors and requests corrections.
- 6a. The HR advisor cancels: no changes are saved.
- 7a. Saving fails: the system rolls back the changes
  and displays an error message.

## Sub-variations
- Personal details can be updated without changing employment
  assignments.
- A role, salary or department change may require an effective
  date and a new assignment record.

## Schedule
Agree the implementation sprint with the team.