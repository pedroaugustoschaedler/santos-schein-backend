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

    /**
     * Cria reserva
     */
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequest request) {
        // Busca usuário e quadra
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        Optional<Court> courtOpt = courtRepository.findById(request.getCourtId());

        if (userOpt.isEmpty() || courtOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário ou Quadra não encontrados.");
        }

        // Configura horários da reserva
        LocalDate date = LocalDate.parse(request.getDate());
        LocalTime startTime = LocalTime.parse(request.getStartTime());
        LocalTime endTime = startTime.plusMinutes(request.getDurationMinutes());

        // Valida horário de término limite
        if (endTime.isAfter(LocalTime.of(21, 30))) {
            return ResponseEntity.badRequest().body("Reservas não podem ultrapassar 21:30.");
        }

        // Verifica conflitos de horários
        List<Reservation> overlapping = reservationRepository.findOverlappingReservations(
                request.getCourtId(), date, startTime, endTime);

        if (!overlapping.isEmpty()) {
            return ResponseEntity.badRequest().body("Este horário já possui conflito com outra reserva.");
        }

        // Salva nova reserva
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

    /**
     * Busca reservas do usuário
     */
    @GetMapping("/user/{userId}")
    public List<ReservationResponse> getUserReservations(@PathVariable UUID userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        List<ReservationResponse> responses = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Formata as reservas para resposta
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

    /**
     * Busca os slots de horários de uma quadra para a semana
     */
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

        // Inicializa mapa de reservas por dia da semana
        Map<String, List<Reservation>> reservationsByDayKey = new HashMap<>();
        for (String k : keys) {
            reservationsByDayKey.put(k, new ArrayList<>());
        }

        // Busca reservas para cada dia da semana
        for (int i = 0; i < 7; i++) {
            LocalDate currentDay = weekStart.plusDays(i);
            String dayKey = keys[i];
            List<Reservation> reservations = reservationRepository.findByCourtIdAndReservationDate(courtId, currentDay);
            reservationsByDayKey.get(dayKey).addAll(reservations);
        }

        // Monta os slots de horários disponíveis
        for (String dayKey : keys) {
            for (String timeStr : times) {
                LocalTime slotTime = LocalTime.parse(timeStr);
                Map<String, Object> slot = new HashMap<>();
                slot.put("dayKey", dayKey);
                slot.put("time", timeStr);
                
                boolean isWeekend = dayKey.equals("sab") || dayKey.equals("dom");
                
                // Verifica se o slot está reservado no horário atual
                boolean isReserved = false;
                for (Reservation r : reservationsByDayKey.get(dayKey)) {
                    boolean isSlotWithinReservation = (slotTime.equals(r.getStartTime()) || slotTime.isAfter(r.getStartTime())) 
                            && slotTime.isBefore(r.getEndTime());
                            
                    if (isSlotWithinReservation) {
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
