import org.gradle.api.Project
import java.net.URI

object Repository {

    fun user(project: Project): String =
        getProperty(project, "repository_user")

    fun password(project: Project): String =
        getProperty(project, "repository_password")

    fun publishUrl(project: Project): URI =
        project.uri(getProperty(project, "repository_url"))

    fun getProperty(project: Project, propertyName: String): String =
        project.findProperty(propertyName)?.toString()
            ?: System.getenv(propertyName)
            ?: throw IllegalStateException("Please setup '$propertyName' env variable")
}
