package koo.securityv1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @ResponseBody
    @GetMapping("/")
    public String index() {
        return "인덱스 페이지 입니다.";
    }

    @ResponseBody
    @GetMapping("/user")
    public String user() {
        return "user";
    }

    @ResponseBody
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @ResponseBody
    @GetMapping("/manager")
    public String manager() {
        return "manager";
    }

    @GetMapping("/login")
    public String login() {
        return "loginForm";
    }

    @ResponseBody
    @GetMapping("/join")
    public String join() {
        return "join";
    }

    @ResponseBody
    @GetMapping("/joinProc")
    public String joinProc() {
        return "회원가입 완료됨!";
    }

}
