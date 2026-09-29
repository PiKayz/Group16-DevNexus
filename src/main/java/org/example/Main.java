package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
            System.exit(-1);
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
                System.out.println("Failed to connect to database attempt " + (i + 1));
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

        try
        {
            Employee emp = app.getEmployee(255530);
            app.displayEmployee(emp);
        }
        finally
        {
            app.disconnect();
        }
    }
}