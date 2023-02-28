package isel.leic.tds.checkers.storage

import java.io.File

class FileStorage<K, T>(
    private val folder: String,
    private val serializer: Serializer<T, String>
) : Storage<K, T> {
    private fun path(id: K) = "$folder/$id.txt"

    /**
     * Create new file with the name of the id with suffix .txt
     * if it does not exist yet.
     * Throws IllegalArgumentException if there is already a file with that name.
     */
    override suspend fun create(id: K, value: T) {
        val path = path(id)
        val file = File(path)
        require(!file.exists()) { "There is already a file with that id $id" }
        val stream = serializer.write(value)
        file.writeText(stream)
    }

    override suspend fun read(id: K): T? {
        val file = File(path(id))
        if (!file.exists()) return null
        val stream = file.readText()
        return serializer.parse(stream)
    }

    override suspend fun update(id: K, value: T) {
        val path = path(id)
        val file = File(path)
        require(file.exists()) { "There is no file with that id $id" }
        val stream = serializer.write(value)
        file.writeText(stream)
    }

    override suspend fun delete(id: K) {
        val path = path(id)
        val file = File(path)
        require(file.exists()) { "There is no file with that id $id" }
        file.delete()
    }
}
