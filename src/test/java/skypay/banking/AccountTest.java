package skypay.banking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import skypay.banking.model.Account;
import skypay.banking.model.Transaction;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    private Account account;
    private final LocalDate testDate = LocalDate.of(2024, 1, 15);

    @BeforeEach
    void setUp() {
        account = new Account();
    }

    @Test
    @DisplayName("Should deposit positive amount successfully")
    void shouldDepositPositiveAmount() {
        // Given
        int depositAmount = 100;
        account.setTransactionDate(testDate);

        // When
        account.deposit(depositAmount);

        // Then
        assertEquals(100, account.getBalance());
        assertEquals(1, account.getTransactions().size());

        Transaction transaction = account.getTransactions().get(0);
        assertEquals(depositAmount, transaction.amount());
        assertEquals(100, transaction.balanceAfter());
        assertEquals(Transaction.TransactionType.DEPOSIT, transaction.type());
        assertEquals(testDate, transaction.date());
    }

    @Test
    @DisplayName("Should handle multiple deposits correctly")
    void shouldHandleMultipleDeposits() {
        // Given & When
        account.deposit(100);
        account.deposit(50);
        account.deposit(25);

        // Then
        assertEquals(175, account.getBalance());
        assertEquals(3, account.getTransactions().size());
        Transaction lastTransaction = account.getTransactions().get(2);
        assertEquals(25, lastTransaction.amount());
        assertEquals(175, lastTransaction.balanceAfter());
    }

    @Test
    @DisplayName("Should throw exception for zero amount deposit")
    void shouldThrowExceptionForZeroDeposit() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(0)
        );
        assertTrue(exception.getMessage().contains("Amount must be positive"));
        assertEquals(0, account.getBalance());
        assertEquals(0, account.getTransactions().size());
    }

    @Test
    @DisplayName("Should throw exception for negative amount deposit")
    void shouldThrowExceptionForNegativeDeposit() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(-50)
        );
        assertTrue(exception.getMessage().contains("Amount must be positive"));
        assertEquals(0, account.getBalance());
        assertEquals(0, account.getTransactions().size());
    }

    @Test
    @DisplayName("Should withdraw amount when sufficient funds available")
    void shouldWithdrawWhenSufficientFunds() {
        // Given
        account.deposit(1000);

        // When
        account.withdraw(300);

        // Then
        assertEquals(700, account.getBalance());
        assertEquals(2, account.getTransactions().size());

        Transaction withdrawalTransaction = account.getTransactions().get(1);
        assertEquals(300, withdrawalTransaction.amount());
        assertEquals(700, withdrawalTransaction.balanceAfter());
        assertEquals(Transaction.TransactionType.WITHDRAWAL, withdrawalTransaction.type());
    }

    @Test
    @DisplayName("Should throw exception when insufficient funds")
    void shouldThrowExceptionWhenInsufficientFunds() {
        // Given
        account.deposit(100);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(150)
        );

        assertTrue(exception.getMessage().contains("Insufficient funds"));
        assertTrue(exception.getMessage().contains("Current balance: 100"));
        assertTrue(exception.getMessage().contains("Requested: 150"));
        assertEquals(100, account.getBalance());
        assertEquals(1, account.getTransactions().size());
    }

    @Test
    @DisplayName("Should throw exception for zero amount withdrawal")
    void shouldThrowExceptionForZeroWithdrawal() {
        // Given
        account.deposit(100);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(0)
        );
        assertTrue(exception.getMessage().contains("Amount must be positive"));
    }

    @Test
    @DisplayName("Should throw exception for negative amount withdrawal")
    void shouldThrowExceptionForNegativeWithdrawal() {
        // Given
        account.deposit(100);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(-50)
        );
        assertTrue(exception.getMessage().contains("Amount must be positive"));
    }

    @Test
    @DisplayName("Should throw exception when withdrawing from empty account")
    void shouldThrowExceptionWhenWithdrawingFromEmptyAccount() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(50)
        );
        assertTrue(exception.getMessage().contains("Insufficient funds"));
        assertTrue(exception.getMessage().contains("Current balance: 0"));
    }

    @Test
    @DisplayName("Should print statement with transactions in reverse chronological order")
    void shouldPrintStatementInReverseOrder() {
        // Given
        account.setTransactionDate(LocalDate.of(2012, 1, 10));
        account.deposit(1000);

        account.setTransactionDate(LocalDate.of(2012, 1, 13));
        account.deposit(2000);

        account.setTransactionDate(LocalDate.of(2012, 1, 14));
        account.withdraw(500);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // When
        account.printStatement();

        // Then
        String output = outputStream.toString();
        String[] lines = output.split("\n");

        System.setOut(originalOut);
        System.out.println("Actual output:");
        System.out.println(output);
        System.setOut(new PrintStream(outputStream));

        assertTrue(lines[0].contains("Date || Amount || Balance"));
        assertTrue(lines[1].contains("14/01/2012 || -500 || 2500"));
        assertTrue(lines[2].contains("13/01/2012 || 2000 || 3000"));
        assertTrue(lines[3].contains("10/01/2012 || 1000 || 1000"));

    }
}
