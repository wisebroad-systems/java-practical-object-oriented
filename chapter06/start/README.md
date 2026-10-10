# 第6章 start：第5章の終了時点

第6章の開始時点のコードです。第5章の終了時点（`chapter05/end`）と同じ内容です。

## 実行する

このディレクトリ（`chapter06/start`）で、次のコマンドを実行します。

```powershell
javac -encoding UTF-8 -d out src/main/java/*.java
java -cp out Main
```

コンパイルされるファイルは、`MenuItem.java`・`OrderItem.java`・`OrderStatus.java`・`PaymentMethod.java`・`CashPayment.java`・`CardPayment.java`・`QrPayment.java`・`FulfillmentMethod.java`・`DineInFulfillment.java`・`TakeoutFulfillment.java`・`Order.java`・`Main.java`・`RejectionExamples.java`の13個です。

## 期待する結果

```text
お釣り：1000円
店内：テーブル3へ配膳、PAID
テイクアウト：受取番号12で受け渡し、PAID
```

拒否する操作の確認は、同じディレクトリで次のコマンドを実行します（コンパイルは上と同じです）。

```powershell
java -cp out RejectionExamples
```

```text
テーブル番号0：テーブル番号は1以上にしてください
受取番号0：受取番号は1以上にしてください
提供方法なし：提供方法はnullにできません
```

## この段階で成り立つルール

- メニュー項目・明細・注文の確定のルールは、第4章と同じです。
- 注文の状態は`DRAFT`・`CONFIRMED`・`PAID`のどれかで、支払えるのは`CONFIRMED`の注文だけです。
- 支払い方法（現金・カード・QRコード）は`PaymentMethod`で共通に扱います。
- 注文は作成時に提供方法（店内・テイクアウト）を受け取り、案内をその提供方法に任せます。
- 金額（価格・小計・合計・預かり金額・お釣り）と数量は、どちらも`int`で表します。価格はメニュー項目、数量は明細が、作成時に検証します。

## 意図的に残している問題

この段階は、次の段階や章で直す前の状態です。次の点は、不具合ではなく、本文で考えるための題材として残しています。

- 金額も数量も`int`なので、型を見ても、その値が金額なのか数量なのかを区別できません。
- 「金額は0以上」「数量は1以上」というルールは、値を使うクラス（メニュー項目・明細）が確かめており、値の型そのものには表れていません。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter06-start:run
.\gradlew.bat :chapter06-start:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter06-start:test
```
