package io.github.servb.eShop.handler.product.v1

import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.annotations.parameters.HeaderParam
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.delete
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import io.github.servb.eShop.model.ProductTable
import io.github.servb.eShop.throwsAuthExceptions
import io.github.servb.eShop.util.SuccessResult
import io.github.servb.eShop.validateRequest
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

@Path("{id}")
data class V1ProductDeleteRequestParams(
    @HeaderParam("Auth token.")
    val `X-Access-Token`: String,
    @PathParam("ID of the product to remove.")
    val id: Int
)

@Response("The product has been removed.", statusCode = 200)
object V1ProductDeleteOkResponse : SuccessResult {
    override val ok = true
}

fun NormalOpenAPIRoute.removeProduct(database: Database, httpClient: HttpClient, authBaseUrl: String) {
    route("product") {
        throws(
            status = HttpStatusCode.NotFound.description("The product does not exist."),
            example = SuccessResult.FAIL,
            exClass = IllegalArgumentException::class
        ) {
            throwsAuthExceptions(SuccessResult.FAIL) {
                delete(database, httpClient, authBaseUrl)
            }
        }
    }
}

private fun NormalOpenAPIRoute.delete(database: Database, httpClient: HttpClient, authBaseUrl: String) {
    delete<V1ProductDeleteRequestParams, V1ProductDeleteOkResponse>(
        info(
            summary = "Remove a product.",
            description = "The product is removed only if a product with the same ID exists. Returns `${SuccessResult::class.simpleName}` saying whether the product has been removed."
        ),
        example = V1ProductDeleteOkResponse
    ) { params ->
        validateRequest(params.`X-Access-Token`, httpClient, authBaseUrl)

        newSuspendedTransaction(db = database) {
            require(ProductTable.select { ProductTable.id.eq(params.id) }.count() != 0L)

            ProductTable.deleteWhere { ProductTable.id.eq(params.id) }
        }

        respond(V1ProductDeleteOkResponse)
    }
}
