package com.MedicNote.doctorService.mapper;

import com.MedicNote.doctorService.dto.DoctorRequestDTO;
import com.MedicNote.doctorService.dto.DoctorResponseDTO;
import com.MedicNote.doctorService.entity.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    /**
     * Maps DoctorRequestDTO to Doctor entity
     */
    @Mapping(target = "doctorExperience", source = "experienceYears")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "doctorId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Doctor requestDTOtoEntity(DoctorRequestDTO requestDTO);

    /**
     * Maps Doctor entity to DoctorResponseDTO
     */
    @Mapping(target = "experienceYears", source = "doctorExperience")
    DoctorResponseDTO entityToResponseDTO(Doctor doctor);

}
