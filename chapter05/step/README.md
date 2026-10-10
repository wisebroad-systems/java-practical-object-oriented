# 第5章 step：継承で提供の違いを表す

第5章の途中段階のコードです。`Order`を抽象クラスにし、店内の注文（`DineInOrder`）とテイクアウトの注文（`TakeoutOrder`）をサブクラスとして表しました。サブクラスが、テーブル番号・受取番号の検証と、提供先の案内を担当します。注文の明細・合計・確定・支払いの機能は、`Order`から引き継いでいます。

## 実行する

このディレクトリ（`chapter05/step`）で、次のコマンドを実行します。

```powershell
javac -encoding UTF-8 -d out src/main/java/*.java
java -cp out Main
```

コンパイルされるファイルは、`MenuItem.java`・`OrderItem.java`・`OrderStatus.java`・`PaymentMethod.java`・`CashPayment.java`・`CardPayment.java`・`QrPayment.java`・`Order.java`・`DineInOrder.java`・`TakeoutOrder.java`・`Main.java`・`RejectionExamples.java`の12個です。

## 期待する結果

```text
お釣り：1000円
店内：テーブル3へ配膳、PAID
テイクアウト：受取番号12で受け渡し、PAID
```

拒否する操作の確認は、同じディレクトリで次のコマンドを実行します（コンパイルは上と同じです）。

```powershell
java -cp out RejectionExamples
```

```text
テーブル番号0：テーブル番号は1以上にしてください
受取番号0：受取番号は1以上にしてください
```

## この段階で成り立つルール

- メニュー項目・明細・注文の確定のルールは、第4章と同じです。
- 注文の状態は`DRAFT`・`CONFIRMED`・`PAID`のどれかで、支払えるのは`CONFIRMED`の注文だけです。
- 支払い方法（現金・カード・QRコード）は`PaymentMethod`で共通に扱います。
- 店内の注文はテーブル番号、テイクアウトの注文は受取番号を持ちます。どちらも1以上で、作成時に検証します。
- `Order`は抽象クラスなので、店内かテイクアウトかを決めずに注文を作ることはできません。
- 提供の仕方と支払い方法は独立しています。店内でもテイクアウトでも、どの支払い方法でも支払えます。

## 意図的に残している問題

この段階は、次の段階や章で直す前の状態です。次の点は、不具合ではなく、本文で考えるための題材として残しています。

- この段階は、同じ要求をコンポジションで表した`end`と比べるための、もうひとつの案です。継承の案も要求を満たしており、不具合があるわけではありません。どちらを選ぶかは、本文で考えます。

## 任意：Gradleで実行する

リポジトリのルートで、次のコマンドを実行します。Windowsで日本語が文字化けする場合は、ルートの[README](../../README.md#windowsで日本語が文字化けする場合)を参照してください。

```powershell
.\gradlew.bat :chapter05-step:run
.\gradlew.bat :chapter05-step:run -PmainClass=RejectionExamples
.\gradlew.bat :chapter05-step:test
```
