package yuki.account.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountBrief {
    private String accountNumber;
    private Long balance;
}
