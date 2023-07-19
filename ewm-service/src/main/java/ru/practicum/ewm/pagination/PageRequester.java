package ru.practicum.ewm.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageRequester {
    public static Pageable of(Integer from, Integer size) {
        return of(from, size, Sort.unsorted());
    }

    public static Pageable of(Integer from, Integer size, Sort sort) {
        return (from != null && size != null) ? PageRequest.of(from / size, size, sort) : null;
    }
}
