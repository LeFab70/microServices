package org.lefab.order.order.exceptions.dtos;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationError(
        LocalDateTime timestamp,
        int status,
        String error,
        Map<String, String> errors,
        String path
) {
}
