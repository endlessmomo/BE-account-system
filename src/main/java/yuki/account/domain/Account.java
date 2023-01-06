package yuki.account.domain;

import lombok.*;
import yuki.account.Type.AccountStatus;
import yuki.account.Type.ErrorCode;
import yuki.account.exception.AccountException;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

import static yuki.account.Type.ErrorCode.EXCEED_BALANCE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Account extends BaseEntity {
    @ManyToOne
    private AccountUser accountUser;
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;

    public void useBalance(Long amount) {
        if (amount > this.balance) {
            throw new AccountException(EXCEED_BALANCE);
        }
        this.balance -= amount;
    }
}
