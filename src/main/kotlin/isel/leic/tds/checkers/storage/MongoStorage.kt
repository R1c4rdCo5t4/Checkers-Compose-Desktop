package isel.leic.tds.checkers.storage


import com.mongodb.ConnectionString
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.reactivestreams.KMongo



class Doc(val _id: String, val data: String)

class MongoStorage<T>(
    connectStr: String,
    databaseName: String,
    collectionName: String,
    private val serializer: Serializer<T, String>
) : Storage<String, T> {

    private val client : CoroutineClient = KMongo.createClient(ConnectionString(connectStr)).coroutine
    private val db : CoroutineDatabase = client.getDatabase(databaseName)
    private val collection = db.getCollection<Doc>(collectionName)


    override suspend fun create(id: String, value: T) {
        require(read(id) == null) { "There is already a document with given id $id" }
        val objStr = serializer.write(value) // entity -> str <=> obj -> str
        collection.insertOne(Doc(id, objStr))
    }

    override suspend fun read(id: String): T? {
        val doc = collection.findOneById(id) ?: return null
        val objStr = doc.data
        return serializer.parse(objStr)
    }

    override suspend fun update(id: String, value: T) {
        require(read(id) != null) { "There is no document with given id $id" }
        val objStr = serializer.write(value) // entity -> str <=> obj -> str
        collection.replaceOneById(id, Doc(id, objStr))
    }

    override suspend fun delete(id: String) {
        require(read(id) != null) { "There is no document with given id $id" }
        collection.deleteOneById(id)
    }
}
