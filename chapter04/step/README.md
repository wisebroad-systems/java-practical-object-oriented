# 第4章 step：支払いを条件分岐で扱う

第4章の途中段階のコードです。注文の状態を`OrderStatus`で表し、支払い済み（`PAID`）を加えました。支払いは、呼び出し側（`Main`）が`switch`文で、現金・カード・QRコードの処理を分けて行います。

## 実行する

このディレクトリ（`chapter04/step`）で、次のコマンドを実行します。

```powershell
javac -encoding UTF-8 -d out src/main/java/*.java
java -cp out Main
```

コンパイルされるファイルは、`MenuItem.java`・`OrderItem.java`・`OrderStatus.java`・`Order.java`・`PaymentType.java`・`Main.java`・`RejectionExamples.java`の7つです。

## 期待する結果

```text
お釣り：1000円
現金：PAID
カード：PAID
QRコード：PAID
無料の注文：PAID
```

拒否する操作の確認は、同じディレクトリで次のコマンドを実行します（コンパイルは上と同じです）。

```powershell
java -cp out RejectionExamples
```

```text
未確定の注文の支払い：確定済み・未払いの注文だけ支払えます
状態：DRAFT
支払い済みの注文の再支払い：確定済み・未払いの注文だけ支払えます
状態：PAID
```

## この段階で成り立つルール

- メニュー項目は名前と価格を持ちます。価格は0円以上で、負の価格は作成時に拒否します。
- 明細の数量は1以上です。明細は、料理の価格と数量から小計を求めます。
- 料理がない注文は確定できません。確定済みの注文は、もう一度確定できません。
- 確定後は料理を追加できません。`getItems()`が返す一覧は変更できません。
- 注文の状態は、`DRAFT`（未確定）・`CONFIRMED`（確定済み・未払い）・`PAID`（支払い済み）のどれかです。
- 支払えるのは`CONFIRMED`の注文だけです。未確定の注文や支払い済みの注文は、支払いを拒否します。
- 支払う金額が0円の注文は、支払いをせずに会計を完了し、`PAID`になります。
- 現金は、預かり金額が足りれば支払いが成功し、お釣りを求めます。

## 意図的に残している問題

この段階は、次の段階や章で直す前の状態です。次の問題は、不具合ではなく、本文で考えるための題材として残しています。

- 支払い方法を増やすたびに、`Main`の`pay`メソッドの`switch`文に`case`が増えます。
- `pay`メソッドは、すべての支払い方法の情報（預かり金額、カードの識別情報、決済コード）を引数で受け取っています。
- 呼び出し側が、「支払えるか確かめる」「支払う」「支払い済みにする」の手順を組み立てています（`checkPayable()`と`markPaid()`は、この段階だけの操作です）。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter04-step:run
.\gradlew.bat :chapter04-step:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter04-step:test
```
