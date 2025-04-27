package com.easj.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CambioPasswordRequest {
    private String correo;

    private String nuevaPassword;

    private String confirmarPassword;

}
