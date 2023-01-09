package yuki.account.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import yuki.account.Type.TransactionResultType;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CancelBalance {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {
        @NotBlank
        private String transactionId;

        @NotBlank
        @Length(min = 10, max = 10)
        private String accountNumber;

        @Min(100)
        @Max(1_000_000)
        private Long amount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String accountNumber;
        private Long amount;
        private Long changeBalance;
        private String transactionId;
        private TransactionResultType transactionResultType;
        private LocalDateTime transactedAt;

        public static Response from(TransactionDto dto){
            return Response.builder()
                    .accountNumber(dto.getAccountNumber())
                    .amount(dto.getAmount())
                    .changeBalance(dto.getBalanceSnapshot())
                    .transactionId(dto.getTransactionId())
                    .transactionResultType(dto.getTransactionResultType())
                    .transactedAt(dto.getTransactedAt())
                    .build();
        }
    }
}
