package project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.entity.UserSubscription;
import project.util.SubscriptionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    Optional<UserSubscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);

    List<UserSubscription> findByUserIdOrderByEndDateDesc(Long userId);

    @Query("SELECT us FROM UserSubscription us WHERE us.userId = :userId AND us.status = :status AND us.endDate > :now ORDER BY us.endDate DESC")
    Optional<UserSubscription> findActiveSubscriptionForUser(@Param("userId") Long userId, @Param("status") SubscriptionStatus status, @Param("now") LocalDateTime now);
}