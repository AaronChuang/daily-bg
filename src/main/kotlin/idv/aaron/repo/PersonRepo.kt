package idv.aaron.repo

import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoEntity
import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoRepository
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import org.bson.types.ObjectId

@ApplicationScoped
class PersonRepo : ReactivePanacheMongoRepository<Person>{
    fun findAllPersons(): Uni<List<Person>> {
        return findAll().list()
    }

    fun findPersonById(id: String): Uni<Person?> {
        return findById(ObjectId(id))
    }
}

@MongoEntity(collection = "persons")
data class Person(
    var _id: ObjectId? = null,
    var name: String = "",
    var age: Int = 0,
) : ReactivePanacheMongoEntity()
