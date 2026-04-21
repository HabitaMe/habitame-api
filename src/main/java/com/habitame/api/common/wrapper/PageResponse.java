package com.habitame.api.common.wrapper;

import java.util.List;

// TODO: Pasar a MAPPER

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}