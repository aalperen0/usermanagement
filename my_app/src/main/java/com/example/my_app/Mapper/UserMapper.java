package com.example.my_app.Mapper;

import com.example.my_app.DTO.UserDTO;
import com.example.my_app.DTO.UserEntityDTO;
import com.example.my_app.DTO.UpdatedUserDTO;
import com.example.my_app.Model.User;


/*
    Managing the translation between the database representation
    and application’s data model.
 */
public class UserMapper {


    // ENTITY TO DTO
    public static UserEntityDTO EntityToUserDTO(User user) {
        UserEntityDTO userEntityDTO = new UserEntityDTO();
        userEntityDTO.setId(user.getId());
        userEntityDTO.setName(user.getUserFirstName());
        userEntityDTO.setLastName(user.getUserLastName());
        userEntityDTO.setEmail(user.getEmail());
        userEntityDTO.setPassword(user.getPassword());
        return userEntityDTO;
    }

    public static UpdatedUserDTO EntityToUpdatedUserDTO(User user) {
        UpdatedUserDTO updatedUserDTO = new UpdatedUserDTO();
        updatedUserDTO.setFirstName(user.getUserFirstName());
        updatedUserDTO.setLastName(user.getUserLastName());
        return updatedUserDTO;
    }

    // DTO TO ENTITY
    public static User RegisterUserDTOToEntity(UserDTO userDTO) {
        User user = new User();
        user.setUserFirstName(userDTO.getFirstName());
        user.setUserLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail().toLowerCase());
        user.setPassword(userDTO.getPassword());
        return user;
    }
}
