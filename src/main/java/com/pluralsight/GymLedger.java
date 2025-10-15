package com.pluralsight;

import com.pluralsight.Transactions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GymLedger {

    private static final String FILE_NAME = "transactions.csv";
    private static final ArrayList<Transactions> transactions = new ArrayList<Transactions>();

    public static void main(String[] args) {
        // 1) Load existing transactions if the CSV exists. If not, start empty.
        //Numbers correlate to iPad notes for further explanation of purpose.
        loadTransactions();

        // 2) Home menu loop
        // (D/P/L/X) per the textbook.
        java.util.Scanner sc = new java.util.Scanner(System.in);
        while (true) {
            System.out.println("\n~~~ HOME ~~~");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
            System.out.print("Choose: ");
            String pick = sc.nextLine().trim().toUpperCase();

            if (pick.equals("D")) {
                // deposit (positive amount)
                try {
                    System.out.print("Date (YYYY-MM-DD): ");
                    String date = sc.nextLine().trim();

                    System.out.print("Time (HH:MM:SS): ");
                    String time = sc.nextLine().trim();

                    System.out.print("Description: ");
                    String desc = sc.nextLine().trim();

                    System.out.print("Vendor: ");
                    String vendor = sc.nextLine().trim();

                    // Loops until a valid number is given
                    double amt;
                    while (true) {
                        System.out.print("Amount: ");
                        String amtText = sc.nextLine().trim();
                        try {
                            amt = Double.parseDouble(amtText);
                            break;
                        } catch (Exception e) {
                            System.out.println("Please enter a number like 150.00");
                        }
                    }
                    amt = Math.abs(amt); // deposits are pos.

                    Transactions t = new Transactions(date, time, desc, vendor, amt);
                    transactions.add(t);
                    System.out.println("Saved deposit (session only).");

                } catch (Exception e) {
                    System.out.println("Bad input. Deposit not saved. Try again.");
                }

            } else if (pick.equals("P")) {
                // payment (negative amount)
                try {
                    System.out.print("Date (YYYY-MM-DD): ");
                    String date = sc.nextLine().trim();

                    System.out.print("Time (HH:MM:SS): ");
                    String time = sc.nextLine().trim();

                    System.out.print("Description: ");
                    String desc = sc.nextLine().trim();

                    System.out.print("Vendor: ");
                    String vendor = sc.nextLine().trim();

                    // Same as with deposits
                    double amt;
                    while (true) {
                        System.out.print("Amount: ");
                        String amtText = sc.nextLine().trim();
                        try {
                            amt = Double.parseDouble(amtText);
                            break;
                        } catch (Exception e) {
                            System.out.println("Please enter a number like 32.50");
                        }
                    }
                    amt = -Math.abs(amt); // payments are neg.

                    Transactions t = new Transactions(date, time, desc, vendor, amt);
                    transactions.add(t);
                    System.out.println("Saved payment (session only).");

                } catch (Exception e) {
                    System.out.println("Bad input. Payment not saved. Try again.");
                }

            } else if (pick.equals("L")) {
                showLedger(sc);

            } else if (pick.equals("X")) {
                System.out.println("Bye!");
                break;

            } else {
                System.out.println("Pick D, P, L, or X.");
            }
        }
        sc.close();
    }

    // Below is for the file to read

    // Load CSV >> transactions list. If file doesn't exist, = we just start empty.
    private static void loadTransactions() {
        transactions.clear();
        File f = new File(FILE_NAME);
        if (!f.exists()) {
            System.out.println("(No CSV found. Starting with an empty ledger.)");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                Transactions t = Transactions.fromCsv(line);
                if (t != null) transactions.add(t);
            }
            System.out.println("(Loaded " + transactions.size() + " transactions)");
        } catch (IOException e) {
            System.out.println("Problem reading file: " + e.getMessage());
        }
    }

    // Ledger
    // (A/D/P/R)

    private static void showLedger(java.util.Scanner sc) {
        while (true) {
            System.out.println("\n~~~ LEDGER ~~~");
            System.out.println("A) All");
            System.out.println("D) Deposits only");
            System.out.println("P) Payments only");
            System.out.println("R) Reports");                 // ← added
            System.out.println("H) Home");
            System.out.print("Choose: ");
            String pick = sc.nextLine().trim().toUpperCase();

            if (pick.equals("A")) {
                printList(transactions);
            } else if (pick.equals("D")) {
                printList(filterDeposits(transactions));
            } else if (pick.equals("P")) {
                printList(filterPayments(transactions));
            } else if (pick.equals("R")) {
                showReports(sc);                             // ← added
            } else if (pick.equals("H")) {
                break;
            } else {
                System.out.println("Pick A, D, P, R, or H.");
            }
        }
    }

    private static ArrayList<Transactions> filterDeposits(ArrayList<Transactions> list) {
        ArrayList<Transactions> out = new ArrayList<Transactions>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getAmount() > 0) {
                out.add(list.get(i));
            }
        }
        return out;
    }

    private static ArrayList<Transactions> filterPayments(ArrayList<Transactions> list) {
        ArrayList<Transactions> out = new ArrayList<Transactions>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getAmount() < 0) {
                out.add(list.get(i));
            }
        }
        return out;
    }

    private static String stamp(Transactions t) {
        // Sorts data correctly as a String
        return t.getDate() + " " + t.getTime();
    }

    // Sorting from newest
    private static void sortNewestFirst(ArrayList<Transactions> list) {
        for (int i = 1; i < list.size(); i++) {
            Transactions key = list.get(i);
            String keyStamp = stamp(key);
            int j = i - 1;
            while (j >= 0 && stamp(list.get(j)).compareTo(keyStamp) < 0) {
                list.set(j + 1, list.get(j)); // shift older forward
                j--;
            }
            list.set(j + 1, key);
        }
    }

    private static void printList(ArrayList<Transactions> list) {
        if (list.isEmpty()) {
            System.out.println("(no entries)");
            return;
        }
        // Uses a copy so the original order isn't changed.
        ArrayList<Transactions> copy = new ArrayList<Transactions>();
        for (int i = 0; i < list.size(); i++) {
            copy.add(list.get(i));
        }
        sortNewestFirst(copy);

        // Print>> (uses Transaction.toString())
        for (int i = 0; i < copy.size(); i++) {
            System.out.println(copy.get(i));
        }
    }

    //Reports

    private static void showReports(java.util.Scanner sc) {
        System.out.println("\n~~~ REPORTS ~~~");
        System.out.println("1) Month To Date");
        System.out.println("5) Search by Vendor");
        System.out.println("0) Back");
        System.out.print("Choose: ");
        String pick = sc.nextLine().trim();

        if (pick.equals("1")) {
            monthToDate();
        } else if (pick.equals("5")) {
            System.out.print("Vendor name: ");
            String vendor = sc.nextLine().trim().toLowerCase();

            ArrayList<Transactions> out = new ArrayList<Transactions>();
            for (int i = 0; i < transactions.size(); i++) {
                if (transactions.get(i).getVendor().toLowerCase().contains(vendor)) {
                    out.add(transactions.get(i));
                }
            }
            printList(out);
        }
        // 0/anything else will = return to ledger
    }

    private static void monthToDate() {
        java.time.LocalDate now = java.time.LocalDate.now();
        ArrayList<Transactions> out = new ArrayList<Transactions>();
        for (int i = 0; i < transactions.size(); i++) {
            java.time.LocalDate d = java.time.LocalDate.parse(transactions.get(i).getDate());
            if (d.getYear() == now.getYear() && d.getMonthValue() == now.getMonthValue()) {
                out.add(transactions.get(i));
            }
        }
        System.out.println("\n-- Month To Date --");
        printList(out);
    }
}

