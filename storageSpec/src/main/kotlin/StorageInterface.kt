interface StorageInterface {
    /**
     * Creates directories based on pattern
     * e.g., s{1..20} creates 20 directories named s1 to s20
     */
    fun createDirectories(pattern: String)

    /**
     * Store files in the storage at specified path
     */
    fun storeFiles(sourcePaths: List<String>, targetPath: String)

    /**
     * Delete file or directory from storage
     */
    fun delete(path: String)

    /**
     * Move files from one location to another in storage
     */
    fun moveFiles(sourcePath: String, targetPath: String)



    /**
     * Rename file or directory in storage
     */
    fun rename(oldPath: String, newName: String)

    /**
     * Search operations
     */
    fun listFiles(dirPath: String): List<java.nio.file.Path>
    fun findByExtension(dirPath: String, extension: String): List<java.nio.file.Path>
    fun findByNamePattern(dirPath: String, pattern: String): List<java.nio.file.Path>
    fun findFileRecursively(dirPath: String, fileName: String): java.nio.file.Path?
    fun listFilesRecursively(dirPath: String): List<java.nio.file.Path>
    fun sortBy(files: List<java.nio.file.Path>, criteria: SortCriteria): List<java.nio.file.Path>
}

enum class SortCriteria {
    NAME_ASC, NAME_DESC,
    DATE_CREATED_ASC, DATE_CREATED_DESC,
    DATE_MODIFIED_ASC, DATE_MODIFIED_DESC
}