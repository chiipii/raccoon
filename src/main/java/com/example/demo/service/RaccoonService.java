package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
	
	/**目撃記録を削除する*/
	@Transactional
	public void delete(Integer id) {
		Raccoon raccoon = findById(id);
		raccoonRepository.delete(raccoon);
	}
	
	/**全期間の目撃回数の合計*/
	public int totalCount() {
		int total = 0;
		for(Raccoon raccoon : raccoonRepository.findAll()) {
			total += raccoon.getCount();
		}
		return total;
	}
	
	/**記録の件数*/
	public long recordCount() {
		return raccoonRepository.count();
	}
	
	/**時間帯ごとの目撃回数（記録のある時間のみ、時刻の昇順）*/
	public Map<Integer, Integer> countByHour(){
		Map<Integer, Integer> result = new TreeMap<>();
		for(Raccoon raccoon : raccoonRepository.findAll()) {
			int hour = raccoon.getSightedTime().getHour();
			result.put(hour, result.getOrDefault(hour, 0) + raccoon.getCount());
		}
		return result;
	}
}
