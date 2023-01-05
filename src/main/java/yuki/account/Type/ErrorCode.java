package yuki.account.Type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("사용자가 존재하지 않습니다.")
    , MAX_COUNT_PER_USER_10("사용자 최대 계좌는 10개 입니다.")
    , ACCOUNT_NUMBER_NOT_FOUND("해당 계좌 번호는 존재하지 않습니다.")
    , UN_MATCH_USER_ACCOUNT("사용자와 계좌 소유주가 다릅니다.")
    , ACCOUNT_BALANCE_NOT_EMPTY("계좌에 잔액이 남아있는 경우 해지할 수 없습니다.")
    , ALREADY_ACCOUNT_UNREGISTERED("계좌가 이미 해지된 상태입니다.")
    ;

    private final String description;
}
