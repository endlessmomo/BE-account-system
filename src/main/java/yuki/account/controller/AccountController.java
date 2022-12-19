package yuki.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import yuki.account.domain.Account;
import yuki.account.dto.CreateAccount;
import yuki.account.service.AccountService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/account")
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request
    ) {
        return CreateAccount.Response.from(
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
}
