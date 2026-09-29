package com.example.demo.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

/**
 *   目撃記録の登録・編集画面の入力値を受け取るクラス。
 *   画面から来た未検証の値を保持する。
 */
@Data
public class RaccoonForm {
	
	/**目撃日*/
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate sightedDate;
	
	/**目撃時刻（時のみ）*/
	private Integer sightedHour;
	
	/**回数*/
	private Integer count;
	
	/**めも*/
	private String memo;
}
