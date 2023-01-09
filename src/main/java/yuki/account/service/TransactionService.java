package yuki.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuki.account.Type.TransactionResultType;
import yuki.account.Type.TransactionType;
import yuki.account.domain.Account;
import yuki.account.domain.AccountUser;
import yuki.account.domain.Transaction;
import yuki.account.dto.TransactionDto;
import yuki.account.exception.AccountException;
import yuki.account.repository.AccountRepository;
import yuki.account.repository.AccountUserRepository;
import yuki.account.repository.TransactionRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static yuki.account.Type.AccountStatus.UNREGISTERED;
import static yuki.account.Type.ErrorCode.*;
import static yuki.account.Type.TransactionResultType.FAIL;
import static yuki.account.Type.TransactionResultType.SUCCESS;
import static yuki.account.Type.TransactionType.CANCEL;
import static yuki.account.Type.TransactionType.USE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountUserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public TransactionDto useBalance(Long userId, String accountNumber, Long amount) {
        AccountUser user = userRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NUMBER_NOT_FOUND));

        validateUseBalance(user, account, amount);

        account.useBalance(amount);

        return TransactionDto.fromEntity(
                getAndSaveTransaction(USE, SUCCESS, account, amount)
        );
    }

    private void validateUseBalance(AccountUser user, Account account, Long amount) {
        if (!Objects.equals(user.getId(), account.getId())) {
            throw new AccountException(UN_MATCH_USER_ACCOUNT);
        }

        if (UNREGISTERED.equals(account.getAccountStatus())) {
            throw new AccountException(ALREADY_ACCOUNT_UNREGISTERED);
        }

        if (account.getBalance() < amount) {
            throw new AccountException((EXCEED_BALANCE));
        }
    }

    @Transactional
    public void useFailedBalance(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        getAndSaveTransaction(USE, FAIL, account, amount);
    }

    private Transaction getAndSaveTransaction(
            TransactionType type
            , TransactionResultType resultType
            , Account account
            , Long amount) {
        return transactionRepository.save(
                Transaction.builder()
                        .transactionType(type)
                        .transactionResultType(resultType)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString().replace("-", ""))
                        .transactedAt(LocalDateTime.now())
                        .build()
        );
    }

    @Transactional
    public TransactionDto cancelBalance(String transactionId, String accountNumber, Long amount) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new AccountException(TRANSACTION_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException((USER_NOT_FOUND)));

        validateCancelBalance(transaction, account, amount);

        return TransactionDto.fromEntity(
                getAndSaveTransaction(CANCEL, SUCCESS, account, amount)
        );
    }

    private void validateCancelBalance(Transaction transaction, Account account, Long amount) {
        if (!Objects.equals(transaction.getAccount().getId(), account.getId())) {
            throw new AccountException(TRANSACTION_UN_MATCH_USER_ACCOUNT);
        }

        if (!Objects.equals(transaction.getAmount(), amount)) {
            throw new AccountException(CANCEL_MUST_FULLY);
        }

        if (transaction.getTransactedAt().isBefore(LocalDateTime.now().minusYears(1))) {
            throw new AccountException(TOO_OLD_ORDER_TO_CANCEL);
        }
    }

    public void cancelFailedBalance(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException((USER_NOT_FOUND)));

        getAndSaveTransaction(CANCEL, FAIL, account, amount);
    }
}
