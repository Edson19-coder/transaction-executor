package com.spin.transaction_executor.controller;

import com.spin.transaction_executor.domain.request.TransactionHistoryRequest;
import com.spin.transaction_executor.domain.request.TransactionRequest;
import com.spin.transaction_executor.domain.response.TransactionResponse;
import com.spin.transaction_executor.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/transactions")
@Tag(name = "Transacciones", description = "Endpoints para el procesamiento y consulta de transacciones")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Enviar una nueva transacción", description = "Valida las reglas de negocio y procesa el cobro con el proveedor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción procesada con éxito", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Reglas de negocio violadas o datos de petición inválidos", content = @Content),
            @ApiResponse(responseCode = "503", description = "Error de comunicación con el proveedor externo", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<?> sendTransaction(@RequestBody @Valid TransactionRequest request) {
        return this.transactionService.sendTransaction(request);
    }

    @GetMapping
    @Operation(summary = "Consultar historial de transacciones", description = "Obtiene la lista de transacciones filtradas de manera paginada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial obtenido o lista vacía", content = @Content),
            @ApiResponse(responseCode = "400", description = "Parámetros de paginación o filtros inválidos", content = @Content)
    })
    public ResponseEntity<?> getTransactios(@ModelAttribute @Valid TransactionHistoryRequest request) {
        return this.transactionService.getTransactions(request);
    }
}
