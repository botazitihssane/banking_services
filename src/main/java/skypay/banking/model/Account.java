package skypay.banking.model;

import skypay.banking.service.AccountService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Account implements AccountService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String HEADER = "Date || Amount || Balance";

    private final List<Transaction> transactions;
    private int balance;
    private LocalDate currentTransactionDate = LocalDate.now();

    public Account() {
        this.transactions = new ArrayList<>(16);
        this.balance = 0;
    }


    @Override
    public synchronized void deposit(int amount) {
        validateAmount(amount);

        balance += amount;
        transactions.add(new Transaction(currentTransactionDate, amount, balance, Transaction.TransactionType.DEPOSIT));
    }

    @Override
    public synchronized void withdraw(int amount) {
        validateAmount(amount);

        if (balance < amount) {
            throw new IllegalArgumentException(
                    String.format("Insufficient funds. Current balance: %d, Requested: %d", balance, amount)
            );
        }

        balance -= amount;
        transactions.add(new Transaction(currentTransactionDate, amount, balance, Transaction.TransactionType.WITHDRAWAL));
    }

    @Override
    public synchronized void printStatement() {
        System.out.println(HEADER);

        for (int i = transactions.size() - 1; i >= 0; i--) {
            Transaction transaction = transactions.get(i);
            System.out.println(formatTransaction(transaction));
        }
    }

    public synchronized void setTransactionDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.currentTransactionDate = date;
    }


    private String formatTransaction(Transaction transaction) {
        String dateStr = transaction.date().format(DATE_FORMATTER);
        String sign = transaction.type() == Transaction.TransactionType.WITHDRAWAL ? "-" : "";
        return String.format("%s || %s%d || %d", dateStr, sign, transaction.amount(), transaction.balanceAfter());
    }

    private void validateAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive. Provided: " + amount);
        }
    }

    public synchronized int getBalance() {
        return balance;
    }

    public synchronized List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
}
