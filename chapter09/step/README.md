# 第9章 step：進め方をServiceに移す

第9章の途中段階のコードです。`Main`が組み立てていた進め方を、`OrderApplicationService`の初回の操作（`confirmAndPay`）と再試行の操作（`retryPayment`）に移します。

## 実行する

このディレクトリ（`chapter09/step`）で、次のコマンドを実行します。

```powershell
javac -encoding UTF-8 -d out src/main/java/*.java
java -cp out Main
```

コンパイルされるファイルは、`Money.java`・`Quantity.java`・`OrderId.java`・`MenuItem.java`・`OrderItem.java`・`OrderStatus.java`・`PaymentMethod.java`・`CashPayment.java`・`CardPayment.java`・`QrPayment.java`・`PaymentGateway.java`・`FakePaymentGateway.java`・`FulfillmentMethod.java`・`DineInFulfillment.java`・`TakeoutFulfillment.java`・`DiscountPolicy.java`・`NoDiscountPolicy.java`・`MemberDiscountPolicy.java`・`CouponDiscountPolicy.java`・`Order.java`・`OrderRepository.java`・`InMemoryOrderRepository.java`・`OrderApplicationService.java`・`Main.java`・`RejectionExamples.java`の25個です。

## 期待する結果

```text
決済の依頼：カード（card-0001）、1700円
初回の結果：false
保存されている注文：order-001、CONFIRMED、お支払い1700円
決済の依頼：カード（card-0001）、1700円
再試行の結果：true
保存されている注文：order-001、PAID、お支払い1700円
0円の注文の結果：true
保存されている注文：order-002、PAID、お支払い0円
```

拒否する操作の確認は、同じディレクトリで次のコマンドを実行します（コンパイルは上と同じです）。

```powershell
java -cp out RejectionExamples
```

```text
支払い方法なし：支払い方法はnullにできません
注文の状態：DRAFT
保存先：注文が見つかりません：order-001
未登録の注文の再試行：注文が見つかりません：order-999
決済の依頼：カード（card-0001）、400円
支払い済みの注文の再試行：確定済み・未払いの注文だけ支払えます
```

## この段階で成り立つルール

- 第8章の終了時点のルール（注文の状態・支払額の固定・割引、支払い方法と決済の窓口、注文IDと注文の保存先）は、そのまま成り立ちます。詳しくは[第8章 end](../../chapter08/end/README.md)を参照してください。
- `OrderApplicationService`は、注文の保存先をコンストラクタで受け取ります。割引方針と支払い方法は、操作ごとに引数で受け取ります。
- 初回の`confirmAndPay`は、注文を確定し（割引額の計算は注文が割引方針に依頼する）、未払いで保存してから支払い、成功したら保存し直します。
- 再試行の`retryPayment`は、注文IDで保存先から注文を取得し、確定時に固定した支払額で支払い、成功したら保存し直します。割引方針は受け取らず、確定し直したり割引を計算し直したりしません。
- どちらの操作も、支払いの成否を`boolean`で返します。支払額が0円なら、決済を依頼せずに支払い済みにして保存し、`true`を返します。
- 必須の引数が`null`なら、確定・保存・決済を始める前に`IllegalArgumentException`で拒否します。注文の状態の違反や、保存されていない注文IDは、`false`に変えずに例外のまま知らせます。
- 支払いの手順（状態の確認、0円の扱い、成功したときだけ支払い済みにする）は、第4章からと同じく注文の`pay`が行います。Serviceは、取得・確定・保存の進め方だけを受け持ちます。

## 意図的に残している問題

この段階は、次の段階や章で直す前の状態です。次の点は、不具合ではなく、本文で考えるための題材として残しています。

- `confirmAndPay`と`retryPayment`の両方に、「注文に支払いを依頼し、成功したら保存し直す」という同じ部分があります。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter09-step:run
.\gradlew.bat :chapter09-step:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter09-step:test
```
