package com.spin.transaction_executor.domain.request;

import com.spin.transaction_executor.util.CardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Estructura requerida para consultar transacciones")
public class TransactionHistoryRequest {
    @Schema(description = "Identificador único de la cuenta del usuario", example = "acc-98765")
    private String accountId;
    @Schema(description = "Estatus de la transacción", example = "EXECUTED", allowableValues = {"EXECUTED", "APPROVED", "REJECTED"})
    private String status;
    @Schema(description = "Tipo de tarjeta utilizada", example = "DEBIT", allowableValues = {"DEBIT", "CREDIT"})
    private CardType type;
    @Schema(description = "Número de página para la paginación", example = "1")
    @Min(1)
    private Integer page;
    @Schema(description = "Cantidad de registros por página", example = "10", defaultValue = "10")
    @Min(1)
    private Integer limit = 10;
}
