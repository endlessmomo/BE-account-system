package yuki.account.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import yuki.account.aop.AccountLock;
import yuki.account.dto.CancelBalance;
import yuki.account.dto.QueryTransactionResponse;
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
    @AccountLock
    public UseBalance.Response useBalance(
            @Valid @RequestBody UseBalance.Request request
    ) throws InterruptedException {
        try {
            Thread.sleep(3000L);
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

    @PostMapping("/cancel")
    @AccountLock
    public CancelBalance.Response cancelBalance(
            @Valid @RequestBody CancelBalance.Request request
    ) {
        try {
            return CancelBalance.Response.from(
                    transactionService.cancelBalance(request.getTransactionId()
                            , request.getAccountNumber(), request.getAmount())
            );
        } catch (AccountException e) {
            log.error("Failed to pay-cancel");

            transactionService.cancelFailedBalance(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }
    }

    @GetMapping("{transactionId}")
    public QueryTransactionResponse queryTransaction(
            @PathVariable String transactionId
    ) {
        return QueryTransactionResponse.from(
                transactionService.queryTransaction(transactionId)
        );
    }
}
