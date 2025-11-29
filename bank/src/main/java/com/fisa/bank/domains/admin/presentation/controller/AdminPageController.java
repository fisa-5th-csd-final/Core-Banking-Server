package com.fisa.bank.domains.admin.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

  @GetMapping("/admin")
  public String adminIndex() {
    return "admin/index";
  }

  @GetMapping("/admin/accounts")
  public String adminAccounts() {
    return "admin/accounts";
  }

  @GetMapping("/admin/loans")
  public String adminLoans() {
    return "admin/loans";
  }

  @GetMapping("/admin/products")
  public String adminProducts() {
    return "admin/products";
  }

  @GetMapping("/admin/login")
  public String adminLogin() {
    return "admin/login";
  }
}
