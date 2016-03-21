import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import java.net.URLEncoder
import java.nio.charset.Charset

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

class Authenticate(private val conf: VaultConfiguration) {

  fun token(newToken: String): Secret {
    val (request, response, result) = (conf.adddress + "/v1/auth/token/lookup-self")
        .httpGet()
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Secret.Deserializer())

    return result
  }

  fun appId(appId: String, userId: String, options: List<Pair<String, Any?>> = emptyList()): Secret {
    val (request, response, result) = (conf.adddress + "/v1/auth/app-id/login")
        .httpPost(listOf(Pair("app_id", appId), Pair("user_id", userId)) + options)
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Secret.Deserializer())

    return result
  }

  fun userpass(username: String, password: String, options: List<Pair<String, Any?>> = emptyList()): Secret {
    val (request, response, result) = (conf.adddress + "/v1/auth/userpass/login" + URLEncoder.encode(username))
        .httpPost(listOf(Pair("password", password)) + options)
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Secret.Deserializer())

    return result
  }

  fun ldap(username: String, password: String, options: List<Pair<String, Any?>> = emptyList()): Secret {
    val (request, response, result) = (conf.adddress + "/v1/auth/ldap/login" +
            URLEncoder.encode(username, Charsets.UTF_8.name()))
        .httpPost(listOf(Pair("password", password)) + options)
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Secret.Deserializer())

    return result
  }

  fun github(githubToken: String): Secret {
    val (request, response, result) = (conf.adddress + "/v1/auth/github/login")
        .httpPost(listOf(Pair("token", githubToken)))
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Secret.Deserializer())

    return result
  }
}