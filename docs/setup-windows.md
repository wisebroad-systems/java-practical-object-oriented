# Windowsでの準備

本書のサンプルコードを、Windows 11（x64）で実行するための準備です。必要なのは、Java 25の開発環境（JDK）だけです。コマンドは、Windowsに標準で入っているPowerShell（Windows PowerShell）で実行します。PowerShell 7を使っている場合も、同じ手順で動きます。GradleやGit、JUnitを別に入れる必要はありません。

すでに準備できている場合は、最後の「準備ができたかを確かめる」だけを行ってください。

## 1. JDK（Eclipse Temurin 25）を入れる

本書では、Eclipse TemurinのJDK 25で動作を確かめています。

1. [Adoptiumのダウンロードページ](https://adoptium.net/temurin/releases/)で、OSに「Windows」、アーキテクチャに「x64」、パッケージの種類に「JDK」、バージョンに「25」を選び、MSIのインストーラーをダウンロードします。「JRE」ではなく「JDK」を選んでください。JREにはコンパイラー（`javac`）が入っていません。
2. ダウンロードしたMSIを実行します。途中の設定で、次の2つを有効にしてください。
   - PATHへの追加（`java`と`javac`のコマンドを使えるようにする設定）
   - JAVA_HOMEの設定（GradleなどがJDKの場所を知るための設定）

詳しい手順は、[TemurinのWindows向けインストール案内](https://adoptium.net/installation/windows/)を参照してください。

## 2. 準備ができたかを確かめる

PowerShellを新しく開き、次のコマンドを実行します。設定を反映させるため、インストールの前から開いていたウィンドウは使わないでください。

| 確かめること | コマンド | 正しい状態 |
|---|---|---|
| Javaの実行環境 | `java -version` | `25`と`Temurin`が表示される |
| コンパイラー | `javac -version` | `javac 25`で始まる |
| JDKの場所 | `$env:JAVA_HOME` | Temurin 25のJDKのディレクトリが表示される |

ここまでできたら、[README](../README.md#最初の実行)の「最初の実行」に進んでください。

## 任意：PowerShell 7.6を入れる

すべての段階をまとめて検証するスクリプト（`scripts/verify.ps1`）を使う場合だけ、PowerShell 7.6が必要です。本書の手順を進めるだけなら、入れる必要はありません。

[MicrosoftのPowerShellのインストール案内](https://learn.microsoft.com/ja-jp/powershell/scripting/install/install-powershell-on-windows?view=powershell-7.6)に従って入れてください（MSIのインストーラーを使う方法があります）。PowerShell 7は、Windowsに標準で入っているWindows PowerShell（バージョン5.1）とは別のもので、両方が並んで存在します。入れた後は、スタートメニューから「PowerShell 7」を開き、`$PSVersionTable.PSVersion`が`7.6`で始まることを確かめてから、`pwsh -File scripts/verify.ps1`を実行します。

## 困ったときは

### `java`や`javac`が見つからない、または25以外が表示される

- **確かめること**：`Get-Command java`と`Get-Command javac`を実行し、表示されたファイルの場所を見ます。
- **原因の見分け方**：何も表示されない場合は、PATHが設定されていません。Temurin 25以外の場所（別のバージョンのJavaなど）が表示される場合は、別のJavaが先に見つかっています。
- **直し方**：TemurinのMSIを再度実行してPATHの設定を有効にするか、Windowsの「環境変数」の設定で、Temurin 25の`bin`ディレクトリがPATHの先頭に来るようにします。直したら、新しいPowerShellを開いて、もう一度確かめます。

### 「ファイルが見つからない」「メインクラスが見つからない」と表示される

- **確かめること**：今いるディレクトリが、実行したい段階（たとえば`chapter01/start`）になっているかを、`Get-Location`で確かめます。
- **原因の見分け方**：リポジトリのルートなど、別のディレクトリにいると、`src/main/java/*.java`や、コンパイルした結果（`out`）が見つかりません。
- **直し方**：`cd chapter01/start`のように段階のディレクトリへ移動してから、コンパイルと実行をやり直します。

### Gradleで実行したときに日本語が文字化けする

本書の標準の手順（`javac`と`java`）では起きません。Gradleで実行した場合だけ起きることがあります。同じPowerShellのウィンドウで次の1行を実行してから、Gradleのコマンドを実行してください。

```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
```

### ソースを変更したら文字化けした、コンパイルできなくなった

ソースファイルは、UTF-8で保存してください。メモ帳を含め、最近のエディターの多くはUTF-8で保存できます。保存した後は、もう一度コンパイルしてから実行します。

### Gradleのコマンドが始まらない

Gradle Wrapper（`gradlew.bat`）は、初めて使うときにGradle本体をダウンロードします。インターネットに接続できる状態で実行してください。2回目からは、ダウンロード済みのものを使います。
