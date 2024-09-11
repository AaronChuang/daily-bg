package idv.aaron.svc

import jakarta.inject.Singleton

@Singleton
class ExampleServiceImpl : MutinyExampleServiceGrpc.ExampleServiceImplBase() {

    override fun getExample(request: ExampleRequest): Uni<ExampleResponse> {
        val response = ExampleResponse.newBuilder()
            .setMessage("Hello, ${request.name}")
            .build()
        return Uni.createFrom().item(response)
    }
}
