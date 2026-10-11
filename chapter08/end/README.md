# 第8章 end：決済の窓口を外から渡す

第8章の終了時点のコードです。決済の窓口（`PaymentGateway`）を導入し、`CardPayment`・`QrPayment`はコンストラクタで受け取った窓口へ支払いを依頼します。`FakePaymentGateway`は、作成時に成功・失敗を指定できます。

## 実行する

このディレクトリ（`chapter08/end`）で、次のコマンドを実行します。

```powershell
javac -encoding UTF-8 -d out src/main/java/*.java
java -cp out Main
```

コンパイルされるファイルは、`Money.java`・`Quantity.java`・`OrderId.java`・`MenuItem.java`・`OrderItem.java`・`OrderStatus.java`・`PaymentMethod.java`・`CashPayment.java`・`CardPayment.java`・`QrPayment.java`・`PaymentGateway.java`・`FakePaymentGateway.java`・`FulfillmentMethod.java`・`DineInFulfillment.java`・`TakeoutFulfillment.java`・`DiscountPolicy.java`・`NoDiscountPolicy.java`・`MemberDiscountPolicy.java`・`CouponDiscountPolicy.java`・`Order.java`・`OrderRepository.java`・`InMemoryOrderRepository.java`・`Main.java`・`RejectionExamples.java`の24個です。

## 期待する結果

```text
確定して保存：order-001、CONFIRMED、お支払い1700円
決済の依頼：カード（card-0001）、1700円
支払いの結果：false
保存されている注文：order-001、CONFIRMED、お支払い1700円
決済の依頼：カード（card-0001）、1700円
再試行の結果：true
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
決済の依頼：カード（card-0001）、400円
支払い済みの注文の再支払い：確定済み・未払いの注文だけ支払えます
```

## この段階で成り立つルール

- 第7章の終了時点のルール（メニュー項目・明細・注文の状態、支払い方法、提供方法、`Money`・`Quantity`、割引と支払額の固定）は、そのまま成り立ちます。詳しくは[第7章 end](../../chapter07/end/README.md)を参照してください。
- 注文は、作成時に注文ID（`OrderId`）と提供方法を受け取ります。注文IDは、`null`・空・空白だけにはできません。
- 同じ文字列の注文IDは、`equals`で等しくなります。`Map`のキーにすると、同じ文字列の別のインスタンスで取り出せます。
- 注文は、注文の保存先（`OrderRepository`）の`save`で保存し、`getById`で取得します。`InMemoryOrderRepository`は、メモリ上の`Map`に保存します。
- `save`は、新しく保存するか、同じIDの注文を置き換えます。`getById`は、保存されていないIDなら`NoSuchElementException`で知らせます。
- メモリ上の保存は同じ注文への参照を持つので、取得した注文の変更は、保存し直す前から見えます。実際のデータベースとは違い、プログラムを終了すると、保存した注文はなくなります。
- `Main`は、確定して未払いの注文を保存し、支払いに成功したら同じIDで保存し直します。
- カード・QRコードの支払いは、コンストラクタで受け取った決済の窓口へ、識別情報と支払額を渡して依頼します。現金の支払いは、決済の窓口を持ちません。
- `FakePaymentGateway`は実際には通信せず、依頼の内容を表示して、作成時に指定した結果（成功・失敗）を返します。
- 決済に失敗した注文は`CONFIRMED`のままで、`Main`は保存し直しません。保存先から取得して、成功する決済の窓口を持つ新しい支払い方法で再試行できます。支払額は、確定時に固定した金額のままです。
- 支払い済みの注文の再試行は拒否し、決済の窓口を呼びません。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter08-end:run
.\gradlew.bat :chapter08-end:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter08-end:test
```
