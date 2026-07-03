package br.com.santoseschein.reserva.repository;

import br.com.santoseschein.reserva.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByCourtIdAndReservationDate(UUID courtId, LocalDate reservationDate);
    List<Reservation> findByUserId(UUID userId);

    @Query("SELECT r FROM Reservation r WHERE r.court.id = :courtId AND r.reservationDate = :date AND " +
           "((r.startTime < :endTime AND r.endTime > :startTime))")
    List<Reservation> findOverlappingReservations(
            @Param("courtId") UUID courtId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
}
