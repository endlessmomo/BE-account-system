package yuki.account.dto;

import lombok.*;
import yuki.account.Type.TransactionResultType;
import yuki.account.aop.AccountLockInterface;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class UseBalance {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request implements AccountLockInterface {
        @NotNull
        @Min(1)
        private Long userId;

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;

        @NotNull
        @Min(100)
        @Max(1_000_000)
        private Long amount;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NotNull
    @Builder
    public static class Response {
        private String accountNumber;
        private TransactionResultType transactionResultType;
        private String transactionId;
        private Long amount;
        private Long changeBalance;
        private LocalDateTime transactedAt;

        public static Response from(TransactionDto dto) {
            return Response.builder()
                    .accountNumber(dto.getAccountNumber())
                    .transactionResultType(dto.getTransactionResultType())
                    .transactionId(dto.getTransactionId())
                    .amount(dto.getAmount())
                    .changeBalance(dto.getBalanceSnapshot())
                    .transactedAt(dto.getTransactedAt())
                    .build();
        }
    }
}
