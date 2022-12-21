package yuki.account.exception;

import lombok.*;
import yuki.account.Type.ErrorCode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountException extends RuntimeException {
    private static final String Error = "[Error] : ";
    private ErrorCode errorCode;
    private String errorMessage;

    public AccountException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.errorMessage = Error + errorCode.getDescription();
    }


}
