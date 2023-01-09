package yuki.account.domain;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import yuki.account.Type.TransactionResultType;
import yuki.account.Type.TransactionStatus;
import yuki.account.Type.TransactionType;

import javax.persistence.*;
import java.time.LocalDateTime;

import static yuki.account.Type.TransactionStatus.CANCELED;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Transaction extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @Enumerated(EnumType.STRING)
    private TransactionResultType transactionResultType;
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @ManyToOne
    private Account account;
    private Long amount;
    private Long balanceSnapshot;

    private String transactionId;
    private LocalDateTime transactedAt;

    public void canceledBalance(TransactionStatus status){
        this.transactionStatus = status;
    }
}
