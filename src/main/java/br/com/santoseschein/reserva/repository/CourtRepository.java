package br.com.santoseschein.reserva.repository;

import br.com.santoseschein.reserva.model.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface CourtRepository extends JpaRepository<Court, UUID> {
    List<Court> findByIsActiveTrue();
}
