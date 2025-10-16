package com.pluralsight;
//ALL EXTENSIVE NOTES = iPad LTCA WRITTEN NOTES
public class Transactions {
    private String date;
    private String time;
    private String description;
    private String vendor;
    private double amount;
    // constructor to easily build an object
    public Transactions(String date, String time, String description, String vendor, double amount) {
        // attributes
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }
//Getters and setters (2a pp.30–35 on classes/encapsulation)
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
    //Parse ONE CSV line into a Transaction (split on pipe >> 2a p.12)
    public String toCsv() {
        return date + "|" + time + "|" + description + "|" + vendor + "|" + amount;
    }
    // Pipe | is a regex char, so escape it with "\\|" (2a p.12).
    public static Transactions fromCsv(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 5) return null;

        if (parts.length != 5) return null;

        String date = parts[0].trim();
        String time = parts[1].trim();
        String desc = parts[2].trim();
        String vend = parts[3].trim();
        double amt = Double.parseDouble(parts[4].trim()); // 2a p.17 shows parse methods

        return new Transactions(date, time, desc, vend, amt);
    }
    public String toString() {
        return date + " " + time + " | " + description + " | " + vendor + " | " + String.format("%.2f", amount);
    }

}