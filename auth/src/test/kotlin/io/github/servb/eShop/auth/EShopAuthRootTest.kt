package io.github.servb.eShop.auth

import io.github.servb.eShop.util.kotest.*
import io.github.servb.eShop.util.ktor.withTestApplication
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeInteger
import io.kotest.matchers.string.shouldEndWith
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest

class EShopAuthRootTest : BehaviorSpec({
    givenTestContainerEShopAuth { eShopAuth ->
        `when`("I call GET /") {
            val call = eShopAuth.handleRequest(HttpMethod.Get, "/")

            then("the response status should be OK") {
                call.response.status() shouldBe HttpStatusCode.OK
            }

            then("the response body should have proper 'name' and 'uptime' fields") {
                val responseJson = call.response.content

                responseJson shouldContainExactly 2.jsonKeyValueEntries

                responseJson.shouldContainJsonKeyValue("name", "e-shop-auth")

                val uptime: String = responseJson shouldContainJsonKeyAndValueOfSpecificType "uptime"

                uptime shouldEndWith "s"
                uptime.dropLast(1).shouldBeInteger()
            }
        }
    }
})
