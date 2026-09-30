package com.example.demo.form;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

/**
 *   目撃記録の登録・編集画面の入力値を受け取るクラス。
 *   画面から来た未検証の値を保持する。
 */
@Data
public class RaccoonForm {
	
	/**目撃日*/
	@NotNull(message="目撃日を入力して下さい。")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate sightedDate;
	
	/**目撃時刻（時のみ）*/
	@NotNull(message="時刻を設定して下さい。")
	@Min(value = 0, message="時刻は0~23で選択して下さい。")
	@Max(value = 23, message="時刻は0~23で選択して下さい。")
	private Integer sightedHour;
	
	/**
	 * 目撃日時がみらいでないこと。
	 * 日時と時刻を組み合わせないと判定できないため、メソッドで検査する。
	 */
	@AssertTrue(message="未来の日時は登録できません。")
	public boolean isNotFuture() {
		//どちらかが未入力なら、それぞれの@NotNullに任せる。
		if(sightedDate == null || sightedHour == null) {
			return true;
		}
		//Min/Maxの範囲外ならLocalTime.ofが例外を投げるので先に逃す。
		if(sightedHour < 0 || sightedHour > 23) {
			return true;
		}
		LocalDateTime sighted = LocalDateTime.of(sightedDate,  LocalTime.of(sightedHour, 0));
		return !sighted.isAfter(LocalDateTime.now());
	}
	
	/**回数*/
	@NotNull(message="回数を入力して下さい。")
	@Min(value = 1, message="回数は1以上で入力して下さい。")
	private Integer count;
	
	/**めも*/
	@Size(max = 255, message="めもは255文字以内で入力して下さい。")
	private String memo;
	
	/**ID(更新時に使用。登録時はnull)*/
	private Integer id;
}
