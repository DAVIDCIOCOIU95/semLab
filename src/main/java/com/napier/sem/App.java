package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        // Create new Application
        App a = new App();

        // Connect to database
        a.connect();
        // Get Employee
        //Employee emp = a.getEmployee(255530);
        Department dep = a.getDepartment("d005");
        a.displayDepartment(dep);
        // Display results
        //a.displayEmployee(emp);

        // Extract employee salary information
        a.printSalariesByDepartment(a.getSalariesByDepartment(dep));

        // Test the size of the returned data - should be 240124
        //System.out.println(employees.size());
        //a.printSalaries(employees);


        // Disconnect from database
        a.disconnect();
    }

    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect() {
        try {
            // Load Database driver
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(30000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect() {
        if (con != null) {
            try {
                // Close connection
                con.close();
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    // Other methods start here

    //** Gets employee's first name, last name and salary*/
    public Employee getEmployee(int ID) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, first_name, last_name, salary "
                            + "FROM employees JOIN salaries ON employees.emp_no = salaries.emp_no "
                            + "WHERE employees.emp_no = " + ID;

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            Employee emp = new Employee();
            // Return new employee if valid.
            // Check one is returned
            if (rset.next()) {

                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.salary = rset.getInt("salary");
            } else {
                return null;
            }

            // Select department
            String strSelectDep = "SELECT departments.dept_name, departments.dept_no " +
                    "FROM departments JOIN dept_emp ON departments.dept_no=dept_emp.dept_no " +
                    "WHERE dept_emp.emp_no = " + ID;
            rset = stmt.executeQuery(strSelectDep);
            if (rset.next()) {
                String depNo = rset.getString("departments.dept_no");
                Department dep = getDepartment(depNo);
                emp.dept = dep;
            } else {
                return null;
            }
            return emp;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    public void displayEmployee(Employee emp) {
        if (emp != null) {
            System.out.println(
                    emp.emp_no + " "
                            + emp.first_name + " "
                            + emp.last_name + "\n"
                            + emp.title + "\n"
                            + "Salary:" + emp.salary + "\n"
                            + emp.dept + "\n"
                            + "Manager: " + emp.manager.emp_no + "\n");
        }
    }

    public void displayDepartment(Department dep) {
        if(dep != null) {
            System.out.println(
                    dep.dept_name + "\n" +
                            dep.dept_no + "\n" +
                            dep.manager.emp_no
            );
        }
    }

    /**
     * Gets all the current employees and salaries.
     *
     * @return A list of all employees and salaries, or null if there is an error.
     */
    public ArrayList<Employee> getAllSalaries() {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries "
                            + "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' "
                            + "ORDER BY employees.emp_no ASC";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    /**
     * Prints a list of employees.
     *
     * @param employees The list of employees to print.
     */
    public void printSalaries(ArrayList<Employee> employees) {
        // Print header
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : employees) {
            String emp_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
        }
    }

    public void getAllDeps() {
        try{
            Statement stmt = con.createStatement();
            String strSelect = "SELECT dept_no FROM departments";
            ResultSet rset = stmt.executeQuery(strSelect);
            while(rset.next()) {
                String currentDepNo = rset.getString("dept_no");
                System.out.printf(currentDepNo);
            }
                return;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
        }



    }
    public Department getDepartment(String dept_no) {
        // Select department
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();

            // Handle dep name and number
            String strSelectDep = "SELECT dept_name, dept_no " +
                    "FROM departments WHERE dept_no = \'" + dept_no + "\'";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelectDep);
            Department dep = new Department();
            //String deptNumb;
            if (rset.next()) {
                dep.dept_name = rset.getString("dept_name");
                dep.dept_no = rset.getString("dept_no");
            } else {
                return null;
            }

            // Handle manager of the department
            String strSelectDepManager = "SELECT dept_manager.emp_no, employees.first_name, employees.last_name " +
                    "FROM dept_manager JOIN employees ON dept_manager.emp_no=employees.emp_no" +
                    " WHERE dept_manager.dept_no = \'" + dept_no + "\'";
            rset = stmt.executeQuery(strSelectDepManager);
            Employee depManager = new Employee();
            if(rset.next()) {
                depManager.emp_no = rset.getInt("dept_manager.emp_no");
                depManager.first_name = rset.getString("employees.first_name");
                depManager.last_name = rset.getString("employees.last_name");
                dep.manager = depManager;
            } else {
                return null;
            }
            // Return the dep with all the fields
            return dep;
        } catch (Exception e) {
        System.out.println(e.getMessage());
        System.out.println("Failed to get department details");
        return null;
        }
    }



    public ArrayList<Employee> getSalariesByDepartment(Department dept) {
        try{
            String depNo;
            if(dept != null)
            {
                depNo = dept.dept_no;
            } else {
                return null;
            }
            Statement stmt = con.createStatement();
            String strSelect =
            "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary " +
                    "FROM employees, salaries, dept_emp, departments " +
                    "WHERE employees.emp_no = salaries.emp_no " +
                    "AND employees.emp_no = dept_emp.emp_no " +
                    "AND dept_emp.dept_no = departments.dept_no " +
                    "AND salaries.to_date = '9999-01-01' " +
                    "AND departments.dept_no = \'" + depNo + "\'" +
                    "ORDER BY employees.emp_no ASC";

            ResultSet rset = stmt.executeQuery(strSelect);
            ArrayList<Employee> salariesByDepartment = new ArrayList<Employee>();
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                salariesByDepartment.add(emp);
            }
            return  salariesByDepartment;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    public void printSalariesByDepartment(ArrayList<Employee> employees) {
        // Print header
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : employees) {
            String emp_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
        }
    }


}