import org.gradle.api.JavaVersion

object Kotlin {
    const val version = "1.7.10"
    val javaSource = JavaVersion.VERSION_11
    val javaTarget = JavaVersion.VERSION_11
}

object Versions {
    const val springBoot = "2.7.2"
    const val springBootDependency = "1.0.12.RELEASE"
    const val springCloudDependency = "2021.0.5"

    const val springMockk = "2.0.1"
    const val testContainers = "1.15.0"

    const val jjwt = "0.11.5"
    const val swagger = "1.6.9"
    const val kotlinCoroutine = "1.4.2"
    const val kotlinLogging = "2.1.23"
    const val kotest = "5.4.1"
    const val jupiter = "5.9.0"

}
