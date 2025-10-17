# 🎀Gymnastics Gym Ledger🎀 

***Welcome to the Accounting Ledger used by the AH Gymnastics Academy. This is a super simple app that lets a gymnastics gym track money going in (deposits) and out (payments) with other viewing features such as
reports for quarterly expense review.***

It reads and writes a file called transactions.csv.

# What it does
	Add Deposit >> saves a positive amount

	Make Payment >> saves a negative amount (the app sets the minus sign for you)

	Ledger (newest first):

		All entries

		Deposits only

		Payments only

		Reports:

			Month-To-Date

			Previous Month

			Year-To-Date

			Previous Year

			Search by Vendor (ex: “AH Gymnastics”)

# Quick Start

1. Open the project in IntelliJ.

2. Make sure transactions.csv is at the project root (same level as src/).

3. If it doesn’t exist, you can create a blank file, or copy the sample.

4. Right-click GymLedger.java >> Run 'GymLedger.main()'.

5. Use the menu: D, P, L, X.
   <img width="326" height="142" alt="Screenshot 2025-10-16 at 3 45 26 PM" src="https://github.com/user-attachments/assets/64a99b6c-8bc6-4f09-ba05-cdc9a6a2016b" />

# How the data is stored

What it does: It takes the details of a financial transaction (like the date, description, and amount) and saves them to a file named transactions.csv.

Each line in transactions.csv is one transaction:

	date|time|description|vendor|amount
	
		date >> YYYY-MM-DD (example: 2025-10-12)

		time >> HH:mm:ss (example: 09:10:45)

		Deposits should be positive amounts

		Payments should be negative amounts
		


<img width="449" height="16" alt="Screenshot 2025-10-16 at 4 06 36 PM" src="https://github.com/user-attachments/assets/acc3e8b0-a2cc-434e-af11-8cb6073077ae" />

<img width="473" height="13" alt="Screenshot 2025-10-16 at 4 08 37 PM" src="https://github.com/user-attachments/assets/34060052-aba6-4dbd-a63e-962f854b4958" />



# Using the app 

1. Run the app >> you’ll see:

		D) Add Deposit
		P) Make Payment
		L) Ledger
		X) Exit


	Press D and add a deposit (example: Tuition).
		What it does: adds money into the account (saves a positive amount).

		What you type: date >> time >> description >> vendor >> amount (just the number).

		What gets saved: a new line in transactions.csv like:

			2025-11-15|08:30:00|Tuition - Level 3|AH Gymnastics|140.00

		Under the hood: amount is forced positive with Math.abs(amt).

		*Common mistakes & fixes:

			-Typo in amount (letters) >> you’ll be re-asked until it parses.

			-Wrong date format >> still saves, but date-based reports might skip it.

<img width="344" height="140" alt="Screenshot 2025-10-16 at 3 53 00 PM" src="https://github.com/user-attachments/assets/3c6ba821-a0f9-49f8-86cb-f9a24f38ea22" />

	Press P and add a payment (example: Chalk bag).
	What it does: records money out (saves a negative amount).

	What you type: date >> time >> description >> vendor >> amount (just the number).

	What gets saved: a new line with a minus in front of the amount (the app adds it):

	Under the hood: amount is forced negative with -Math.abs(amt).

	*Common mistakes & fixes:

		-Entering a negative number yourself >> app keeps it negative anyway.

<img width="434" height="140" alt="Screenshot 2025-10-16 at 4 05 52 PM" src="https://github.com/user-attachments/assets/af73c474-04bc-4a52-a291-06c0b33eca5e" />

	Press L then:

		A to see all entries (newest at top)
	
		D for deposits only
		
		P for payments only

		R for reports:

<img width="319" height="158" alt="Screenshot 2025-10-16 at 4 15 41 PM" src="https://github.com/user-attachments/assets/5826b23b-d825-4e90-bc5a-ab5a0afacfa2" />

	Press X then:
	
		What it does: closes the app.

		Tip: your data is already saved to transactions.csv each time you add something, so it’s safe to exit any time.

From the Ledger screen, choose R) Reports to analyze your data.
You’ll see:

	1) Month To Date
	2) Previous Month
	3) Year To Date
	4) Previous Year
	5) Search by Vendor
	0) Back

<img width="457" height="181" alt="Screenshot 2025-10-16 at 4 32 40 PM" src="https://github.com/user-attachments/assets/16de8f85-0fbb-4bb0-b5a9-84f8d4be3371" />	


## How the reports work

The app looks at each transaction’s date (the YYYY-MM-DD part).

It compares that date to a time window (like “this month”).

If a transaction’s date is inside that window, it gets printed.

Results appear in newest-first order because the ledger copy is already sorted that way.

	The date must be in ISO format YYYY-MM-DD (example: 2025-11-15).
	If a date can’t be parsed, that row is skipped (not crashed).


# How it works

-Transactions.java defines one receipt (fields + constructor + getters/setters).

<img width="1062" height="642" alt="Screenshot 2025-10-16 at 8 13 57 PM" src="https://github.com/user-attachments/assets/8d8e5f61-033a-42f2-97c4-2fd6104ca147" />


-toCsv() and fromCsv(...) or our translators to translate between objects and file lines.

<img width="1063" height="517" alt="Screenshot 2025-10-16 at 8 15 07 PM" src="https://github.com/user-attachments/assets/58138c8f-14f1-4177-b79b-79e51150ee4f" />


-GymLedger.java:

	>Loads all lines into an ArrayList<Transactions> at startup.

<img width="1075" height="175" alt="Screenshot 2025-10-16 at 8 16 23 PM" src="https://github.com/user-attachments/assets/42f8162b-aee8-49d0-93ba-5e323aca833e" />

	>After adding an entry, it rewrites the whole file.

	>Ledger is sorted newest first by comparing the date/time strings.

<img width="1070" height="302" alt="Screenshot 2025-10-16 at 8 17 57 PM" src="https://github.com/user-attachments/assets/56de04a3-597f-4d3b-bf12-27296a8e217f" />

	>Reports use LocalDate to filter by month/year.

# Troubleshooting

-Nothing shows in Ledger

	Make sure transactions.csv is in the project root and has data, or add entries via the menu.

-Dates don’t show up in reports

	Use date format YYYY-MM-DD (example: 2025-11-15). Other formats won’t parse.

-Vendor search prints nothing

	In Reports, hit 5 first, then type the vendor (e.g., AH Gymnastics).

-Amounts look wrong

	Deposits are positive, payments are negative. The app enforces this when you enter data.

# My most interesting part of code

<img width="1070" height="747" alt="Screenshot 2025-10-17 at 8 07 54 AM" src="https://github.com/user-attachments/assets/fb3240f3-43ee-4492-af32-9cbd99c3a6a9" />


# A glimpse into the beginning stages of the Ledger
	This is in reference to my process and the strategy I used to formulate this code in small steps 
	to pace myself (which ended up coming in lots of handy).
	
![Untitled Notebook-2](https://github.com/user-attachments/assets/ea478c45-0d5d-4943-9e6e-17f7a5c7dc81)
![Untitled Notebook-3](https://github.com/user-attachments/assets/cc83b7d3-5392-42dc-bf49-63cfec886638)

