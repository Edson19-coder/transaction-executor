package com.spin.transaction_executor.domain.request;

import com.spin.transaction_executor.util.CardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Estructura requerida para procesar una transacción con el proveedor")
public class TransactionProviderRequest {
    @Schema(description = "Identificador único de la cuenta del usuario", example = "acc-98765")
    @NotBlank(message = "Must not be empty")
    private String accountId;
    @Schema(description = "Tipo de tarjeta utilizada", example = "DEBIT", allowableValues = {"DEBIT", "CREDIT"})
    @NotNull(message = "Must not be empty")
    private CardType type;
    @Schema(description = "Monto de la operación. Si es DEBIT no puede superar los 10,000.00", example = "1550.50")
    @DecimalMin(value = "1", inclusive = false, message = "Transaction rejected. Amount must be greater than $1.00")
    private BigDecimal amount;
    @Schema(description = "Código de la moneda en formato ISO. Solo se acepta MXN", example = "MXN")
    @NotBlank(message = "Must not be empty")
    private String currency;
}
