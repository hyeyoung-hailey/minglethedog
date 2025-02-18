package com.example.minglethedog.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ListEntityBody<T> {
    private boolean hasNext;
    private Long lastCursor;
    private List<T> items;

    public ListEntityBody(boolean hasNext, Long lastCursor, List<T> items) {
        this.hasNext = hasNext;
        this.lastCursor = lastCursor;
        this.items = items;
    }
}