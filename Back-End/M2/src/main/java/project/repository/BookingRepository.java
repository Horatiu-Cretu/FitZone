package project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.entity.Booking;
import project.entity.TrainingSession;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByClientId(Long clientId);
    List<Booking> findByTrainingSessionId(Long trainingSessionId);
    Optional<Booking> findByClientIdAndTrainingSessionId(Long clientId, Long trainingSessionId);
    boolean existsByClientIdAndTrainingSessionId(Long clientId, Long trainingSessionId);
}