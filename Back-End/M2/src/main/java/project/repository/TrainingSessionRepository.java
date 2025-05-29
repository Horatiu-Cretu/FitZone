package project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.entity.TrainingSession;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {

    List<TrainingSession> findAllByStartTimeAfter(LocalDateTime dateTime);

    List<TrainingSession> findByTrainerId(Long trainerId);

    @Modifying
    @Query("UPDATE TrainingSession ts SET ts.currentParticipants = ts.currentParticipants + 1 WHERE ts.id = :sessionId AND ts.currentParticipants < ts.capacity")
    int incrementParticipants(@Param("sessionId") Long sessionId);

    @Modifying
    @Query("UPDATE TrainingSession ts SET ts.currentParticipants = ts.currentParticipants - 1 WHERE ts.id = :sessionId AND ts.currentParticipants > 0")
    int decrementParticipants(@Param("sessionId") Long sessionId);
}