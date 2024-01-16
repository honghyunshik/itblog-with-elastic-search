package org.example.controller.post;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/itblog")
public class PostController {

    @GetMapping("/post/new")
    public String writeNewPost(){
        return "/post/writePostPage.html";
    }
}
