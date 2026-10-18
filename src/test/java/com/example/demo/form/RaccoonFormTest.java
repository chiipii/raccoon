package com.example.demo.form;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * RaccoonFormの入力チェックを検証する。
 */
public class RaccoonFormTest {
	
	private static Validator validator;
	
	@BeforeAll
	static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator(); 
	}
	
	/**検証に通る値をつめたフォームを作る。各テストで1項目だけ壊して使用	 */
	private RaccoonForm validForm() {
		RaccoonForm form = new RaccoonForm();
		form.setSightedDate(LocalDate.now().minusDays(1));
		form.setSightedHour(22);
		form.setCount(2);
		form.setMemo("駐車場で遊んでいた");
		return form;
	}
	
	/**違反したメッセージだけを取り出す*/
	private Set<String> messagesOf(RaccoonForm form){
		return validator.validate(form).stream()
				.map(ConstraintViolation::getMessage)
				.collect(Collectors.toSet());
	}
	
	@Test
	@DisplayName("正しい値ならエラーにならない")
	void 正常値() {
		assertThat(messagesOf(validForm())).isEmpty();
	}
	
	@Test
	@DisplayName("目撃日が未入力ならエラー")
	void 目撃日未入力() {
		RaccoonForm form = validForm();
		form.setSightedDate(null);
		assertThat(messagesOf(form)).containsExactly("目撃日を入力して下さい。");
	}
	
	@Test
	@DisplayName("時刻が未選択ならエラー")
	void 時刻未選択() {
		RaccoonForm form = validForm();
		form.setSightedHour(null);
		assertThat(messagesOf(form)).contains("時刻を設定して下さい。");
	}
	
	@Test
	@DisplayName("時刻が24,-1ならエラー,0,23なら通る")
	void 時刻() {
		RaccoonForm over = validForm();
		over.setSightedHour(24);
		assertThat(messagesOf(over)).contains("時刻は0〜23で選択して下さい。");
		
		RaccoonForm minus = validForm();
		minus.setSightedHour(-1);
		assertThat(messagesOf(minus)).contains("時刻は0〜23で選択して下さい。");
		
		RaccoonForm Zero = validForm();
		Zero.setSightedHour(0);
		assertThat(messagesOf(Zero)).isEmpty();
		
		RaccoonForm hourMax = validForm();
		hourMax.setSightedHour(23);
		assertThat(messagesOf(hourMax)).isEmpty();
	}
	
	@Test
	@DisplayName("回数が未入力ならエラー")
	void 回数未入力() {
		RaccoonForm form = validForm();
		form.setCount(null);
		assertThat(messagesOf(form)).contains("回数を入力して下さい。");
	}
	
	@Test
	@DisplayName("回数が0ならエラー、1なら通る")
	void 回数() {
		RaccoonForm countZero = validForm();
		countZero.setCount(0);
		assertThat(messagesOf(countZero)).contains("回数は1以上で入力して下さい。");
		
		RaccoonForm countOne = validForm();
		countOne.setCount(1);
		assertThat(messagesOf(countOne)).isEmpty();
	}
	
	@Test
	@DisplayName("めもが256文字ならエラー、255文字なら通る")
	void 文字数() {
		RaccoonForm over = validForm();
		over.setMemo("あ".repeat(256));
		assertThat(messagesOf(over)).contains("めもは255文字以内で入力して下さい。");
		
		RaccoonForm just = validForm();
		just.setMemo("あ".repeat(255));
		assertThat(messagesOf(just)).isEmpty();
	}
	
	@Test
	@DisplayName("明日の日付は未来なのでエラー")
	void 未来の日付() {
		RaccoonForm form = validForm();
		form.setSightedDate(LocalDate.now().plusDays(1));
		form.setSightedHour(0);
		assertThat(messagesOf(form)).contains("未来の日付は登録できません。");
	}
	
	@Test
	@DisplayName("今日の現在時刻は未来ではないので通る")
	void 現在時刻() {
		RaccoonForm form = validForm();
		form.setSightedDate(LocalDate.now());
		form.setSightedHour(LocalDateTime.now().getHour());
		assertThat(messagesOf(form)).isEmpty();
	}
}
