package project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.entity.Booking;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByClientId(Long clientId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.trainingSession WHERE b.clientId = :clientId")
    List<Booking> findByClientIdWithTrainingSession(@Param("clientId") Long clientId);

    List<Booking> findByTrainingSessionId(Long trainingSessionId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.trainingSession ts WHERE ts.id = :trainingSessionId")
    List<Booking> findByTrainingSessionIdWithTrainingSession(@Param("trainingSessionId") Long trainingSessionId);

    Optional<Booking> findByClientIdAndTrainingSessionId(Long clientId, Long trainingSessionId);

    boolean existsByClientIdAndTrainingSessionId(Long clientId, Long trainingSessionId);
}