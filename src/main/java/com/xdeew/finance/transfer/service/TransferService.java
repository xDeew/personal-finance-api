package com.xdeew.finance.transfer.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xdeew.finance.account.model.Account;
import com.xdeew.finance.account.repository.AccountRepository;
import com.xdeew.finance.transfer.dto.CreateTransferRequest;
import com.xdeew.finance.transfer.dto.TransferResponse;
import com.xdeew.finance.transfer.model.Transfer;
import com.xdeew.finance.transfer.repository.TransferRepository;
import com.xdeew.finance.user.model.User;
import com.xdeew.finance.user.service.UserService;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final UserService userService;
    private final AccountRepository accountRepository;

    public TransferService(TransferRepository transferRepository,
            UserService userService,
            AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.userService = userService;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public TransferResponse createTransfer(String userEmail, CreateTransferRequest request) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Account sourceAccount = accountRepository.findByIdAndUserId(request.sourceAccountId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        Account targetAccount = accountRepository.findByIdAndUserId(request.targetAccountId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Target account not found"));

        if (!sourceAccount.isActive() || !targetAccount.isActive()) {
            throw new IllegalArgumentException("Both accounts must be active");
        }

        if (sourceAccount.getId().equals(targetAccount.getId())) {
            throw new IllegalArgumentException("Source and target accounts must be different");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.amount()));
        targetAccount.setBalance(targetAccount.getBalance().add(request.amount()));

        if (sourceAccount.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient balance for this transfer");
        }

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        Transfer transfer = Transfer.builder()
                .description(request.description())
                .amount(request.amount())
                .transferDate(request.transferDate())
                .user(user)
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .build();

        Transfer savedTransfer = transferRepository.save(transfer);

        return mapToResponse(savedTransfer);
    }

    @Transactional(readOnly = true)
    public com.xdeew.finance.common.dto.PagedResponse<TransferResponse> getTransfers(String userEmail, int page, int size) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        org.springframework.data.domain.Pageable pageable
                = org.springframework.data.domain.PageRequest.of(
                        page,
                        size,
                        org.springframework.data.domain.Sort.by(
                                org.springframework.data.domain.Sort.Direction.DESC,
                                "transferDate"
                        )
                );

        org.springframework.data.domain.Page<Transfer> transferPage
                = transferRepository.findAllByUserIdOrderByTransferDateDesc(user.getId(), pageable);

        return new com.xdeew.finance.common.dto.PagedResponse<>(
                transferPage.getContent().stream().map(this::mapToResponse).toList(),
                transferPage.getNumber(),
                transferPage.getSize(),
                transferPage.getTotalElements(),
                transferPage.getTotalPages(),
                transferPage.isFirst(),
                transferPage.isLast()
        );
    }

    private TransferResponse mapToResponse(Transfer transfer) {
        return new TransferResponse(
                transfer.getId(),
                transfer.getDescription(),
                transfer.getAmount(),
                transfer.getTransferDate(),
                transfer.getSourceAccount().getId(),
                transfer.getTargetAccount().getId()
        );
    }
}
