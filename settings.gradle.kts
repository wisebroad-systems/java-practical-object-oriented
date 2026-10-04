rootProject.name = "java-practical-object-oriented"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

// 公開する段階の一覧。Gradleのプロジェクト名と、実際のディレクトリを対応付ける。
// 段階を公開するときに、ここへ追加する。
val stages = mapOf("chapter01-start" to "chapter01/start")

stages.forEach { (name, directory) ->
    include(name)
    project(":$name").projectDir = file(directory)
}
