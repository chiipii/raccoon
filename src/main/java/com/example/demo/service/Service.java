package com.example.demo.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Raccoon;
import com.example.demo.repository.Repository;

/**
 * 目撃記録に関する業務処理
 */
@Transactional(readOnly = true)
public class Service {
	private final Repository repository;
	
	//コンストラクタが1つだけなら@Autowiredは省略できる。
	public Service(Repository repository) {
		this.repository = repository;
	}
	
	/**目撃日時の新しい順に全件返す*/
	public List<Raccoon> findAll(){
		return repository.findAllByOrderBySightedDateDescSightedTimeDesc();
	}
}
