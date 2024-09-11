package idv.aaron.svc

import idv.aaron.grpc.Empty
import idv.aaron.grpc.GrpcPerson
import idv.aaron.grpc.PersonById
import idv.aaron.grpc.PersonList
import idv.aaron.grpc.PersonService
import idv.aaron.repo.Person
import idv.aaron.repo.PersonRepo
import io.quarkus.grpc.GrpcService
import io.smallrye.common.annotation.RunOnVirtualThread
import io.smallrye.mutiny.Uni
import jakarta.inject.Inject
import org.bson.types.ObjectId

@GrpcService
class PersonServiceImpl:PersonService {

    @Inject
    lateinit var repo: PersonRepo

    @RunOnVirtualThread
    override fun getAll(request: Empty?): Uni<PersonList?>? {
        return repo.findAllPersons()
            .map { persons ->
                PersonList.newBuilder()
                    .addAllPersons(persons.map { it.toGrpcPerson() })
                    .build()
            }
    }

    @RunOnVirtualThread
    override fun getById(request: PersonById?): Uni<GrpcPerson?>? {
        if(request == null || request.id.isNullOrEmpty()) return null
        val id = request.id
        return repo.findPersonById(id).map{
            println("XXXXX $it")
            it?.toGrpcPerson()
        }
    }

    @RunOnVirtualThread
    override fun create(request: GrpcPerson?): Uni<GrpcPerson?>? {
        val person = request?.toPerson() ?: return null
        return repo.persist(person)
            .map { person.toGrpcPerson() }
    }

    @RunOnVirtualThread
    override fun update(request: GrpcPerson?): Uni<GrpcPerson?>? {
        val updatedPerson = request?.toPerson()
        if(updatedPerson == null || updatedPerson._id == null) return null
        return repo.findPersonById(updatedPerson._id!!.toHexString())
            .flatMap { existingPerson ->
                if (existingPerson != null) {
                    if (updatedPerson.name.isNotBlank()) {
                        existingPerson.name = updatedPerson.name
                    }
                    if (updatedPerson.age > 0) {
                        existingPerson.age = updatedPerson.age
                    }
                    repo.update(existingPerson).map { existingPerson.toGrpcPerson() }
                } else {
                    Uni.createFrom().nullItem()
                }
            }
    }

    @RunOnVirtualThread
    override fun delete(request: PersonById?): Uni<Empty?>? {
        if(request == null || request.id.isNullOrEmpty()) return null
        val id = request.id
        return repo.deleteById(ObjectId(id))
            .map { if (it) Empty.getDefaultInstance() else null }
    }

    fun Person.toGrpcPerson(): GrpcPerson {
        return GrpcPerson.newBuilder()
            .setId(this.id?.toHexString() ?: "")
            .setName(this.name)
            .setAge(this.age)
            .build()
    }

    fun GrpcPerson.toPerson(): Person {
        var id = if(this.id.isNullOrBlank()) ObjectId() else ObjectId(this.id)
        return Person(
            _id = id,
            name = this.name,
            age = this.age
        )
    }
}
