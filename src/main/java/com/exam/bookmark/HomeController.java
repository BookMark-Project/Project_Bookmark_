package com.exam.bookmark;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {
	
	@RequestMapping(value = "/home.do")
	public String home() {
		return "home";
	}
	
	@RequestMapping(value = "/list.do")
	public String list() {
		return "board_list";
	}
	
	@RequestMapping(value = "/view.do")
	public String view() {
		return "board_view";
	}
	
	@RequestMapping(value = "/book_list.do")
	public String book_list() {
		return "book_list";
	}
	
	@RequestMapping(value = "/book_info.do")
	public String book_info() {
		return "book_info";
	}
	
	@RequestMapping(value = "/login.do")
	public String login() {
		return "login";
	}
	
	@RequestMapping(value = "/mypage.do")
	public String mypage() {
		return "mypage";
	}
	
	@RequestMapping(value = "/search.do")
	public String search() {
		return "search";
	}
	
	@RequestMapping(value = "/signup.do")
	public String signup() {
		return "signup";
	}
	
	@RequestMapping(value = "/admin.do")
	public String admin() {
		return "admin";
	}
	
}
