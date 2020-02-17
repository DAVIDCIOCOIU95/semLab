# USE CASE: 4 Add new details of an employee to ensure successful payment

## CHARACTERISTIC INFORMATION

### Goal in Context

As an HR advisor I want to add a new employee's details so that I can ensure the new employee is paid.

### Scope

Company.

### Level

Primary task.

### Preconditions

Database contains the structure necessary in order to add a new employee. 

### Success End Condition

A new employee's details is added to the database

### Failed End Condition

No details are added.

### Primary Actor

HR Advisor.

### Trigger

A request for payment is sent to HR.

## MAIN SUCCESS SCENARIO

1. Need to pay an employee.
2. HR advisor adds new details for an employee.

## EXTENSIONS

3. **Role does not exist**:
    1. HR advisor finds out there is no one to add.

## SUB-VARIATIONS

Wrong details inserted. 

## SCHEDULE

**DUE DATE**: Release 1.0