package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * アライグマ目撃記録1件を表すクラス。raccoonテーブルに対応する。
 */

@Entity
@Data
@Table(name = "raccoon")
public class Raccoon {
	public Raccoon() {}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private LocalDate sightedDate;
	
	private LocalTime sightedTime;
	
	private Integer count;
	
	private String memo;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
}
