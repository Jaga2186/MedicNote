package com.MedicNote.prescriptionService.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PrescriptionStatus {
    ACTIVE, COMPLETED, CANCELLED;

    @JsonCreator
    public static PrescriptionStatus from(String value) {
        if (value == null) return null;
        return PrescriptionStatus.valueOf(value.toUpperCase());
    }
}
