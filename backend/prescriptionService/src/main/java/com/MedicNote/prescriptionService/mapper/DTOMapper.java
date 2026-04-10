package com.MedicNote.prescriptionService.mapper;

import com.MedicNote.prescriptionService.dto.MedicationDTO;
import com.MedicNote.prescriptionService.dto.PrescriptionRequestDTO;
import com.MedicNote.prescriptionService.dto.PrescriptionResponseDTO;
import com.MedicNote.prescriptionService.entity.Medication;
import com.MedicNote.prescriptionService.entity.Prescription;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DTOMapper {

    @Mapping(target = "prescriptionId", ignore = true)
    @Mapping(target = "doctorName", ignore = true)
    @Mapping(target = "patientName", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "medications", ignore = true)
    Prescription requestDTOtoEntity(PrescriptionRequestDTO requestDTO);

    @Mapping(target = "medications", source = "medications")
    PrescriptionResponseDTO entityToResponseDTO(Prescription prescription);

    @Mapping(target = "medicationId", ignore = true)
    @Mapping(target = "prescription", ignore = true)
    Medication medicationDTOtoEntity(MedicationDTO medicationDTO);

    MedicationDTO medicationEntityToDTO(Medication medication);

    List<MedicationDTO> medicationEntitiesToDTOs(List<Medication> medications);
}
