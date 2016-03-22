
import com.adamkunicki.vault.Vault
import com.adamkunicki.vault.VaultConfiguration
import com.adamkunicki.vault.api.Secret
import org.junit.Assert
import org.junit.Test
import org.slf4j.LoggerFactory

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

class TestAuthenticate {
  companion object {
    val LOG = LoggerFactory.getLogger(TestAuthenticate::class.java)
  }
  val token = "d25dd11c-ec80-b00c-31de-1c62222f356d"
  val address = "http://localhost:8200"

  @Test
  fun testTokenSercretDeserializer(): Unit {
    val secret = Secret.Deserializer().deserialize(javaClass.getResource("token.json").openStream().bufferedReader())
    Assert.assertEquals("b054c1d3-bfdf-098b-79e7-fbe02197a3c2", secret.data["id"])
  }

  @Test
  fun testStuff(): Unit {
    val vault = Vault(VaultConfiguration(address, token))

    LOG.info(vault.auth.token(token).toString())

    val auths = vault.sys.auth.auths()

    LOG.info(auths.toString())

    LOG.info(vault.logical.write("secret/hello", listOf("value" to "world")).toString())

    LOG.info(vault.logical.list("secret").toString())

    LOG.info(vault.logical.read("secret/hello").toString())
  }
}