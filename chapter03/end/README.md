# 第3章 end：小計の計算を明細に任せる

第3章の終了時点のコードです。明細（`OrderItem`）が`subtotal()`で小計を求め、注文はそれを足して合計を求めます。

## 実行する

このディレクトリ（`chapter03/end`）で、次のコマンドを実行します。

```powershell
javac -encoding UTF-8 -d out src/main/java/*.java
java -cp out Main
```

コンパイルされるファイルは、`MenuItem.java`・`OrderItem.java`・`Order.java`・`Main.java`・`RejectionExamples.java`の5つです。

## 期待する結果

```text
コーヒー × 2：800円
ハンバーグ × 1：1200円
合計：2000円
確定済み：true
```

拒否する操作の確認は、同じディレクトリで次のコマンドを実行します（コンパイルは上と同じです）。

```powershell
java -cp out RejectionExamples
```

```text
数量0での追加：数量は1以上にしてください
明細数：1、数量：2、合計：800円、確定済み：false
負の数量での追加：数量は1以上にしてください
明細数：1、数量：2、合計：800円、確定済み：false
確定後の追加：確定後は料理を追加できません
明細数：1、数量：2、合計：800円、確定済み：true
```

## この段階で成り立つルール

- メニュー項目は名前と価格を持ちます。価格は0円以上で、負の価格は作成時に拒否します。
- 名前が`null`・空文字・空白だけのメニュー項目は、作成時に拒否します。
- 明細の数量は1以上です。数量が0や負の明細、料理が`null`の明細は作れません。
- 明細は、料理の価格と数量から小計を求めます。
- 同じ料理を後から追加すると、別の明細になります（明細はまとめません）。
- 料理がない注文は確定できません。0円の料理だけの注文は確定できます。
- 確定済みの注文は、もう一度確定できません。
- 確定後は、`addItem()`で料理を追加できません。
- `getItems()`が返す一覧は変更できません。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter03-end:run
.\gradlew.bat :chapter03-end:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter03-end:test
```
