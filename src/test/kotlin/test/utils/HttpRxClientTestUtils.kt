package test.utils

import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.client.annotation.Client
import io.micronaut.rxjava3.http.client.Rx3HttpClient
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.token.generator.TokenGenerator
import io.micronaut.security.token.jwt.generator.AccessTokenConfigurationProperties
import jakarta.inject.Named
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

@Singleton
class HttpRxClientTestUtils(
    @Named("jwt") private val tokenGenerator: TokenGenerator,
    private val accessTokenConfigurationProperties: AccessTokenConfigurationProperties,
    @Client("/") private val rx3HttpClient: Rx3HttpClient,
) {

    suspend fun <O> requestExchange(uri: String, argument: Argument<O>) = requestExchange(HttpRequest.GET<String>(uri), argument)

    suspend fun <I, O> requestExchange(request: HttpRequest<I>, argument: Argument<O>): HttpResponse<O> = withContext(Dispatchers.IO) {
        rx3HttpClient.toBlocking()
        .exchange(request, argument)
    }

    suspend fun <O> requestExchangeWithAuth(uri: String, argument: Argument<O>, clientId: UUID): HttpResponse<O> =
        requestExchangeWithAuth(HttpRequest.GET<String>(uri), argument, clientId)

    suspend fun <I, O> requestExchangeWithAuth(request: MutableHttpRequest<I>, argument: Argument<O>, clientId: UUID): HttpResponse<O> = withContext(Dispatchers.IO) {
        val authentication = Authentication.build("Testing user", mapOf("id" to clientId.toString()))
        val bearerToken = tokenGenerator.generateToken(authentication, accessTokenConfigurationProperties.expiration)
        rx3HttpClient
            .exchange(
                request.bearerAuth(bearerToken.get()),
                argument
            ).blockingFirst()
    }
}
