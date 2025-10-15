package com.pluralsight;
//ALL EXTENSIVE NOTES = iPad LTCA WRITTEN NOTES
// Loads transactions from "transactions.csv" using BufferedReader (3a pp.12–15).
// Home >> D/P/L/X menu (workshop 2w p.4).
// Ledger >> All/Deposits/Payments/Reports (simple filtering and insertion sort).

import java.io.BufferedReader;      // reading files (WB 3a File I/O Reading)
import java.io.FileReader;
import java.io.FileWriter;          // writing files (WB 3a p.21)
import java.io.BufferedWriter;      // buffered writing (WB 3a p.22)
import java.io.IOException;
import java.util.ArrayList;         // ArrayList basics (WB 3a Collections/ArrayList)
import java.util.Scanner;           // console input

public class GymLedger {

    // The CSV we read & write. One transaction per line: date|time|description|vendor|amount
    private static final String FILE_NAME = "transactions.csv";

    // Keep all transactions in memory here
    private static final ArrayList<Transactions> transactions = new ArrayList<>();

    public static void main(String[] args) {
        // Load CSV once at startup
        loadTransactions(); // wb 3a File I/O Reading style

        // Home menu loop (Strings: trim(), toUpperCase(); Loops: while/if)
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n~~~ HOME (Gym Ledger) ~~~");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
            System.out.print("Choose: ");
            String pick = sc.nextLine().trim().toUpperCase(); // wb 2a string methods

            if (pick.equals("D")) {
                Transactions t = promptTransaction(sc, true);    // deposit = pos
                if (t != null) {
                    transactions.add(t);                        // ArrayList add (wb 3a)
                    writeAll();                                 // Save entire file (wb 3a pp.21–23)
                    System.out.println("Deposit saved to CSV!");
                }
            } else if (pick.equals("P")) {
                Transactions t = promptTransaction(sc, false);   // payment = neg
                if (t != null) {
                    transactions.add(t);
                    writeAll();
                    System.out.println("Payment saved to CSV!");
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


    // Loading from file: read the CSV into the ArrayList
    private static void loadTransactions() {
        transactions.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            // read a line until null loop (wb 3a File I/O Reading)
            while ((line = br.readLine()) != null) {
                // Split the line into parts inside Transactions.fromCsv (wb 2a: split("\\|"))
                Transactions t = Transactions.fromCsv(line.trim());
                if (t != null) transactions.add(t);
            }
            System.out.println("(Loaded " + transactions.size() + " entries from " + FILE_NAME + ")");
        } catch (IOException e) {
            // If file is missing or unreadable, just start with empty list
            System.out.println("(Could not read " + FILE_NAME + "; starting with empty ledger)");
        }
    }


    // Saving to file: write the ArrayList back to the CSV
    // FileWriter + BufferedWriter (wb 3a pp.21–23).
    private static void writeAll() {
        BufferedWriter bw = null;
        try {
            // Overwrite the file each time
            FileWriter fw = new FileWriter(FILE_NAME);
            bw = new BufferedWriter(fw);

            for (int i = 0; i < transactions.size(); i++) {
                String row = transactions.get(i).toCsv();  // "date|time|description|vendor|amount"
                bw.write(row);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("ERROR writing file: " + e.getMessage());
        } finally {
            try { if (bw != null) bw.close(); } catch (IOException ignore) {}
        }
    }


    // For the prompt in wb: build one Transaction from user input
    // Strings >> numbers with Double.parseDouble (wb 2a)
    private static Transactions promptTransaction(Scanner sc, boolean isDeposit) {
        System.out.print("Date (YYYY-MM-DD): ");   // format helps later with LocalDate.parse (wb 2a)
        String date = sc.nextLine().trim();

        System.out.print("Time (HH:mm:ss): ");
        String time = sc.nextLine().trim();

        System.out.print("Description: ");
        String desc = sc.nextLine().trim();

        System.out.print("Vendor: ");
        String vend = sc.nextLine().trim();

        double amt;
        while (true) {
            System.out.print("Amount (just the number): ");
            String amtStr = sc.nextLine().trim();
            try {
                amt = Double.parseDouble(amtStr); // WB 2a parseDouble
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a number like 150.00 or 32.50");
            }
        }

        //deposits positive, payments negative
        if (isDeposit) {
            amt = Math.abs(amt);
        } else {
            amt = -Math.abs(amt);
        }

        // Returns a new Transaction
        return new Transactions(date, time, desc, vend, amt);
    }

    // Ledger menu: All / Deposits / Payments / Reports / Home
    // Sort newest first
    private static void showLedger(Scanner sc) {
        // work on a copy so the “home list” stays untouched
        ArrayList<Transactions> copy = new ArrayList<>(transactions);
        sortNewestFirst(copy); // newest (later date/time) first

        while (true) {
            System.out.println("\n~~~ LEDGER ~~~");
            System.out.println("A) All");
            System.out.println("D) Deposits only");
            System.out.println("P) Payments only");
            System.out.println("R) Reports");
            System.out.println("H) Home");
            System.out.print("Choose: ");
            String pick = sc.nextLine().trim().toUpperCase();

            if (pick.equals("A")) {
                printList(copy);
            } else if (pick.equals("D")) {
                printFiltered(copy, true);
            } else if (pick.equals("P")) {
                printFiltered(copy, false);
            } else if (pick.equals("R")) {
                showReports(sc, copy);   // auto-computed
            } else if (pick.equals("H")) {
                break;
            } else {
                System.out.println("Please type A, D, P, R, or H.");
            }
        }
    }

    //Sort by with date/time as strings (YYYY-MM-DD, HH:mm:ss).
    private static void sortNewestFirst(ArrayList<Transactions> list) {
        for (int i = 1; i < list.size(); i++) {
            Transactions key = list.get(i);
            int j = i - 1;
            while (j >= 0 && isAfter(key, list.get(j))) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    // Return true if a is NEWER than b (compare date first, then time)
    private static boolean isAfter(Transactions a, Transactions b) {
        int cmpDate = a.getDate().compareTo(b.getDate());
        if (cmpDate > 0) return true;
        if (cmpDate < 0) return false;
        return a.getTime().compareTo(b.getTime()) > 0;
    }

    private static void printList(ArrayList<Transactions> list) {
        if (list.isEmpty()) {
            System.out.println("(no entries)");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
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


    // REPORTS MENU:
    // Uses LocalDate

    private static void showReports(Scanner sc, ArrayList<Transactions> list) {
        while (true) {
            System.out.println("\n~~~ REPORTS ~~~");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("0) Back");
            System.out.print("Choose: ");
            String pick = sc.nextLine().trim();

            if (pick.equals("0")) break;

            if (pick.equals("1")) {
                monthToDate(list);
            } else if (pick.equals("2")) {
                previousMonth(list);
            } else if (pick.equals("3")) {
                yearToDate(list);
            } else if (pick.equals("4")) {
                previousYear(list);
            } else if (pick.equals("5")) {
                System.out.print("Vendor to search: ");
                String term = sc.nextLine().trim().toLowerCase();  // wb 2a toLowerCase + contains
                boolean any = false;
                for (int i = 0; i < list.size(); i++) {
                    Transactions t = list.get(i);
                    if (t.getVendor().toLowerCase().contains(term)) {
                        System.out.println(t);
                        any = true;
                    }
                }
                if (!any) System.out.println("(no matches)");
            } else {
                System.out.println("Pick 0–5.");
            }
        }
    }

    // to help with the date
    // Parses the ISO date.
    // If bad date, return null and skip.
    private static java.time.LocalDate parseDateOrNull(String yyyyMmDd) {
        try {
            return java.time.LocalDate.parse(yyyyMmDd);  // wb 2a LocalDate.parse with format
        } catch (Exception e) {
            return null;
        }
    }

    private static void monthToDate(ArrayList<Transactions> list) {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate firstDay = today.withDayOfMonth(1);

        ArrayList<Transactions> out = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            java.time.LocalDate d = parseDateOrNull(list.get(i).getDate());
            if (d == null) continue;
            if (!d.isBefore(firstDay) && !d.isAfter(today)) {
                out.add(list.get(i));
            }
        }
        System.out.println("\n-- Month To Date (" + firstDay + " .. " + today + ") --");
        printList(out);
    }

    private static void previousMonth(ArrayList<Transactions> list) {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate firstPrev = today.minusMonths(1).withDayOfMonth(1);
        java.time.LocalDate lastPrev = firstPrev.withDayOfMonth(firstPrev.lengthOfMonth());

        ArrayList<Transactions> out = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            java.time.LocalDate d = parseDateOrNull(list.get(i).getDate());
            if (d == null) continue;
            if (!d.isBefore(firstPrev) && !d.isAfter(lastPrev)) {
                out.add(list.get(i));
            }
        }
        System.out.println("\n-- Previous Month (" + firstPrev + " .. " + lastPrev + ") --");
        printList(out);
    }

    private static void yearToDate(ArrayList<Transactions> list) {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate jan1 = java.time.LocalDate.of(today.getYear(), 1, 1);

        ArrayList<Transactions> out = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            java.time.LocalDate d = parseDateOrNull(list.get(i).getDate());
            if (d == null) continue;
            if (!d.isBefore(jan1) && !d.isAfter(today)) {
                out.add(list.get(i));
            }
        }
        System.out.println("\n-- Year To Date (" + jan1 + " .. " + today + ") --");
        printList(out);
    }

    private static void previousYear(ArrayList<Transactions> list) {
        java.time.LocalDate today = java.time.LocalDate.now();
        int prevYear = today.getYear() - 1;

        ArrayList<Transactions> out = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            java.time.LocalDate d = parseDateOrNull(list.get(i).getDate());
            if (d == null) continue;
            if (d.getYear() == prevYear) {
                out.add(list.get(i));
            }
        }
        System.out.println("\n-- Previous Year (" + prevYear + ") --");
        printList(out);
    }
}
