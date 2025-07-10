package com.delivery_tracker_app.app.dto.v1.common;

import java.util.List;

    public record PagedResponse<T>(
        List<T> data,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
}
