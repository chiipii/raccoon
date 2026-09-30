package com.example.demo.controller;

import java.time.LocalTime;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
		
		model.addAttribute("totalCount", raccoonService.totalCount());
		model.addAttribute("recordCount", raccoonService.recordCount());
		model.addAttribute("countByHour", raccoonService.countByHour());
		
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
	
	/**編集画面を表示する*/
	@GetMapping("/sightings/{id}")
	public String edit(@PathVariable Integer id, Model model) {
		Raccoon raccoon = raccoonService.findById(id);
		
		RaccoonForm raccoonForm = new RaccoonForm();
		raccoonForm.setId(raccoon.getId());
		raccoonForm.setSightedDate(raccoon.getSightedDate());
		raccoonForm.setSightedHour(raccoon.getSightedTime().getHour());
		raccoonForm.setCount(raccoon.getCount());
		raccoonForm.setMemo(raccoon.getMemo());
		
		model.addAttribute("raccoonForm", raccoonForm);
		return "sightings/edit";
	}
	
	/**更新する*/
	@PostMapping("sightings/{id}")
	public String update(@PathVariable Integer id, @Valid @ModelAttribute RaccoonForm raccoonForm,
			             BindingResult bindingResult) {
		
		if(bindingResult.hasErrors()) {
			return "sightings/edit";
		}
		
		Raccoon raccoon = new Raccoon();
		raccoon.setId(id);
		raccoon.setSightedDate(raccoonForm.getSightedDate());
		raccoon.setSightedTime(LocalTime.of(raccoonForm.getSightedHour(), 0));
		raccoon.setCount(raccoonForm.getCount());
		raccoon.setMemo(raccoonForm.getMemo());
		
		raccoonService.update(raccoon);
		return "redirect:/sightings";
	}
	
	/**削除確認画面を表示する*/
	@GetMapping("/sightings/{id}/delete")
	public String confirmDelete(@PathVariable Integer id, Model model) {
		model.addAttribute("sighting", raccoonService.findById(id));
		return "sightings/delete";
	}
	
	/**削除する*/
	@PostMapping("/sightings/{id}/delete")
	public String delete(@PathVariable Integer id) {
		raccoonService.delete(id);
		return "redirect:/sightings";
	}
}
