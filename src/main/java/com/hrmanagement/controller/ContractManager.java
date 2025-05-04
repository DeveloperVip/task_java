package com.hrmanagement.controller;

import com.hrmanagement.dao.ContractDAO;
import com.hrmanagement.dao.EmployeeDAO;
import com.hrmanagement.dao.impl.ContractDAOImpl;
import com.hrmanagement.dao.impl.EmployeeDAOImpl;
import com.hrmanagement.model.Contract;
import com.hrmanagement.model.Employee;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

/**
 * Manager class for Contract operations
 */
public class ContractManager {
    private final ContractDAO contractDAO;
    private final EmployeeDAO employeeDAO;
    private final Scanner scanner;
    private final SimpleDateFormat dateFormat;

    public ContractManager(Scanner scanner) {
        this.contractDAO = new ContractDAOImpl();
        this.employeeDAO = new EmployeeDAOImpl();
        this.scanner = scanner;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }

    /**
     * Display contract management menu
     */
    public void displayMenu() {
        boolean exit = false;
        while (!exit) {
            displayContractMenu();

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
                    addContract();
                    break;
                case 2:
                    extendContract();
                    break;
                case 3:
                    terminateContract();
                    break;
                case 4:
                    listContracts();
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

    private void displayContractMenu() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║             CONTRACT MANAGEMENT               ║");
        System.out.println("╠═══════════════════════════════════════════════╣");
        System.out.println("║  [1] Add New Contract                         ║");
        System.out.println("║  [2] Extend Contract                          ║");
        System.out.println("║  [3] Terminate Contract                       ║");
        System.out.println("║  [4] View All Contracts                       ║");
        System.out.println("║  [0] Back to Main Menu                        ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
    }

    /**
     * Add a new contract
     */
    public void addContract() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║               ADD NEW CONTRACT                ║");
        System.out.println("╚═══════════════════════════════════════════════╝");

        // Display employee list for selection
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

        // Get employee ID
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

        // Get contract details
        System.out.print("Start Date (dd/MM/yyyy): ");
        Date startDate;
        try {
            startDate = new Date(dateFormat.parse(scanner.nextLine()).getTime());
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use dd/MM/yyyy format.");
            pressEnterToContinue();
            return;
        }

        System.out.print("End Date (dd/MM/yyyy, leave empty for indefinite contract): ");
        String endDateStr = scanner.nextLine();
        Date endDate = null;
        if (!endDateStr.trim().isEmpty()) {
            try {
                endDate = new Date(dateFormat.parse(endDateStr).getTime());
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use dd/MM/yyyy format.");
                pressEnterToContinue();
                return;
            }
        }

        // Create and save contract
        Contract contract = new Contract();
        contract.setEmployeeId(employeeId);
        contract.setStartDate(startDate);
        contract.setEndDate(endDate);
        contract.setStatus("Active");

        if (contractDAO.addContract(contract)) {
            System.out.println("Contract added successfully!");
        } else {
            System.out.println("Failed to add contract. Please try again later.");
        }
        pressEnterToContinue();
    }

    /**
     * Extend an existing contract
     */
    public void extendContract() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║                EXTEND CONTRACT                ║");
        System.out.println("╚═══════════════════════════════════════════════╝");

        // Display active contracts
        List<Contract> contracts = contractDAO.getAllContracts();
        boolean hasActiveContracts = false;

        System.out.println("Active Contracts:");
        System.out.println("--------------------------------------------------");
        for (Contract contract : contracts) {
            if ("Active".equals(contract.getStatus())) {
                Employee employee = employeeDAO.getEmployeeById(contract.getEmployeeId());
                String employeeName = employee != null ? employee.getName() : "Unknown";
                System.out.println(contract.getId() + ". Employee: " + employeeName
                        + ", Start: " + dateFormat.format(contract.getStartDate())
                        + ", End: "
                        + (contract.getEndDate() != null ? dateFormat.format(contract.getEndDate()) : "N/A"));
                hasActiveContracts = true;
            }
        }
        System.out.println("--------------------------------------------------");

        if (!hasActiveContracts) {
            System.out.println("No active contracts found.");
            pressEnterToContinue();
            return;
        }

        // Get contract ID
        System.out.print("Enter Contract ID to extend: ");
        int contractId;
        try {
            contractId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
            pressEnterToContinue();
            return;
        }

        // Verify contract exists and is active
        Contract contract = contractDAO.getContractById(contractId);
        if (contract == null) {
            System.out.println("Contract not found. Please try again.");
            pressEnterToContinue();
            return;
        }

        if (!"Active".equals(contract.getStatus())) {
            System.out.println("Only active contracts can be extended.");
            pressEnterToContinue();
            return;
        }

        // Get new end date
        System.out.print("New End Date (dd/MM/yyyy): ");
        Date newEndDate;
        try {
            newEndDate = new Date(dateFormat.parse(scanner.nextLine()).getTime());

            // Validate new end date is after current end date
            if (contract.getEndDate() != null && !newEndDate.after(contract.getEndDate())) {
                System.out.println("New end date must be after the current end date.");
                pressEnterToContinue();
                return;
            }
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use dd/MM/yyyy format.");
            pressEnterToContinue();
            return;
        }

        // Update contract
        contract.setEndDate(newEndDate);

        if (contractDAO.updateContract(contract)) {
            System.out.println("Contract extended successfully!");
        } else {
            System.out.println("Failed to extend contract. Please try again later.");
        }
        pressEnterToContinue();
    }

    /**
     * Terminate an existing contract
     */
    public void terminateContract() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║              TERMINATE CONTRACT               ║");
        System.out.println("╚═══════════════════════════════════════════════╝");

        // Display active contracts
        List<Contract> contracts = contractDAO.getAllContracts();
        boolean hasActiveContracts = false;

        System.out.println("Active Contracts:");
        System.out.println("--------------------------------------------------");
        for (Contract contract : contracts) {
            if ("Active".equals(contract.getStatus())) {
                Employee employee = employeeDAO.getEmployeeById(contract.getEmployeeId());
                String employeeName = employee != null ? employee.getName() : "Unknown";
                System.out.println(contract.getId() + ". Employee: " + employeeName
                        + ", Start: " + dateFormat.format(contract.getStartDate())
                        + ", End: "
                        + (contract.getEndDate() != null ? dateFormat.format(contract.getEndDate()) : "N/A"));
                hasActiveContracts = true;
            }
        }
        System.out.println("--------------------------------------------------");

        if (!hasActiveContracts) {
            System.out.println("No active contracts found.");
            pressEnterToContinue();
            return;
        }

        // Get contract ID
        System.out.print("Enter Contract ID to terminate: ");
        int contractId;
        try {
            contractId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
            pressEnterToContinue();
            return;
        }

        // Verify contract exists and is active
        Contract contract = contractDAO.getContractById(contractId);
        if (contract == null) {
            System.out.println("Contract not found. Please try again.");
            pressEnterToContinue();
            return;
        }

        if (!"Active".equals(contract.getStatus())) {
            System.out.println("This contract has already been terminated.");
            pressEnterToContinue();
            return;
        }

        // Get termination date
        System.out.print("Termination Date (dd/MM/yyyy): ");
        Date terminationDate;
        try {
            terminationDate = new Date(dateFormat.parse(scanner.nextLine()).getTime());

            // Validate termination date is after start date
            if (terminationDate.before(contract.getStartDate())) {
                System.out.println("Termination date must be after the start date.");
                pressEnterToContinue();
                return;
            }
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use dd/MM/yyyy format.");
            pressEnterToContinue();
            return;
        }

        // Update contract
        contract.setEndDate(terminationDate);
        contract.setStatus("Terminated");

        if (contractDAO.updateContract(contract)) {
            System.out.println("Contract terminated successfully!");
        } else {
            System.out.println("Failed to terminate contract. Please try again later.");
        }
        pressEnterToContinue();
    }

    /**
     * List all contracts
     */
    private void listContracts() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║                  CONTRACTS LIST               ║");
        System.out.println("╚═══════════════════════════════════════════════╝");

        List<Contract> contracts = contractDAO.getAllContracts();
        if (contracts.isEmpty()) {
            System.out.println("No contracts found in the system.");
            return;
        }

        System.out.printf("%-4s %-20s %-12s %-12s %-10s\n", "ID", "Employee", "Start Date", "End Date", "Status");
        System.out.println("------------------------------------------------------------------");

        for (Contract contract : contracts) {
            Employee employee = employeeDAO.getEmployeeById(contract.getEmployeeId());
            String employeeName = employee != null ? employee.getName() : "Unknown";
            System.out.printf("%-4d %-20s %-12s %-12s %-10s\n",
                    contract.getId(),
                    employeeName,
                    dateFormat.format(contract.getStartDate()),
                    contract.getEndDate() != null ? dateFormat.format(contract.getEndDate()) : "N/A",
                    contract.getStatus());
        }
    }

    private void clearScreen() {
        // Clear screen
        System.out.print("\033[H\033[2J");
        System.out.flush();

        // For Windows systems where ANSI escape codes might not work
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    private void pressEnterToContinue() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}