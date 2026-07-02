CREATE OR REPLACE PACKAGE BODY PKG_TRANSACTION AS
    PROCEDURE ADD_TRANSACTION(pId IN VARCHAR2, pAccountId IN VARCHAR2, pType IN VARCHAR2, pAmount IN NUMBER, pCurrency IN VARCHAR2, pDescription IN VARCHAR2, pStatus IN VARCHAR2, pProviderTransactionId IN VARCHAR2, pBalanceAfter IN NUMBER, pCreatedAt IN VARCHAR, oResult OUT VARCHAR2) IS
    BEGIN
        INSERT INTO T_TRANSACTIONS (ID, ACCOUNT_ID, TYPE, AMOUNT, CURRENCY, DESCRIPTION, STATUS, PROVIDER_TRANSACTION_ID, BALANCE_AFTER, CREATED_AT)
        VALUES (pId, pAccountId, pType, pAmount, pCurrency, pDescription, pStatus, pProviderTransactionId, pBalanceAfter, TO_TIMESTAMP_TZ(pCreatedAt, 'YYYY-MM-DD"T"HH24:MI:SS"Z"'));
        oResult:='true';
    END ADD_TRANSACTION;

    PROCEDURE GET_TRANSACTION(pAccountId IN VARCHAR2, pStatus IN VARCHAR2, pType IN VARCHAR2, pPage IN NUMBER, pLimit IN NUMBER, rc OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN rc FOR
            SELECT ID, ACCOUNT_ID, TYPE, AMOUNT, CURRENCY, DESCRIPTION, STATUS, PROVIDER_TRANSACTION_ID, BALANCE_AFTER, TO_CHAR(CREATED_AT, 'YYYY-MM-DD"T"HH24:MI:SS"Z"')
            FROM T_TRANSACTIONS
            WHERE (pAccountId = 'null' OR ACCOUNT_ID = pAccountId)
            AND (pStatus = 'null' OR STATUS = pStatus)
            AND (pType = 'null' OR TYPE = pType)
            ORDER BY CREATED_AT DESC
            OFFSET ((pPage - 1) * pLimit) ROWS
            FETCH NEXT pLimit ROWS ONLY;
    END GET_TRANSACTION;

    PROCEDURE GET_TRANSACTION_COUNT(pAccountId IN VARCHAR2, pStatus IN VARCHAR2, pType IN VARCHAR2, oCount OUT NUMBER) IS
    BEGIN
        SELECT COUNT(*) INTO oCount
        FROM T_TRANSACTIONS
        WHERE (pAccountId = 'null' OR ACCOUNT_ID = pAccountId)
        AND (pStatus = 'null' OR STATUS = pStatus)
        AND (pType = 'null' OR TYPE = pType);
    END GET_TRANSACTION_COUNT;
END PKG_TRANSACTION;
