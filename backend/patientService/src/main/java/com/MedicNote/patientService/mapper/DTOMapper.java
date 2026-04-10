package com.MedicNote.patientService.mapper;

import com.MedicNote.patientService.dto.*;
import com.MedicNote.patientService.entity.*;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DTOMapper {

    /**
     * Maps PatientRequestDTO to Patient entity
     */
    @Mapping(target = "patientId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", constant = "true")

    Patient requestDTOtoEntity(PatientRequestDTO requestDTO);


    /**
     * Maps Patient entity to PatientResponseDTO
     */
    PatientResponseDTO entityToResponseDTO(Patient patient);
}