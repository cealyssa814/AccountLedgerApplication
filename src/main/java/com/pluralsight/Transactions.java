package com.pluralsight;

import java.io.File;

public class Transactions {

    private String date;
    private String time;
    private String description;
    private String vendor;
    private double amount;

    public Transactions(String date, String time, String description, String vendor, double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public String getDate()        { return date; }
    public String getTime()        { return time; }
    public String getDescription() { return description; }
    public String getVendor()      { return vendor; }
    public double getAmount()      { return amount; }

    public void setDate(String date)              { this.date = date; }
    public void setTime(String time)              { this.time = time; }
    public void setDescription(String description){ this.description = description; }
    public void setVendor(String vendor)          { this.vendor = vendor; }
    public void setAmount(double amount)          { this.amount = amount; }

    public String toCsv() {
        return date + "|" + time + "|" + description + "|" + vendor + "|" + amount;
    }

    public static Transactions fromCsv(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 5) return null;

        String date = parts[0].trim();
        String time = parts[1].trim();
        String description = parts[2].trim();
        String vendor = parts[3].trim();
        double amount = Double.parseDouble(parts[4].trim());

        return new Transactions(date, time, description, vendor, amount);
    }

    @Override
    public String toString() {
        return String.format("%s %s | %-24s | %-12s | %8.2f",
                date, time, description, vendor, amount);
    }

}