package com.example.sever.dto.Pos;


import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResult<T> {

    private List<T> content;
    private int page;           // page hiện tại (0-based)
    private int size;           // size mỗi trang
    private long totalElements; // tổng bản ghi
    private int totalPages;     // tổng số trang
}