package com.swp391.maid4uni.repository;

import com.swp391.maid4uni.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * The interface Feedback repository.
 */
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    /**
     * Find all by receiver id list.
     *
     * @param receiverId the receiver id
     * @return the list
     */
    List<Feedback> findAllByReceiverId(int receiverId);

    /**
     * Find all by sender id list.
     *
     * @param senderId the sender id
     * @return the list
     */
    List<Feedback> findAllBySenderId(int senderId);
}
