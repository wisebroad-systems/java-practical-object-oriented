import com.diffplug.spotless.LineEnding

plugins {
    id("com.diffplug.spotless") version "8.10.3"
}

// 全段階のJava（本体・テスト）とKotlin DSLの整形を、ルートでまとめて管理する（D-69・D-70）。
spotless {
    encoding = Charsets.UTF_8
    lineEndings = LineEnding.UNIX

    java {
        target("chapter*/*/src/**/*.java")
        googleJavaFormat("1.36.1").aosp()
    }

    kotlinGradle {
        target("*.gradle.kts", "chapter*/*/*.gradle.kts")
        ktfmt("0.64").kotlinlangStyle()
    }
}
