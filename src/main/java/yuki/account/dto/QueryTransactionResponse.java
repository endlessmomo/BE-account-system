package yuki.account.dto;

import lombok.*;
import yuki.account.Type.TransactionResultType;
import yuki.account.Type.TransactionStatus;
import yuki.account.Type.TransactionType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryTransactionResponse {
    private String accountNumber;
    private Long amount;
    private Long changeBalance;
    private String transactionId;
    private TransactionType transactionType;
    private TransactionResultType transactionResultType;
    private LocalDateTime transactedAt;

    public static QueryTransactionResponse from(TransactionDto dto){
        return QueryTransactionResponse.builder()
                .accountNumber(dto.getAccountNumber())
                .amount(dto.getAmount())
                .changeBalance(dto.getBalanceSnapshot())
                .transactionId(dto.getTransactionId())
                .transactionType(dto.getTransactionType())
                .transactionResultType(dto.getTransactionResultType())
                .transactedAt(dto.getTransactedAt())
                .build();
    }
}
