package com.MedicNote.patientService.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BloodGroup {
    A_POS, A_NEG, B_POS, B_NEG, O_POS, O_NEG, AB_POS, AB_NEG;

    @JsonCreator
    public static BloodGroup from(String value) {
        if(value == null) return null;
        return BloodGroup.valueOf(value.toUpperCase());
    }
}