package com.hrmanagement.dao.impl;

import com.hrmanagement.dao.ContractDAO;
import com.hrmanagement.model.Contract;
import com.hrmanagement.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractDAOImpl implements ContractDAO {

    @Override
    public boolean addContract(Contract contract) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Database.getConnection();
            String sql = "INSERT INTO contracts (employee_id, start_date, end_date, status) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, contract.getEmployeeId());
            stmt.setDate(2, contract.getStartDate());
            stmt.setDate(3, contract.getEndDate());
            stmt.setString(4, contract.getStatus());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding contract: " + e.getMessage());
            return false;
        } finally {
            Database.close(conn, stmt, null);
        }
    }

    @Override
    public Contract getContractById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Database.getConnection();
            String sql = "SELECT * FROM contracts WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return extractContractFromResultSet(rs);
            }

            return null;
        } catch (SQLException e) {
            System.err.println("Error getting contract information: " + e.getMessage());
            return null;
        } finally {
            Database.close(conn, stmt, rs);
        }
    }

    @Override
    public List<Contract> getAllContracts() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Contract> contracts = new ArrayList<>();

        try {
            conn = Database.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM contracts";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                contracts.add(extractContractFromResultSet(rs));
            }

            return contracts;
        } catch (SQLException e) {
            System.err.println("Error getting contract list: " + e.getMessage());
            return contracts;
        } finally {
            Database.close(conn, stmt, rs);
        }
    }

    @Override
    public List<Contract> getContractsByEmployeeId(int employeeId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Contract> contracts = new ArrayList<>();

        try {
            conn = Database.getConnection();
            String sql = "SELECT * FROM contracts WHERE employee_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                contracts.add(extractContractFromResultSet(rs));
            }

            return contracts;
        } catch (SQLException e) {
            System.err.println("Error getting contracts by employee ID: " + e.getMessage());
            return contracts;
        } finally {
            Database.close(conn, stmt, rs);
        }
    }

    @Override
    public boolean updateContract(Contract contract) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Database.getConnection();
            String sql = "UPDATE contracts SET employee_id = ?, start_date = ?, end_date = ?, status = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, contract.getEmployeeId());
            stmt.setDate(2, contract.getStartDate());
            stmt.setDate(3, contract.getEndDate());
            stmt.setString(4, contract.getStatus());
            stmt.setInt(5, contract.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating contract: " + e.getMessage());
            return false;
        } finally {
            Database.close(conn, stmt, null);
        }
    }

    @Override
    public boolean deleteContract(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Database.getConnection();
            String sql = "DELETE FROM contracts WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting contract: " + e.getMessage());
            return false;
        } finally {
            Database.close(conn, stmt, null);
        }
    }

    private Contract extractContractFromResultSet(ResultSet rs) throws SQLException {
        Contract contract = new Contract();
        contract.setId(rs.getInt("id"));
        contract.setEmployeeId(rs.getInt("employee_id"));
        contract.setStartDate(rs.getDate("start_date"));
        contract.setEndDate(rs.getDate("end_date"));
        contract.setStatus(rs.getString("status"));
        return contract;
    }
}