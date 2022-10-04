package io.github.martnsbreno.quarkussocial.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
public class CreateUserRequest {

    @NotBlank(message = "name is required")
    private String name;
    @NotNull(message = "age is required")
    private Integer age;

}
