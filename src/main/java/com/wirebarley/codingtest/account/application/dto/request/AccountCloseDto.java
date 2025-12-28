package com.wirebarley.codingtest.account.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AccountCloseDto(
       @NotNull
       @Positive
       Long accountId
) {
}
