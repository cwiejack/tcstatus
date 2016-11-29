import io.spring.gradle.dependencymanagement.DependencyManagementExtension
import io.spring.gradle.dependencymanagement.ImportsHandler
import org.gradle.api.tasks.wrapper.Wrapper

buildscript {
    repositories {
        maven {
            setUrl("https://plugins.gradle.org/m2/")
        }
        maven {
            setUrl("https://repo.spring.io/libs-snapshot")
        }
        jcenter()
        mavenCentral()
        gradleScriptKotlin()
    }

    dependencies {
        classpath("io.spring.gradle:dependency-management-plugin:0.6.1.RELEASE")
        classpath("org.springframework.boot:spring-boot-gradle-plugin:1.4.2.RELEASE")
        classpath(kotlinModule("gradle-plugin"))
        classpath("gradle.plugin.com.github.opengl-8080:assertjGen-gradle-plugin:1.1.0")
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.0-M2")
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.0-M2")
    }
}

apply {
    plugin("kotlin")
    plugin("org.springframework.boot")
    plugin("java")
    plugin("idea")
    plugin("com.github.opengl-BOBO.assertjGen")
    plugin("org.junit.platform.gradle.plugin")

}

val junitJupiterVersion = "5.0.0-M2"
val junitPlatformVersion = "1.0.0-M2"
val junit4Version = "4.12"
val junitVintageVersion = "4.12.0-M2"
val log4JVersion = "2.6.2"

repositories {
    gradleScriptKotlin()
    jcenter()
    mavenCentral()
    maven {
        setUrl("http://dl.bintray.com/jetbrains/teamcity-rest-client")
    }
}
configurations.all {
    dependencies {
        it.exclude(module = "spring-boot-starter-tomcat")
        it.exclude(module = "spring-boot-starter-logging")
    }
}

dependencies {
    compile(kotlinModule("stdlib"))
    compile("com.vaadin:vaadin-spring-boot-starter:1.1.0")
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.springframework.boot:spring-boot:1.4.2.RELEASE")
    compile("org.springframework.boot:spring-boot-starter-undertow")
    compile("org.springframework.boot:spring-boot-starter-log4j2")
    compile("commons-cli:commons-cli:1.3.1")
    compile("com.vaadin:vaadin-themes")
    compile("com.vaadin:vaadin-push")
    compile("org.jetbrains.teamcity:teamcity-rest-client:0.1.49")
    compile("org.apache.commons:commons-lang3:3.5")

    testCompile("org.springframework.boot:spring-boot-starter-test")
    testCompile("org.mockito:mockito-core:1.10.19")
    testCompile("org.assertj:assertj-core:3.5.2")

    testCompile("org.junit.jupiter:junit-jupiter-api:${junitJupiterVersion}")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:${junitJupiterVersion}")
    testCompile("junit:junit:${junit4Version}")
    testRuntime("org.junit.vintage:junit-vintage-engine:${junitVintageVersion}")

    testRuntime("org.apache.logging.log4j:log4j-core:${log4JVersion}")
    testRuntime("org.apache.logging.log4j:log4j-jul:${log4JVersion}")

    testCompile("com.nhaarman:mockito-kotlin:0.12.0")
}

task(name = "wrapper", type = Wrapper::class) {
    distributionUrl = "https://repo.gradle.org/gradle/dist-snapshots/gradle-script-kotlin-3.3-20161123161139+0000-all.zip"
}

configure<DependencyManagementExtension> {
    imports(delegateClosureOf<ImportsHandler> {
        mavenBom("com.vaadin:vaadin-bom:7.7.4")
    })
}

fun <T> delegateClosureOf(action: T.() -> Unit) =
        object : groovy.lang.Closure<Unit>(null, null) {
            fun doCall() = (delegate as T).action()
        }