# 第7章 end：割引のルールを割引方針に分ける

第7章の終了時点のコードです。割引額の計算を、割引方針（`DiscountPolicy`）の実装（`NoDiscountPolicy`・`MemberDiscountPolicy`・`CouponDiscountPolicy`）へ移しました。呼び出し側が割引方針を一つ選んで`confirm(DiscountPolicy)`に渡し、注文は割引方針から割引額を受け取って、支払額を固定します。

## 実行する

このディレクトリ（`chapter07/end`）で、次のコマンドを実行します。

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

- メニュー項目・明細・注文の確定のルールは、第4章と同じです。
- 注文の状態は`DRAFT`・`CONFIRMED`・`PAID`のどれかで、支払えるのは`CONFIRMED`の注文だけです。
- 支払い方法（現金・カード・QRコード）は`PaymentMethod`で共通に扱います。
- 注文は作成時に提供方法（店内・テイクアウト）を受け取り、案内をその提供方法に任せます。
- 金額は`Money`、数量は`Quantity`で表します。負の金額や、0・負の数量は作れません。
- 割引は、割引なし・会員割引・クーポン割引から一つを選び、注文の確定時に指定します。併用はできません。
- 会員割引は明細合計の10％で、1円未満は切り捨てます。クーポン割引は300円で、明細合計が300円より少なければ明細合計と同じ額です。
- 注文は、確定時に支払額（明細合計から割引額を引いた金額）を固定します。支払額は`getAmountDue()`で取得でき、確定前は取得できません。合計（`totalPrice()`）は割引前の明細合計のままです。
- 支払いは、固定した支払額で行います。支払額が0円なら、支払い方法に依頼せずに支払い済みにします。
- `Money.subtract`は、引いた結果が負になる場合を拒否します。
- 割引方針は、割引前の明細合計を受け取り、割引額を返します。注文は、割引方針の種類を知りません。
- 注文は、割引額が`null`のときや合計を超えるときは確定を拒否し、未確定のままにします。割引方針の`null`も拒否します。
- 割引なしの`confirm()`は、`confirm(new NoDiscountPolicy())`と同じです。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter07-end:run
.\gradlew.bat :chapter07-end:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter07-end:test
```
