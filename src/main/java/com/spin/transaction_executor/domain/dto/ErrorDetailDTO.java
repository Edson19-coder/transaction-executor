package com.spin.transaction_executor.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorDetailDTO {
    private String field;
    private String message;
}
