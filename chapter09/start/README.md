# 第9章 start：第8章の終了時点

第9章の開始時点のコードです。第8章の終了時点（`chapter08/end`）と同じ内容です。

## 実行する

このディレクトリ（`chapter09/start`）で、次のコマンドを実行します。

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

- 第8章の終了時点のルール（注文の状態・支払額の固定・割引、支払い方法と決済の窓口、注文IDと注文の保存先）は、そのまま成り立ちます。詳しくは[第8章 end](../../chapter08/end/README.md)を参照してください。

## 意図的に残している問題

この段階は、次の段階や章で直す前の状態です。次の点は、不具合ではなく、本文で考えるための題材として残しています。

- 注文を確定して保存し、支払って、成功したら保存し直すという一連の進め方を、呼び出し側（`Main`）が自分で組み立てています。注文を受け付ける場所が増えると、同じ進め方をそれぞれに書くことになります。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter09-start:run
.\gradlew.bat :chapter09-start:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter09-start:test
```
