# 第9章 advanced：確定後のクーポン（Advanced課題の解説例）

第9章末のAdvanced課題「確定後にクーポンが提示されたら？」の解説例です。本編の`chapter09/end`をもとに、注文の内容の確定と支払額の固定を別の操作に分けています。本編の続きではなく、課題のための別の例です（第10章は`chapter09/end`を引き継ぎます）。

## 実行する

このディレクトリ（`chapter09/advanced`）で、次のコマンドを実行します。

```powershell
javac -encoding UTF-8 -d out src/main/java/*.java
java -cp out Main
```

コンパイルされるファイルは、`Money.java`・`Quantity.java`・`OrderId.java`・`MenuItem.java`・`OrderItem.java`・`OrderStatus.java`・`PaymentMethod.java`・`CashPayment.java`・`CardPayment.java`・`QrPayment.java`・`PaymentGateway.java`・`FakePaymentGateway.java`・`FulfillmentMethod.java`・`DineInFulfillment.java`・`TakeoutFulfillment.java`・`DiscountPolicy.java`・`NoDiscountPolicy.java`・`MemberDiscountPolicy.java`・`CouponDiscountPolicy.java`・`Order.java`・`OrderRepository.java`・`InMemoryOrderRepository.java`・`OrderApplicationService.java`・`Main.java`・`RejectionExamples.java`の25個です。

## 期待する結果

```text
内容を確定して保存：order-001、CONTENT_CONFIRMED
決済の依頼：カード（card-0001）、1700円
初回の結果：false
保存されている注文：order-001、AMOUNT_FIXED、お支払い1700円
決済の依頼：カード（card-0001）、1700円
再試行の結果：true
保存されている注文：order-001、PAID、お支払い1700円
```

拒否する操作の確認は、同じディレクトリで次のコマンドを実行します（コンパイルは上と同じです）。

```powershell
java -cp out RejectionExamples
```

```text
内容の確定後の追加：確定後は料理を追加できません
固定前の支払額：支払額はまだ固定されていません
固定前の再試行：支払額を固定した未払いの注文だけ支払えます
決済の依頼：カード（card-0001）、1700円
失敗後に別の割引：支払額はすでに固定されています
注文の状態：AMOUNT_FIXED、お支払い1700円
```

## この段階で成り立つルール

- 本編の`chapter09/end`のルールのうち、メニュー項目・明細・`Money`・`Quantity`・注文ID、支払い方法と決済の窓口、割引方針、注文の保存先は、そのまま成り立ちます。
- 注文の状態は、`DRAFT`（作成中）→`CONTENT_CONFIRMED`（料理・数量を確定済み。支払額はまだ決めていない）→`AMOUNT_FIXED`（支払額を固定済み・未払い）→`PAID`（支払い済み）と進みます。本編の`CONFIRMED`を、意味がはっきりする2つの状態に分けています。
- 注文の`confirmContent`は、料理・数量を確定します。確定後は料理を追加できません。支払額はまだ固定しないので、`getAmountDue`は拒否します。
- 注文の`fixAmountDue`は、内容を確定した注文だけに使えます。割引額の計算を割引方針に依頼し、合計を超えないことを確かめてから、支払額を固定します。一度固定した支払額は、別の割引で選び直せません。
- 注文の`pay`は、支払額を固定した未払いの注文だけに使えます。決済に失敗したら`AMOUNT_FIXED`のままで、固定した金額で再試行できます。
- `OrderApplicationService`の`confirmOrder`は、内容を確定して保存し、いったん終わります。`fixAmountAndPay`は、注文を取得して支払額を固定し、未払いで保存してから支払います。`retryPayment`は、割引を計算し直さずに、固定した金額で支払いをやり直します。
- 支払い済みの注文には、割引の適用も再試行もできません。どちらも決済を依頼する前に拒否します。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter09-advanced:run
.\gradlew.bat :chapter09-advanced:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter09-advanced:test
```
