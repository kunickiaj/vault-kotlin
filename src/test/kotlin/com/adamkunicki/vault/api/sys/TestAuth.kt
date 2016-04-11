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

package com.adamkunicki.vault.api.sys

import com.adamkunicki.vault.VaultConfiguration
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockserver.client.server.MockServerClient
import org.mockserver.junit.MockServerRule
import org.mockserver.matchers.Times
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.HttpStatusCode
import org.mockserver.verify.VerificationTimes
import org.slf4j.LoggerFactory
import java.io.Reader

class TestAuth {
  companion object {
    val LOG = LoggerFactory.getLogger(TestAuth::class.java)
  }

  @get:Rule
  val mockServerRule = MockServerRule(this)

  val token = "d25dd11c-ec80-b00c-31de-1c62222f356d"
  val tokenHeader = Header.header("X-Vault-Token", token)
  val contentJson = Header.header("Content-Type", "application/json")
  val localhost = "127.0.0.1"
  val address = "http://" + localhost + ":" + mockServerRule.port
  val conf = VaultConfiguration(address, token)
  val auth = Auth(conf)

  private fun getReader(resource: String): Reader {
    return javaClass.getResource(resource).openStream().bufferedReader()
  }

  @Test
  fun testSingleAuth(): Unit {
    MockServerClient(localhost, mockServerRule.port)
        .`when`(
            HttpRequest.request()
                .withMethod("GET")
                .withHeader(tokenHeader)
                .withPath("/v1/sys/auth"),
            Times.exactly(1)
        )
        .respond(
            HttpResponse.response()
                .withStatusCode(HttpStatusCode.OK_200.code())
                .withHeader(contentJson)
                .withBody(getReader("/sys_auth_single.json").readText())
        )
    val resp = auth.list()
    Assert.assertEquals("token", resp["token/"]?.type)
    Assert.assertEquals("token based credentials", resp["token/"]?.description)
  }

  @Test
  fun testMultipleAuths(): Unit {
    MockServerClient(localhost, mockServerRule.port)
        .`when`(
            HttpRequest.request()
                .withMethod("GET")
                .withHeader(tokenHeader)
                .withPath("/v1/sys/auth"),
            Times.exactly(1)
        )
        .respond(
            HttpResponse.response()
                .withStatusCode(HttpStatusCode.OK_200.code())
                .withHeader(contentJson)
                .withBody(getReader("/sys_auth_multiple.json").readText())
        )
    val resp = auth.list()
    Assert.assertEquals("token", resp["token/"]?.type)
    Assert.assertEquals("token based credentials", resp["token/"]?.description)
    Assert.assertEquals("github", resp["github/"]?.type)
    Assert.assertEquals("github access token", resp["github/"]?.description)
  }

  @Test
  fun testEnableAuth(): Unit {
    val request = HttpRequest.request()
        .withMethod("POST")
        .withHeader(tokenHeader)
        .withPath("/v1/sys/auth/github")
    val mockServerClient = MockServerClient(localhost, mockServerRule.port)

    mockServerClient
        .`when`(request)
        .respond(
            HttpResponse.response()
                .withStatusCode(HttpStatusCode.NO_CONTENT_204.code())
                .withHeader(contentJson)
        )
    Assert.assertTrue(auth.enable("github", "github", "github access token"))

    mockServerClient.verify(request, VerificationTimes.once())
  }

  @Test
  fun testDisableAuth(): Unit {
    val request = HttpRequest.request()
        .withMethod("DELETE")
        .withHeader(tokenHeader)
        .withPath("/v1/sys/auth/github")
    val mockServerClient = MockServerClient(localhost, mockServerRule.port)

    mockServerClient
        .`when`(request)
        .respond(
            HttpResponse.response()
                .withStatusCode(HttpStatusCode.NO_CONTENT_204.code())
                .withHeader(contentJson)
        )
    Assert.assertTrue(auth.disable("github"))

    mockServerClient.verify(request, VerificationTimes.once())
  }
}