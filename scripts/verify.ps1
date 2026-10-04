#Requires -Version 7.6
<#
.SYNOPSIS
    公開しているすべての段階を検証する。

.DESCRIPTION
    WindowsとUbuntuで共通に使う検証スクリプト（PowerShell 7.6以降）。
    リポジトリのルートで実行する。

    1. java・javacが25であることと、実行環境を記録する。
    2. 各段階を、本文と同じjavac/javaのコマンドでコンパイル・実行し、期待する出力と比べる。
    3. 各段階を、Gradle Wrapper経由でも実行し、期待する出力と比べる。
    4. すべての段階のJUnitのテストと、Spotlessの整形チェックを実行する。

    段階の一覧と期待する出力は、scripts/expected/<Gradleのプロジェクト名>/<実行クラス>.txt で管理する。
    一つでも失敗すると、終了コードを1にする。結果は build/verify/ にログとして保存する。

.EXAMPLE
    pwsh -File scripts/verify.ps1
#>
[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

# 子プロセスの日本語の出力をUTF-8として受け取る（WindowsのコンソールはShift_JISが既定のため）。
$previousOutputEncoding = [Console]::OutputEncoding
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$gradlew = if ($IsWindows) { Join-Path $root 'gradlew.bat' } else { Join-Path $root 'gradlew' }
$logDir = Join-Path $root 'build/verify'
New-Item -ItemType Directory -Force -Path $logDir | Out-Null
$logFile = Join-Path $logDir ("verify-{0}.log" -f (Get-Date -Format 'yyyyMMdd-HHmmss'))
$results = [System.Collections.Generic.List[pscustomobject]]::new()

function Write-Log([string] $message) {
    Write-Host $message
    Add-Content -Path $logFile -Value $message -Encoding utf8
}

function Add-Result([string] $stage, [string] $check, [bool] $passed, [string] $detail = '') {
    $results.Add([pscustomobject]@{ Stage = $stage; Check = $check; Passed = $passed; Detail = $detail })
    $mark = if ($passed) { 'OK  ' } else { 'FAIL' }
    Write-Log ("[{0}] {1} {2} {3}" -f $mark, $stage, $check, $detail)
}

function ConvertTo-Normalized([object] $text) {
    # 改行コードの違い（CRLFとLF）と、末尾の改行だけを吸収する。
    return ((($text | Out-String) -replace "`r`n", "`n").TrimEnd("`n"))
}

function Invoke-Native([string] $file, [string[]] $arguments) {
    $output = & $file @arguments 2>&1
    return [pscustomobject]@{ ExitCode = $LASTEXITCODE; Output = (ConvertTo-Normalized $output) }
}

try {
    # 1. 環境の記録と確認
    Write-Log "== 環境"
    Write-Log ("日時: {0}" -f (Get-Date -Format 'yyyy-MM-dd HH:mm:ss K'))
    $commit = if (Get-Command git -ErrorAction SilentlyContinue) { (git rev-parse --short HEAD 2>$null) } else { $null }
    Write-Log ("コミット: {0}" -f ($commit ?? '不明'))
    Write-Log ("OS: {0}" -f [System.Runtime.InteropServices.RuntimeInformation]::OSDescription)
    Write-Log ("PowerShell: {0}" -f $PSVersionTable.PSVersion)
    Write-Log ("JAVA_HOME: {0}" -f ($env:JAVA_HOME ?? '未設定'))
    $javaVersion = Invoke-Native 'java' @('-version')
    $javacVersion = Invoke-Native 'javac' @('-version')
    Write-Log $javaVersion.Output
    Write-Log $javacVersion.Output
    Add-Result 'environment' 'java 25' ($javaVersion.Output -match 'version "25[."]') ''
    Add-Result 'environment' 'javac 25' ($javacVersion.Output -match '^javac 25(\.|$)') ''

    # 段階の一覧：期待する出力のディレクトリと、settings.gradle.ktsの登録が一致していることを確かめる。
    $expectedRoot = Join-Path $root 'scripts/expected'
    $stages = Get-ChildItem -Directory $expectedRoot | Sort-Object Name | ForEach-Object { $_.Name }
    $settings = Get-Content (Join-Path $root 'settings.gradle.kts') -Raw
    $registered = [regex]::Matches($settings, '"(chapter[^"]+)" to "') | ForEach-Object { $_.Groups[1].Value } | Sort-Object
    Add-Result 'stages' 'settings.gradle.ktsと期待値の一致' ($null -eq (Compare-Object $stages $registered)) ("{0}段階" -f $stages.Count)

    foreach ($stage in $stages) {
        $stageDir = Join-Path $root ($stage -replace '-(?=[^-]+$)', '/')
        $mainClasses = Get-ChildItem -File (Join-Path $expectedRoot $stage) -Filter '*.txt' | ForEach-Object { $_.BaseName }

        # 2. 本文と同じjavac/javaのコマンド（以前の生成物を使わないよう、新しい出力先を使う）
        $outDir = Join-Path ([System.IO.Path]::GetTempPath()) ("verify-{0}-{1}" -f $stage, [guid]::NewGuid())
        Push-Location $stageDir
        try {
            # Windowsでは読者と同じく*.javaをそのまま渡す（javacが展開する）。
            # それ以外のOSでは、変数で渡した*.javaをPowerShellもjavacも展開しないため、ファイルの一覧に展開する。
            $sources = if ($IsWindows) {
                @('src/main/java/*.java')
            } else {
                @(Get-ChildItem -File 'src/main/java' -Filter '*.java' | ForEach-Object { "src/main/java/$($_.Name)" })
            }
            $compile = Invoke-Native 'javac' (@('-encoding', 'UTF-8', '-d', $outDir) + $sources)
        } finally {
            Pop-Location
        }
        Add-Result $stage 'javac' ($compile.ExitCode -eq 0) $(if ($compile.ExitCode -ne 0) { $compile.Output } else { '' })

        foreach ($mainClass in $mainClasses) {
            $expected = ConvertTo-Normalized (Get-Content (Join-Path $expectedRoot "$stage/$mainClass.txt") -Raw -Encoding utf8)
            if ($compile.ExitCode -ne 0) {
                Add-Result $stage "java $mainClass" $false 'コンパイルに失敗したため実行しない'
                continue
            }
            # パイプで受け取るため、出力の文字コードをUTF-8に揃える（読者の手順では不要）。
            $run = Invoke-Native 'java' @('-Dstdout.encoding=UTF-8', '-Dstderr.encoding=UTF-8', '-cp', $outDir, $mainClass)
            Add-Result $stage "java $mainClass" (($run.ExitCode -eq 0) -and ($run.Output -ceq $expected)) $(if ($run.Output -cne $expected) { "出力が期待と異なる:`n$($run.Output)" } else { '' })
        }
        Remove-Item -Recurse -Force $outDir -ErrorAction SilentlyContinue

        # 3. Gradle Wrapper経由の実行
        foreach ($mainClass in $mainClasses) {
            $expected = ConvertTo-Normalized (Get-Content (Join-Path $expectedRoot "$stage/$mainClass.txt") -Raw -Encoding utf8)
            $gradleRun = Invoke-Native $gradlew @('-q', '--console=plain', ":${stage}:run", "-PmainClass=$mainClass")
            Add-Result $stage "gradle run $mainClass" (($gradleRun.ExitCode -eq 0) -and ($gradleRun.Output -ceq $expected)) $(if ($gradleRun.Output -cne $expected) { "出力が期待と異なる:`n$($gradleRun.Output)" } else { '' })
        }
    }

    # 4. JUnitと整形チェック（全段階）
    Write-Log "== Gradle: test spotlessCheck"
    $gradleVersion = Invoke-Native $gradlew @('--version')
    Write-Log $gradleVersion.Output
    $check = Invoke-Native $gradlew @('--console=plain', 'test', 'spotlessCheck')
    Add-Content -Path $logFile -Value $check.Output -Encoding utf8
    Add-Result 'all' 'JUnit・spotlessCheck' ($check.ExitCode -eq 0) $(if ($check.ExitCode -ne 0) { 'Gradleの出力はログを参照' } else { '' })
} finally {
    [Console]::OutputEncoding = $previousOutputEncoding
}

$failed = @($results | Where-Object { -not $_.Passed })
Write-Log ''
Write-Log ("== 結果: {0}件中 {1}件成功、{2}件失敗" -f $results.Count, ($results.Count - $failed.Count), $failed.Count)
Write-Log ("ログ: {0}" -f $logFile)
exit $(if ($failed.Count -eq 0) { 0 } else { 1 })
