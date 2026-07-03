package br.com.santoseschein.reserva.dto;

import java.util.UUID;

public class ReservationResponse {
    private UUID id;
    private String courtName;
    private String date;
    private String time;
    private String status;

    public ReservationResponse() {}

    public ReservationResponse(UUID id, String courtName, String date, String time, String status) {
        this.id = id;
        this.courtName = courtName;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
