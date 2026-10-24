package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.entity.Raccoon;
import com.example.demo.repository.RaccoonRepository;

/**RaccoonServiceの業務処理を検証する。*/
@ExtendWith(MockitoExtension.class)/**Mockitoを簡単に作るためのライブラリを連動させるための拡張機能*/
public class RaccoonServiceTest {
	
	@Mock/**偽物のオブジェクトを作る*/
	private RaccoonRepository raccoonRepository;
	
	@InjectMocks/**テスト対象のクラスに偽物たちを自動で組み込む*/
	private RaccoonService raccoonService;
	
	/**テスト用の目撃記録を組み立てる*/
	private Raccoon raccoon(Integer id, LocalDate date, int hour, int count) {
		Raccoon raccoon = new Raccoon();
		raccoon.setId(id);
		raccoon.setSightedDate(date);
		raccoon.setSightedTime(LocalTime.of(hour, 0));
		raccoon.setCount(count);
		return raccoon;
	}
	
	/**保存されたRaccoonを取り出す*/
	private Raccoon savedRaccoon() {
		ArgumentCaptor<Raccoon> captor = ArgumentCaptor.forClass(Raccoon.class);
		verify(raccoonRepository).save(captor.capture());
		return captor.getValue();
	}
	
	@Test
	@DisplayName("findAllは並び替えつきのメソッドに委譲する")
	void 一覧取得() {
		//準備（期待値の作成）
		List<Raccoon> expected = List.of(raccoon(1, LocalDate.of(2026, 10, 1), 22, 2));
		
		//動作の指定（モックの振る舞い）
		when(raccoonRepository.findAllByOrderBySightedDateDescSightedTimeDesc()).thenReturn(expected);
		
		//実行と検証（サービスの呼び出しと結果の確認）
		assertThat(raccoonService.findAll()).isEqualTo(expected);
		
		//並び替えなしのfindAll()と取り違えていないこと
		verify(raccoonRepository, never()).findAll();
	}
	
	@Test
	@DisplayName("recordCountはリポジトリの件数をそのまま返す")
	void 件数取得() {
		when(raccoonRepository.count()).thenReturn(3L);
		assertThat(raccoonService.recordCount()).isEqualTo(3L);
	}
	
	@Test
	@DisplayName("登録するとcreatedAtとupdatedAtが同じ時刻で入る")
	void 登録() {
		LocalDateTime before = LocalDateTime.now();
		
		raccoonService.register(raccoon(null, LocalDate.of(2026, 10, 1), 22, 3));
		
		Raccoon saved = savedRaccoon();
		assertThat(saved.getCreatedAt()).isNotNull();
		//テストがテスト開始直前よりも後の時間になっていることをチェックする
		assertThat(saved.getCreatedAt()).isAfterOrEqualTo(before);
		assertThat(saved.getUpdatedAt()).isEqualTo(saved.getCreatedAt());
	}
	
	@Test
	@DisplayName("見つからないIDを取得すると例外")
	void 取得失敗() {
		when(raccoonRepository.findById(99)).thenReturn(Optional.empty());
		
		//存在しないデータを指定された時、例外を投げるという機能が働いているか確認する
		assertThatThrownBy(() -> raccoonService.findById(99))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("id=99");
	}
	
	@Test
	@DisplayName("更新するとcreatedAtは維持され、updatedAtだけ新しくなる")
	void 更新() {
		//基準となる作成日時（答え合わせ用）
		LocalDateTime created = LocalDateTime.of(2026, 10, 1, 9, 0);
		//テスト用のダミーデータ（古いデータ）
		Raccoon stored = raccoon(1, LocalDate.of(2026,  10, 1), 22, 1);
		//古いデータの初期状態をセットする
		stored.setCreatedAt(created);
		stored.setUpdatedAt(created);
		stored.setMemo("変更前");
		when(raccoonRepository.findById(1)).thenReturn(Optional.of(stored));
		
		//新しく上書きする入力データの作成と更新
		Raccoon input = raccoon(1, LocalDate.of(2026, 10, 5), 23, 4);
		input.setMemo("変更後");
		raccoonService.update(input);
		
		//更新処理が終わったあとDBに保存されるはずのデータを受け取る
		Raccoon saved = savedRaccoon();
		//検証
		assertThat(saved.getCreatedAt()).isEqualTo(created);
		assertThat(saved.getUpdatedAt()).isAfter(created);
		assertThat(saved.getSightedDate()).isEqualTo(LocalDate.of(2026, 10, 5));
		assertThat(saved.getSightedTime()).isEqualTo(LocalTime.of(23, 0));
		assertThat(saved.getCount()).isEqualTo(4);
		assertThat(saved.getMemo()).isEqualTo("変更後");
	}
	
	@Test
	@DisplayName("削除は取得してから消す")
	void 削除() {
		//削除対象のダミーデータ
		Raccoon stored = raccoon(1, LocalDate.of(2026, 10, 1), 22, 2);
		//ID1のデータがあることを確認して返す。
		when(raccoonRepository.findById(1)).thenReturn(Optional.of(stored));
		
		raccoonService.delete(1);
		
		//repositoryのdeleteメソッドに対して、取得したstoredを渡して削除処理が実行されたか確認する機能
		verify(raccoonRepository).delete(stored);
	}
	
	@Test
	@DisplayName("存在しないIDの削除は例外になり、delete呼ばない")
	void 削除失敗() {
		when(raccoonRepository.findById(99)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> raccoonService.delete(99))
		.isInstanceOf(IllegalArgumentException.class);
		
		//deleteメソッドが一度も呼び出されていないことの検証
		verify(raccoonRepository, never()).delete(any(Raccoon.class));
	}
	
	@Test
	@DisplayName("totalCountは回数を合計し、countByHourは時間帯別に合算して昇順で返す")
	void 集計() {
		when(raccoonRepository.findAll()).thenReturn(List.of(
				raccoon(1, LocalDate.of(2026, 10, 1), 22, 2),
				raccoon(2, LocalDate.of(2026, 10, 2), 5, 1),
				raccoon(3, LocalDate.of(2026, 10, 3), 22, 3)));
		
		//全体の合計数の検証
		assertThat(raccoonService.totalCount()).isEqualTo(6);
		
		//時間->数の組み合わせを返す。
		Map<Integer, Integer> byHour = raccoonService.countByHour();
		
		//5時に1匹。22時に5匹が入っていることの検証
		assertThat(byHour).containsEntry(5, 1).containsEntry(22, 5);
		//昇順であるかの検証
		assertThat(byHour.keySet()).containsExactly(5, 22);
	}
	
	@Test
	@DisplayName("記録が１件もなければ合計0、時間帯別は空")
	void 記録ゼロ件() {
		when(raccoonRepository.findAll()).thenReturn(List.of());
		
		//データが１件もない状態で総計を計算すると、0となることの検証
		assertThat(raccoonService.totalCount()).isZero();
		//時間帯別の集計結果が空出あることの検証
		assertThat(raccoonService.countByHour()).isEmpty();
	}
}
