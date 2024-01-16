package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/itblog")
public class IndexController {

    @GetMapping("/main")
    public String index(){
        return "main.html";
    }

    @GetMapping("/fragment/myBlog")
    public String myBlog(){return "main/myBlog.html";}

    @GetMapping("/fragment/allPost")
    public String allPost(){return "main/allPost.html";}

}
