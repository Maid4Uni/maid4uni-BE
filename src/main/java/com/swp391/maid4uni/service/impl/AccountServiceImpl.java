package com.swp391.maid4uni.service.impl;

import com.swp391.maid4uni.converter.AccountConverter;
import com.swp391.maid4uni.entity.Account;
import com.swp391.maid4uni.entity.Role;
import com.swp391.maid4uni.enums.ENUMS;
import com.swp391.maid4uni.exception.Maid4UniException;
import com.swp391.maid4uni.repository.AccountRepository;
import com.swp391.maid4uni.request.LoginByUsernameRequest;
import com.swp391.maid4uni.request.RegisterAccountRequest;
import com.swp391.maid4uni.request.UpdateAccountRequest;
import com.swp391.maid4uni.response.AccountResponse;
import com.swp391.maid4uni.response.LoginByUsernameResponse;
import com.swp391.maid4uni.service.AccountService;
import com.swp391.maid4uni.ulti.JwtTokenUtil;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Account service.
 */
@Service
@Data
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@NoArgsConstructor(force = true)
@Builder
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    /**
     * Login by username
     */
    @Override
    public LoginByUsernameResponse loginByUsername(LoginByUsernameRequest request) {
        Account account = accountRepository
                .findByUsernameAndLogicalDeleteStatus(request.getUsername(), (short) 0)
                .orElseThrow(() -> new RuntimeException("Account not found."));

        // flow: lấy pw nhận vào -> encode -> nếu trùng với trong DB -> authen
        boolean isAuthentication = passwordEncoder
                .matches(request.getPassword(), account.getPassword());
        if (!isAuthentication) {
            throw Maid4UniException.badRequest("Username or password is incorrect.");
        }

        // build token payload từ account
        // set token timeout 1 ngày
        String accessToken = jwtTokenUtil.generateToken(AccountConverter.INSTANCE.fromAccountToTokenPayload(account), ENUMS.JWT_EXPIRATION_ONE_DAY);
        return LoginByUsernameResponse
                .builder()
                .account(AccountConverter.INSTANCE.fromAccountToAccountDTO(account))
                .accessToken(accessToken)
                .build();
    }

    /**
     * Get list account
     */

    @Override
    public List<AccountResponse> getAccountList() {
        List<Account> accountList = accountRepository.findAllByLogicalDeleteStatus((short) 0);
        // !CollectionUtils.isEmpty(accountList) trả về true nếu list k rỗng và khác null
        List<AccountResponse> accountResponseList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(accountList)) {
            accountResponseList =
                    accountList.stream()
                            .map(AccountConverter.INSTANCE::fromAccountToAccountResponse)
                            .toList();
        }
        return accountResponseList;
    }

    /**
     * Get list manager
     */

    @Override
    public List<AccountResponse> getManagerList() {
        List<Account> managerList = accountRepository.findByRoleAndLogicalDeleteStatus(Role.MANAGER, (short) 0);
        List<AccountResponse> managerResponseList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(managerList)) {
            managerResponseList =
            managerList.stream()
                    .map(AccountConverter.INSTANCE::fromAccountToAccountResponse)
                    .toList();
        }
        return managerResponseList;
    }

    /**
     * Get list staff
     */

    @Override
    public List<AccountResponse> getStaffList() {
        List<Account> staffList = accountRepository.findByRoleAndLogicalDeleteStatus(Role.STAFF, (short) 0);
        List<AccountResponse> staffResponseList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(staffList)) {
            staffResponseList =
            staffList.stream()
                    .map(AccountConverter.INSTANCE::fromAccountToAccountResponse)
                    .toList();
        }
        return staffResponseList;
    }

    @Override
    public List<AccountResponse> getCustomerList() {
        List<Account> customerList = accountRepository.findByRoleAndLogicalDeleteStatus(Role.CUSTOMER, (short) 0);
        List<AccountResponse> customerResponseList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(customerList)) {
            customerResponseList =
            customerList.stream()
                    .map(AccountConverter.INSTANCE::fromAccountToAccountResponse)
                    .toList();
        }
        return customerResponseList;
    }

    @Override
    public AccountResponse register(RegisterAccountRequest registerAccountRequest) {
        log.info("Start Validating register request");
        validateRegisterAccountRequest(registerAccountRequest);
        Account account = AccountConverter.INSTANCE.fromRegisterAccountRequestToAccount(registerAccountRequest);
        // mã hóa password
        String rawPassword = account.getPassword();
        account.setPassword(passwordEncoder.encode(rawPassword));
        // set Role cho account
        account.setRole(Role.CUSTOMER);
        // accountRepository.save(account) trả về 1 account entity
        account = accountRepository.save(account);
        return AccountConverter.INSTANCE.fromAccountToAccountResponse(account);
    }

    @Override
    public AccountResponse update(Integer accountId, UpdateAccountRequest updateAccountRequest) {
        log.info("Start Validating update request");
        validateUpdateAccountRequest(updateAccountRequest);
        Optional<Account> oldAccount = accountRepository.findById(accountId);
        Account updatedAccount = new Account();
        if(oldAccount.isPresent()){
            updateAccountRequest.setId(oldAccount.get().getId());
            updatedAccount = AccountConverter.INSTANCE.fromUpdateAccountRequestToAccount(updateAccountRequest);
            String rawPassword = updatedAccount.getPassword();
            updatedAccount.setPassword(passwordEncoder.encode(rawPassword));
            updatedAccount = accountRepository.save(updatedAccount);
        } else {
                // handle logic sau
        }
        return AccountConverter.INSTANCE.fromAccountToAccountResponse(updatedAccount);
    }

    private void validateRegisterAccountRequest(RegisterAccountRequest RegisterAccountRequest) {
        if (accountRepository.existsByUsernameAndLogicalDeleteStatus(RegisterAccountRequest.getUsername(), (short) 0)) {
            throw Maid4UniException.badRequest("Username is existed");
        }
        if (accountRepository.existsByEmailAndLogicalDeleteStatus(RegisterAccountRequest.getEmail(), (short) 0)) {
            throw Maid4UniException.badRequest("Email is existed");
        }
        if (accountRepository.existsByPhoneNumberAndLogicalDeleteStatus(RegisterAccountRequest.getPhoneNumber(), (short) 0)) {
            throw Maid4UniException.badRequest("Phone is existed");
        }
    }

    private void validateUpdateAccountRequest(UpdateAccountRequest updateAccountRequest) {
        if (accountRepository.existsByUsernameAndLogicalDeleteStatus(updateAccountRequest.getUsername(), (short) 0)) {
            throw Maid4UniException.badRequest("Username is existed");
        }
        if (accountRepository.existsByEmailAndLogicalDeleteStatus(updateAccountRequest.getEmail(), (short) 0)) {
            throw Maid4UniException.badRequest("Email is existed");
        }
        if (accountRepository.existsByPhoneNumberAndLogicalDeleteStatus(updateAccountRequest.getPhoneNumber(), (short) 0)) {
            throw Maid4UniException.badRequest("Phone is existed");
        }
    }
}
