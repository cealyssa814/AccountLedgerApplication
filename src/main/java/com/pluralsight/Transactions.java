package com.pluralsight;

public class Transactions {
    // fields match the CSV columns
    private String date;        // YYYY-MM-DD
    private String time;        // HH:MM:SS
    private String description; // what happened
    private String vendor;      // who it was with
    private double amount;      // + for deposits, - for payments

