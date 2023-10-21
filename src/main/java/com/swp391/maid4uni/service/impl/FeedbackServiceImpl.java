package com.swp391.maid4uni.service.impl;

import com.swp391.maid4uni.converter.FeedbackConverter;
import com.swp391.maid4uni.entity.Feedback;
import com.swp391.maid4uni.repository.FeedbackRepository;
import com.swp391.maid4uni.response.FeedbackResponse;
import com.swp391.maid4uni.service.FeedbackService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Feedback service.
 */
@Service
@Data
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@NoArgsConstructor(force = true)
@Builder
@AllArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Override
    public List<FeedbackResponse> getAllFeedbackList() {
        List<Feedback> feedbackList = feedbackRepository.findAll();
        List<FeedbackResponse> feedbackResponseList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(feedbackList)) {
            feedbackResponseList =
                    feedbackList.stream()
                            .map(FeedbackConverter.INSTANCE::fromFeedbackToFeedbackResponse)
                            .toList();
        }
        return feedbackResponseList;
    }
}