package com.hrmanagement.dao.impl;

import com.hrmanagement.dao.CandidateDAO;
import com.hrmanagement.model.Candidate;
import com.hrmanagement.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidateDAOImpl implements CandidateDAO {

    @Override
    public boolean addCandidate(Candidate candidate) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Database.getConnection();
            String sql = "INSERT INTO candidates (name, position_applied, status) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, candidate.getName());
            stmt.setString(2, candidate.getPositionApplied());
            stmt.setString(3, candidate.getStatus());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding candidate: " + e.getMessage());
            return false;
        } finally {
            Database.close(conn, stmt, null);
        }
    }

    @Override
    public Candidate getCandidateById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Database.getConnection();
            String sql = "SELECT * FROM candidates WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return extractCandidateFromResultSet(rs);
            }

            return null;
        } catch (SQLException e) {
            System.err.println("Error getting candidate information: " + e.getMessage());
            return null;
        } finally {
            Database.close(conn, stmt, rs);
        }
    }

    @Override
    public List<Candidate> getAllCandidates() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Candidate> candidates = new ArrayList<>();

        try {
            conn = Database.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM candidates";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                candidates.add(extractCandidateFromResultSet(rs));
            }

            return candidates;
        } catch (SQLException e) {
            System.err.println("Error getting candidate list: " + e.getMessage());
            return candidates;
        } finally {
            Database.close(conn, stmt, rs);
        }
    }

    @Override
    public boolean updateCandidate(Candidate candidate) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Database.getConnection();
            String sql = "UPDATE candidates SET name = ?, position_applied = ?, status = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, candidate.getName());
            stmt.setString(2, candidate.getPositionApplied());
            stmt.setString(3, candidate.getStatus());
            stmt.setInt(4, candidate.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating candidate: " + e.getMessage());
            return false;
        } finally {
            Database.close(conn, stmt, null);
        }
    }

    @Override
    public boolean deleteCandidate(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Database.getConnection();
            String sql = "DELETE FROM candidates WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting candidate: " + e.getMessage());
            return false;
        } finally {
            Database.close(conn, stmt, null);
        }
    }

    private Candidate extractCandidateFromResultSet(ResultSet rs) throws SQLException {
        Candidate candidate = new Candidate();
        candidate.setId(rs.getInt("id"));
        candidate.setName(rs.getString("name"));
        candidate.setPositionApplied(rs.getString("position_applied"));
        candidate.setStatus(rs.getString("status"));
        return candidate;
    }
}