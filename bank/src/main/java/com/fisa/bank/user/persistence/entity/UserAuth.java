package com.fisa.bank.user.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserAuth {


    @Id private String loginId;
    private String password;

    @OneToOne(mappedBy = "userAuth")
    private User user;

    public UserAuth(String loginId, String password){
        this.loginId = loginId;
        this.password = password;
    }

    public static UserAuth create(String loginId, String password){
        return new UserAuth(loginId, password);
    }

}
