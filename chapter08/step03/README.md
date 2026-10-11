# 第8章 step03：決済の代替実装を直接使う

第8章の3つ目の途中段階のコードです。カード・QRコードの支払い（`CardPayment`・`QrPayment`）が、外部の決済サービスの代わり（`FakePaymentGateway`）を中で直接作り、識別情報と支払額を渡して支払いを依頼します。保存はstep02と同じです。

## 実行する

このディレクトリ（`chapter08/step03`）で、次のコマンドを実行します。

```powershell
javac -encoding UTF-8 -d out src/main/java/*.java
java -cp out Main
```

コンパイルされるファイルは、`Money.java`・`Quantity.java`・`OrderId.java`・`MenuItem.java`・`OrderItem.java`・`OrderStatus.java`・`PaymentMethod.java`・`CashPayment.java`・`CardPayment.java`・`QrPayment.java`・`FakePaymentGateway.java`・`FulfillmentMethod.java`・`DineInFulfillment.java`・`TakeoutFulfillment.java`・`DiscountPolicy.java`・`NoDiscountPolicy.java`・`MemberDiscountPolicy.java`・`CouponDiscountPolicy.java`・`Order.java`・`OrderRepository.java`・`InMemoryOrderRepository.java`・`Main.java`・`RejectionExamples.java`の23個です。

## 期待する結果

```text
確定して保存：order-001、CONFIRMED、お支払い1700円
決済の依頼：カード（card-0001）、1700円
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
未登録の注文ID：注文が見つかりません：order-999
```

## この段階で成り立つルール

- 第7章の終了時点のルール（メニュー項目・明細・注文の状態、支払い方法、提供方法、`Money`・`Quantity`、割引と支払額の固定）は、そのまま成り立ちます。詳しくは[第7章 end](../../chapter07/end/README.md)を参照してください。
- 注文は、作成時に注文ID（`OrderId`）と提供方法を受け取ります。注文IDは、`null`・空・空白だけにはできません。
- 同じ文字列の注文IDは、`equals`で等しくなります。`Map`のキーにすると、同じ文字列の別のインスタンスで取り出せます。
- 注文は、注文の保存先（`OrderRepository`）の`save`で保存し、`getById`で取得します。`InMemoryOrderRepository`は、メモリ上の`Map`に保存します。
- `save`は、新しく保存するか、同じIDの注文を置き換えます。`getById`は、保存されていないIDなら`NoSuchElementException`で知らせます。
- メモリ上の保存は同じ注文への参照を持つので、取得した注文の変更は、保存し直す前から見えます。実際のデータベースとは違い、プログラムを終了すると、保存した注文はなくなります。
- `Main`は、確定して未払いの注文を保存し、支払いに成功したら同じIDで保存し直します。
- `FakePaymentGateway`は実際には通信せず、依頼の内容を表示して、成功を返します。

## 意図的に残している問題

この段階は、次の段階や章で直す前の状態です。次の点は、不具合ではなく、本文で考えるための題材として残しています。

- `CardPayment`・`QrPayment`が、`FakePaymentGateway`を中で直接作っています。決済先を変えるには、支払い方法の実装を書き換える必要があります。
- 決済先を外から渡せないので、失敗する決済先を使って、失敗した後の注文の状態や再試行を確かめることができません。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter08-step03:run
.\gradlew.bat :chapter08-step03:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter08-step03:test
```
