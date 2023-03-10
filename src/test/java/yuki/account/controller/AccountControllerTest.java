package yuki.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import yuki.account.Type.AccountStatus;
import yuki.account.Type.ErrorCode;
import yuki.account.domain.Account;
import yuki.account.dto.AccountDto;
import yuki.account.dto.CreatedAccount;
import yuki.account.dto.DeleteAccount;
import yuki.account.exception.AccountException;
import yuki.account.service.AccountService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static yuki.account.Type.ErrorCode.ACCOUNT_NUMBER_NOT_FOUND;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("계좌 생성 성공")
    void successCreateAccount() throws Exception {
        //given
        given(accountService.createAccount(anyLong(), anyLong()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unregisteredAt(LocalDateTime.now())
                        .build());
        //when

        //then
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreatedAccount.Request(1L, 100L)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());
    }

    @Test
    @DisplayName("계좌 생성 실패")
    void failCreateAccount() throws Exception {
        //given
        given(accountService.createAccount(anyLong(), anyLong()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unregisteredAt(LocalDateTime.now())
                        .build());
        //when
        //then
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreatedAccount.Request(1L, 100L)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());

    }

    @Test
    @DisplayName("계좌 삭제 성공")
    void successDeleteAccount() throws Exception {
        //given
        given(accountService.deleteAccount(anyLong(), anyString()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unregisteredAt(LocalDateTime.now())
                        .build());
        //when

        //then
        mockMvc.perform(delete("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DeleteAccount.Request(1L, "1234567890")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());

    }

    @Test
    @DisplayName("계좌 조회 성공")
    void successGetAccountsByUserId() throws Exception {
        // given
        List <AccountDto> accountDtos = Arrays.asList(
                AccountDto.builder()
                        .accountNumber("1234567890")
                        .balance(1000L)
                        .build(),
                AccountDto.builder()
                        .accountNumber("1234567891")
                        .balance(30000L)
                        .build(),
                AccountDto.builder()
                        .accountNumber("1234567892")
                        .balance(10000L)
                        .build(),
                AccountDto.builder()
                        .accountNumber("1234567893")
                        .balance(5000L)
                        .build()
        );

        given(accountService.getAccountsByUserId(anyLong()))
                .willReturn(accountDtos);
        //when
        //then
        mockMvc.perform(get("/account?user_id=1"))
                .andDo(print())
                .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
                .andExpect(jsonPath("$[0].balance").value(1000))
                .andExpect(jsonPath("$[1].accountNumber").value("1234567891"))
                .andExpect(jsonPath("$[1].balance").value(30000))
                .andExpect(jsonPath("$[2].accountNumber").value("1234567892"))
                .andExpect(jsonPath("$[2].balance").value(10000))
                .andExpect(jsonPath("$[3].accountNumber").value("1234567893"))
                .andExpect(jsonPath("$[3].balance").value(5000));
    }

    @Test
    void successGetAccount() throws Exception {
        //given
        given(accountService.getAccount(anyLong()))
                .willReturn(Account.builder()
                        .accountNumber("3456")
                        .accountStatus(AccountStatus.IN_USE)
                        .build());

        //when
        //then
        mockMvc.perform(get("/account/876"))
                .andDo(print())
                .andExpect(jsonPath("$.accountNumber").value("3456"))
                .andExpect(jsonPath("$.accountStatus").value("IN_USE"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("계좌 확인 실패")
    void failGetAccount() throws Exception {
        //given
        given(accountService.getAccount(anyLong()))
                .willThrow(new AccountException(ACCOUNT_NUMBER_NOT_FOUND));
        //when

        //then
        mockMvc.perform(get("/account/876"))
                .andDo(print())
                .andExpect(jsonPath("$.errorCode").value(ACCOUNT_NUMBER_NOT_FOUND+""))
                .andExpect(jsonPath("$.errorMessage").value("[Error] : 해당 계좌 번호는 존재하지 않습니다."))
                .andExpect(status().isOk());

    }
}