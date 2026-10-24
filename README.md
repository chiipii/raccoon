# raccoon

アライグマの目撃記録を登録・集計する Web アプリケーションです。
目撃した日付・時刻・頭数・メモを記録し、総目撃数や時間帯ごとの傾向を一覧画面で確認できます。

Spring Boot の学習用に作成したものです。

## 技術スタック

| 分類 | 使用技術 |
|---|---|
| 言語 | Java 21 |
| フレームワーク | Spring Boot 4.1.1 |
| テンプレートエンジン | Thymeleaf |
| データアクセス | Spring Data JPA (Hibernate) |
| データベース | PostgreSQL |
| その他 | Lombok, Spring Security, Bean Validation |
| ビルド | Maven (Maven Wrapper 同梱) |

## セットアップ

### 1. データベースを作成する

PostgreSQL に `raccoon` データベースを作成します。

```sql
CREATE DATABASE raccoon;
```

### 2. テーブルを作成する

作成したデータベースに接続し、[`db/schema.sql`](db/schema.sql) を実行します。

```bash
psql -U postgres -d raccoon -f db/schema.sql
```

> `spring.jpa.hibernate.ddl-auto` が `validate` に設定されているため、**テーブルを作らずに起動すると失敗します**。アプリはテーブルを自動生成しません。

### 3. 接続情報を設定する

`src/main/resources/application.properties` の接続情報は開発用のサンプル値です。自分の環境に合わせて書き換えてください。

```properties
spring.datasource.url = jdbc:postgresql://localhost:5432/raccoon
spring.datasource.username = postgres
spring.datasource.password = postgres
```

### 4. 起動する

```bash
./mvnw spring-boot:run
```

起動後、ブラウザで **http://localhost:8080/sightings** を開きます。

> ルートパス（`/`）にはマッピングがありません。`/sightings` を直接開いてください。

## 使い方

| 画面 | URL | 内容 |
|---|---|---|
| 一覧・集計 | `GET /sightings` | 目撃記録を新しい順に表示し、集計結果を併せて表示する |
| 新規登録フォーム | `GET /sightings/new` | 登録画面を表示する |
| 登録 | `POST /sightings` | 目撃記録を登録する |
| 編集フォーム | `GET /sightings/{id}` | 既存の記録を編集画面に表示する |
| 更新 | `POST /sightings/{id}` | 目撃記録を更新する |
| 削除確認 | `GET /sightings/{id}/delete` | 削除の確認画面を表示する |
| 削除 | `POST /sightings/{id}/delete` | 目撃記録を削除する |

### 集計項目

一覧画面には以下が表示されます。

- **総目撃数** — 全記録の頭数の合計
- **記録件数** — 登録されている記録の数
- **時間帯ごとの目撃回数** — 記録のある時間帯のみ、時刻の昇順で表示

### 入力チェック

| 項目 | ルール |
|---|---|
| 目撃日 | 必須 |
| 時刻 | 必須、0〜23 の範囲 |
| 目撃日時 | 未来の日時は登録できない |
| 回数 | 必須、1 以上 |
| メモ | 255 文字以内（任意） |

## データベース

テーブルは `raccoon` の 1 つだけです。DDL は [`db/schema.sql`](db/schema.sql) にあります。

| カラム | 型 | NULL | 説明 |
|---|---|---|---|
| `id` | `serial` | × | 主キー。自動採番 |
| `sighted_date` | `date` | × | 目撃日 |
| `sighted_time` | `time` | × | 目撃時刻（分・秒は 0 固定で、時のみを記録） |
| `count` | `integer` | × | 目撃した頭数 |
| `memo` | `varchar(255)` | ○ | 自由記述のメモ |
| `created_at` | `timestamp` | ○ | 登録日時。アプリが登録時にセットする |
| `updated_at` | `timestamp` | ○ | 更新日時。アプリが登録時・更新時にセットする |

## テスト

```bash
./mvnw test
```

以下のテストが含まれます。

- `RaccoonFormTest` — 入力チェックの検証
- `RaccoonServiceTest` — 業務処理の検証
- `SightingControllerTest` — 画面遷移とリクエスト処理の検証
- `RaccoonApplicationTests` — コンテキストの起動確認

## ディレクトリ構成

```
raccoon/
├── db/
│   └── schema.sql                  テーブル定義
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── RaccoonApplication.java
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/
│   │   │   │   └── SightingController.java
│   │   │   ├── entity/
│   │   │   │   └── Raccoon.java          raccoon テーブルに対応
│   │   │   ├── form/
│   │   │   │   └── RaccoonForm.java      画面の入力値と入力チェック
│   │   │   ├── repository/
│   │   │   │   └── RaccoonRepository.java
│   │   │   └── service/
│   │   │       └── RaccoonService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/css/style.css
│   │       └── templates/
│   │           ├── fragments/              共通ヘッダ・入力欄
│   │           └── sightings/              一覧・登録・編集・削除
│   └── test/java/com/example/demo/
└── pom.xml
```

## 注意

学習用のアプリケーションのため、以下の点にご注意ください。

- **認証をかけていません。** `SecurityConfig` で `anyRequest().permitAll()` を指定し、すべてのリクエストを認証なしで許可しています。そのままインターネット上に公開しないでください。認証を有効にする場合は `permitAll()` を `authenticated()` に書き換えます。
- **DB の認証情報がソースコードに含まれています。** `application.properties` の値はローカル開発用のサンプルです。実運用する場合は環境変数などの外部設定に移してください。
