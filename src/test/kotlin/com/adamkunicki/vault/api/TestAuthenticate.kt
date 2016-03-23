/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.adamkunicki.vault.api

import com.adamkunicki.vault.VaultConfiguration
import com.github.kittinunf.fuel.core.Manager
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockserver.client.server.MockServerClient
import org.mockserver.junit.MockServerRule
import org.mockserver.matchers.Times.exactly
import org.mockserver.model.Header.header
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.HttpStatusCode.OK_200
import org.slf4j.LoggerFactory
import java.io.Reader

class TestAuthenticate {
  companion object {
    val LOG = LoggerFactory.getLogger(TestAuthenticate::class.java)
  }

  @get:Rule
  val mockServerRule = MockServerRule(this)

  val token = "d25dd11c-ec80-b00c-31de-1c62222f356d"

  private fun getReader(resource: String): Reader {
    return javaClass.getResource(resource).openStream().bufferedReader()
  }

  @Test
  fun testTokenSercretDeserializer(): Unit {
    val secret = Secret.Deserializer().deserialize(getReader("/token_auth.json"))
    Assert.assertEquals("b054c1d3-bfdf-098b-79e7-fbe02197a3c2", secret.data["id"])
  }

  @Test
  fun testTokenAuth(): Unit {
    val address = "http://127.0.0.1:" + mockServerRule.port

    Manager.instance.baseHeaders = emptyMap()
    val conf = VaultConfiguration(address, token)
    val auth = Authenticate(conf)
    MockServerClient("127.0.0.1", mockServerRule.port)
        .`when`(
              request()
                  .withMethod("GET")
                  .withHeaders(header("X-Vault-Token", token))
                  .withPath("/v1/auth/token/lookup-self"),
            exactly(1)
        )
        .respond(
            response()
                .withStatusCode(OK_200.code())
                .withHeader("Content-Type", "application/json")
                .withBody(getReader("/token_auth.json").readText())
        )
    val resp = auth.token(token)

    assertEquals(token, resp.data["id"])
  }
}