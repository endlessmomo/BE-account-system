package yuki.account.dto;

import lombok.*;
import yuki.account.domain.BaseEntity;

import java.time.LocalDateTime;

public class CreateAccount {

    @Getter
    @Setter
    public static class Request {
        private Long userId;
        private Long balance;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response extends BaseEntity {
        private Long userId;
        private String accountNumber;
        private LocalDateTime registeredAt;
    }
}
