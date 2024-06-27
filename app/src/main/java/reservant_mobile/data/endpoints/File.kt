package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/uploads/{fileName}")
class File(val fileName: String)

fun String.getFileName() = this.split("/").last()