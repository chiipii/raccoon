package com.example.demo.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.demo.config.SecurityConfig;
import com.example.demo.entity.Raccoon;
import com.example.demo.form.RaccoonForm;
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

	@Test
	@DisplayName("一覧画面を開いたときに、ControllerがServiceから取得したデータを、正しいModelに詰めて、正しい画面へ渡しているか")
	void 一覧表示() throws Exception {
		//準備
		List<Raccoon> sightings = List.of(sample());
		//findAll()が呼ばれたら、sightingsを返す。
		when(raccoonService.findAll()).thenReturn(sightings);
		//totalCount()が呼ばれたら、2を返す。
		when(raccoonService.totalCount()).thenReturn(2);
		//recordCount()が呼ばれたら、long型の1を返す。
		when(raccoonService.recordCount()).thenReturn(1L);
		//countByHour()が呼ばれたら、{22,2}というMapを返す。
		when(raccoonService.countByHour()).thenReturn(Map.of(22, 2));

		//ブラウザから/sightingsにGETリクエストを送ったつもりになる。
		mockMvc.perform(get("/sightings"))
				//HTTPステータスが200 Okだったか。（HTTP通信が正常に終わったか）
				.andExpect(status().isOk())
				//Controllerがsightings/listというviewを返したか。
				.andExpect(view().name("sightings/list"))
				//Modelへデータを入れているか確認する。
				.andExpect(model().attribute("sightings", sightings))
				.andExpect(model().attribute("totalCount", 2))
				.andExpect(model().attribute("recordCount", 1L))
				.andExpect(model().attribute("countByHour", Map.of(22, 2)));
	}

	@Test
	@DisplayName("登録画面には空のフォームを渡す")
	void 登録画面表示() throws Exception {
		MvcResult result = mockMvc.perform(get("/sightings/new"))
				.andExpect(status().isOk())
				.andExpect(view().name("sightings/new"))
				//実行結果を取り出す
				.andReturn();

		//初期表示なので全項目が未入力であること
		RaccoonForm form = (RaccoonForm) result.getModelAndView().getModel().get("raccoonForm");
		assertThat(form.getSightedDate()).isNull();
		assertThat(form.getSightedHour()).isNull();
		assertThat(form.getCount()).isNull();
		assertThat(form.getMemo()).isNull();
		assertThat(form.getId()).isNull();
	}

	@Test
	@DisplayName("登録すると分を0にした時刻でサービスに渡し、一覧へリダイレクトする")
	void 登録() throws Exception {
		mockMvc.perform(post("/sightings")
				.with(csrf())
				//formからsightedDate=2026-10-01が送信されたことを再現している。
				.param("sightedDate", "2026-10-01")
				.param("sightedHour", "22")
				.param("count", "2")
				.param("memo", "駐車場にいた"))
				//HTTPステータスが3xx系のリダイレクトになっていることを確認する。
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/sightings"));

		//Serviceのresister()に実際に渡されたRaccoonを取り出している
		Raccoon registered = registeredRaccoon();
		//日付を検証
		assertThat(registered.getSightedDate()).isEqualTo(LocalDate.of(2026, 10, 1));
		//時刻を検証。画面は時だけ選ばせるので、分は0になる。
		assertThat(registered.getSightedTime()).isEqualTo(LocalTime.of(22, 0));
		//頭数を検証
		assertThat(registered.getCount()).isEqualTo(2);
		//メモを検証
		assertThat(registered.getMemo()).isEqualTo("駐車場にいた");
		//IDがnullであることを検証。登録時はDBに採番させるのでIDはnullのまま渡す
		assertThat(registered.getId()).isNull();
	}

	@Test
	@DisplayName("編集画面を開いた時に、ID=1のデータをServiceから取得して、それを正しい内容のformとして編集画面に渡しているか")
	void 編集画面表示() throws Exception {
		//テスト中にraccoonService.findById(1)が呼ばれたら、sample()を返して下さい。
		when(raccoonService.findById(1)).thenReturn(sample());

		MvcResult result = mockMvc.perform(get("/sightings/1"))
				.andExpect(status().isOk())
				.andExpect(view().name("sightings/edit"))
				.andReturn();

		RaccoonForm form = (RaccoonForm) result.getModelAndView().getModel().get("raccoonForm");
		assertThat(form.getId()).isEqualTo(1);
		assertThat(form.getSightedDate()).isEqualTo(LocalDate.of(2026, 10, 1));
		assertThat(form.getSightedHour()).isEqualTo(22);
		assertThat(form.getCount()).isEqualTo(2);
		assertThat(form.getMemo()).isEqualTo("駐車場にいた");

		//そもそもServiceをID=1を指定して呼んだか確認
		verify(raccoonService).findById(1);
	}

	@Test
	@DisplayName("更新はフォームのIDではなく、URLのIDを使う")
	void 更新() throws Exception {
		mockMvc.perform(post("/sightings/1")
				.with(csrf())
				//わざと別のIDを入れて送る
				.param("is", "99")
				.param("sightedDate", "2026-10-05")
				.param("sightedHour", "23")
				.param("count", "4")
				.param("memo", "変更後"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/sightings"));

		Raccoon updated = updatedRaccoon();
		//URL側の1が採用され、フォームの99は無視される。
		assertThat(updated.getId()).isEqualTo(1);
		assertThat(updated.getSightedDate()).isEqualTo(LocalDate.of(2026, 10, 5));
		assertThat(updated.getSightedTime()).isEqualTo(LocalTime.of(23, 0));
		assertThat(updated.getCount()).isEqualTo(4);
		assertThat(updated.getMemo()).isEqualTo("変更後");
	}

	@Test
	@DisplayName("更新の入力エラーなら編集画面に戻し、Serviceを呼ばない")
	void 更新_入力エラー() throws Exception {
		mockMvc.perform(post("/sightings/1")
				.with(csrf())
				.param("id", "1")
				.param("sightedDate", "2026-10-05")
				.param("sightedHour", "23")
				//回数は1以上の制限あり
				.param("count", "0"))
		
				.andExpect(status().isOk())
				.andExpect(view().name("sightings/edit"))
				.andExpect(model().attributeHasFieldErrors("raccoonForm", "count"));

		//Raccoon型の引数ならなんでもよいが、とにかくupdate()は呼んでいないことの確認。
		verify(raccoonService, never()).update(any(Raccoon.class));
	}
	
	@Test
	@DisplayName("削除確認画面には記録をsightingという名前で渡す")
	void 削除確認画面表示() throws Exception{
		Raccoon sighting = sample();
		when(raccoonService.findById(1)).thenReturn(sighting);
		
		mockMvc.perform(get("/sightings/1/delete"))
		.andExpect(status().isOk())
		.andExpect(view().name("sightings/delete"))
		//一覧のsightingsと間違えないこと(削除対象のRaccoonをsightingという名前でModelに渡したか確認する)	
		.andExpect(model().attribute("sighting", sighting));
	}
	
	@Test
	@DisplayName("削除はURLのIDで消して一覧へリダイレクトする")
	void 削除() throws Exception{
		mockMvc.perform(post("/sightings/1/delete").with(csrf()))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/sightings"));
		
		//delete()が引数1で呼ばれたか確認
		verify(raccoonService).delete(1);
	}
}
