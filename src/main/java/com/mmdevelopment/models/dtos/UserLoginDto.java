package com.mmdevelopment.models.dtos;

import com.mmdevelopment.Validations;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Data
public class UserLoginDto implements  DTO{

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String userName;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min=3, max=1000, message = "La contraseña debe tener al menos 3 caracteres")
    private String password;

    public void validate() {
        Validations.getInstance().validate(this, true);
    }

}
