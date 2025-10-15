package com.pluralsight;// GymLedger.java.
// Loads transactions from "transactions.csv" using BufferedReader (3a pp.12–15).
// Home >> D/P/L/X menu (workshop 2w p.4).
// Ledger >> All/Deposits/Payments/Reports (simple filtering and insertion sort).

import java.io.BufferedReader;     // reading files (3a pp.12–15)
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;        // collections (3a p.49 shows ArrayList in use)
import java.util.Scanner;          // console input


public class GymLedger {
    private static final String FILE_NAME = "transactions.csv";
    private static final ArrayList<Transactions> transactions = new ArrayList<>(); // growing list (3a p.49)

    public static void main(String[] args) {
        loadTransactions(); // readLine loop pattern (3a pp.12–15)

        // 2) Home menu loop (simple while-true menu, 2a pp.55–59 for loops)
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== HOME (Gym Ledger) ===");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
            System.out.print("Choose: ");
            String pick = sc.nextLine().trim().toUpperCase(); //(string methods 2a pp.9–10)

            if (pick.equals("D")) {
                Transactions t = promptTransaction(sc, true);     // deposit = pos
                if (t != null) {
                    transactions.add(t);                          // add (3a p.49)
                    System.out.println("Deposit added (memory only).");
                }
            } else if (pick.equals("P")) {
                Transactions t = promptTransaction(sc, false);    // payment = neg
                if (t != null) {
                    transactions.add(t);
                    System.out.println("Payment added (memory only).");
                }
            } else if (pick.equals("L")) {
                showLedger(sc);
            } else if (pick.equals("X")) {
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Please type D, P, L, or X.");
            }
        }
        sc.close();
    }

   //File Loading
    private static void loadTransactions() {
        transactions.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            // read until end of file: while((line = br.readLine()) != null) … (3a pp.12–13)
            while ((line = br.readLine()) != null) {
                Transactions t = Transactions.fromCsv(line.trim());
                // split on "|" (2a p.12)
                // parse numbers (2a p.17)
                if (t != null) transactions.add(t);
            }
            System.out.println("(Loaded " + transactions.size() + " entries from " + FILE_NAME + ")");
        } catch (IOException e) {
            // If file not found or any error, just start with empty list for now (beginner choice)
            System.out.println("(Could not read " + FILE_NAME + "; starting with empty ledger)");
        }
    }

    // Prompt for a new transaction
    private static Transactions promptTransaction(Scanner sc, boolean isDeposit) {
        System.out.print("Date (YYYY-MM-DD): ");      // format (2a p.18 talks about LocalDate.parse and ISO)
        String date = sc.nextLine().trim();

        System.out.print("Time (HH:mm:ss): ");
        String time = sc.nextLine().trim();

        System.out.print("Description: ");
        String desc = sc.nextLine().trim();

        System.out.print("Vendor: ");
        String vend = sc.nextLine().trim();

        System.out.print("Amount (positive number): ");
        String amtStr = sc.nextLine().trim();
        double amt = 0.0;
        try {
            amt = Double.parseDouble(amtStr);         // string >> number (2a p.17)
        } catch (NumberFormatException ex) {
            System.out.println("Not a valid number. Cancelled.");
            return null;
        }

        if (!isDeposit) {
            amt = -Math.abs(amt); // make payments neg
        } else {
            amt = Math.abs(amt);  // make deposits pos
        }

        return new Transactions( time, desc, vend, vend, amt);
    }

    //Ledger Screen
    private static void showLedger(Scanner sc) {
        // Show newest first (2a p.72 shows sorting basics)
        ArrayList<Transactions> copy = new ArrayList<>(transactions);
        sortNewestFirst(copy);

        while (true) {
            System.out.println("\n=== LEDGER ===");
            System.out.println("A) All");
            System.out.println("D) Deposits only");
            System.out.println("P) Payments only");
            System.out.println("R) Reports");
            System.out.println("H) Home");
            System.out.print("Choose: ");
            String pick = sc.nextLine().trim().toUpperCase();

            if (pick.equals("A")) {
                printList(copy);                         // all
            } else if (pick.equals("D")) {
                printFiltered(copy, true);               // deposits
            } else if (pick.equals("P")) {
                printFiltered(copy, false);              // payments
            } else if (pick.equals("R")) {
                showReports(sc, copy);                   // step 8: month-to-date, prev month, YTD, prev year, search by vendor
            } else if (pick.equals("H")) {
                break;
            } else {
                System.out.println("Please type A, D, P, R, or H.");
            }
        }
    }

    //newest (bigger date/time) should come first
    private static void sortNewestFirst(ArrayList<Transactions> list) {
        for (int i = 1; i < list.size(); i++) {
            Transactions key = list.get(i);
            int j = i - 1;
            // Compare by date string, then time string
            while (j >= 0 && isAfter(key, list.get(j))) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    // Return true if a is NEWER than b
    private static boolean isAfter(Transactions a, Transactions b) {
        int cmpDate = a.getDate().compareTo(b.getDate());
        if (cmpDate > 0) return true;
        if (cmpDate < 0) return false;
        // same date => compare times
        return a.getTime().compareTo(b.getTime()) > 0;
    }

    private static void printList(ArrayList<Transactions> list) {
        if (list.isEmpty()) {
            System.out.println("(no entries)");
            return;
        }
        for (int i = 0; i < list.size(); i++) {          // simple indexed loop (2a pp.55–59)
            System.out.println(list.get(i));
        }
    }

    private static void printFiltered(ArrayList<Transactions> list, boolean deposits) {
        boolean any = false;
        for (int i = 0; i < list.size(); i++) {
            Transactions t = list.get(i);
            if (deposits && t.getAmount() > 0) { System.out.println(t); any = true; }
            if (!deposits && t.getAmount() < 0) { System.out.println(t); any = true; }
        }
        if (!any) System.out.println("(none)");
    }

    //Reports
    private static void showReports(Scanner sc, ArrayList<Transactions> list) {
        while (true) {
            System.out.println("\n=== REPORTS ===");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("0) Back");
            System.out.print("Choose: ");
            String pick = sc.nextLine().trim();

            if (pick.equals("0")) break;

            if (pick.equals("5")) {
                System.out.print("Vendor to search: ");
                String term = sc.nextLine().trim().toLowerCase();
                boolean any = false;
                for (int i = 0; i < list.size(); i++) {
                    Transactions t = list.get(i);
                    if (t.getVendor().toLowerCase().contains(term)) { // string contains (2a pp.9–12 examples)
                        System.out.println(t);
                        any = true;
                    }
                }
                if (!any) System.out.println("(no matches)");
                continue;
            }

            if (pick.equals("1")) {
                System.out.print("Type current year-month (YYYY-MM), e.g. 2025-10: ");
                String ym = sc.nextLine().trim();
                printByYearMonth(list, ym);
            } else if (pick.equals("2")) {
                System.out.print("Type previous year-month (YYYY-MM): ");
                String ym = sc.nextLine().trim();
                printByYearMonth(list, ym);
            } else if (pick.equals("3")) {
                System.out.print("Type current year (YYYY), e.g. 2025: ");
                String y = sc.nextLine().trim();
                printByYear(list, y);
            } else if (pick.equals("4")) {
                System.out.print("Type previous year (YYYY): ");
                String y = sc.nextLine().trim();
                printByYear(list, y);
            } else {
                System.out.println("Pick 0–5.");
            }
        }
    }

    private static void printByYearMonth(ArrayList<Transactions> list, String yearMonth) {
        boolean any = false;
        for (int i = 0; i < list.size(); i++) {
            Transactions t = list.get(i);
            // date starts with YYYY-MM so startsWith works
            if (t.getDate().startsWith(yearMonth)) {     // string startsWith example style (2a p.11)
                System.out.println(t);
                any = true;
            }
        }
        if (!any) System.out.println("(none)");
    }

    private static void printByYear(ArrayList<Transactions> list, String year) {
        boolean any = false;
        for (int i = 0; i < list.size(); i++) {
            Transactions t = list.get(i);
            if (t.getDate().startsWith(year)) {
                System.out.println(t);
                any = true;
            }
        }
        if (!any) System.out.println("(none)");
    }
}
