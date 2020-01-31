package com.napier.sem;

import java.sql.*;

public class App
{
    public static void main(String[] args)
    {
        // Create new Application
        App a = new App();

        // Connect to database
        a.connect();
        // Get Employee
        //Employee emp = a.getEmployee(255530);
        // Display results
       // a.displayEmployee(emp);

        // Try to display world details
        a.displayWorld();


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
    public void connect()
    {
        try
        {
            // Load Database driver
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");
            try
            {
                // Wait a bit for db to start
                Thread.sleep(30000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/world?useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                // Close connection
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    public Employee getEmployee(int ID)
    {
        try
        {
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

            // Select department and manager
            String strSelectDep = "SELECT departments.dept_name, departments.dept_no " +
                    "FROM departments JOIN dept_emp ON departments.dept_no=dept_emp.dept_no " +
                    "WHERE dept_emp.emp_no = " + ID;
            rset =stmt.executeQuery(strSelectDep);
            String deptNumb;
            if(rset.next()) {
                emp.dept_name = rset.getString("departments.dept_name");
                deptNumb = rset.getString("departments.dept_no");
            } else {
                return null;
            }

            // Select manager
            String strSelectManager = "SELECT first_name, last_name " +
                    "FROM employees JOIN dept_manager ON employees.emp_no=dept_manager.emp_no " +
                    "JOIN departments ON dept_manager.dept_no=departments.dept_no " +
                    "WHERE departments.dept_no = " +
                    "(SELECT dept_no " +
                    "FROM dept_emp " +
                    "WHERE emp_no = " + ID + ")";
            rset = stmt.executeQuery(strSelectManager);
            if(rset.next()) {
                emp.manager = rset.getString("employees.first_name") + " " + rset.getString("employees.last_name");
            }
            return emp;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    public void displayEmployee(Employee emp)
    {
        if (emp != null)
        {
            System.out.println(
                    emp.emp_no + " "
                            + emp.first_name + " "
                            + emp.last_name + "\n"
                            + emp.title + "\n"
                            + "Salary:" + emp.salary + "\n"
                            + emp.dept_name + "\n"
                            + "Manager: " + emp.manager + "\n");
        }
    }

    public void displayWorld() {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT Name, Population " +
                            "FROM country " +
                            "ORDER BY Population ASC";

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            ResultSetMetaData rsmd = rset.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while(rset.next()) {

                    String columnValue = rset.getString("Name");
                    System.out.println(columnValue );
            }

            // Check one is returned

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return;
        }
    }
}