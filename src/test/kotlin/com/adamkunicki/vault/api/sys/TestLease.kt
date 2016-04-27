package com.adamkunicki.vault.api.sys

import com.adamkunicki.vault.VaultConfiguration
import com.adamkunicki.vault.VaultException
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockserver.client.server.MockServerClient
import org.mockserver.junit.MockServerRule
import org.mockserver.matchers.Times
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.HttpStatusCode
import org.slf4j.LoggerFactory
import java.io.Reader

class TestLease {
  companion object {
    val LOG = LoggerFactory.getLogger(Lease::class.java)
  }

  @get:Rule
  val thrown = ExpectedException.none()

  @get:Rule
  val mockServerRule = MockServerRule(this)

  val token = "d25dd11c-ec80-b00c-31de-1c62222f356d"
  val tokenHeader = Header.header("X-Vault-Token", token)
  val contentJson = Header.header("Content-Type", "application/json")
  val localhost = "127.0.0.1"
  val address = "http://" + localhost + ":" + mockServerRule.port
  val conf = VaultConfiguration(address, token)
  val lease = Lease(conf)
  val leaseId = "aws/creds/s3ReadOnly/895aad27-028e-301b-5475-906a6561f8f8"

  private fun getReader(resource: String): Reader {
    return javaClass.getResource(resource).openStream().bufferedReader()
  }

  @Test
  fun testRenewal(): Unit {
    MockServerClient(localhost, mockServerRule.port)
        .`when`(
            HttpRequest.request()
                .withMethod("PUT")
                .withHeader(tokenHeader)
                .withPath("/v1/sys/renew/" + leaseId),
            Times.exactly(1)
        )
        .respond(
            HttpResponse.response()
                .withStatusCode(HttpStatusCode.OK_200.code())
                .withHeader(contentJson)
                .withBody(getReader("/sys_lease_renew.json").readText())
        )
    val resp = lease.renew(leaseId)
    Assert.assertEquals(leaseId, resp.lease_id)
    Assert.assertEquals(60, resp.lease_duration)
    Assert.assertEquals(true, resp.renewable)
  }

  @Test
  fun testRenewalFailure(): Unit {
    thrown.expect(VaultException::class.java)
    MockServerClient(localhost, mockServerRule.port)
        .`when`(
            HttpRequest.request()
                .withMethod("PUT")
                .withHeader(tokenHeader)
                .withPath("/v1/sys/renew/" + leaseId),
            Times.exactly(1)
        )
        .respond(
            HttpResponse.response()
                .withStatusCode(HttpStatusCode.NO_CONTENT_204.code())
                .withHeader(contentJson)
//                .withBody(getReader("/sys_lease_renew_error.json").readText())
        )
    lease.renew(leaseId)
  }
}