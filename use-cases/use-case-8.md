# Use Case 8: Delete Employee Details

## Goal in Context
As an HR advisor, I want to delete an employee's details
so that the company is compliant with data retention legislation.

## Scope
Group16-DevNexus HR System.

## Level
User goal.

## Preconditions
- The HR advisor has permission to delete employee records.
- The employee database is available.
- The employee record exists.
- The record is eligible for deletion under the organisation's
  approved data retention policy.

## Success Condition
The employee record and related records eligible for deletion
are removed successfully.

## Failed Condition
The deletion is not completed, and the system explains the failure.
No partial deletion occurs.

## Primary Actor
HR Advisor.

## Trigger
The HR advisor requests deletion of an employee's details.

## Main Success Scenario
1. The HR advisor enters the employee number.
2. The system retrieves and displays the employee's details.
3. The system checks whether the record is eligible for deletion.
4. The system displays a deletion confirmation request.
5. The HR advisor confirms the deletion.
6. The system deletes the employee and related records
   eligible for deletion in one transaction.
7. The system displays a success message.

## Extensions
- 1a. The employee number is invalid:
  the system requests a valid employee number.
- 2a. The employee does not exist:
  the system displays an employee-not-found message.
- 3a. The record must be retained:
  the system explains why deletion is unavailable.
- 5a. The HR advisor cancels: no records are deleted.
- 6a. Deletion fails: the system rolls back the transaction
  and displays an error message.

## Sub-variations
None.

## Schedule
Agree the implementation sprint with the team.