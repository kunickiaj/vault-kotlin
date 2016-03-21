import com.github.kittinunf.fuel.httpGet
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
  val token = "aad8d6c7-3ea8-26e4-4b1a-a5643028b991"
  val address = "http://localhost:8200"

  @Test
  fun testToken(): Unit {
    val conf = VaultConfiguration(address, token)
    val vault = Vault(conf)

    val secret = vault.auth.token(token)

    println(secret)
    LOG.info(secret.toString())
  }
}