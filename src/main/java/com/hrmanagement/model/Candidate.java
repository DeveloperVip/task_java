package com.hrmanagement.model;

public class Candidate {
    private int id;
    private String name;
    private String positionApplied;
    private String status;

    public Candidate() {
    }

    public Candidate(int id, String name, String positionApplied, String status) {
        this.id = id;
        this.name = name;
        this.positionApplied = positionApplied;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPositionApplied() {
        return positionApplied;
    }

    public void setPositionApplied(String positionApplied) {
        this.positionApplied = positionApplied;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", positionApplied='" + positionApplied + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}