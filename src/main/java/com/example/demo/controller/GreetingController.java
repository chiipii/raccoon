package com.example.demo.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GreetingController {

	@GetMapping("/greeting")
	public String greetring(@RequestParam(defaultValue = "ゲスト")String name, Model model) {
		model.addAttribute("message", name + "さん、こんにちは");
		model.addAttribute("today", LocalDate.now());
		return "greeting";
	}
}
