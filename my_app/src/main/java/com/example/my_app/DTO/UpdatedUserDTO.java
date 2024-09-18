package com.example.my_app.DTO;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdatedUserDTO {
    private String firstName;
    private String lastName;
}
