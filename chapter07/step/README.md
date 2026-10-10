# 第7章 step：割引を注文の中で計算する

第7章の途中段階のコードです。会員割引とクーポン割引を加えました。確定時に割引の種類を文字列（`"none"`・`"member"`・`"coupon"`）で指定すると、注文（`Order`）の中の`switch`文で割引額を計算し、支払額を固定します。

## 実行する

このディレクトリ（`chapter07/step`）で、次のコマンドを実行します。

```powershell
javac -encoding UTF-8 -d out src/main/java/*.java
java -cp out Main
```

コンパイルされるファイルは、`Money.java`・`Quantity.java`・`MenuItem.java`・`OrderItem.java`・`OrderStatus.java`・`PaymentMethod.java`・`CashPayment.java`・`CardPayment.java`・`QrPayment.java`・`FulfillmentMethod.java`・`DineInFulfillment.java`・`TakeoutFulfillment.java`・`Order.java`・`Main.java`・`RejectionExamples.java`の15個です。

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
対応していない割引：対応していない割引です：birthday
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
- 割引なしの`confirm()`は、`confirm("none")`と同じです。対応していない割引の種類と`null`は拒否し、注文は未確定のままです。

## 意図的に残している問題

この段階は、次の段階や章で直す前の状態です。次の点は、不具合ではなく、本文で考えるための題材として残しています。

- 割引の計算式が`Order`の中にあります。割引の種類が増えたり、計算のルールが変わったりするたびに、注文の明細・確定・支払いを扱う`Order`を変更することになります。
- 割引の種類を文字列で指定するので、つづりを間違えても、実行するまで分かりません。
- `confirm(String)`は、この段階だけのメソッドです。`end`では`confirm(DiscountPolicy)`に置き換えます。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter07-step:run
.\gradlew.bat :chapter07-step:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter07-step:test
```
