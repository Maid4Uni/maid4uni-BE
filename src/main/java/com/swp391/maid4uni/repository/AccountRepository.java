package com.swp391.maid4uni.repository;

import com.swp391.maid4uni.entity.Account;
import com.swp391.maid4uni.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The interface Account repository.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    /**
     * Find by username optional.
     *
     * @param username the username
     * @return the optional
     */
    Optional<Account> findByUsername(String username);

    /**
     * Exists by username boolean.
     *
     * @param username the username
     * @return the boolean
     */
    boolean existsByUsername(String username);

    /**
     * Exists by email boolean.
     *
     * @param email the email
     * @return the boolean
     */
    boolean existsByEmail(String email);

    /**
     * Find by role list.
     *
     * @param role the role
     * @return the list
     */
    List<Account> findByRole(Role role);

    /**
     * Find by username and logical delete status optional.
     *
     * @param username            the username
     * @param logicalDeleteStatus the logical delete status
     * @return the optional
     */
    Optional<Account> findByUsernameAndLogicalDeleteStatus(String username, short logicalDeleteStatus);

    /**
     * Find all by logical delete status list.
     *
     * @param logicalDeleteStatus the logical delete status
     * @return the list
     */
    List<Account> findAllByLogicalDeleteStatus(short logicalDeleteStatus);

    /**
     * Find by role and logical delete status list.
     *
     * @param role                the role
     * @param logicalDeleteStatus the logical delete status
     * @return the list
     */
    List<Account> findByRoleAndLogicalDeleteStatus(Role role, short logicalDeleteStatus);

    /**
     * Exists by username and logical delete status boolean.
     *
     * @param username            the username
     * @param logicalDeleteStatus the logical delete status
     * @return the boolean
     */
    boolean existsByUsernameAndLogicalDeleteStatus(String username, short logicalDeleteStatus);

    /**
     * Exists by email and logical delete status boolean.
     *
     * @param email               the email
     * @param logicalDeleteStatus the logical delete status
     * @return the boolean
     */
    boolean existsByEmailAndLogicalDeleteStatus(String email, short logicalDeleteStatus);

    /**
     * Exists by phone number and logical delete status boolean.
     *
     * @param phoneNumber         the phone number
     * @param logicalDeleteStatus the logical delete status
     * @return the boolean
     */
    boolean existsByPhoneNumberAndLogicalDeleteStatus(String phoneNumber, short logicalDeleteStatus);
}
