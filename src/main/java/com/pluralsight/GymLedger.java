package com.pluralsight;

import java.io.*;
import java.util.ArrayList;

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
    }
}

