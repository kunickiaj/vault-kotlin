/*
 * Copyright 2016 Adam Kunicki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adamkunicki.vault.api

import com.adamkunicki.vault.VaultConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockserver.client.server.MockServerClient
import org.mockserver.junit.MockServerRule
import org.mockserver.matchers.Times
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse
import org.mockserver.model.HttpStatusCode
import org.mockserver.verify.VerificationTimes
import org.slf4j.LoggerFactory
import java.io.Reader

class TestLogical {
  companion object {
    val LOG = LoggerFactory.getLogger(TestLogical::class.java)
  }

  @get:Rule
  val mockServerRule = MockServerRule(this)

  val token = "d25dd11c-ec80-b00c-31de-1c62222f356d"
  val tokenHeader = Header.header("X-Vault-Token", token)
  val contentJson = Header.header("Content-Type", "application/json")
  val localhost = "127.0.0.1"
  val address = "http://" + localhost + ":" + mockServerRule.port
  val conf = VaultConfiguration(address, token)
  val logical = Logical(conf)

  private fun getReader(resource: String): Reader {
    return javaClass.getResource(resource).openStream().bufferedReader()
  }

  @Test
  @Suppress("UNCHECKED_CAST")
  fun testList(): Unit {
    MockServerClient(localhost, mockServerRule.port)
        .`when`(
            request()
                .withMethod("GET")
                .withHeader(tokenHeader)
                .withPath("/v1/secret")
                .withQueryStringParameter("list", "true"),
            Times.exactly(2)
        )
        .respond(
            HttpResponse.response()
                .withStatusCode(HttpStatusCode.OK_200.code())
                .withHeader(contentJson)
                .withBody(getReader("/logical_list_secret.json").readText())
        )
    val resp = logical.list("secret")

    assertEquals(0, resp.lease_duration)
    assertEquals("", resp.lease_id)
    assertEquals(false, resp.renewable)
    val keys: List<String> = resp.data["keys"] as List<String>
    assertEquals(1, keys.size)
    assertEquals("hello", keys.first())

    // Also check that a leading '/' won't cause issues.
    val respWithLeadingSlash = logical.list("/secret")

    assertEquals(0, respWithLeadingSlash.lease_duration)
    assertEquals("", respWithLeadingSlash.lease_id)
    assertEquals(false, respWithLeadingSlash.renewable)
    val keys2: List<String> = respWithLeadingSlash.data["keys"] as List<String>
    assertEquals(1, keys2.size)
    assertEquals("hello", keys2.first())
  }

  @Test
  fun testRead(): Unit {
    MockServerClient(localhost, mockServerRule.port)
        .`when`(
            request()
                .withMethod("GET")
                .withHeader(tokenHeader)
                .withPath("/v1/secret"),
            Times.exactly(1)
        )
        .respond(
            HttpResponse.response()
                .withStatusCode(HttpStatusCode.OK_200.code())
                .withHeader(contentJson)
                .withBody(getReader("/logical_read_secret_hello.json").readText())
        )
    val resp = logical.read("/secret")

    assertEquals(2592000, resp.lease_duration)
    assertEquals("", resp.lease_id)
    assertEquals(false, resp.renewable)
    assertTrue(resp.data.contains("value"))
    assertEquals("world", resp.data["value"])
  }

  @Test
  fun testWrite(): Unit {
    val request = request()
        .withMethod("PUT")
        .withHeader(tokenHeader)
        .withPath("/v1/secret/hello")
    val mockServerClient = MockServerClient(localhost, mockServerRule.port)

    mockServerClient
        .`when`(request)
        .respond(
            HttpResponse.response()
                .withStatusCode(HttpStatusCode.NO_CONTENT_204.code())
                .withHeader(contentJson)
        )
    assertTrue(logical.write("/secret/hello", listOf("value" to "world")))

    mockServerClient.verify(request, VerificationTimes.once())
  }

  @Test
  fun testDelete(): Unit {
    val request = request()
        .withMethod("DELETE")
        .withHeader(tokenHeader)
        .withPath("/v1/secret/hello")

    val mockServerClient = MockServerClient(localhost, mockServerRule.port)
    mockServerClient
        .`when`(request)
        .respond(
            HttpResponse.response()
                .withStatusCode(HttpStatusCode.NO_CONTENT_204.code())
        )

    assertTrue(logical.delete("/secret/hello"))

    mockServerClient.verify(request, VerificationTimes.once())
  }
}
