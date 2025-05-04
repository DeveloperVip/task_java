package com.hrmanagement.dao.impl;

import com.hrmanagement.dao.EmployeeDAO;
import com.hrmanagement.model.Employee;
import com.hrmanagement.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAOImpl implements EmployeeDAO {

    @Override
    public boolean addEmployee(Employee employee) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Database.getConnection();
            String sql = "INSERT INTO employees (name, position, join_date) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getPosition());
            stmt.setDate(3, employee.getJoinDate());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
            return false;
        } finally {
            Database.close(conn, stmt, null);
        }
    }

    @Override
    public Employee getEmployeeById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Database.getConnection();
            String sql = "SELECT * FROM employees WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return extractEmployeeFromResultSet(rs);
            }

            return null;
        } catch (SQLException e) {
            System.err.println("Error getting employee information: " + e.getMessage());
            return null;
        } finally {
            Database.close(conn, stmt, rs);
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Employee> employees = new ArrayList<>();

        try {
            conn = Database.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM employees";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                employees.add(extractEmployeeFromResultSet(rs));
            }

            return employees;
        } catch (SQLException e) {
            System.err.println("Error getting employee list: " + e.getMessage());
            return employees;
        } finally {
            Database.close(conn, stmt, rs);
        }
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Database.getConnection();
            String sql = "UPDATE employees SET name = ?, position = ?, join_date = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getPosition());
            stmt.setDate(3, employee.getJoinDate());
            stmt.setInt(4, employee.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            return false;
        } finally {
            Database.close(conn, stmt, null);
        }
    }

    @Override
    public boolean deleteEmployee(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Database.getConnection();
            String sql = "DELETE FROM employees WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            return false;
        } finally {
            Database.close(conn, stmt, null);
        }
    }

    private Employee extractEmployeeFromResultSet(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getInt("id"));
        employee.setName(rs.getString("name"));
        employee.setPosition(rs.getString("position"));
        employee.setJoinDate(rs.getDate("join_date"));
        return employee;
    }
}