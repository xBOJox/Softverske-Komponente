import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import spec.ReportInterface
import java.io.InputStreamReader
import storageImpl
import java.io.File
import java.util.*


data class Schedule(
    val subject: String,
    val classroom: String,
    val year: Int,
    val group: String,
    val day: String,
    val time_from: String,
    val time_to: String
)

fun prepareData(jsonData: InputStreamReader): Map<String, List<String>> {
    val gson = Gson()
    val scheduleType = object : TypeToken<List<Schedule>>() {}.type
    val schedules: List<Schedule> = gson.fromJson(jsonData, scheduleType)

    // Convert the list into a Map<String, List<String>> where key is column name and value is a list of corresponding column data
    val reportData: Map<String, List<String>> = mapOf(
        "subject" to schedules.map { it.subject },
        "classroom" to schedules.map { it.classroom },
        "year" to schedules.map { it.year.toString() },
        "group" to schedules.map { it.group },
        "day" to schedules.map { it.day },
        "time_from" to schedules.map { it.time_from },
        "time_to" to schedules.map { it.time_to }
    )

    return reportData
}

fun handleStorage(args: Array<String>) {
    val storage = storageImpl()  // Direct instantiation instead of ServiceLoader

    try {
        when (args[0]) {
            "mkdir" -> {
                if (args.size < 2) throw IllegalArgumentException("Missing directory pattern")
                storage.createDirectories(args[1])
                println("Created directories with pattern: ${args[1]}")
            }

            "store" -> {
                if (args.size < 3) throw IllegalArgumentException("Missing source files and target path")
                val sourceFiles = args.slice(1..args.size - 2)
                val targetPath = args.last()
                storage.storeFiles(sourceFiles, targetPath)
                println("Stored files in: $targetPath")
            }

            "delete" -> {
                if (args.size != 2) throw IllegalArgumentException("Usage: delete <path>")
                storage.delete(args[1])
                println("Deleted: ${args[1]}")
            }

            "move" -> {
                if (args.size != 3) throw IllegalArgumentException("Usage: move <source> <target>")
                storage.moveFiles(args[1], args[2])
                println("Moved from ${args[1]} to ${args[2]}")
            }

            "rename" -> {
                if (args.size != 3) throw IllegalArgumentException("Usage: rename <path> <newName>")
                storage.rename(args[1], args[2])
                println("Renamed ${args[1]} to ${args[2]}")
            }

            "list" -> {
                if (args.size < 2) throw IllegalArgumentException("Usage: list <path> [--recursive]")
                val files = if (args.getOrNull(2) == "--recursive") {
                    storage.listFilesRecursively(args[1])
                } else {
                    storage.listFiles(args[1])
                }
                println("Files in ${args[1]}:")
                files.forEach { println(it.fileName) }
            }

            "find" -> {
                if (args.size < 3) throw IllegalArgumentException("Usage: find <type> <dirPath> <pattern>")
                val results = when (args[1]) {
                    "ext" -> {
                        if (args.size != 4) throw IllegalArgumentException("Usage: find ext <dirPath> <extension>")
                        storage.findByExtension(args[2], args[3])
                    }
                    "name" -> {
                        if (args.size != 4) throw IllegalArgumentException("Usage: find name <dirPath> <pattern>")
                        storage.findByNamePattern(args[2], args[3])
                    }
                    "file" -> {
                        if (args.size != 4) throw IllegalArgumentException("Usage: find file <dirPath> <fileName>")
                        listOfNotNull(storage.findFileRecursively(args[2], args[3]))
                    }
                    else -> throw IllegalArgumentException("Unknown find type. Use: ext, name, or file")
                }
                println("Found files:")
                results.forEach { println(it.fileName) }
            }

            "sort" -> {
                if (args.size != 4) throw IllegalArgumentException("Usage: sort <dirPath> <criteria> <asc|desc>")
                val files = storage.listFiles(args[1])
                val criteria = when (args[2].uppercase()) {
                    "NAME" -> if (args[3].lowercase() == "asc") SortCriteria.NAME_ASC else SortCriteria.NAME_DESC
                    "DATE_CREATED" -> if (args[3].lowercase() == "asc") SortCriteria.DATE_CREATED_ASC else SortCriteria.DATE_CREATED_DESC
                    "DATE_MODIFIED" -> if (args[3].lowercase() == "asc") SortCriteria.DATE_MODIFIED_ASC else SortCriteria.DATE_MODIFIED_DESC
                    else -> throw IllegalArgumentException("Invalid criteria. Use: NAME, DATE_CREATED, or DATE_MODIFIED")
                }
                val sortedFiles = storage.sortBy(files, criteria)
                println("Sorted files:")
                sortedFiles.forEach { println(it.fileName) }
            }

            else -> {
                println("""
                    Available commands:
                    mkdir <pattern>                    - Create directories (e.g., test{1..3})
                    store <sourceFiles...> <targetPath> - Store files in storage
                    delete <path>                      - Delete file or directory
                    move <source> <target>             - Move files
                    rename <path> <newName>            - Rename file or directory
                    list <path> [--recursive]          - List files in directory
                    find ext <dirPath> <extension>     - Find files by extension
                    find name <dirPath> <pattern>      - Find files by name pattern
                    find file <dirPath> <fileName>     - Find specific file
                    sort <dirPath> <criteria> <asc|desc> - Sort files by criteria
                """.trimIndent())
            }
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("""
            Usage:
            storage <command> [args] - Storage operations
            report <json-file> - Generate reports
        """.trimIndent())
        return
    }

    when (args[0]) {
        "storage" -> handleStorage(args.drop(1).toTypedArray())
        "report" -> handleReport(args.drop(1).toTypedArray())
        else -> println("Unknown command: ${args[0]}")
    }
}

fun handleReport(args: Array<String>) {
    if (args.isEmpty()) {
        println("""
            Usage: report <json-file> [options]
            Options:
              --title <title>     Set report title
              --summary <summary> Set report summary
              --no-header        Exclude header from report
        """.trimIndent())
        return
    }

    val jsonFilePath = args[0]
    val serviceLoader = ServiceLoader.load(ReportInterface::class.java)
    val exporterServices = mutableMapOf<String, ReportInterface>()

    serviceLoader.forEach { service ->
        exporterServices[service.implName] = service
    }

    try {
        val inputStream = File(jsonFilePath).inputStream()
        val reader = InputStreamReader(inputStream)
        val data = prepareData(reader)
        reader.close()

        exporterServices.forEach { (key, service) ->
            val outputFile = "report.${key.lowercase()}"
            service.generateReport(data, outputFile, true)
            println("Generated report: $outputFile")
        }

        println("Available services: ${exporterServices.keys}")
    } catch (e: Exception) {
        println("Error generating report: ${e.message}")
        e.printStackTrace()
    }
}

// STARI
//fun main() {
//    val serviceLoader = ServiceLoader.load(ReportInterface::class.java)
//
//    val exporterServices = mutableMapOf<String, ReportInterface> ()
//
//    serviceLoader.forEach{
//            service ->
//        exporterServices[service.implName] = service
//    }
//
//    println(exporterServices.keys)
//
//
//    val inputStream = File("data.json").inputStream()
//
//    try {
//        val reader = InputStreamReader(inputStream)
//
//        val data = prepareData(reader)
//        reader.close()
//
//        println(data)
//
//        exporterServices.map {
//
//            it.value.generateReport(data, "file.${it.key.lowercase()}", true)
//        }
//    }
//    catch(e: Exception) {
//        e.printStackTrace()
//    }
//    println("Available services: ${exporterServices.keys}")
//
//
//
//}