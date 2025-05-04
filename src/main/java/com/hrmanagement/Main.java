package com.hrmanagement;

import com.hrmanagement.controller.ContractManager;
import com.hrmanagement.controller.LeaveManager;
import com.hrmanagement.controller.RecruitmentManager;
import com.hrmanagement.util.Database;

import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Database.initializeDatabase();

        ContractManager contractManager = new ContractManager(scanner);
        LeaveManager leaveManager = new LeaveManager(scanner);
        RecruitmentManager recruitmentManager = new RecruitmentManager(scanner);

        boolean exit = false;
        while (!exit) {
            displayMainMenu();

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
                    contractManager.displayMenu();
                    break;
                case 2:
                    leaveManager.displayMenu();
                    break;
                case 3:
                    recruitmentManager.displayMenu();
                    break;
                case 0:
                    exit = true;
                    System.out.println("\nThank you for using HR Management System!");
                    System.out.println("Application has been terminated successfully.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

        scanner.close();
    }

    private static void displayMainMenu() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║             HR MANAGEMENT SYSTEM              ║");
        System.out.println("╠═══════════════════════════════════════════════╣");
        System.out.println("║  [1] Contract Management                      ║");
        System.out.println("║  [2] Leave Management                         ║");
        System.out.println("║  [3] Recruitment Management                   ║");
        System.out.println("║  [0] Exit                                     ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}