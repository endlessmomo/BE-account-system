package yuki.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import yuki.account.domain.Account;
import yuki.account.dto.CreatedAccount;
import yuki.account.dto.DeleteAccount;
import yuki.account.service.AccountService;

import javax.validation.Valid;

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
    ){
        return DeleteAccount.Response.from(
                accountService.deleteAccount(
                        request.getUserId(),
                        request.getAccountNumber()
                )
        );
    }
}
