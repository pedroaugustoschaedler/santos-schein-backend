package br.com.santoseschein.reserva.controller;

import br.com.santoseschein.reserva.dto.ReservationRequest;
import br.com.santoseschein.reserva.dto.ReservationResponse;
import br.com.santoseschein.reserva.model.Court;
import br.com.santoseschein.reserva.model.Reservation;
import br.com.santoseschein.reserva.model.User;
import br.com.santoseschein.reserva.repository.CourtRepository;
import br.com.santoseschein.reserva.repository.ReservationRepository;
import br.com.santoseschein.reserva.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourtRepository courtRepository;

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequest request) {
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        Optional<Court> courtOpt = courtRepository.findById(request.getCourtId());

        if (userOpt.isEmpty() || courtOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário ou Quadra não encontrados.");
        }

        LocalDate date = LocalDate.parse(request.getDate());
        LocalTime startTime = LocalTime.parse(request.getStartTime());
        LocalTime endTime = startTime.plusMinutes(request.getDurationMinutes());

        // Check if booking goes beyond 21:00 or end of day limit if needed (e.g. 22:00)
        // User requested: "de 30 minutos como inicio ate as 21 horas"
        // Let's ensure the booking end time doesn't exceed 21:30 (since last slot starts at 21:00)
        if (endTime.isAfter(LocalTime.of(21, 30))) {
            return ResponseEntity.badRequest().body("Reservas não podem ultrapassar 21:30.");
        }

        List<Reservation> overlapping = reservationRepository.findOverlappingReservations(
                request.getCourtId(), date, startTime, endTime);

        if (!overlapping.isEmpty()) {
            return ResponseEntity.badRequest().body("Este horário já possui conflito com outra reserva.");
        }

        Reservation reservation = new Reservation(
                userOpt.get(),
                courtOpt.get(),
                date,
                startTime,
                endTime,
                "CONFIRMED"
        );

        reservationRepository.save(reservation);
        return ResponseEntity.ok("Reserva realizada com sucesso!");
    }

    @GetMapping("/user/{userId}")
    public List<ReservationResponse> getUserReservations(@PathVariable UUID userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        List<ReservationResponse> responses = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Reservation r : reservations) {
            responses.add(new ReservationResponse(
                    r.getId(),
                    r.getCourt().getName(),
                    r.getReservationDate().format(dateFormatter),
                    r.getStartTime().toString() + " - " + r.getEndTime().toString(),
                    r.getStatus()
            ));
        }
        return responses;
    }

    @GetMapping("/slots")
    public List<Map<String, Object>> getSlots(
            @RequestParam UUID courtId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {

        String[] times = {
            "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00",
            "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00"
        };
        String[] keys = {"sex", "sab", "dom", "seg", "ter", "qua", "qui"};

        List<Map<String, Object>> slots = new ArrayList<>();

        // Fetch reservations for the whole week
        Map<String, List<Reservation>> reservationsByDayKey = new HashMap<>();
        for (String k : keys) {
            reservationsByDayKey.put(k, new ArrayList<>());
        }

        for (int i = 0; i < 7; i++) {
            LocalDate currentDay = weekStart.plusDays(i);
            String dayKey = keys[i];
            List<Reservation> reservations = reservationRepository.findByCourtIdAndReservationDate(courtId, currentDay);
            reservationsByDayKey.get(dayKey).addAll(reservations);
        }

        for (String dayKey : keys) {
            for (String timeStr : times) {
                LocalTime slotTime = LocalTime.parse(timeStr);
                Map<String, Object> slot = new HashMap<>();
                slot.put("dayKey", dayKey);
                slot.put("time", timeStr);
                
                boolean isWeekend = dayKey.equals("sab") || dayKey.equals("dom");
                
                // A slot is reserved if slotTime is between r.startTime (inclusive) and r.endTime (exclusive)
                boolean isReserved = false;
                for (Reservation r : reservationsByDayKey.get(dayKey)) {
                    if ((slotTime.equals(r.getStartTime()) || slotTime.isAfter(r.getStartTime())) 
                            && slotTime.isBefore(r.getEndTime())) {
                        isReserved = true;
                        break;
                    }
                }

                slot.put("isAvailable", !isWeekend && !isReserved);
                slots.add(slot);
            }
        }

        return slots;
    }
}
