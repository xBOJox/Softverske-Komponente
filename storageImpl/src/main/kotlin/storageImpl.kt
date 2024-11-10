import java.nio.file.*
import kotlin.io.path.*

class storageImpl : StorageInterface{
        private val rootPath: Path = Paths.get("./storage").toAbsolutePath()

        init {
            if (!rootPath.exists()) {
                rootPath.createDirectory()
            }
        }

    override fun createDirectories(pattern: String) {
        when {
            pattern.contains("..") -> {
                val regex = """(.*)\{(\d+)\.\.(\d+)\}(.*)""".toRegex()
                val matchResult = regex.find(pattern)
                if (matchResult != null) {
                    val (prefix, start, end, suffix) = matchResult.destructured
                    for (i in start.toInt()..end.toInt()) {
                        val dirPath = rootPath.resolve("$prefix$i$suffix")
                        if (!dirPath.exists()) {
                            dirPath.createDirectory()
                        }
                    }
                } else {
                    throw IllegalArgumentException("Invalid pattern format")
                }
            }
            else -> {
                val dirPath = rootPath.resolve(pattern)
                if (!dirPath.exists()) {
                    dirPath.createDirectories()
                }
            }
        }
    }

    override fun storeFiles(sourcePaths: List<String>, targetPath: String) {
        val targetDir = rootPath.resolve(targetPath)
        if (!targetDir.exists()) {
            targetDir.createDirectories()
        }

        sourcePaths.forEach { sourcePath ->
            val sourceFile = Paths.get(sourcePath)
            if (!sourceFile.exists()) {
                throw IllegalArgumentException("Source file does not exist: $sourcePath")
            }
            val targetFile = targetDir.resolve(sourceFile.fileName)
            sourceFile.copyTo(targetFile, overwrite = true)
        }
    }

    override fun delete(path: String) {
        val targetPath = rootPath.resolve(path)
        if (!targetPath.exists()) {
            throw NoSuchFileException(targetPath.toString())
        }
        if (targetPath.isDirectory()) {
            targetPath.toFile().deleteRecursively()
        } else {
            targetPath.deleteExisting()
        }
    }

    override fun moveFiles(sourcePath: String, targetPath: String) {
        val source = rootPath.resolve(sourcePath)
        val target = rootPath.resolve(targetPath)

        if (!source.exists()) {
            throw NoSuchFileException(source.toString())
        }

        if (!target.parent.exists()) {
            target.parent.createDirectories()
        }

        source.moveTo(target, overwrite = true)
    }

    override fun rename(oldPath: String, newName: String) {
        val source = rootPath.resolve(oldPath)
        val target = source.resolveSibling(newName)

        if (!source.exists()) {
            throw NoSuchFileException(source.toString())
        }

        if (target.exists()) {
            throw FileAlreadyExistsException(target.toString())
        }

        source.moveTo(target, overwrite = false)
    }

    override fun listFiles(dirPath: String): List<Path> {
        val targetDir = rootPath.resolve(dirPath)
        if (!targetDir.exists() || !targetDir.isDirectory()) {
            throw NotDirectoryException(targetDir.toString())
        }

        return targetDir.listDirectoryEntries()
    }

    override fun findByExtension(dirPath: String, extension: String): List<Path> {
        return listFiles(dirPath).filter {
            it.isRegularFile() && it.extension.equals(extension, ignoreCase = true)
        }
    }

    override fun findByNamePattern(dirPath: String, pattern: String): List<Path> {
        return listFiles(dirPath).filter {
            it.fileName.toString().contains(pattern, ignoreCase = true)
        }
    }

    @OptIn(ExperimentalPathApi::class)
    override fun findFileRecursively(dirPath: String, fileName: String): Path? {
        val dir = rootPath.resolve(dirPath)
        if (!dir.exists() || !dir.isDirectory()) {
            throw NotDirectoryException(dir.toString())
        }
        return dir.walk().find { it.fileName.toString().equals(fileName, ignoreCase = true) }
    }

    @OptIn(ExperimentalPathApi::class)
    override fun listFilesRecursively(dirPath: String): List<Path> {
        val targetDir = rootPath.resolve(dirPath)
        if (!targetDir.exists() || !targetDir.isDirectory()) {
            throw NotDirectoryException(targetDir.toString())
        }
        return targetDir.walk().filter { it.isRegularFile() }.toList()
    }

    override fun sortBy(files: List<Path>, criteria: SortCriteria): List<Path> {
        return when (criteria) {
            SortCriteria.NAME_ASC -> files.sortedBy { it.fileName.toString().lowercase() }
            SortCriteria.NAME_DESC -> files.sortedByDescending { it.fileName.toString().lowercase() }
            SortCriteria.DATE_CREATED_ASC -> files.sortedBy { it.getLastModifiedTime() }
            SortCriteria.DATE_CREATED_DESC -> files.sortedByDescending { it.getLastModifiedTime() }
            SortCriteria.DATE_MODIFIED_ASC -> files.sortedBy { it.getLastModifiedTime() }
            SortCriteria.DATE_MODIFIED_DESC -> files.sortedByDescending { it.getLastModifiedTime() }
        }
    }
}