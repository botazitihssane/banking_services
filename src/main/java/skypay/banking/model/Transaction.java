package skypay.banking.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record Transaction(LocalDate date, int amount, int balanceAfter, TransactionType type) {

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String toString() {
        String sign = type == TransactionType.WITHDRAWAL ? "-" : "";
        return String.format("%s || %s%d || %d",
                date.format(FORMATTER), sign, amount, balanceAfter);
    }
}