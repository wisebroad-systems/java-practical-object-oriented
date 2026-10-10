# 第6章 end：数量もQuantityで表す

第6章の終了時点のコードです。金額の`Money`に加えて、数量を`Quantity`で表しました。数量が1以上であることは`Quantity`が守り、明細と注文は`Quantity`を受け取ります。

## 実行する

このディレクトリ（`chapter06/end`）で、次のコマンドを実行します。

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
- 金額は`Money`で表します。負の金額は作れません。計算は新しい`Money`を返し、`int`の範囲を超える計算は`ArithmeticException`で知らせます。
- 数量は`Quantity`で表します。0や負の数量は作れません。
- 同じ値の`Money`・`Quantity`は、`equals`で等しくなります。
- 明細は、追加した時点のメニュー項目の価格を持ち続けます。価格を改定するときは、新しい価格のメニュー項目を作り、その後に追加する明細から使います。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter06-end:run
.\gradlew.bat :chapter06-end:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter06-end:test
```
