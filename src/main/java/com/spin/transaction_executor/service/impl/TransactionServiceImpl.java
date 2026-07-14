package com.spin.transaction_executor.service.impl;

import com.spin.transaction_executor.client.HttpProviderClient;
import com.spin.transaction_executor.domain.exception.impl.BusinessRuleException;
import com.spin.transaction_executor.domain.exception.impl.FatalException;
import com.spin.transaction_executor.domain.exception.impl.ProviderCommunicationException;
import com.spin.transaction_executor.domain.request.TransactionHistoryRequest;
import com.spin.transaction_executor.domain.request.TransactionRequest;
import com.spin.transaction_executor.domain.response.ProviderTransactionResponse;
import com.spin.transaction_executor.domain.response.TransactionResponse;
import com.spin.transaction_executor.domain.response.list.TransactionHistoryResponse;
import com.spin.transaction_executor.repository.TransactionRepository;
import com.spin.transaction_executor.service.TransactionService;
import com.spin.transaction_executor.util.CardType;
import com.spin.transaction_executor.util.Constants;
import com.spin.transaction_executor.util.Util;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private Constants constants;
    @Autowired
    private HttpProviderClient httpProviderClient;
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public TransactionResponse sendTransaction(TransactionRequest request) {
        TransactionResponse response = null;
        try {
            /*
             * Validación de reglad de negocio:
             * "Monto máximo por transacción: Las transacciones de tipo DEBIT no pueden
             * exceder $10,000.00 por operación. Las de tipo CREDIT no tienen límite"
             *
             * Se opta por el uso de enum ya que al agregar una consulta a base de datos a un catalogo , afectaría el performance de la aplicación
             * TODO: En caso de que el negocio tenga planeado agregar mas CardTypes , lo mejor es optar por un catalogo que se pueda consultar.
             */
            if (CardType.DEBIT.equals(request.getType()) && request.getAmount().compareTo(constants.DEBIT_MAX_AMOUNT) > 0) {
                response = Util.fillFailedTransactionResponse(request, "Debit transactions cannot exceed $10,000.00 per transaction");
                Util.saveTransaction(response, transactionRepository);
                throw new BusinessRuleException(response.getDescription(), response);
            }

            /*
             * Validación de reglad de negocio:
             * "Solo se aceptan transacciones en MXN. Cualquier otra
             * moneda debe ser rechazada"
             *
             * Se opta por el uso de un array configurable a nivel aplicativo ya que al agregar una consulta a base de datos a un catalogo , afectaría el performance de la aplicación
             * TODO: En caso de que el negocio tenga planeado agregar mas currencies con configuraciones adicionales o un amplio catalogo ,
             *  lo mejor es optar por un catalogo que se pueda consultar en base de datos.
             */
            if (!constants.ALLOWED_CURRENCIES.contains(request.getCurrency())) {
                response = Util.fillFailedTransactionResponse(request, "Unsupported currency type");
                Util.saveTransaction(response, transactionRepository);
                throw new BusinessRuleException(response.getDescription(), response);
            }

            // Mandamos a llamar al proveeder para procesar la transacción
            ProviderTransactionResponse providerTransactionResponse = httpProviderClient.executeTransaction(request);
            // Verificamos si es una respuesta valida
            if (providerTransactionResponse == null || StringUtils.isBlank(providerTransactionResponse.getStatus())) {
                response = Util.fillFailedTransactionResponse(request, "Error retrieving the response from the provider");
                Util.saveTransaction(response, transactionRepository);
                throw new ProviderCommunicationException(response.getDescription(), response);
            }

            response = Util.fillTransactionResponse(request, providerTransactionResponse);
            Util.saveTransaction(response, transactionRepository);
            return response;
        } catch (BusinessRuleException | ProviderCommunicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(Constants.ERROR, e.getMessage());
            response = Util.fillFailedTransactionResponse(request, "Error processing the transaction; we are working on a solution");
            Util.saveTransaction(response, transactionRepository);
            throw new FatalException(response.getDescription(), response);
        }
    }

    @Override
    public TransactionHistoryResponse getTransactions(TransactionHistoryRequest request) {
        // Validamos que la petición siempre sea valida
        if (!Util.validTransactionHistoryRequest(request)) {
            throw new BusinessRuleException("Invalid request");
        }

        // Verificamos con el count si tenemos transacciones disponibles que mostrar base a los filtros
        Long totalTrx = transactionRepository.getTotalTransactions(request);
        if (totalTrx == null || totalTrx <= 0) {
            log.info("Invalid or empty total transaction count: {}", totalTrx);
            log.info("No transactions found for the given criteria");
            return Util.fillTransactionHistoryResponse(List.of(), 0L, request.getPage(), request.getLimit());
        }
        log.info("Total transaction: {}", totalTrx);

        // Obtenemos el detalle y lista de transacciones
        List<TransactionResponse> transactions = transactionRepository.getTransactions(request);
        if (transactions == null || transactions.isEmpty()) {
            log.info("No transactional records returned from repository");
            return Util.fillTransactionHistoryResponse(List.of(), 0L, request.getPage(), request.getLimit());
        }

        return Util.fillTransactionHistoryResponse(transactions, totalTrx, request.getPage(), request.getLimit());
    }
}
