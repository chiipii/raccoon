package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.Raccoon;
import com.example.demo.service.RaccoonService;

/**
 * 目撃情報の画面遷移を担当する。
 */
@Controller
public class SightingController {
	
	private final RaccoonService raccoonService;
	
	public SightingController(RaccoonService raccoonService) {
		this.raccoonService = raccoonService;
	}
	
	/**一覧画面を表示する*/
	@GetMapping("/sightings")
	public String List(Model model) {
		List <Raccoon> sightings = raccoonService.findAll();
		model.addAttribute("sightings", sightings);
		return "sightings/list";
	}

}
