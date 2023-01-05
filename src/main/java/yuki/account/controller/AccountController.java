package yuki.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import yuki.account.domain.Account;
import yuki.account.dto.AccountInfo;
import yuki.account.dto.CreatedAccount;
import yuki.account.dto.DeleteAccount;
import yuki.account.service.AccountService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/account")
    public CreatedAccount.Response createAccount(
            @RequestBody @Valid CreatedAccount.Request request
    ) {
        return CreatedAccount.Response.from(
                accountService.createAccount(
                        request.getUserId(),
                        request.getBasicBalance()
                )
        );
    }

    @GetMapping("/account/{id}")
    public Account getAccount(
            @PathVariable Long id) {
        return accountService.getAccount(id);
    }

    @DeleteMapping("/account")
    public DeleteAccount.Response deleteAccount(
            @RequestBody @Valid DeleteAccount.Request request
    ) {
        return DeleteAccount.Response.from(
                accountService.deleteAccount(
                        request.getUserId(),
                        request.getAccountNumber()
                )
        );
    }

    @GetMapping("/account")
    public List <AccountInfo> getAccountsByUserID(
            @RequestParam("user_id") Long userId
    ) {
        return accountService.getAccountsByUserId(userId)
                .stream().map(dto -> AccountInfo.builder()
                        .accountNUmber(dto.getAccountNumber())
                        .balance(dto.getBalance())
                        .build())
                .collect(toList());
    }
}
