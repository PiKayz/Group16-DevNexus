package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Main
{
    private Connection con = null;

    public void connect()
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");

            try
            {
                Thread.sleep(30000);

                con = DriverManager.getConnection(
                        "jdbc:mysql://db:3306/employees?allowPublicKeyRetrieval=true&useSSL=false",
                        "root",
                        "example"
                );

                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException e)
            {
                System.out.println(
                        "Failed to connect to database attempt " + (i + 1)
                );
                System.out.println(e.getMessage());
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                System.out.println("Connection interrupted");
                return;
            }
        }
    }

    public Employee getEmployee(int ID)
    {
        if (con == null)
        {
            System.out.println("Not connected to the database");
            return null;
        }

        String strSelect =
                "SELECT emp_no, first_name, last_name "
                        + "FROM employees "
                        + "WHERE emp_no = " + ID;

        try (Statement stmt = con.createStatement();
             ResultSet rset = stmt.executeQuery(strSelect))
        {
            if (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                return emp;
            }

            return null;
        }
        catch (SQLException e)
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
                            + "Salary: " + emp.salary + "\n"
                            + emp.dept_name + "\n"
                            + "Manager: " + emp.manager + "\n"
            );
        }
    }

    public ArrayList<Employee> getSalariesByRole(String role)
            throws SQLException
    {
        if (con == null)
        {
            throw new SQLException("Not connected to the database");
        }

        if (role == null || role.isBlank())
        {
            throw new IllegalArgumentException("A role title is required");
        }

        String sql =
                "SELECT employees.emp_no, employees.first_name, "
                        + "employees.last_name, salaries.salary "
                        + "FROM employees "
                        + "JOIN salaries ON employees.emp_no = salaries.emp_no "
                        + "JOIN titles ON employees.emp_no = titles.emp_no "
                        + "WHERE salaries.to_date = '9999-01-01' "
                        + "AND titles.to_date = '9999-01-01' "
                        + "AND titles.title = ? "
                        + "ORDER BY employees.emp_no ASC";

        ArrayList<Employee> employees = new ArrayList<>();

        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            stmt.setString(1, role.trim());

            try (ResultSet rset = stmt.executeQuery())
            {
                while (rset.next())
                {
                    Employee emp = new Employee();
                    emp.emp_no = rset.getInt("emp_no");
                    emp.first_name = rset.getString("first_name");
                    emp.last_name = rset.getString("last_name");
                    emp.salary = rset.getInt("salary");
                    employees.add(emp);
                }
            }
        }

        return employees;
    }

    public void displaySalaries(ArrayList<Employee> employees)
    {
        if (employees == null || employees.isEmpty())
        {
            System.out.println("No employees found for this role");
            return;
        }

        System.out.printf(
                "%-12s %-20s %-20s %10s%n",
                "Employee No", "First Name", "Last Name", "Salary"
        );

        for (Employee emp : employees)
        {
            System.out.printf(
                    "%-12d %-20s %-20s %10d%n",
                    emp.emp_no,
                    emp.first_name,
                    emp.last_name,
                    emp.salary
            );
        }

        System.out.println("Total employees: " + employees.size());
    }

    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                con.close();
            }
            catch (SQLException e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    public static void main(String[] args)
    {
        Main app = new Main();
        app.connect();

        if (app.con == null)
        {
            System.out.println("Could not connect to the database");
            System.exit(1);
        }

        int exitCode = 0;

        try
        {
            Employee emp = app.getEmployee(255530);
            app.displayEmployee(emp);

            String role = "Engineer";
            System.out.println("Salary report for role: " + role);

            ArrayList<Employee> employees = app.getSalariesByRole(role);
            app.displaySalaries(employees);
        }
        catch (SQLException e)
        {
            System.out.println("Failed to produce salary report");
            System.out.println(e.getMessage());
            exitCode = 1;
        }
        finally
        {
            app.disconnect();
        }

        System.exit(exitCode);
    }
}