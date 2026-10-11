# 第8章 外部の仕組みとの境界を作る

この章で考える問いは、次のひとつです。

> 保存先・決済先を変える都合を、どこへ閉じ込めればよいでしょうか？

- 本文：[第8章　外部の仕組みとの境界を作る](https://zenn.dev/wb_chrono/books/java-practical-object-oriented/viewer/ch08-boundaries)

## 段階

| 段階 | 役割 |
|---|---|
| [start](start/README.md) | 第7章の終了時点と同じ。保存も外部の決済もまだない |
| [step01](step01/README.md) | 注文IDを導入し、呼び出し側が`Map`で注文を保存・取得する |
| [step02](step02/README.md) | 保存を注文の保存先（`OrderRepository`）に任せる |
| [step03](step03/README.md) | カード・QRコードが、決済の代替実装を中で直接作って使う |
| [end](end/README.md) | 決済の窓口（`PaymentGateway`）を外から渡す。後の章はこの構成を引き継ぐ |

## 前後の章とのつながり

- 前：[第7章](../chapter07/README.md)では、割引のように変わりやすいルールを、注文から割引方針へ分けました。
- 次：[第9章](../chapter09/README.md)では、確定・保存・支払いの進行を、Serviceにまとめます。
