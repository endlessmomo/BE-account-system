package yuki.account.service;

import yuki.account.Type.ErrorCode;
import yuki.account.domain.Account;
import yuki.account.domain.AccountUser;
import yuki.account.dto.AccountDto;
import yuki.account.exception.AccountException;
import yuki.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yuki.account.repository.AccountUserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static yuki.account.Type.AccountStatus.IN_USE;

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
}
