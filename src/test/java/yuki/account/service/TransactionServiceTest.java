package yuki.account.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yuki.account.Type.ErrorCode;
import yuki.account.domain.Account;
import yuki.account.domain.AccountUser;
import yuki.account.domain.Transaction;
import yuki.account.dto.TransactionDto;
import yuki.account.exception.AccountException;
import yuki.account.repository.AccountRepository;
import yuki.account.repository.AccountUserRepository;
import yuki.account.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static yuki.account.Type.AccountStatus.IN_USE;
import static yuki.account.Type.AccountStatus.UNREGISTERED;
import static yuki.account.Type.ErrorCode.ALREADY_ACCOUNT_UNREGISTERED;
import static yuki.account.Type.ErrorCode.EXCEED_BALANCE;
import static yuki.account.Type.TransactionResultType.SUCCESS;
import static yuki.account.Type.TransactionType.CANCEL;
import static yuki.account.Type.TransactionType.USE;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountUserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("계좌 잔액 사용 성공")
    void successUseBalance() {
        //given
        AccountUser user = AccountUser.builder()
                .name("yuki")
                .build();

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .balance(100000L)
                .accountNumber("1000000012")
                .build();

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(SUCCESS)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(30000L)
                .balanceSnapshot(70000L)
                .build();

        // BaseEntity 를 사용하기 때문에 직접 세팅 해줘야한다.
        user.setId(1L);
        account.setId(1L);
        transaction.setId(1L);

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        // #1
        given(transactionRepository.save(any()))
                .willReturn(transaction);

        ArgumentCaptor <Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        // #2
        //when
        TransactionDto dto = transactionService.useBalance(1L
                , "1000000000", 500L);

        //then
        // #1에 대한 결과값
        assertEquals(SUCCESS, dto.getTransactionResultType());
        assertEquals(USE, dto.getTransactionType());
        assertEquals(70000, dto.getBalanceSnapshot());
        assertEquals(30000, dto.getAmount());

        // #2에 대한 결과값
        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(500L, captor.getValue().getAmount());
        assertEquals(99500, captor.getValue().getBalanceSnapshot());
    }

    @Test
    @DisplayName("계좌 잔액 사용 실패 - 사용자가 존재하지 않는 경우 ")
    void failedUseBalance_UserNotFound() {
        //given
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1000000000", 500L));
        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌 잔액 사용 실패 - 계좌가 없는 경우")
    void failedUseBalance_AccountNumberNotFound() {
        //given
        AccountUser yuki = AccountUser.builder()
                .name("yuki")
                .build();
        yuki.setId(1L);

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(yuki));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1000000000", 500L));

        //then
        assertEquals(ErrorCode.ACCOUNT_NUMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("게좌 잔액 사용 실패 - 소유주가 다른 경우")
    void failedUseBalance_UnMatchUserAccount() {
        //given
        AccountUser yuki = AccountUser.builder()
                .name("yuki")
                .build();
        yuki.setId(1L);

        AccountUser hayoon = AccountUser.builder()
                .name("hayoon")
                .build();
        hayoon.setId(2L);

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(yuki));

        Account account = Account.builder()
                .accountUser(hayoon)
                .accountNumber("1000000012")
                .balance(0L)
                .build();
        account.setId(2L);

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(2L, "1000000012", 500L));

        //then
        assertEquals(ErrorCode.UN_MATCH_USER_ACCOUNT, exception.getErrorCode());
    }

    @Test
    @DisplayName("게좌 잔액 사용 실패 - 해지된 계좌인 경우")
    void failedUseBalance_AlreadyAccountUnregistered() {
        //given
        AccountUser hayoon = AccountUser.builder()
                .name("hayoon")
                .build();
        hayoon.setId(1L);

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(hayoon));

        Account account = Account.builder()
                .accountUser(hayoon)
                .accountNumber("1000000012")
                .balance(0L)
                .accountStatus(UNREGISTERED)
                .build();
        account.setId(1L);

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1000000012", 500L));

        //then
        assertEquals(ALREADY_ACCOUNT_UNREGISTERED, exception.getErrorCode());
    }

    @Test
    @DisplayName("게좌 잔액 사용 실패 - 계좌 잔액을 초과한 결제 금액인 경우")
    void failedUseBalance_ExceedBalance() {
        //given
        AccountUser hayoon = AccountUser.builder()
                .name("hayoon")
                .build();
        hayoon.setId(1L);

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(hayoon));

        Account account = Account.builder()
                .accountUser(hayoon)
                .accountNumber("1000000012")
                .balance(300L)
                .accountStatus(IN_USE)
                .build();
        account.setId(1L);

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1000000012", 500L));

        //then
        assertEquals(EXCEED_BALANCE, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌 거래 확인")
    void successQueryTransaction() {
        //given
        AccountUser user = AccountUser.builder()
                .name("yuki")
                .build();

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .balance(100000L)
                .accountNumber("1000000012")
                .build();

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(SUCCESS)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(30000L)
                .balanceSnapshot(70000L)
                .build();

        // BaseEntity 를 사용하기 때문에 직접 세팅 해줘야한다.
        user.setId(1L);
        account.setId(1L);
        transaction.setId(1L);

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));
        //when
        TransactionDto dto = transactionService.queryTransaction("trxId");

        //then
        assertEquals(USE, dto.getTransactionType());
        assertEquals(SUCCESS,dto.getTransactionResultType());
        assertEquals("transactionId", dto.getTransactionId());
    }
}