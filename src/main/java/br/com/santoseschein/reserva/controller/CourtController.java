package br.com.santoseschein.reserva.controller;

import br.com.santoseschein.reserva.model.Court;
import br.com.santoseschein.reserva.repository.CourtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courts")
public class CourtController {

    @Autowired
    private CourtRepository courtRepository;

    @GetMapping
    public List<Court> getAllCourts() {
        return courtRepository.findByIsActiveTrue();
    }

    @PostMapping
    public Court createCourt(@RequestBody Court court) {
        return courtRepository.save(court);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Court> updateCourt(@PathVariable UUID id, @RequestBody Court courtDetails) {
        return courtRepository.findById(id)
                .map(court -> {
                    court.setName(courtDetails.getName());
                    court.setSportType(courtDetails.getSportType());
                    court.setIsActive(courtDetails.getIsActive());
                    return ResponseEntity.ok(courtRepository.save(court));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
