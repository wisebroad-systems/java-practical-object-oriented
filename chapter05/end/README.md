# 第5章 end：コンポジションで提供の違いを表す

第5章の終了時点のコードです。提供の仕方を`FulfillmentMethod`として表し、店内（`DineInFulfillment`）とテイクアウト（`TakeoutFulfillment`）がそれぞれ番号の検証と案内を担当します。注文は、作るときに提供方法を受け取り、案内をその提供方法に任せます。

## 実行する

このディレクトリ（`chapter05/end`）で、次のコマンドを実行します。

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
- 店内の提供方法はテーブル番号、テイクアウトの提供方法は受取番号を持ちます。どちらも1以上で、作成時に検証します。
- 注文は作成時に提供方法を受け取ります。提供方法のない注文は作れず、作成後に提供方法を変える操作はありません。
- 提供の仕方と支払い方法は独立しています。店内でもテイクアウトでも、どの支払い方法でも支払えます。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter05-end:run
.\gradlew.bat :chapter05-end:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter05-end:test
```
