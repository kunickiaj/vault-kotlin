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

class TestAudit() {
  companion object {
    val LOG = LoggerFactory.getLogger(TestAudit::class.java)
  }

  @get:Rule
  val mockServerRule = MockServerRule(this)

  val token = "d25dd11c-ec80-b00c-31de-1c62222f356d"
  val tokenHeader = Header.header("X-Vault-Token", token)
  val contentJson = Header.header("Content-Type", "application/json")
  val localhost = "127.0.0.1"
  val address = "http://" + localhost + ":" + mockServerRule.port
  val conf = VaultConfiguration(address, token)
  val audit = Audit(conf)

  private fun getReader(resource: String): Reader {
    return javaClass.getResource(resource).openStream().bufferedReader()
  }

  @Test
  fun testSingleAudit(): Unit {
    MockServerClient(localhost, mockServerRule.port)
        .`when`(
            HttpRequest.request()
                .withMethod("GET")
                .withHeader(tokenHeader)
                .withPath("/v1/sys/audit"),
            Times.exactly(1)
        )
        .respond(
            HttpResponse.response()
                .withStatusCode(HttpStatusCode.OK_200.code())
                .withHeader(contentJson)
                .withBody(getReader("/sys_audit_single.json").readText())
        )
    val resp = audit.list()
    Assert.assertEquals("file", resp["file-audit/"]?.type)
    Assert.assertEquals("File audit", resp["file-audit/"]?.description)
    Assert.assertEquals("file-audit/", resp["file-audit/"]?.path)
    Assert.assertEquals("/tmp/vault", resp["file-audit/"]?.options?.get("path"))
  }

  @Test
  fun testMultipleAudits(): Unit {
    MockServerClient(localhost, mockServerRule.port)
        .`when`(
            HttpRequest.request()
                .withMethod("GET")
                .withHeader(tokenHeader)
                .withPath("/v1/sys/audit"),
            Times.exactly(1)
        )
        .respond(
            HttpResponse.response()
                .withStatusCode(HttpStatusCode.OK_200.code())
                .withHeader(contentJson)
                .withBody(getReader("/sys_audit_multiple.json").readText())
        )
    val resp = audit.list()
    Assert.assertEquals("file", resp["file-audit/"]?.type)
    Assert.assertEquals("File audit", resp["file-audit/"]?.description)
    Assert.assertEquals("file-audit/", resp["file-audit/"]?.path)
    Assert.assertEquals("/tmp/vault", resp["file-audit/"]?.options?.get("path"))

    Assert.assertEquals("file", resp["file-audit2/"]?.type)
    Assert.assertEquals("", resp["file-audit2/"]?.description)
    Assert.assertEquals("file-audit2/", resp["file-audit2/"]?.path)
    Assert.assertEquals("/tmp/vault2", resp["file-audit2/"]?.options?.get("path"))
  }

  @Test
  fun testEnableAudit(): Unit {
    val request = HttpRequest.request()
        .withMethod("PUT")
        .withHeader(tokenHeader)
        .withPath("/v1/sys/audit/file-audit")
        .withBody("""{"type":"file","description":"","options":{"path":"/tmp/vault"}}""")
    val mockServerClient = MockServerClient(localhost, mockServerRule.port)

    mockServerClient
        .`when`(request)
        .respond(
            HttpResponse.response()
                .withStatusCode(HttpStatusCode.NO_CONTENT_204.code())
                .withHeader(contentJson)
        )
    Assert.assertTrue(audit.enable("file-audit", "file", options = listOf("path" to "/tmp/vault")))

    mockServerClient.verify(request, VerificationTimes.once())
  }

  @Test
  fun testDisableAudit(): Unit {
    val request = HttpRequest.request()
        .withMethod("DELETE")
        .withHeader(tokenHeader)
        .withPath("/v1/sys/audit/file-audit")
    val mockServerClient = MockServerClient(localhost, mockServerRule.port)

    mockServerClient
        .`when`(request)
        .respond(
            HttpResponse.response()
                .withStatusCode(HttpStatusCode.NO_CONTENT_204.code())
                .withHeader(contentJson)
        )
    Assert.assertTrue(audit.disable("file-audit"))

    mockServerClient.verify(request, VerificationTimes.once())
  }
}