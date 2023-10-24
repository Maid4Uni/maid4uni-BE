package com.swp391.maid4uni.controller;

import com.swp391.maid4uni.enums.API_PARAMS;
import com.swp391.maid4uni.request.LoginByUsernameRequest;
import com.swp391.maid4uni.request.RegisterAccountRequest;
import com.swp391.maid4uni.request.UpdateAccountRequest;
import com.swp391.maid4uni.response.AccountResponse;
import com.swp391.maid4uni.response.ResponseObject;
import com.swp391.maid4uni.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The type Account controller.
 */
@RestController
@RequestMapping(API_PARAMS.API_VERSION)
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Slf4j
public class AccountController {
    @Autowired
    private AccountService accountService;

    /**
     * Login using username response entity.
     *
     * @param request the request
     * @return the response entity
     */
    @PostMapping(API_PARAMS.LOGIN_BY_USERNAME)
    public ResponseEntity<ResponseObject> loginByUsername(
            @RequestBody LoginByUsernameRequest request) {
        log.info("Start login by username");
        return ResponseEntity.ok().body(
                new ResponseObject("OK", "LOGIN SUCCESSFULLY", accountService.loginByUsername(request))
        );
    }

    /**
     * Get account list response entity.
     *
     * @return the response entity
     */
    @GetMapping(API_PARAMS.GET_ACCOUNT_LIST)
    public ResponseEntity<ResponseObject> getAccountList() {
        log.info("Start get account list");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "GET ALL ACCOUNT SUCCESSFULLY", accountService.getAccountList())
        );
    }

    /**
     * Get manager list response entity.
     *
     * @return the response entity
     */
    @GetMapping(API_PARAMS.GET_MANAGER_LIST)
    public ResponseEntity<ResponseObject> getManagerList() {
        log.info("Start get manager list");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "GET ALL MANAGER SUCCESSFULLY", accountService.getManagerList())
        );
    }

    /**
     * Get staff list response entity.
     *
     * @return the response entity
     */
    @GetMapping(API_PARAMS.GET_STAFF_LIST)
    public ResponseEntity<ResponseObject> getStaffList() {
        log.info("Start get staff list");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "GET ALL STAFF SUCCESSFULLY", accountService.getStaffList())
        );

    }

    /**
     * Gets customer list.
     *
     * @return the customer list
     */
    @GetMapping(API_PARAMS.GET_CUSTOMER_LIST)
    public ResponseEntity<ResponseObject> getCustomerList() {
        log.info("Start get customer list");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "GET ALL CUSTOMER SUCCESSFULLY", accountService.getCustomerList())
        );
    }

    /**
     * Register response entity.
     *
     * @param registerAccountRequest the register account request
     * @return the response entity
     */
    @PostMapping(API_PARAMS.REGISTER)
    public ResponseEntity<ResponseObject> register(
            @RequestBody RegisterAccountRequest registerAccountRequest) {
        log.info("Start register");
        return ResponseEntity.ok().body(
                new ResponseObject("OK", "REGISTER SUCCESSFULLY", accountService.register(registerAccountRequest))
        );
    }

    /**
     * Update response entity.
     *
     * @param updateAccountRequest the update account request
     * @return the response entity
     */
    @PutMapping(API_PARAMS.UPDATE_ACCOUNT_INFO)
    public ResponseEntity<ResponseObject> update(@RequestBody UpdateAccountRequest updateAccountRequest) {
        log.info("Start update");
        Integer accountId = updateAccountRequest.getId();
        return ResponseEntity.ok().body(
                new ResponseObject("OK", "UPDATE SUCCESSFULLY", accountService.update(accountId, updateAccountRequest))
        );
    }

}
