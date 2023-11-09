package org.example.domain.member;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Entity
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;


    @Column(name = "member_name")
    private String name;

    @Column(name = "member_gender")
    private String gender;

    @Column(name = "member_birth")
    private String birth;

    @Column(name = "member_email")
    private String email;

    @Column(name = "member_phone_number")
    private String phoneNumber;

    @Column(name = "member_password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role")
    private Role role;

    @Builder
    public Member(String birth, String email, String name, String password,String gender, Role role,
                  String phoneNumber){
        this.birth = birth;
        this.email = email;
        this.name = name;
        this.password = password;
        this.gender = gender;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }
}
