package com.example.demo.controller;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.config.SecurityConfig;
import com.example.demo.entity.Raccoon;
import com.example.demo.service.RaccoonService;

/**SightingControllerの画面遷移を検証する*/
@WebMvcTest(SightingController.class) //Controller層に絞る
@Import(SecurityConfig.class) //セキュリティのルールだけ持ち込む
public class SightingControllerTest {
	
	@Autowired
	private MockMvc mockMvc; //ブラウザの代わりにHTTPリクエストを送る
	
	@MockitoBean
	private RaccoonService raccoonService; //テスト対象のクラスに偽物たちを自動で組み込む
	
	/**テスト用の目撃記録作成*/
	private Raccoon sample() {
		Raccoon raccoon = new Raccoon();
		raccoon.setId(1);
		raccoon.setSightedDate(LocalDate.of(2026, 10, 1));
		raccoon.setSightedTime(LocalTime.of(22, 0));
		raccoon.setCount(2);
		raccoon.setMemo("駐車場にいた");
		return raccoon;
	}
	
	/**registerに渡されたRaccoonを取り出す*/
	private Raccoon registeredRaccoon() {
		//Raccoonクラスのオブジェクトが渡されたら、それ（引数）をキャッチする
		ArgumentCaptor<Raccoon> captor = ArgumentCaptor.forClass(Raccoon.class);
		//serviceクラスのregisterメソッドがちゃんと1回呼ばれたことを検証
		verify(raccoonService).register(captor.capture());
		return captor.getValue();
	}
	
	/**updateに渡されたRaccoonを取り出す*/
	private Raccoon updatedRaccoon() {
		ArgumentCaptor<Raccoon> captor = ArgumentCaptor.forClass(Raccoon.class);
		verify(raccoonService).update(captor.capture());
		return captor.getValue();
	}
}
