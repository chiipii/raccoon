package com.example.demo.controller;

import java.time.LocalTime;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.Raccoon;
import com.example.demo.form.RaccoonForm;
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
	public String list(Model model) {
		List <Raccoon> sightings = raccoonService.findAll();
		model.addAttribute("sightings", sightings);
		return "sightings/list";
	}
	
	/**登録画面を表示する*/
	@GetMapping("/sightings/new")
	public String newSighting(Model model) {
		model.addAttribute("raccoonForm", new RaccoonForm());
		return "sightings/new";
	}
	
	/**登録する*/
	@PostMapping("/sightings")
	public String create(@Valid @ModelAttribute RaccoonForm raccoonForm, BindingResult bindingResult) {
		
		if(bindingResult.hasErrors()) {
			return "sightings/new";
		}
		
		Raccoon raccoon = new Raccoon();
		raccoon.setSightedDate(raccoonForm.getSightedDate());
		raccoon.setSightedTime(LocalTime.of(raccoonForm.getSightedHour(), 0));
		raccoon.setCount(raccoonForm.getCount());
		raccoon.setMemo(raccoonForm.getMemo());
		
		raccoonService.register(raccoon);
		return "redirect:/sightings";
	}

}
