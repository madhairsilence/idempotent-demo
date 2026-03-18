package com.example.idempotentapi.dto;

import com.example.idempotentapi.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdemUser {

    @NotBlank
    private String idemKey;

    @Valid
    @NotNull
    private User userData;
}
