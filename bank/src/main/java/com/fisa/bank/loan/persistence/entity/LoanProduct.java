package com.fisa.bank.loan.persistence.entity;

import com.fisa.bank.loan.persistence.enums.InterestType;
import com.fisa.bank.loan.persistence.enums.LoanType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
public class LoanProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long loanProductId;

    // LoanProduct 1 : N LoanLedger
    @OneToMany(mappedBy = "loanProduct")
    private List<LoanLedger> loanLedgerList = new ArrayList<>();

    // LoanProduct 1 : N InterestRate
    @OneToMany(mappedBy = "loanProduct")
    private List<InterestRate> interestRateList = new ArrayList<>();

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private LoanType type;

}
