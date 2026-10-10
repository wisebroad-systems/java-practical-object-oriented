# 第7章 start：第6章の終了時点

第7章の開始時点のコードです。第6章の終了時点（`chapter06/end`）と同じ内容です。

## 実行する

このディレクトリ（`chapter07/start`）で、次のコマンドを実行します。

```powershell
javac -encoding UTF-8 -d out src/main/java/*.java
java -cp out Main
```

コンパイルされるファイルは、`Money.java`・`Quantity.java`・`MenuItem.java`・`OrderItem.java`・`OrderStatus.java`・`PaymentMethod.java`・`CashPayment.java`・`CardPayment.java`・`QrPayment.java`・`FulfillmentMethod.java`・`DineInFulfillment.java`・`TakeoutFulfillment.java`・`Order.java`・`Main.java`・`RejectionExamples.java`の15個です。

## 期待する結果

```text
お釣り：1000円
店内：テーブル3へ配膳、PAID
テイクアウト：受取番号12で受け渡し、PAID
同じオブジェクトか：false
同じ金額か：true
2倍：800円、元の金額：400円
明細：コーヒー、400円
明細：コーヒー、450円
合計：850円
```

拒否する操作の確認は、同じディレクトリで次のコマンドを実行します（コンパイルは上と同じです）。

```powershell
java -cp out RejectionExamples
```

```text
負の金額：金額は0以上にしてください
intの範囲を超える金額：integer overflow
数量0：数量は1以上にしてください
明細の数：1
```

## この段階で成り立つルール

- メニュー項目・明細・注文の確定のルールは、第4章と同じです。
- 注文の状態は`DRAFT`・`CONFIRMED`・`PAID`のどれかで、支払えるのは`CONFIRMED`の注文だけです。
- 支払い方法（現金・カード・QRコード）は`PaymentMethod`で共通に扱います。
- 注文は作成時に提供方法（店内・テイクアウト）を受け取り、案内をその提供方法に任せます。
- 金額は`Money`、数量は`Quantity`で表します。負の金額や、0・負の数量は作れません。
- 同じ値の`Money`・`Quantity`は、`equals`で等しくなります。
- 明細は、追加した時点のメニュー項目の価格を持ち続けます。

## 意図的に残している問題

この段階は、次の段階や章で直す前の状態です。次の点は、不具合ではなく、本文で考えるための題材として残しています。

- 割引はまだありません。注文の確定は割引なしの`confirm()`だけで、支払いには合計（`totalPrice()`）をそのまま使います。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter07-start:run
.\gradlew.bat :chapter07-start:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter07-start:test
```
