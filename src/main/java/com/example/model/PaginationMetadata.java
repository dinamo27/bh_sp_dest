package com.example.model;

import lombok.Data;
import java.io.Serializable;

/**
 * Contains pagination metadata for product category queries
 */
@Data
public class PaginationMetadata implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Total number of products matching the query
     */
    private long totalCount;

    /**
     * Current page number (1-based)
     */
    private int page;

    /**
     * Number of items per page
     */
    private int pageSize;

    /**
     * Total number of pages available
     */
    private int totalPages;

    /**
     * Whether there is a next page available
     */
    private boolean hasNextPage;

    /**
     * Whether there is a previous page available
     */
    private boolean hasPreviousPage;

    /**
     * Whether subcategories were included in the query
     */
    private boolean includesSubcategories;
}