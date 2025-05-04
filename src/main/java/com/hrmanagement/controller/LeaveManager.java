package com.hrmanagement.controller;

import com.hrmanagement.dao.EmployeeDAO;
import com.hrmanagement.dao.LeaveDAO;
import com.hrmanagement.dao.impl.EmployeeDAOImpl;
import com.hrmanagement.dao.impl.LeaveDAOImpl;
import com.hrmanagement.model.Employee;
import com.hrmanagement.model.Leave;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class LeaveManager {
    private final LeaveDAO leaveDAO;
    private final EmployeeDAO employeeDAO;
    private final Scanner scanner;
    private final SimpleDateFormat dateFormat;

    public LeaveManager(Scanner scanner) {
        this.leaveDAO = new LeaveDAOImpl();
        this.employeeDAO = new EmployeeDAOImpl();
        this.scanner = scanner;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }

    public void displayMenu() {
        boolean exit = false;
        while (!exit) {
            displayLeaveMenu();

            int choice;
            try {
                System.out.print("Enter your choice: ");
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    requestLeave();
                    break;
                case 2:
                    approveLeave();
                    break;
                case 3:
                    rejectLeave();
                    break;
                case 4:
                    listLeaveRequests();
                    pressEnterToContinue();
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    private void displayLeaveMenu() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║               LEAVE MANAGEMENT                ║");
        System.out.println("╠═══════════════════════════════════════════════╣");
        System.out.println("║  [1] Request Leave                            ║");
        System.out.println("║  [2] Approve Leave Request                    ║");
        System.out.println("║  [3] Reject Leave Request                     ║");
        System.out.println("║  [4] View All Leave Requests                  ║");
        System.out.println("║  [0] Back to Main Menu                        ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
    }

    public void requestLeave() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║                 REQUEST LEAVE                 ║");
        System.out.println("╚═══════════════════════════════════════════════╝");

        List<Employee> employees = employeeDAO.getAllEmployees();
        if (employees.isEmpty()) {
            System.out.println("No employees in the system. Please add employees first.");
            pressEnterToContinue();
            return;
        }

        System.out.println("Employee List:");
        System.out.println("--------------------------------------------------");
        for (Employee employee : employees) {
            System.out.println(employee.getId() + ". " + employee.getName() + " - " + employee.getPosition());
        }
        System.out.println("--------------------------------------------------");

        System.out.print("Enter Employee ID: ");
        int employeeId;
        try {
            employeeId = Integer.parseInt(scanner.nextLine());
            if (employeeDAO.getEmployeeById(employeeId) == null) {
                System.out.println("Employee doesn't exist. Please try again.");
                pressEnterToContinue();
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
            pressEnterToContinue();
            return;
        }

        System.out.print("Start Date (dd/MM/yyyy): ");
        Date startDate;
        try {
            startDate = new Date(dateFormat.parse(scanner.nextLine()).getTime());
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use dd/MM/yyyy format.");
            pressEnterToContinue();
            return;
        }

        System.out.print("End Date (dd/MM/yyyy): ");
        Date endDate;
        try {
            endDate = new Date(dateFormat.parse(scanner.nextLine()).getTime());

            if (endDate.before(startDate)) {
                System.out.println("End date must be after start date.");
                pressEnterToContinue();
                return;
            }
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use dd/MM/yyyy format.");
            pressEnterToContinue();
            return;
        }

        System.out.print("Reason for Leave: ");
        String reason = scanner.nextLine();

        Leave leave = new Leave();
        leave.setEmployeeId(employeeId);
        leave.setStartDate(startDate);
        leave.setEndDate(endDate);
        leave.setReason(reason);
        leave.setStatus("Pending");

        if (leaveDAO.addLeave(leave)) {
            System.out.println("Leave request submitted successfully!");
        } else {
            System.out.println("Failed to submit leave request. Please try again later.");
        }
        pressEnterToContinue();
    }

    public void approveLeave() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║              APPROVE LEAVE REQUEST            ║");
        System.out.println("╚═══════════════════════════════════════════════╝");

        List<Leave> leaves = leaveDAO.getAllLeaves();
        boolean hasPendingLeaves = false;

        System.out.println("Pending Leave Requests:");
        System.out.println("--------------------------------------------------");
        for (Leave leave : leaves) {
            if ("Pending".equals(leave.getStatus())) {
                Employee employee = employeeDAO.getEmployeeById(leave.getEmployeeId());
                String employeeName = employee != null ? employee.getName() : "Unknown";
                System.out.println(leave.getId() + ". Employee: " + employeeName
                        + ", From: " + dateFormat.format(leave.getStartDate())
                        + ", To: " + dateFormat.format(leave.getEndDate())
                        + ", Reason: " + leave.getReason());
                hasPendingLeaves = true;
            }
        }
        System.out.println("--------------------------------------------------");

        if (!hasPendingLeaves) {
            System.out.println("No pending leave requests found.");
            pressEnterToContinue();
            return;
        }

        System.out.print("Enter Leave Request ID to approve: ");
        int leaveId;
        try {
            leaveId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
            pressEnterToContinue();
            return;
        }

        Leave leave = leaveDAO.getLeaveById(leaveId);
        if (leave == null) {
            System.out.println("Leave request not found. Please try again.");
            pressEnterToContinue();
            return;
        }

        if (!"Pending".equals(leave.getStatus())) {
            System.out.println("This request has already been processed.");
            pressEnterToContinue();
            return;
        }

        leave.setStatus("Approved");

        if (leaveDAO.updateLeave(leave)) {
            System.out.println("Leave request approved successfully!");
        } else {
            System.out.println("Failed to approve leave request. Please try again later.");
        }
        pressEnterToContinue();
    }

    public void rejectLeave() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║              REJECT LEAVE REQUEST             ║");
        System.out.println("╚═══════════════════════════════════════════════╝");

        List<Leave> leaves = leaveDAO.getAllLeaves();
        boolean hasPendingLeaves = false;

        System.out.println("Pending Leave Requests:");
        System.out.println("--------------------------------------------------");
        for (Leave leave : leaves) {
            if ("Pending".equals(leave.getStatus())) {
                Employee employee = employeeDAO.getEmployeeById(leave.getEmployeeId());
                String employeeName = employee != null ? employee.getName() : "Unknown";
                System.out.println(leave.getId() + ". Employee: " + employeeName
                        + ", From: " + dateFormat.format(leave.getStartDate())
                        + ", To: " + dateFormat.format(leave.getEndDate())
                        + ", Reason: " + leave.getReason());
                hasPendingLeaves = true;
            }
        }
        System.out.println("--------------------------------------------------");

        if (!hasPendingLeaves) {
            System.out.println("No pending leave requests found.");
            pressEnterToContinue();
            return;
        }

        System.out.print("Enter Leave Request ID to reject: ");
        int leaveId;
        try {
            leaveId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
            pressEnterToContinue();
            return;
        }

        Leave leave = leaveDAO.getLeaveById(leaveId);
        if (leave == null) {
            System.out.println("Leave request not found. Please try again.");
            pressEnterToContinue();
            return;
        }

        if (!"Pending".equals(leave.getStatus())) {
            System.out.println("This request has already been processed.");
            pressEnterToContinue();
            return;
        }

        leave.setStatus("Rejected");

        if (leaveDAO.updateLeave(leave)) {
            System.out.println("Leave request rejected successfully!");
        } else {
            System.out.println("Failed to reject leave request. Please try again later.");
        }
        pressEnterToContinue();
    }

    private void listLeaveRequests() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║              LEAVE REQUESTS LIST              ║");
        System.out.println("╚═══════════════════════════════════════════════╝");

        List<Leave> leaves = leaveDAO.getAllLeaves();
        if (leaves.isEmpty()) {
            System.out.println("No leave requests found in the system.");
            return;
        }

        System.out.printf("%-4s %-20s %-12s %-12s %-25s %-10s\n",
                "ID", "Employee", "Start Date", "End Date", "Reason", "Status");
        System.out.println("-----------------------------------------------------------------------------");

        for (Leave leave : leaves) {
            Employee employee = employeeDAO.getEmployeeById(leave.getEmployeeId());
            String employeeName = employee != null ? employee.getName() : "Unknown";
            System.out.printf("%-4d %-20s %-12s %-12s %-25s %-10s\n",
                    leave.getId(),
                    employeeName,
                    dateFormat.format(leave.getStartDate()),
                    dateFormat.format(leave.getEndDate()),
                    leave.getReason().length() > 25 ? leave.getReason().substring(0, 22) + "..." : leave.getReason(),
                    leave.getStatus());
        }
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    private void pressEnterToContinue() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}