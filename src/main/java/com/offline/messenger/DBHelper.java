package com.offline.messenger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


public class DBHelper {
    private static final String DB_URL = "jdbc:sqlite:users.db";

    public DBHelper() {
        createUsersTable();
        createGroupsTable(); //store group names
    }

    private void createUsersTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                         "username TEXT PRIMARY KEY, " +
                         "password TEXT NOT NULL)";
            stmt.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createGroupsTable() { //storing group names 
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS groups (" +
                         "group_name TEXT PRIMARY KEY, " +
                         "members TEXT NOT NULL)";
            stmt.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    public boolean validateLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getUserCount() {
        String sql = "SELECT COUNT(*) AS total FROM users";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<String> getAllUsernamesExcept(String username) {
        List<String> users = new ArrayList<>();
        String sql = "SELECT username FROM users WHERE username != ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public boolean saveGroup(String groupName, List<String> members) {
        String sql = "INSERT OR REPLACE INTO groups (group_name, members) VALUES (?, ?)";
        String membersStr = String.join(",", members);

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, groupName);
            pstmt.setString(2, membersStr);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error saving group: " + e.getMessage());
            return false;
        }
    }
    
    public List<String> getGroupsForUser(String username) {
        List<String> groups = new ArrayList<>();
        String sql = "SELECT group_name, members FROM groups";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String members = rs.getString("members");
                if (Arrays.asList(members.split(",")).contains(username)) {
                    groups.add(rs.getString("group_name"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }
    
    public boolean removeUserFromGroup(String username, String groupName) {
        String sqlSelect = "SELECT members FROM groups WHERE group_name = ?";
        String sqlUpdate = "UPDATE groups SET members = ? WHERE group_name = ?";
        String sqlDelete = "DELETE FROM groups WHERE group_name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement selectStmt = conn.prepareStatement(sqlSelect)) {

            selectStmt.setString(1, groupName);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                List<String> members = new ArrayList<>(Arrays.asList(rs.getString("members").split(",")));
                members.remove(username);

                if (members.isEmpty()) {
                    try (PreparedStatement deleteStmt = conn.prepareStatement(sqlDelete)) {
                        deleteStmt.setString(1, groupName);
                        deleteStmt.executeUpdate();
                        return true;
                    }
                } else {
                    try (PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate)) {
                        updateStmt.setString(1, String.join(",", members));
                        updateStmt.setString(2, groupName);
                        updateStmt.executeUpdate();
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public List<String> getGroupMembers(String groupName) {
        List<String> members = new ArrayList<>();
        String sql = "SELECT members FROM groups WHERE group_name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, groupName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String membersStr = rs.getString("members");
                if (membersStr != null && !membersStr.isEmpty()) {
                    members = Arrays.asList(membersStr.split(","));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }
}
