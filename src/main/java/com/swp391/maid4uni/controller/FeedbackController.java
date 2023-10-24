package com.swp391.maid4uni.controller;

import com.swp391.maid4uni.dto.AccountDto;
import com.swp391.maid4uni.enums.API_PARAMS;
import com.swp391.maid4uni.response.ResponseObject;
import com.swp391.maid4uni.service.FeedbackService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Feedback controller.
 */
@RestController
@RequestMapping(API_PARAMS.API_VERSION)
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    /**
     * Get all feedback list response entity.
     *
     * @return the response entity
     */
    @GetMapping(API_PARAMS.GET_ALL_FEEDBACK_LIST)
    public ResponseEntity<ResponseObject> getAllFeedbackList(){
        log.info("Start get all feedback list");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "GET ALL FEEDBACK LIST SUCCESSFULLY", feedbackService.getAllFeedbackList())
        );
    }

    /**
     * Get feedback by receiver id response entity.
     *
     * @param accountDto the account dto
     * @return the response entity
     */
    @GetMapping(API_PARAMS.GET_FEEDBACK_BY_RECEIVER_ID)
    public ResponseEntity<ResponseObject> getFeedbackByReceiverId(
            @RequestBody AccountDto accountDto){
        log.info("Start get feedback by receiver id");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK","GET FEEDBACK BY RECEIVER ID SUCCESSFULLY",
                        feedbackService.getFeedbackByReceiverId(accountDto))
        );
    }

    /**
     * Get feedback by sender id response entity.
     *
     * @param accountDto the account dto
     * @return the response entity
     */
    @GetMapping(API_PARAMS.GET_FEEDBACK_BY_SENDER_ID)
    public ResponseEntity<ResponseObject> getFeedbackBySenderId(
            @RequestBody AccountDto accountDto){
        log.info("Start get feedback by sender id");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK","GET FEEDBACK BY SENDER ID SUCCESSFULLY",
                        feedbackService.getFeedbackBySenderId(accountDto))
        );
    }
}
