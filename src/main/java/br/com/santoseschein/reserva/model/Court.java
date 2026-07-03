package br.com.santoseschein.reserva.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "courts")
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "sport_type", nullable = false, length = 50)
    private String sportType;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Court() {}

    public Court(String name, String sportType, Boolean isActive) {
        this.name = name;
        this.sportType = sportType;
        this.isActive = isActive;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
