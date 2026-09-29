package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Raccoon;
import com.example.demo.repository.RaccoonRepository;

/**
 * 目撃記録に関する業務処理
 */
@Service
@Transactional(readOnly = true)
public class RaccoonService {
	private final RaccoonRepository raccoonRepository;
	
	//コンストラクタが1つだけなら@Autowiredは省略できる。
	public RaccoonService(RaccoonRepository raccoonRepository) {
		this.raccoonRepository = raccoonRepository;
	}
	
	/**目撃日時の新しい順に全件返す*/
	public List<Raccoon> findAll(){
		return raccoonRepository.findAllByOrderBySightedDateDescSightedTimeDesc();
	}
}
