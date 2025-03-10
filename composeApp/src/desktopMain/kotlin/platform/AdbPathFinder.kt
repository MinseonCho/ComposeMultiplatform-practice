package platform

import model.AdbDevice
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

actual object AdbPathFinder {

    private val adbDir = File(System.getProperty("user.home"), "Desktop")
    private val adbBinary = File(adbDir, if (isWindows()) "adb.exe" else "adb")
    private fun isWindows() = System.getProperty("os.name").lowercase().contains("win")

    actual fun findAdbPath(): String? {
        return runCatching {
            if (adbBinary.exists().not()) {
                extractAdbBinary()
            }

            adbBinary.absolutePath
        }.getOrNull()
    }

    private fun extractAdbBinary() {
        adbDir.mkdirs()
        val adbResourcePath = "/adb/${if (isWindows()) "adb.exe" else "adb"}"
        val inputStream = AdbPathFinder::class.java.getResourceAsStream(adbResourcePath)
            ?: throw IOException("ADB 바이너리를 찾을 수 없습니다.")

        Files.copy(inputStream, adbBinary.toPath(), StandardCopyOption.REPLACE_EXISTING)

        if (!isWindows()) {
            adbBinary.setExecutable(true)
        }
    }

    actual fun getDevices(adbPath: String): List<AdbDevice> {
        return runCatching {
            val process = Runtime.getRuntime().exec("$adbPath devices -l")
            val output = process.inputStream.bufferedReader().use { it.readText() }
            
            output.lines()
                .drop(1) // 첫 줄은 "List of devices attached" 이므로 제외
                .filter { it.isNotBlank() }
                .map { line ->
                    val parts = line.trim().split(Regex("\\s+"))
                    AdbDevice(
                        id = parts[0],
                        description = parts.drop(1).joinToString(" ")
                    )
                }
        }.getOrDefault(emptyList())
    }
}
