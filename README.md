# Gym Ledger
A super simple console app that lets a gymnastics gym track money going in (deposits) and out (payments).
It reads and writes a file called transactions.csv.
# What this Application does
Add Deposit (D): add money coming in (saved as a positive number)
Make Payment (P): add money going out (saved as a negative number)
Ledger (L):
	All entries (newest first)
	Deposits only
	Payments only
	Reports:

		Month To Date
		Previous Month
		Year To Date
		Previous Year
		Search by Vendor (e.g., type pro to match “Pro Shop”)
Exit (X)
Every time you add a deposit or payment, the app saves the whole list back to transactions.csv.
# Data format (CSV)
Each line in transactions.csv looks like this:
	date|time|description|vendor|amount
