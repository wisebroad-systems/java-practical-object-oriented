plugins {
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

application {
    mainClass.set(providers.gradleProperty("mainClass").orElse("Main"))
}

// Gradleから実行すると標準出力がコンソールではなくなり、WindowsではOSの既定の文字コード（MS932）で
// 書き出されて日本語が文字化けする。Gradleが受け取る文字コードに合わせてUTF-8を指定する。
tasks.named<JavaExec>("run") { jvmArgs("-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8") }

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.test {
    useJUnitPlatform()
}
