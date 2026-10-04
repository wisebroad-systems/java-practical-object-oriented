# Javaで学ぶ実践オブジェクト指向 サンプルコード

Zennの本『Javaで学ぶ実践オブジェクト指向』のサンプルコードです。ひとつのレストランの注文システムを、章を追うごとに少しずつ育てながら、オブジェクト指向で設計を考える力を身につけることを目指します。

- 本：準備中（本文は未公開です。公開したらリンクを追加します）
- ライセンス：[MIT License](LICENSE)（Copyright (c) 2026 chrono1119）

各章のコードは、`start`（開始時点）・`step`（途中段階）・`end`（終了時点）のディレクトリに分かれています。どの段階も、ほかの段階に頼らずに単独でコンパイル・実行できます。

## 必要な環境

- Java 25（JDK）。本書ではEclipse Temurin 25で確認しています。
- 基準とする環境は、Windows 11とPowerShell 7.6です。コマンドはbashなどでも同じです。

次のコマンドで、`java`と`javac`がどちらも25であることを確かめてください。

```powershell
java -version
javac -version
```

Windowsでの詳しい準備手順は、準備中です。

## 最初の実行

1. このページの「Code」→「Download ZIP」からソースを取得し、展開します（Gitの操作は必要ありません）。
2. `chapter01/start`のディレクトリへ移動します。
3. 次のコマンドでコンパイルし、実行します。

```powershell
javac -encoding UTF-8 -d out src/main/java/*.java
java -cp out Main
```

次のように表示されれば成功です。

```text
会計：2000円
注文確認：2000円
```

`src/main/java/*.java`は、そのディレクトリにあるすべてのJavaファイルを指します。別の段階を試すときは、その段階のディレクトリへ移動して、同じコマンドを実行します。全章のファイルをまとめてコンパイルすることはできません（段階ごとに同じ名前のクラスがあるため）。

## 章と段階の一覧

| 章 | 段階 | 内容 | 本文 |
|---|---|---|---|
| [第1章](chapter01/README.md) 「クラスを作る」だけがオブジェクト指向ではない | [start](chapter01/start/README.md) | 会計表示と注文確認で、同じ合計の計算が重複している | 未公開 |

ほかの章・段階は準備中です。公開した段階から、この一覧に追加します。

## 任意：Gradle・テスト・整形

本書の手順は`javac`と`java`だけで完結します。以下は、Gradleを使って実行したい人向けの任意の方法です。Gradle本体のインストールは必要ありません（同梱のGradle Wrapperが、必要なバージョンを取得します。初回はネットワークが必要です）。

リポジトリのルートで実行します。WindowsのPowerShellでは`.\gradlew.bat`、bashなどでは`./gradlew`を使います。

| やりたいこと | コマンド（PowerShell） |
|---|---|
| ある段階を実行する | `.\gradlew.bat :chapter01-start:run` |
| ある段階のテストを実行する | `.\gradlew.bat :chapter01-start:test` |
| すべての段階のテストを実行する | `.\gradlew.bat test` |
| 整形を確認する | `.\gradlew.bat spotlessCheck` |
| 整形する | `.\gradlew.bat spotlessApply` |

テストはJUnit 6、整形はSpotless（Javaはgoogle-java-formatのAOSPスタイル、Gradleの設定はktfmt）を使っています。

## 動作確認の範囲

動作確認の対象は、Java 25・Windows 11の実機と、GitHub ActionsのUbuntu 24.04です。いずれも準備中で、検証の結果は公開時にここへ記載します。
