package com.spin.transaction_executor.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Schema(description = "Estructura requerida para procesar una transacción")
public class TransactionRequest extends TransactionProviderRequest {
    @Schema(description = "Descripción del proceso", example = "Transferencia recibida")
    @NotBlank(message = "Must not be empty")
    private String description;
}
