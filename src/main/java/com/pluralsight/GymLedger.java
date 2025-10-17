package com.pluralsight;
//ALL EXTENSIVE NOTES = iPad LTCA WRITTEN NOTES
// Loads transactions from "transactions.csv" using BufferedReader (3a pp.12–15).
// Home >> D/P/L/X menu (workshop 2w p.4).
// Ledger >> All/Deposits/Payments/Reports.

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

        //Have to assume we don’t know how many times the user will use the menu.
        //It keeps going until the user says “Exit.”
        //break gives an exit path.
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
        //File ends when readLine() returns null.
        //We don’t know how many lines are in the file, so a while loop checks “keep going until no more lines.”
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

        //We don’t know how many tries the user will need.
        //We only stop when we successfully parse a number (break).
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
        // Start at the 2nd item (index 1) because a single item (index 0) is already "sorted".
        for (int i = 1; i < list.size(); i++) {
            // Grab the current card/receipt we want to insert in the correct spot.
            // Ex. call it "key" because we're trying to find the right "lock" position for it.
            Transactions key = list.get(i);
            // j points to the item just before i.
            // Ex. We walk left (toward the front) to find where key belongs.
            int j = i - 1;
            // Ex. While we haven't run off the left edge (j >= 0)
            // Ex. AND the "key" is NEWER than the thing at position j...
            // Ex. keep sliding items to the right to make room for key.
            while (j >= 0 && isAfter(key, list.get(j))) {
                // Move the item at j one step to the right (j -> j+1),
                // basically shifting the older item over to make space.
                list.set(j + 1, list.get(j));
                // Step left and compare again (keep shuffling until key is in the right spot).
                j--;
            }
            // Ex. The keyhole is found and the "key" is now in the opening at j+1.
            // j+1? Because the loop moved j one step too far to the left.
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
        // "today" = the current calendar day on this computer.
        java.time.LocalDate today = java.time.LocalDate.now();
        // firstDay = the 1st day of the current month (e.g., Nov 1st if today is Nov 18th).
        java.time.LocalDate firstDay = today.withDayOfMonth(1);

        ArrayList<Transactions> out = new ArrayList<>();
        // Walk through every transaction we were given.
        for (int i = 0; i < list.size(); i++) {
            // Turn the transaction's date string (YYYY-MM-DD) into a LocalDate.
            // If the date string is bad, we get null instead of crashing.
            java.time.LocalDate d = parseDateOrNull(list.get(i).getDate());
            if (d == null) continue;
            // Keep it only if d is NOT before firstDay AND NOT after today
            // (aka: d is between firstDay and today, inclusive).
            if (!d.isBefore(firstDay) && !d.isAfter(today)) {
                out.add(list.get(i));
            }
        }
        // Header so the user knows the date window used.
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
            if (d == null) continue; // skip bad date rows
            // Keep if date is NOT before firstPrev AND NOT after lastPrev
            // (aka: inside the previous month, inclusive).
            if (!d.isBefore(firstPrev) && !d.isAfter(lastPrev)) {
                out.add(list.get(i));
            }
        }
        // Tell the user exactly which window we used.
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

        //Piece of code to be proud of:

        //1. figured out the date window (start/end or exact year)
        //2. loop through the list
        //3. parsed each date
        //4. kept only the ones inside the window
        //5. printed the filtered list with a clear header.
            //* using !d.isBefore(start) and !d.isAfter(end) makes the range inclusive (includes the endpoints).
            //* parseDateOrNull protects the app from bad date strings—bad rows are skipped
    }
}
