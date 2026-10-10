# 第5章 start：第4章の終了時点

第5章の開始時点のコードです。第4章の終了時点（`chapter04/end`）と同じ内容です。

## 実行する

このディレクトリ（`chapter05/start`）で、次のコマンドを実行します。

```powershell
javac -encoding UTF-8 -d out src/main/java/*.java
java -cp out Main
```

コンパイルされるファイルは、`MenuItem.java`・`OrderItem.java`・`OrderStatus.java`・`PaymentMethod.java`・`CashPayment.java`・`CardPayment.java`・`QrPayment.java`・`Order.java`・`Main.java`・`RejectionExamples.java`の10個です。

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

- メニュー項目・明細・注文の確定のルールは、第4章と同じです。
- 注文の状態は`DRAFT`・`CONFIRMED`・`PAID`のどれかで、支払えるのは`CONFIRMED`の注文だけです。
- 支払い方法（現金・カード・QRコード）は`PaymentMethod`で共通に扱います。

## 意図的に残している問題

この段階は、次の段階や章で直す前の状態です。次の点は、不具合ではなく、本文で考えるための題材として残しています。

- 料理をどこへ提供するか（店内かテイクアウトか）を扱えません。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter05-start:run
.\gradlew.bat :chapter05-start:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter05-start:test
```
