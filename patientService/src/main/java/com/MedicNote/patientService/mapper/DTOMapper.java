package com.MedicNote.patientService.mapper;

import com.MedicNote.patientService.dto.PatientRequestDTO;
import com.MedicNote.patientService.dto.PatientResponseDTO;
import com.MedicNote.patientService.entity.BloodGroup;
import com.MedicNote.patientService.entity.Gender;
import com.MedicNote.patientService.entity.Patient;

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

    // Enum mappings
    @Mapping(target = "gender", expression = "java(Gender.valueOf(requestDTO.getGender()))")
    @Mapping(target = "medicalInfo.bloodGroup",
            expression = "java(requestDTO.getMedicalInfo() != null && requestDTO.getMedicalInfo().getBloodGroup() != null ? BloodGroup.valueOf(requestDTO.getMedicalInfo().getBloodGroup()) : null)")

    Patient requestDTOtoEntity(PatientRequestDTO requestDTO);


    /**
     * Maps Patient entity to PatientResponseDTO
     */
    @Mapping(target = "gender",
            expression = "java(patient.getGender() != null ? patient.getGender().name() : null)")
    @Mapping(target = "medicalInfo.bloodGroup",
            expression = "java(patient.getMedicalInfo() != null && patient.getMedicalInfo().getBloodGroup() != null ? patient.getMedicalInfo().getBloodGroup().name() : null)")

    PatientResponseDTO entityToResponseDTO(Patient patient);

    default Gender mapGender(String gender) {
        return gender != null ? Gender.valueOf(gender) : null;
    }

    default String mapGenderToString(Gender gender) {
        return gender != null ? gender.name() : null;
    }

    default BloodGroup mapBloodGroup(String bloodGroup) {
        return bloodGroup != null ? BloodGroup.valueOf(bloodGroup) : null;
    }

    default String mapBloodGroupToString(BloodGroup bloodGroup) {
        return bloodGroup != null ? bloodGroup.name() : null;
    }
}