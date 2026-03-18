package com.example.idempotentapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotNull
    private Integer phone;
}
