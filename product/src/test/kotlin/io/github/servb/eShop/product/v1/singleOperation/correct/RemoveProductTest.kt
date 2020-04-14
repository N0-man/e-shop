package io.github.servb.eShop.product.v1.singleOperation.correct

import io.github.servb.eShop.product.givenTestContainerEShopProduct
import io.github.servb.eShop.util.kotest.shouldMatchJson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest

class RemoveProductTest : BehaviorSpec({
    givenTestContainerEShopProduct { eShopProduct ->
        `when`("I call DELETE nonexistent /v1/product") {
            val call = eShopProduct.handleRequest(HttpMethod.Delete, "/v1/product/5") {
                this.addHeader("X-Access-Token", "no token")
            }

            then("the response status should be NotFound") {
                call.response.status() shouldBe HttpStatusCode.NotImplemented
            }

            then("the response body should have only proper 'ok' field") {
                call.response.content shouldMatchJson """{"ok": false}"""
            }
        }
    }
})
