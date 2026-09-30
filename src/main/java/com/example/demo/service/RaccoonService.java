package com.example.demo.service;

import java.time.LocalDateTime;
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
	
	/**目撃記録を登録する*/
	@Transactional
	public void register(Raccoon raccoon) {
		LocalDateTime now = LocalDateTime.now();
		raccoon.setCreatedAt(now);
		raccoon.setUpdatedAt(now);
		raccoonRepository.save(raccoon);
	}
	
	/**1件取得する。見つからなければ例外*/
	public Raccoon findById(Integer id) {
		return raccoonRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("記録が見つかりません：id=" + id));
	}
	
	/**目撃記録を更新する*/
	@Transactional
	public void update(Raccoon input) {
		//DBから現在の値を取り直す
		Raccoon raccoon = findById(input.getId());
		
		//画面から来た項目だけを上書きする
		raccoon.setSightedDate(input.getSightedDate());
		raccoon.setSightedTime(input.getSightedTime());
		raccoon.setCount(input.getCount());
		raccoon.setMemo(input.getMemo());
		raccoon.setUpdatedAt(LocalDateTime.now());
		//createdAtはあえて触らない
		
		raccoonRepository.save(raccoon);
	}
}
