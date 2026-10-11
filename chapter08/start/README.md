# 第8章 start：第7章の終了時点

第8章の開始時点のコードです。第7章の終了時点（`chapter07/end`）と同じ内容です。

## 実行する

このディレクトリ（`chapter08/start`）で、次のコマンドを実行します。

```powershell
javac -encoding UTF-8 -d out src/main/java/*.java
java -cp out Main
```

コンパイルされるファイルは、`Money.java`・`Quantity.java`・`MenuItem.java`・`OrderItem.java`・`OrderStatus.java`・`PaymentMethod.java`・`CashPayment.java`・`CardPayment.java`・`QrPayment.java`・`FulfillmentMethod.java`・`DineInFulfillment.java`・`TakeoutFulfillment.java`・`DiscountPolicy.java`・`NoDiscountPolicy.java`・`MemberDiscountPolicy.java`・`CouponDiscountPolicy.java`・`Order.java`・`Main.java`・`RejectionExamples.java`の19個です。

## 期待する結果

```text
割引なし：合計2000円、お支払い2000円
会員割引：合計2000円、お支払い1800円
クーポン：合計2000円、お支払い1700円
お釣り：300円
クーポンの注文：PAID
会員割引：合計999円、お支払い900円
クーポン：合計200円、お支払い0円
```

拒否する操作の確認は、同じディレクトリで次のコマンドを実行します（コンパイルは上と同じです）。

```powershell
java -cp out RejectionExamples
```

```text
負になる引き算：引いた結果が負の金額になります
未確定の支払額：確定前の注文には支払額がありません
割引方針なし：割引方針はnullにできません
状態：DRAFT
```

## この段階で成り立つルール

- 第7章の終了時点のルール（メニュー項目・明細・注文の状態、支払い方法、提供方法、`Money`・`Quantity`、割引と支払額の固定）は、そのまま成り立ちます。詳しくは[第7章 end](../../chapter07/end/README.md)を参照してください。

## 意図的に残している問題

この段階は、次の段階や章で直す前の状態です。次の点は、不具合ではなく、本文で考えるための題材として残しています。

- 注文は、プログラムの中で作ってそのまま使っています。保存して後から取り出すことはできません。
- カード・QRコードの支払いは、外部の決済サービスを使わない簡単な処理です（第4章と同じ）。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter08-start:run
.\gradlew.bat :chapter08-start:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter08-start:test
```
