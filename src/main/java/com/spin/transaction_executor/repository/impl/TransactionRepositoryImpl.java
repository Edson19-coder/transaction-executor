package com.spin.transaction_executor.repository.impl;

import com.spin.transaction_executor.domain.request.TransactionHistoryRequest;
import com.spin.transaction_executor.domain.response.TransactionResponse;
import com.spin.transaction_executor.repository.TransactionRepository;
import com.spin.transaction_executor.util.CardType;
import com.spin.transaction_executor.util.Constants;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class TransactionRepositoryImpl implements TransactionRepository {
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Override
    public boolean saveTransaction(TransactionResponse response) {
        log.info("Executing procedure PKG_TRANSACTION.ADD_TRANSACTION()");
        EntityManager em = entityManagerFactory.createEntityManager();
        try{
            if (em.isOpen()) {
                StoredProcedureQuery query = em.createStoredProcedureQuery("PKG_TRANSACTION.ADD_TRANSACTION")
                        .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(4, BigDecimal.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(5, String.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(6, String.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(7, String.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(8, String.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(9, BigDecimal.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(10, String.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(11, String.class, ParameterMode.OUT)
                        .setParameter(1, response.getId())
                        .setParameter(2, response.getAccountId())
                        .setParameter(3, response.getType().toString())
                        .setParameter(4, response.getAmount())
                        .setParameter(5, response.getCurrency())
                        .setParameter(6, response.getDescription())
                        .setParameter(7, response.getStatus())
                        .setParameter(8, response.getProviderTransactionId())
                        .setParameter(9, response.getBalanceAfter())
                        .setParameter(10, response.getCreatedAt());
                query.execute();
                return Objects.nonNull(query.getOutputParameterValue(11)) && Boolean.parseBoolean(query.getOutputParameterValue(11).toString());
            }
        } catch(Exception e) {
            log.error(Constants.ERROR, e.getMessage());
        } finally {
            em.close();
        }
        return false;
    }

    @Override
    public List<TransactionResponse> getTransactions(TransactionHistoryRequest request) {
        log.info("Executing procedure PKG_TRANSACTION.GET_TRANSACTION()");
        EntityManager em = entityManagerFactory.createEntityManager();
        List<TransactionResponse> result = new ArrayList<>();
        try{
            if (em.isOpen()) {
                StoredProcedureQuery query = em.createStoredProcedureQuery("PKG_TRANSACTION.GET_TRANSACTION")
                        .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(4, Integer.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(5, Integer.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(6, Class.class, ParameterMode.REF_CURSOR)
                        .setParameter(1, StringUtils.isNotEmpty(request.getAccountId()) ? request.getAccountId() : "null")
                        .setParameter(2, StringUtils.isNotEmpty(request.getStatus()) ? request.getStatus() : "null")
                        .setParameter(3, request.getType() != null && StringUtils.isNotEmpty(request.getType().toString()) ? request.getType().toString() : "null")
                        .setParameter(4, request.getPage())
                        .setParameter(5, request.getLimit());
                query.execute();
                List<Object[]> rptTrx = query.getResultList();
                if (rptTrx != null) {
                    for (Object o : rptTrx) {
                        Object[] obj = (Object[]) o;
                        TransactionResponse trx = TransactionResponse.builder()
                                .id(String.valueOf(obj[0]))
                                .accountId(String.valueOf(obj[1]))
                                .type(CardType.valueOf(String.valueOf(obj[2])))
                                .amount(obj[3] != null ? (BigDecimal) obj[3] : null)
                                .currency(String.valueOf(obj[4]))
                                .description(String.valueOf(obj[5]))
                                .status(String.valueOf(obj[6]))
                                .providerTransactionId(String.valueOf(obj[7]))
                                .balanceAfter(obj[8] != null ? (BigDecimal) obj[8] : null)
                                .createdAt(String.valueOf(obj[9]))
                                .build();
                        result.add(trx);
                    }

                    return result;
                }
            }
        } catch(Exception e) {
            log.error(Constants.ERROR, e.getMessage());
        } finally {
            em.close();
        }
        return null;
    }

    @Override
    public Long getTotalTransactions(TransactionHistoryRequest request) {
        log.info("Executing procedure PKG_TRANSACTION.GET_TRANSACTION_COUNT()");
        EntityManager em = entityManagerFactory.createEntityManager();
        try{
            if (em.isOpen()) {
                StoredProcedureQuery query = em.createStoredProcedureQuery("PKG_TRANSACTION.GET_TRANSACTION_COUNT")
                        .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                        .registerStoredProcedureParameter(4, Long.class, ParameterMode.OUT)
                        .setParameter(1, StringUtils.isNotEmpty(request.getAccountId()) ? request.getAccountId() : "null")
                        .setParameter(2, StringUtils.isNotEmpty(request.getStatus()) ? request.getStatus() : "null")
                        .setParameter(3, request.getType() != null && StringUtils.isNotEmpty(request.getType().toString()) ? request.getType().toString() : "null");
                query.execute();
                return Objects.nonNull(query.getOutputParameterValue(4)) ? Long.parseLong(query.getOutputParameterValue(4).toString()) : null;
            }
        } catch(Exception e) {
            log.error(Constants.ERROR, e.getMessage());
        } finally {
            em.close();
        }
        return null;
    }
}
