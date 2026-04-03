package com.finance.dashboard.dto.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Wraps Spring's Page<T> into a clean JSON response.
 * All paginated endpoints return this shape.
 */
@Getter
public class PageResponse<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public PageResponse(Page<T> pageData) {
        this.content       = pageData.getContent();
        this.page          = pageData.getNumber();
        this.size          = pageData.getSize();
        this.totalElements = pageData.getTotalElements();
        this.totalPages    = pageData.getTotalPages();
    }
}
