package yuki.account.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountInfo {
    private String accountNUmber;
    private Long balance;
}
