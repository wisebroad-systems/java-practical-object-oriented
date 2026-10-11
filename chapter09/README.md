# 第9章 Serviceには何を書くべきか？

この章で考える問いは、次のひとつです。

> Serviceは「何でも処理する場所」なのでしょうか？

- 本文：[第9章　Serviceには何を書くべきか？](https://zenn.dev/wb_chrono/books/java-practical-object-oriented/viewer/ch09-service)

## 段階

| 段階 | 役割 |
|---|---|
| [start](start/README.md) | 第8章の終了時点と同じ。確定・保存・支払いの進め方を`Main`が組み立てる |
| [step](step/README.md) | 進め方を`OrderApplicationService`の初回の操作と再試行の操作に移す |
| [end](end/README.md) | 初回と再試行で共通する支払いの部分をまとめる。後の章はこの構成を引き継ぐ |
| [advanced](advanced/README.md) | 章末のAdvanced課題「確定後にクーポンが提示されたら？」の解説例（任意） |

## 前後の章とのつながり

- 前：[第8章](../chapter08/README.md)では、保存先と決済先を、注文の保存先（`OrderRepository`）と決済の窓口（`PaymentGateway`）の裏へ閉じ込めました。
- 次：[第10章](https://zenn.dev/wb_chrono/books/java-practical-object-oriented/viewer/ch10-good-design)では、ここまでの設計の判断を振り返ります。
