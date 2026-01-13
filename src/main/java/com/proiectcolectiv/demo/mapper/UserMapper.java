package com.proiectcolectiv.demo.mapper;

import com.proiectcolectiv.demo.dto.user.UserDTO;
import com.proiectcolectiv.demo.dto.user.UserResponseDTO;
import com.proiectcolectiv.demo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO userToUserDTO(User user);

    @Mapping(target = "id", ignore = true)
    User userDTOToUser(UserDTO userDTO);

    UserResponseDTO userToUserResponseDTO(User user);

}
