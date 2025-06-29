package skypay;

import skypay.banking.model.Account;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        System.out.println("=== Skypay Banking Service  ===");
        Account account = new Account();

        try {
            account.setTransactionDate(LocalDate.of(2012, 1, 10));
            account.deposit(1000);
            System.out.println("Deposited 1000");

            account.setTransactionDate(LocalDate.of(2012, 1, 13));
            account.deposit(2000);
            System.out.println("Deposited 2000");

            account.setTransactionDate(LocalDate.of(2012, 1, 14));
            account.withdraw(500);
            System.out.println("Withdrew 500");

            System.out.println("\n=== Bank Statement ===");
            account.printStatement();

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}