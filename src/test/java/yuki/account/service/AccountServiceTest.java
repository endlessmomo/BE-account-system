package yuki.account.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yuki.account.Type.AccountStatus;
import yuki.account.Type.ErrorCode;
import yuki.account.domain.Account;
import yuki.account.domain.AccountUser;
import yuki.account.dto.AccountDto;
import yuki.account.exception.AccountException;
import yuki.account.repository.AccountRepository;
import yuki.account.repository.AccountUserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("계좌 생성 성공")
    void createAccountSuccess() {
        //given
        AccountUser user = AccountUser.builder()
                .name("yuki").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1000000012").build()));

        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000013").build());

        ArgumentCaptor <Account> captor = ArgumentCaptor.forClass(Account.class);

        //when
        AccountDto dto = accountService.createAccount(1L, 1000L);

        //then
        assertEquals(user.getId(), dto.getUserId());
        assertEquals("1000000013", dto.getAccountNumber());
        verify(accountRepository, times(1)).save(captor.capture());
    }

    @Test
    @DisplayName("계좌 생성 실패 - 해당 유저가 없는 경우")
    void createAccountFail_UserNotFound() {
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));


        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("사용자 최대 계좌 갯수가 10개인지")
    void createAccount_maxAccountIs10() {
        //given
        AccountUser user = AccountUser.builder()
                .name("Yuki")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.countByAccountUser(any()))
                .willReturn(10);

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));

        //then
        assertEquals(ErrorCode.MAX_COUNT_PER_USER_10, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌 삭제 성공")
    void deleteAccountSuccess() {
        //given
        AccountUser user = AccountUser.builder()
                .name("yuki").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .balance(0L)
                        .accountNumber("1000000000").build()));

        ArgumentCaptor <Account> captor = ArgumentCaptor.forClass(Account.class);

        //when
        AccountDto dto = accountService.deleteAccount(1L, "1234567890");

        //then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals("1000000000", dto.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, captor.getValue().getAccountStatus());
    }

    @Test
    @DisplayName("계좌 삭제 실패 - 해당 유저 없음")
    void deleteAccountFail_UserNotFound() {
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌 삭제 실패 - 해당 계좌 없음")
    void deleteAccountFail_AccountNotFound() {
        //given
        AccountUser user = AccountUser.builder()
                .name("Yuki")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        //then
        assertEquals(ErrorCode.ACCOUNT_NUMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌 삭제 실패 - 계좌 소유주가 다른 경우")
    void deleteAccountFail_TheOtherAccountUser() {
        //given
        AccountUser Yuki = AccountUser.builder()
                .name("Yuki")
                .build();
        Yuki.setId(1L);

        AccountUser Hayoon = AccountUser.builder()
                .name("Hayoon")
                .build();
        Hayoon.setId(2L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Yuki));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(Hayoon)
                        .balance(0L)
                        .accountNumber("1000000000").build()));
        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1000000000"));

        //then
        assertEquals(ErrorCode.UN_MATCH_USER_ACCOUNT, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌 삭제 실패 - 계좌 내 잔액이 있으면 안된다.")
    void deleteAccountFail_AccountBalanceNotEmpty() {
        //given
        AccountUser Yuki = AccountUser.builder()
                .name("Yuki")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Yuki));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(Yuki)
                        .balance(1000L)
                        .accountNumber("1000000000").build()));
        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1000000000"));

        //then
        assertEquals(ErrorCode.ACCOUNT_BALANCE_NOT_EMPTY, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌 삭제 실패 - 이미 계좌가 해지된 상태")
    void deleteAccountFail_AlreadyUnregistered() {
        //given
        AccountUser Yuki = AccountUser.builder()
                .name("Yuki")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Yuki));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(Yuki)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("1000000000").build()));
        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1000000000"));

        //then
        assertEquals(ErrorCode.ALREADY_ACCOUNT_UNREGISTERED, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌 조회 성공")
    void successGetAccountsByUserId(){
        //given
        AccountUser yuki = AccountUser.builder()
                .name("Yuki")
                .build();
        yuki.setId(1L);

        List<Account> accounts = Arrays.asList(
                Account.builder()
                        .accountUser(yuki)
                        .accountNumber("1234567890")
                        .balance(1000L)
                        .build(),
                Account.builder()
                        .accountUser(yuki)
                        .accountNumber("1234567892")
                        .balance(2000L)
                        .build(),
                Account.builder()
                        .accountUser(yuki)
                        .accountNumber("1234567893")
                        .balance(3000L)
                        .build()
        );

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(yuki));
        given(accountRepository.findByAccountUser(any()))
                .willReturn(accounts);

        //when
        List <AccountDto> accountDtos = accountService.getAccountsByUserId(1L);

        //then
        assertEquals(3, accountDtos.size());
        assertEquals("1234567890", accountDtos.get(0).getAccountNumber());
        assertEquals(1000, accountDtos.get(0).getBalance());
    }

    @Test
    @DisplayName("계좌 조회 실패")
    void failedGetAccountsByUserId(){
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.getAccountsByUserId(1L));
        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }
}