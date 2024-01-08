package org.example.controller.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/itblog")
public class MemberController {

    @GetMapping("/login")
    public String loginPage(){
        return "auth/loginPage";
    }

    @GetMapping("/register")
    public String regPage(){
        return "auth/regPage";
    }
}
