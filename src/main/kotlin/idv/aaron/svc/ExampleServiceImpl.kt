package idv.aaron.svc

import idv.aaron.grpc.DailyService
import idv.aaron.grpc.ExampleRequest
import idv.aaron.grpc.ExampleResponse
import io.quarkus.grpc.GrpcService
import io.smallrye.common.annotation.RunOnVirtualThread
import io.smallrye.mutiny.Uni

@GrpcService
class ExampleServiceImpl : DailyService {

    @RunOnVirtualThread
    override fun getExample(request: ExampleRequest): Uni<ExampleResponse> {
        println("TEST!!!!")
        val response = ExampleResponse.newBuilder()
            .setMessage("Hello, ${request.name}")
            .build()
        return Uni.createFrom().item(response)
    }
}
