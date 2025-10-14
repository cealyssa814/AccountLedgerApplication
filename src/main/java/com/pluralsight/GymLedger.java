package com.pluralsight;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class GymLedger {
    private static final String FILE_NAME = "transactions.csv";
    private static final ArrayList<Transactions> transactions = new ArrayList<>();

    public static void main(String[] args) {
        loadTransactions();
        System.out.println("~~~ Transactions (with demo data added) ~~~");
        for (int i = 0; i < transactions.size(); i++) {
            System.out.println(transactions.get(i));
        }
        System.out.println("Total entries: " + transactions.size());
    }

    private static void loadTransactions() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                Transactions t = Transactions.fromCsv(line.trim());
                if (t != null) transactions.add(t);
            }
            System.out.println("(Loaded " + transactions.size() + " transactions)");
        } catch (IOException e) {
            System.out.println("Problem reading file");
        }

        java.util.Scanner sc = new java.util.Scanner(System.in);
        while (true) {
            System.out.println("\n=== HOME ===");
            System.out.println("A) Add Deposit");
            System.out.println("B) Make Payment");
            System.out.println("C) Ledger");
            System.out.println("D) Exit");
            System.out.print("Choose: ");
            String pick = sc.nextLine().trim().toUpperCase();

            if (pick.equals("A")) {
                try {
                    System.out.print("Date (YYYY-MM-DD): ");
                    String date = sc.nextLine().trim();

                    System.out.print("Time (HH:MM:SS): ");
                    String time = sc.nextLine().trim();

                    System.out.print("Description: ");
                    String desc = sc.nextLine().trim();

                    System.out.print("Vendor: ");
                    String vendor = sc.nextLine().trim();

                    System.out.print("Amount: ");
                    double amt = Double.parseDouble(sc.nextLine().trim());

                    amt = Math.abs(amt);

                    Transactions t = new Transactions(date, time, desc, vendor, amt);
                    transactions.add(t);
                    System.out.println("Saved deposit!");
                } catch (Exception e) {
                    System.out.println("Bad input. Deposit not saved. Try again.");
                }

            } else if (pick.equals("B")) {
                try {
                    System.out.print("Date (YYYY-MM-DD): ");
                    String date = sc.nextLine().trim();

                    System.out.print("Time (HH:MM:SS): ");
                    String time = sc.nextLine().trim();

                    System.out.print("Description: ");
                    String desc = sc.nextLine().trim();

                    System.out.print("Vendor: ");
                    String vendor = sc.nextLine().trim();

                    System.out.print("Amount: ");
                    double amt = Double.parseDouble(sc.nextLine().trim());

                    amt = -Math.abs(amt);

                    Transactions t = new Transactions(date, time, desc, vendor, amt);
                    transactions.add(t);
                    System.out.println("Saved payment!");
                } catch (Exception e) {
                    System.out.println("Bad input. Payment not saved. Try again.");
                }

            } else if (pick.equals("C")) {
                showLedger(sc);

            } else if (pick.equals("D")) {
                System.out.println("Bye!");
                break;

            } else {
                System.out.println("Pick D, P, L, or X.");
            }
        }
        sc.close();
    }

    private static void showLedger(Scanner sc) {
    }
}
