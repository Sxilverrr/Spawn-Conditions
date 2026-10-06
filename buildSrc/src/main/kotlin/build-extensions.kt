import org.gradle.api.Project
import org.gradle.kotlin.dsl.expand
import org.gradle.language.jvm.tasks.ProcessResources

val Project.mod: ModData get() = ModData(this)
fun Project.prop(key: String): String? = findProperty(key)?.toString()
fun String.upperCaseFirst() = replaceFirstChar(Char::uppercaseChar)

fun ProcessResources.properties(files: Iterable<String>, vararg properties: Pair<String, Any>) {
    for ((name, value) in properties) inputs.property(name, value)
    filesMatching(files) {
        expand(properties.toMap())
    }
}

@JvmInline
value class ModData(private val project: Project) {
    val id get() = prop("id")
    val name get() = prop("name")
    val version get() = prop("version")

    fun prop(key: String) = project.property("mod.$key").toString()
    fun dep(key: String) = project.property("deps.$key").toString()
}
