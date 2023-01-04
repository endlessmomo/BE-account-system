package yuki.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yuki.account.Type.ErrorCode;
import yuki.account.domain.Account;
import yuki.account.domain.AccountUser;
import yuki.account.dto.AccountDto;
import yuki.account.exception.AccountException;
import yuki.account.repository.AccountRepository;
import yuki.account.repository.AccountUserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;

import static yuki.account.Type.AccountStatus.IN_USE;
import static yuki.account.Type.AccountStatus.UNREGISTERED;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;

    /**
     * @param userId
     * @param basicBalance 사용자가 있는지 확인지 조회
     *                     계좌의 번호를 생성
     *                     계좌를 레파지토리의 저장하고, 그 정보를 반환한다.
     */
    @Transactional
    public AccountDto createAccount(Long userId, Long basicBalance) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        validateCreateAccount(accountUser);

        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> (Integer.parseInt(account.getAccountNumber()) + 1) + "")
                .orElse("1000000000");

        return AccountDto.fromEntity(
                accountRepository.save(
                        Account.builder()
                                .accountUser(accountUser)
                                .accountStatus(IN_USE)
                                .accountNumber(newAccountNumber)
                                .balance(basicBalance)
                                .registeredAt(LocalDateTime.now())
                                .build()
                )
        );
    }

    private void validateCreateAccount(AccountUser accountUser) {
        if(accountRepository.countByAccountUser(accountUser) == 10){
            throw new AccountException(ErrorCode.MAX_COUNT_PER_USER_10);
        }
    }

    @Transactional
    public Account getAccount(Long id) {
        if (id < 0) {
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();
    }

    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        AccountUser user = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new AccountException(ErrorCode.ACCOUNT_NUMBER_NOT_FOUND));

        validateDeleteAccount(user, account);

        account.setAccountStatus(UNREGISTERED);
        account.setUnRegisteredAt(LocalDateTime.now());

        return AccountDto.fromEntity(account);
    }

    private void validateDeleteAccount(AccountUser user, Account account){
        if(!Objects.equals(user.getId(), account.getAccountUser().getId())){
            throw new AccountException(ErrorCode.UN_MATCH_USER_ACCOUNT);
        }

        if(account.getAccountStatus() == UNREGISTERED){
            throw new AccountException(ErrorCode.ALREADY_ACCOUNT_UNREGISTERED);
        }

        if(account.getBalance() > 0){
            throw new AccountException(ErrorCode.ACCOUNT_BALANCE_NOT_EMPTY);
        }
    }
}
