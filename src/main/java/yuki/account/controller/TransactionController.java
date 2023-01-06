package yuki.account.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yuki.account.dto.UseBalance;
import yuki.account.exception.AccountException;
import yuki.account.service.TransactionService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/use")
    public UseBalance.Response useBalance(
            @Valid @RequestBody UseBalance.Request request
    ) {
        try {
            return UseBalance.Response.from(
                    transactionService.useBalance(request.getUserId(),
                            request.getAccountNumber(), request.getAmount())
            );
        } catch (AccountException e) {
            log.error("Failed to use balance");

            transactionService.useFailedBalance(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }
    }
}
