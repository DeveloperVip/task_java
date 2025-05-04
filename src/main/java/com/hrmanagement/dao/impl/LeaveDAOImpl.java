package com.hrmanagement.dao.impl;

import com.hrmanagement.dao.LeaveDAO;
import com.hrmanagement.model.Leave;
import com.hrmanagement.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAOImpl implements LeaveDAO {

    @Override
    public boolean addLeave(Leave leave) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Database.getConnection();
            String sql = "INSERT INTO leaves (employee_id, start_date, end_date, reason, status) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, leave.getEmployeeId());
            stmt.setDate(2, leave.getStartDate());
            stmt.setDate(3, leave.getEndDate());
            stmt.setString(4, leave.getReason());
            stmt.setString(5, leave.getStatus());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding leave request: " + e.getMessage());
            return false;
        } finally {
            Database.close(conn, stmt, null);
        }
    }

    @Override
    public Leave getLeaveById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Database.getConnection();
            String sql = "SELECT * FROM leaves WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return extractLeaveFromResultSet(rs);
            }

            return null;
        } catch (SQLException e) {
            System.err.println("Error getting leave information: " + e.getMessage());
            return null;
        } finally {
            Database.close(conn, stmt, rs);
        }
    }

    @Override
    public List<Leave> getAllLeaves() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Leave> leaves = new ArrayList<>();

        try {
            conn = Database.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM leaves";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                leaves.add(extractLeaveFromResultSet(rs));
            }

            return leaves;
        } catch (SQLException e) {
            System.err.println("Error getting leave list: " + e.getMessage());
            return leaves;
        } finally {
            Database.close(conn, stmt, rs);
        }
    }

    @Override
    public List<Leave> getLeavesByEmployeeId(int employeeId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Leave> leaves = new ArrayList<>();

        try {
            conn = Database.getConnection();
            String sql = "SELECT * FROM leaves WHERE employee_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                leaves.add(extractLeaveFromResultSet(rs));
            }

            return leaves;
        } catch (SQLException e) {
            System.err.println("Error getting leaves by employee ID: " + e.getMessage());
            return leaves;
        } finally {
            Database.close(conn, stmt, rs);
        }
    }

    @Override
    public boolean updateLeave(Leave leave) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Database.getConnection();
            String sql = "UPDATE leaves SET employee_id = ?, start_date = ?, end_date = ?, reason = ?, status = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, leave.getEmployeeId());
            stmt.setDate(2, leave.getStartDate());
            stmt.setDate(3, leave.getEndDate());
            stmt.setString(4, leave.getReason());
            stmt.setString(5, leave.getStatus());
            stmt.setInt(6, leave.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating leave request: " + e.getMessage());
            return false;
        } finally {
            Database.close(conn, stmt, null);
        }
    }

    @Override
    public boolean deleteLeave(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Database.getConnection();
            String sql = "DELETE FROM leaves WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting leave request: " + e.getMessage());
            return false;
        } finally {
            Database.close(conn, stmt, null);
        }
    }

    private Leave extractLeaveFromResultSet(ResultSet rs) throws SQLException {
        Leave leave = new Leave();
        leave.setId(rs.getInt("id"));
        leave.setEmployeeId(rs.getInt("employee_id"));
        leave.setStartDate(rs.getDate("start_date"));
        leave.setEndDate(rs.getDate("end_date"));
        leave.setReason(rs.getString("reason"));
        leave.setStatus(rs.getString("status"));
        return leave;
    }
}