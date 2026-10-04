# Javaで学ぶ実践オブジェクト指向 サンプルコード

Zennの本『Javaで学ぶ実践オブジェクト指向』のサンプルコードです。ひとつのレストランの注文システムを、章を追うごとに少しずつ育てながら、オブジェクト指向で設計を考える力を身につけることを目指します。

- 本：準備中（本文は未公開です。公開したらリンクを追加します）
- ライセンス：[MIT License](LICENSE)（Copyright (c) 2026 chrono1119）

各章のコードは、`start`（開始時点）・`step`（途中段階）・`end`（終了時点）のディレクトリに分かれています。どの段階も、ほかの段階に頼らずに単独でコンパイル・実行できます。

## 必要な環境

- Java 25（JDK）。本書ではEclipse Temurin 25で確認しています。
- 基準とする環境は、Windows 11と、Windowsに標準で入っているPowerShell（Windows PowerShell）です。PowerShell 7でも、bashなどでも、同じコマンドで動きます。

次のコマンドで、`java`と`javac`がどちらも25であることを確かめてください。

```powershell
java -version
javac -version
```

Windowsでの詳しい準備手順は、[docs/setup-windows.md](docs/setup-windows.md)にあります。

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
| | [end](chapter01/end/README.md) | 合計の計算を注文に任せる | 未公開 |
| [第2章](chapter02/README.md) オブジェクトに自分の状態を守らせる | [start](chapter02/start/README.md) | 第1章の終了時点 | 未公開 |
| | [step](chapter02/step/README.md) | 追加と確定の操作を導入する（一覧からの抜け道が残る） | 未公開 |
| | [end](chapter02/end/README.md) | 一覧を変更できない形で公開する | 未公開 |
| [第3章](chapter03/README.md) オブジェクト同士に仕事を分担させる | [start](chapter03/start/README.md) | 第2章の終了時点 | 未公開 |
| | [step](chapter03/step/README.md) | 明細と数量を導入する（小計はまだ注文が計算する） | 未公開 |
| | [end](chapter03/end/README.md) | 小計の計算を明細に任せる | 未公開 |

第4章以降は準備中です。公開した段階から、この一覧に追加します。

## 任意：Gradle・テスト・整形

本書の手順は`javac`と`java`だけで完結します。以下は、Gradleを使って実行したい人向けの任意の方法です。Gradle本体のインストールは必要ありません（同梱のGradle Wrapperが、必要なバージョンを取得します。初回はネットワークが必要です）。

リポジトリのルートで実行します。WindowsのPowerShellでは`.\gradlew.bat`、bashなどでは`./gradlew`を使います。

| やりたいこと | コマンド（PowerShell） |
|---|---|
| ある段階を実行する | `.\gradlew.bat :chapter01-start:run` |
| 拒否する操作の確認を実行する | `.\gradlew.bat :chapter02-end:run -PmainClass=RejectionExamples` |
| ある段階のテストを実行する | `.\gradlew.bat :chapter01-start:test` |
| すべての段階のテストを実行する | `.\gradlew.bat test` |
| 整形を確認する | `.\gradlew.bat spotlessCheck` |
| 整形する | `.\gradlew.bat spotlessApply` |

テストはJUnit 6、整形はSpotless（Javaはgoogle-java-formatのAOSPスタイル、Gradleの設定はktfmt）を使っています。

### Windowsで日本語が文字化けする場合

PowerShellでGradleから実行すると、実行結果の日本語が文字化けすることがあります。GradleがUTF-8で画面に出力するのに対し、日本語版Windowsのコンソールは、既定でShift_JIS（コードページ932）として表示するためです。その場合は、同じPowerShellのウィンドウで次の1行を実行してから、Gradleのコマンドを実行してください。

```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
```

この設定は、そのウィンドウを閉じるまで有効です。本書の標準の手順（`javac`と`java`）は、この設定をしなくても正しく表示されます。

### すべての段階をまとめて検証する

`scripts/verify.ps1`は、公開しているすべての段階について、`javac`・`java`での実行結果、Gradleでの実行結果、テスト、整形を確かめるスクリプトです。このスクリプトだけは、PowerShell 7.6以降が必要です（Windowsに標準で入っているWindows PowerShell 5.1では動きません）。PowerShell 7.6の入れ方は、[docs/setup-windows.md](docs/setup-windows.md#任意powershell-76を入れる)にあります。リポジトリのルートから実行します。

```powershell
pwsh -File scripts/verify.ps1
```

結果は`build/verify/`にログとして保存されます。期待する実行結果は`scripts/expected/`にあります。

## 動作確認の範囲

動作確認の対象は、Java 25と、次の2つの環境です。

| 環境 | 確認の方法 | 状況 |
|---|---|---|
| Windows 11（x64）・PowerShell | 実機で`scripts/verify.ps1`を実行して確認 | 第1〜3章の全段階で確認済み（2026-10-04）。新しい段階は、公開前に確認します |
| Ubuntu 24.04（GitHub Actions） | `scripts/verify.ps1`で、pushのたびに全段階を確認 | [![verify](https://github.com/wisebroad-systems/java-practical-object-oriented/actions/workflows/verify.yml/badge.svg)](https://github.com/wisebroad-systems/java-practical-object-oriented/actions/workflows/verify.yml) |

これ以外の環境（macOS、ほかのバージョンのJavaなど）での動作は保証していません。
