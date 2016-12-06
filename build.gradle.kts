import com.github.opengl8080.gradle.plugin.assertj.AssertjGen
import com.github.opengl8080.gradle.plugin.assertj.AssertjGenConfiguration
import io.spring.gradle.dependencymanagement.DependencyManagementExtension
import io.spring.gradle.dependencymanagement.ImportsHandler
import org.gradle.api.internal.HasConvention
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.internal.impldep.org.eclipse.jdt.internal.core.Assert
import org.gradle.language.java.JavaSourceSet
import org.gradle.script.lang.kotlin.*
import org.jetbrains.kotlin.incremental.javaSourceRoots
import org.jetbrains.kotlin.utils.addIfNotNull
import java.io.File

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
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.0-M3")
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

val junitJupiterVersion = "5.0.0-M3"
val junitPlatformVersion = "1.0.0-M3"
val junit4Version = "4.12"
val junitVintageVersion = "4.12.0-M3"
val log4JVersion = "2.6.2"

repositories {
    gradleScriptKotlin()
    jcenter()
    mavenCentral()
    maven {
        setUrl("http://dl.bintray.com/jetbrains/teamcity-rest-client")
    }
}

(tasks.getByName("compileTestJava") as JavaCompile).apply {
    options.compilerArgs + "-parameters"
}
val generatedJavaDir = File("$buildDir/test-generated/java")

configure<JavaPluginConvention> {
    val testSourceDirs = HashSet(sourceSets.getByName("test").allJava.srcDirs).apply {
        add(generatedJavaDir)
    }
    sourceSets.getByName("test").allJava.setSrcDirs(testSourceDirs)
}

configure <AssertjGenConfiguration> {
    classOrPackageNames.add("de.swp.model")
    outputDir = generatedJavaDir.absolutePath
}


dependencies {
    compile(kotlinModule("stdlib"))
    compile("com.vaadin:vaadin-spring-boot-starter:1.1.0")
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.springframework.boot:spring-boot")
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

    testCompile("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    testCompile("junit:junit:$junit4Version")
    testRuntime("org.junit.vintage:junit-vintage-engine:$junitVintageVersion")

    testRuntime("org.apache.logging.log4j:log4j-core:$log4JVersion")
    testRuntime("org.apache.logging.log4j:log4j-jul:$log4JVersion")

    testCompile("com.nhaarman:mockito-kotlin:0.12.0")
}

task(name = "wrapper", type = Wrapper::class) {
    distributionUrl = "https://repo.gradle.org/gradle/dist-snapshots/gradle-script-kotlin-3.3-20161202104140+0000-all.zip"
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