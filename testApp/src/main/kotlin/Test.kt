import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import spec.ReportInterface
import java.io.File
import java.io.InputStreamReader
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
fun main() {
    val serviceLoader = ServiceLoader.load(ReportInterface::class.java)

    val exporterServices = mutableMapOf<String, ReportInterface> ()

    serviceLoader.forEach{
            service ->
        exporterServices[service.implName] = service
    }

    println(exporterServices.keys)


    val inputStream = File("data.json").inputStream()

    try {
        val reader = InputStreamReader(inputStream)

        val data = prepareData(reader)
        reader.close()

        println(data)

        exporterServices.map {

            it.value.generateReport(data, "kurac.${it.key.lowercase()}", true)
        }
    }
    catch(e: Exception) {
        e.printStackTrace()
    }
    println("Available services: ${exporterServices.keys}")



}