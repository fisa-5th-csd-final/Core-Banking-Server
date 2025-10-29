package com.fisa.bank.loan.application.dto.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/*
    페이징 처리한 응답값
 */
@Getter
public class PagedResponse<T> {

    private List<T> data;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public PagedResponse(Page<T> page){
        this.data = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }

}
