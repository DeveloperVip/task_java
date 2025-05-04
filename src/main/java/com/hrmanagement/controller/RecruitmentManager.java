package com.hrmanagement.controller;

import com.hrmanagement.dao.CandidateDAO;
import com.hrmanagement.dao.EmployeeDAO;
import com.hrmanagement.dao.impl.CandidateDAOImpl;
import com.hrmanagement.dao.impl.EmployeeDAOImpl;
import com.hrmanagement.model.Candidate;
import com.hrmanagement.model.Employee;

import java.sql.Date;
import java.util.List;
import java.util.Scanner;

public class RecruitmentManager {
    private final CandidateDAO candidateDAO;
    private final EmployeeDAO employeeDAO;
    private final Scanner scanner;

    public RecruitmentManager(Scanner scanner) {
        this.candidateDAO = new CandidateDAOImpl();
        this.employeeDAO = new EmployeeDAOImpl();
        this.scanner = scanner;
    }

    public void displayMenu() {
        boolean exit = false;
        while (!exit) {
            displayRecruitmentMenu();

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
                    addCandidate();
                    break;
                case 2:
                    evaluateCandidate();
                    break;
                case 3:
                    convertToEmployee();
                    break;
                case 4:
                    listCandidates();
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

    private void displayRecruitmentMenu() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║            RECRUITMENT MANAGEMENT             ║");
        System.out.println("╠═══════════════════════════════════════════════╣");
        System.out.println("║  [1] Add New Candidate                        ║");
        System.out.println("║  [2] Evaluate Candidate                       ║");
        System.out.println("║  [3] Convert Candidate to Employee            ║");
        System.out.println("║  [4] View All Candidates                      ║");
        System.out.println("║  [0] Back to Main Menu                        ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
    }

    public void addCandidate() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║               ADD NEW CANDIDATE               ║");
        System.out.println("╚═══════════════════════════════════════════════╝");

        System.out.print("Enter Candidate Name: ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            System.out.println("Candidate name cannot be empty. Please try again.");
            pressEnterToContinue();
            return;
        }

        System.out.print("Enter Position Applied For: ");
        String position = scanner.nextLine();
        if (position.trim().isEmpty()) {
            System.out.println("Position cannot be empty. Please try again.");
            pressEnterToContinue();
            return;
        }

        Candidate candidate = new Candidate();
        candidate.setName(name);
        candidate.setPositionApplied(position);
        candidate.setStatus("New");

        if (candidateDAO.addCandidate(candidate)) {
            System.out.println("Candidate added successfully!");
        } else {
            System.out.println("Failed to add candidate. Please try again later.");
        }
        pressEnterToContinue();
    }

    public void evaluateCandidate() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║               EVALUATE CANDIDATE              ║");
        System.out.println("╚═══════════════════════════════════════════════╝");

        List<Candidate> candidates = candidateDAO.getAllCandidates();
        boolean hasNewCandidates = false;

        System.out.println("New Candidates List:");
        System.out.println("--------------------------------------------------");
        for (Candidate candidate : candidates) {
            if ("New".equals(candidate.getStatus())) {
                System.out.println(candidate.getId() + ". " + candidate.getName()
                        + " - Position: " + candidate.getPositionApplied());
                hasNewCandidates = true;
            }
        }
        System.out.println("--------------------------------------------------");

        if (!hasNewCandidates) {
            System.out.println("No new candidates found for evaluation.");
            pressEnterToContinue();
            return;
        }

        System.out.print("Enter Candidate ID to evaluate: ");
        int candidateId;
        try {
            candidateId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
            pressEnterToContinue();
            return;
        }

        Candidate candidate = candidateDAO.getCandidateById(candidateId);
        if (candidate == null) {
            System.out.println("Candidate not found. Please try again.");
            pressEnterToContinue();
            return;
        }

        if (!"New".equals(candidate.getStatus())) {
            System.out.println("This candidate has already been evaluated.");
            pressEnterToContinue();
            return;
        }

        System.out.println("Select evaluation result: ");
        System.out.println("1. Pass (Proceed to interview)");
        System.out.println("2. Reject");
        System.out.print("Your choice: ");

        int evaluationResult;
        try {
            evaluationResult = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice. Please enter a number.");
            pressEnterToContinue();
            return;
        }

        if (evaluationResult == 1) {
            candidate.setStatus("Interviewed");
        } else if (evaluationResult == 2) {
            candidate.setStatus("Rejected");
        } else {
            System.out.println("Invalid choice.");
            pressEnterToContinue();
            return;
        }

        if (candidateDAO.updateCandidate(candidate)) {
            System.out.println("Candidate evaluated successfully!");
        } else {
            System.out.println("Failed to evaluate candidate. Please try again later.");
        }
        pressEnterToContinue();
    }

    public void convertToEmployee() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║         CONVERT CANDIDATE TO EMPLOYEE         ║");
        System.out.println("╚═══════════════════════════════════════════════╝");

        List<Candidate> candidates = candidateDAO.getAllCandidates();
        boolean hasInterviewedCandidates = false;

        System.out.println("Interviewed Candidates:");
        System.out.println("--------------------------------------------------");
        for (Candidate candidate : candidates) {
            if ("Interviewed".equals(candidate.getStatus())) {
                System.out.println(candidate.getId() + ". " + candidate.getName()
                        + " - Position: " + candidate.getPositionApplied());
                hasInterviewedCandidates = true;
            }
        }
        System.out.println("--------------------------------------------------");

        if (!hasInterviewedCandidates) {
            System.out.println("No interviewed candidates found to convert to employees.");
            pressEnterToContinue();
            return;
        }

        System.out.print("Enter Candidate ID to convert to employee: ");
        int candidateId;
        try {
            candidateId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
            pressEnterToContinue();
            return;
        }

        Candidate candidate = candidateDAO.getCandidateById(candidateId);
        if (candidate == null) {
            System.out.println("Candidate not found. Please try again.");
            pressEnterToContinue();
            return;
        }

        if (!"Interviewed".equals(candidate.getStatus())) {
            System.out.println("Only interviewed candidates can be converted to employees.");
            pressEnterToContinue();
            return;
        }

        Employee employee = new Employee();
        employee.setName(candidate.getName());
        employee.setPosition(candidate.getPositionApplied());
        employee.setJoinDate(new Date(System.currentTimeMillis()));

        if (employeeDAO.addEmployee(employee)) {
            candidate.setStatus("Hired");
            if (candidateDAO.updateCandidate(candidate)) {
                System.out.println("Candidate successfully converted to employee!");
                System.out.println("New Employee ID: " + employee.getId());
            } else {
                System.out.println("Error updating candidate status.");
            }
        } else {
            System.out.println("Failed to create new employee. Please try again later.");
        }
        pressEnterToContinue();
    }

    private void listCandidates() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║                CANDIDATES LIST                ║");
        System.out.println("╚═══════════════════════════════════════════════╝");

        List<Candidate> candidates = candidateDAO.getAllCandidates();
        if (candidates.isEmpty()) {
            System.out.println("No candidates found in the system.");
            return;
        }

        System.out.printf("%-4s %-25s %-25s %-10s\n",
                "ID", "Name", "Position Applied", "Status");
        System.out.println("----------------------------------------------------------------------");

        for (Candidate candidate : candidates) {
            System.out.printf("%-4d %-25s %-25s %-10s\n",
                    candidate.getId(),
                    candidate.getName().length() > 25 ? candidate.getName().substring(0, 22) + "..."
                            : candidate.getName(),
                    candidate.getPositionApplied().length() > 25
                            ? candidate.getPositionApplied().substring(0, 22) + "..."
                            : candidate.getPositionApplied(),
                    candidate.getStatus());
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