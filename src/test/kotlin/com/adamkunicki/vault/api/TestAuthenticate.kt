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
  val tokenHeader = header("X-Vault-Token", token)
  val contentJson = header("Content-Type", "application/json")
  val localhost = "127.0.0.1"
  val address = "http://" + localhost + ":" + mockServerRule.port
  val conf = VaultConfiguration(address, token)
  val auth = Authenticate(conf)

  private fun getReader(resource: String): Reader {
    return javaClass.getResource(resource).openStream().bufferedReader()
  }

  @Test
  fun testTokenSecretDeserializer(): Unit {
    val secret = Secret.Deserializer().deserialize(getReader("/auth_token.json"))
    Assert.assertEquals(token, secret.data["id"])
  }

  @Test
  fun testTokenAuth(): Unit {
    MockServerClient(localhost, mockServerRule.port)
        .`when`(
              request()
                  .withMethod("GET")
                  .withHeader(tokenHeader)
                  .withPath("/v1/auth/token/lookup-self"),
            exactly(1)
        )
        .respond(
            response()
                .withStatusCode(OK_200.code())
                .withHeader(contentJson)
                .withBody(getReader("/auth_token.json").readText())
        )
    val resp = auth.token(token)

    assertEquals(token, resp.data["id"])
    assertEquals(0, resp.lease_duration)
    assertEquals("", resp.lease_id)
    assertEquals(false, resp.renewable)
  }

  @Test
  fun testAppIdAuth(): Unit {
    MockServerClient(localhost, mockServerRule.port).`when`(
        request()
            .withMethod("POST")
            .withHeader(tokenHeader)
            .withPath("/v1/auth/app-id/login")
            .withBody("""{"app_id":"foo","user_id":"bar"}"""),
        exactly(1)
    )
    .respond(
        response()
            .withStatusCode(OK_200.code())
            .withHeader(contentJson)
            .withBody(getReader("/auth_app_id.json").readText())
    )
    val resp = auth.appId("foo", "bar")

    assertEquals("26845a04-96f4-2b21-e404-c6e858d08a71", resp.auth.client_token)
    assertEquals("sha1:0beec7b5ea3f0fdbc95d0dd47f3c5bc275da8a33", resp.auth.metadata["app-id"])
    assertEquals("sha1:62cdb7020ff920e5aa642c3d4066950dd1f01f4d", resp.auth.metadata["user-id"])
    assertEquals(0, resp.lease_duration)
    assertEquals("", resp.lease_id)
    assertEquals(false, resp.renewable)
  }

  @Test
  fun testUserPassAuth(): Unit {
    MockServerClient(localhost, mockServerRule.port).`when`(
        request()
            .withMethod("POST")
            .withHeader(tokenHeader)
            .withPath("/v1/auth/userpass/login/mitchellh")
            .withBody("""{"password":"bar"}"""),
        exactly(1)
    )
        .respond(
            response()
                .withStatusCode(OK_200.code())
                .withHeader(contentJson)
                .withBody(getReader("/auth_user_pass.json").readText())
        )
    val resp = auth.userPass("mitchellh", "bar")

    assertEquals("484791d3-513d-daa3-0ea2-52ed8e6983e7", resp.auth.client_token)
    assertEquals("mitchellh", resp.auth.metadata["username"])
    assertEquals(0, resp.lease_duration)
    assertEquals("", resp.lease_id)
    assertEquals(false, resp.renewable)
  }

  @Test
  fun testLdapAuth(): Unit {
    MockServerClient(localhost, mockServerRule.port).`when`(
        request()
            .withMethod("POST")
            .withHeader(tokenHeader)
            .withPath("/v1/auth/ldap/login/mitchellh")
            .withBody("""{"password":"bar"}"""),
        exactly(1)
    )
        .respond(
            response()
                .withStatusCode(OK_200.code())
                .withHeader(contentJson)
                // Same response as userPass auth.
                .withBody(getReader("/auth_user_pass.json").readText())
        )
    val resp = auth.ldap("mitchellh", "bar")

    assertEquals("484791d3-513d-daa3-0ea2-52ed8e6983e7", resp.auth.client_token)
    assertEquals("mitchellh", resp.auth.metadata["username"])
    assertEquals(0, resp.lease_duration)
    assertEquals("", resp.lease_id)
    assertEquals(false, resp.renewable)
  }

  @Test
  fun testGitHubAuth(): Unit {
    MockServerClient(localhost, mockServerRule.port).`when`(
        request()
            .withMethod("POST")
            .withHeader(tokenHeader)
            .withPath("/v1/auth/github/login")
            .withBody("""{"token":"your_github_personal_access_token"}"""),
        exactly(1)
    )
        .respond(
            response()
                .withStatusCode(OK_200.code())
                .withHeader(contentJson)
                .withBody(getReader("/auth_github.json").readText())
        )
    val resp = auth.github("your_github_personal_access_token")

    assertEquals("c4f280f6-fdb2-18eb-89d3-589e2e834cdb", resp.auth.client_token)
    assertEquals("kunickiaj", resp.auth.metadata["username"])
    assertEquals("test_org", resp.auth.metadata["org"])
    assertEquals(0, resp.lease_duration)
    assertEquals("", resp.lease_id)
    assertEquals(false, resp.renewable)
  }
}