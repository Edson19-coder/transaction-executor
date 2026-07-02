package com.spin.transaction_executor.domain.response.list;

import com.spin.transaction_executor.domain.dto.ErrorDetailDTO;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class ErrorDetailList {
    List<ErrorDetailDTO> errors;
}
