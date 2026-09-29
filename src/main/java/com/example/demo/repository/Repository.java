package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Raccoon;

/**
 * raccoonテーブルへのアクセスを担当する。
 * 実装クラスはSpring Data JPAが自動生成するため、宣言のみ記載。
 */
public interface Repository extends JpaRepository<Raccoon, Integer>{
	
	//目撃日時の新しい順に全件取得する。
	List<Raccoon> findAllByOrderBySightedDateDescSightedTimeDesc();

}
