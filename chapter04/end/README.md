# 第4章 end：支払い方法を共通の要求で扱う

第4章の終了時点のコードです。支払い方法の共通の要求を`PaymentMethod`として表し、現金（`CashPayment`）・カード（`CardPayment`）・QRコード（`QrPayment`）がそれぞれの方法で支払います。注文は`pay(PaymentMethod)`で、どの方法かを知らずに支払いを依頼します。

## 実行する

このディレクトリ（`chapter04/end`）で、次のコマンドを実行します。

```powershell
javac -encoding UTF-8 -d out src/main/java/*.java
java -cp out Main
```

コンパイルされるファイルは、`MenuItem.java`・`OrderItem.java`・`OrderStatus.java`・`Order.java`・`PaymentMethod.java`・`CashPayment.java`・`CardPayment.java`・`QrPayment.java`・`Main.java`・`RejectionExamples.java`の10個です。

## 期待する結果

```text
お釣り：1000円
現金：PAID
カード：PAID
QRコード：PAID
無料の注文：PAID
```

拒否する操作の確認は、同じディレクトリで次のコマンドを実行します（コンパイルは上と同じです）。

```powershell
java -cp out RejectionExamples
```

```text
未確定の注文の支払い：確定済み・未払いの注文だけ支払えます
状態：DRAFT
支払い済みの注文の再支払い：確定済み・未払いの注文だけ支払えます
状態：PAID
```

## この段階で成り立つルール

- メニュー項目は名前と価格を持ちます。価格は0円以上で、負の価格は作成時に拒否します。
- 明細の数量は1以上です。明細は、料理の価格と数量から小計を求めます。
- 料理がない注文は確定できません。確定済みの注文は、もう一度確定できません。
- 確定後は料理を追加できません。`getItems()`が返す一覧は変更できません。
- 注文の状態は、`DRAFT`（未確定）・`CONFIRMED`（確定済み・未払い）・`PAID`（支払い済み）のどれかです。
- 支払えるのは`CONFIRMED`の注文だけです。未確定の注文や支払い済みの注文は、支払いを拒否します。
- 支払う金額が0円の注文は、支払いをせずに会計を完了し、`PAID`になります。
- 現金は、預かり金額が足りれば支払いが成功し、お釣りを求めます。足りなければ失敗し、注文は`CONFIRMED`のままです。お釣りは、支払いが成功した後だけ取得できます。
- カードの識別情報とQRコードの決済コードは、空にできません。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter04-end:run
.\gradlew.bat :chapter04-end:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter04-end:test
```
