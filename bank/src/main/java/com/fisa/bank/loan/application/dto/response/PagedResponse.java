package com.fisa.bank.loan.application.dto.response;

import lombok.Getter;

import java.util.List;

import org.springframework.data.domain.Page;

/*
   페이징 처리한 응답값
*/
@Getter
public class PagedResponse<T> {

  private List<T> content;
  private int pageNumber;
  private int pageSize;
  private long totalElements;
  private int totalPages;

  public PagedResponse(Page<T> page) {
    this.content = page.getContent();
    this.pageNumber = page.getNumber();
    this.pageSize = page.getSize();
    this.totalElements = page.getTotalElements();
    this.totalPages = page.getTotalPages();
  }
}
