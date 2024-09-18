package com.example.my_app.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/*
Login credentials of user
 */

@Data
@Getter
@Setter
public class LoginRequestDTO {
    private String email;
    private String password;
}
