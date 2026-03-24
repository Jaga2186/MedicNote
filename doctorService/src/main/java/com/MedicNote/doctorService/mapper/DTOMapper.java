package com.MedicNote.doctorService.mapper;

import com.MedicNote.doctorService.dto.DoctorRequestDTO;
import com.MedicNote.doctorService.dto.DoctorResponseDTO;
import com.MedicNote.doctorService.entity.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DTOMapper {
    /**
     * Maps DoctorRequestDTO to Doctor entity
     */
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "doctorId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Doctor requestDTOtoEntity(DoctorRequestDTO requestDTO);

    /**
     * Maps Doctor entity to DoctorResponseDTO
     */
    DoctorResponseDTO entityToResponseDTO(Doctor doctor);

}
