# 第8章 step01：注文IDで保存する

第8章の1つ目の途中段階のコードです。注文ID（`OrderId`）を導入し、注文は作成時にIDと提供方法を受け取ります。呼び出し側（`Main`）が`Map<OrderId, Order>`へ注文を直接保存し、IDで取得します。

## 実行する

このディレクトリ（`chapter08/step01`）で、次のコマンドを実行します。

```powershell
javac -encoding UTF-8 -d out src/main/java/*.java
java -cp out Main
```

コンパイルされるファイルは、`Money.java`・`Quantity.java`・`OrderId.java`・`MenuItem.java`・`OrderItem.java`・`OrderStatus.java`・`PaymentMethod.java`・`CashPayment.java`・`CardPayment.java`・`QrPayment.java`・`FulfillmentMethod.java`・`DineInFulfillment.java`・`TakeoutFulfillment.java`・`DiscountPolicy.java`・`NoDiscountPolicy.java`・`MemberDiscountPolicy.java`・`CouponDiscountPolicy.java`・`Order.java`・`Main.java`・`RejectionExamples.java`の20個です。

## 期待する結果

```text
確定して保存：order-001、CONFIRMED、お支払い1700円
支払いの結果：true
支払い後に保存：order-001、PAID、お支払い1700円
```

拒否する操作の確認は、同じディレクトリで次のコマンドを実行します（コンパイルは上と同じです）。

```powershell
java -cp out RejectionExamples
```

```text
空白の注文ID：注文IDは空にできません
注文IDなし：注文IDはnullにできません
```

## この段階で成り立つルール

- 第7章の終了時点のルール（メニュー項目・明細・注文の状態、支払い方法、提供方法、`Money`・`Quantity`、割引と支払額の固定）は、そのまま成り立ちます。詳しくは[第7章 end](../../chapter07/end/README.md)を参照してください。
- 注文は、作成時に注文ID（`OrderId`）と提供方法を受け取ります。注文IDは、`null`・空・空白だけにはできません。
- 同じ文字列の注文IDは、`equals`で等しくなります。`Map`のキーにすると、同じ文字列の別のインスタンスで取り出せます。
- `Main`は、確定して未払いの注文を`Map`に保存し、支払いに成功したら同じIDで保存し直します。

## 意図的に残している問題

この段階は、次の段階や章で直す前の状態です。次の点は、不具合ではなく、本文で考えるための題材として残しています。

- 呼び出し側が、保存先が`Map`であることと、その操作（`put`・`get`）を知っています。保存先を変えると、呼び出し側も変わります。
- カード・QRコードの支払いは、外部の決済サービスを使わない簡単な処理です（第4章と同じ）。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter08-step01:run
.\gradlew.bat :chapter08-step01:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter08-step01:test
```
