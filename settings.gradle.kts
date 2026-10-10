rootProject.name = "java-practical-object-oriented"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

// 公開する段階の一覧。Gradleのプロジェクト名と、実際のディレクトリを対応付ける。
// 段階を公開するときに、ここへ追加する。
val stages =
    mapOf(
        "chapter01-start" to "chapter01/start",
        "chapter01-end" to "chapter01/end",
        "chapter02-start" to "chapter02/start",
        "chapter02-step" to "chapter02/step",
        "chapter02-end" to "chapter02/end",
        "chapter03-start" to "chapter03/start",
        "chapter03-step" to "chapter03/step",
        "chapter03-end" to "chapter03/end",
        "chapter04-start" to "chapter04/start",
        "chapter04-step" to "chapter04/step",
        "chapter04-end" to "chapter04/end",
        "chapter05-start" to "chapter05/start",
        "chapter05-step" to "chapter05/step",
        "chapter05-end" to "chapter05/end",
        "chapter06-start" to "chapter06/start",
        "chapter06-step" to "chapter06/step",
        "chapter06-end" to "chapter06/end",
    )

stages.forEach { (name, directory) ->
    include(name)
    project(":$name").projectDir = file(directory)
}
