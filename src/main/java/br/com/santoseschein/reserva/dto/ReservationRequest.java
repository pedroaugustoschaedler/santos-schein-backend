package br.com.santoseschein.reserva.dto;

import java.util.UUID;

public class ReservationRequest {
    private UUID userId;
    private UUID courtId;
    private String date; // YYYY-MM-DD
    private String startTime; // HH:MM
    private int durationMinutes = 30; // 30, 60, 90, 120, etc.

    public ReservationRequest() {}

    public ReservationRequest(UUID userId, UUID courtId, String date, String startTime, int durationMinutes) {
        this.userId = userId;
        this.courtId = courtId;
        this.date = date;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getCourtId() { return courtId; }
    public void setCourtId(UUID courtId) { this.courtId = courtId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
}
