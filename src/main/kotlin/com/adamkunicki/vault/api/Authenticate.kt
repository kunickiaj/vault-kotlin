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
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.salomonbrys.kotson.jsonObject
import java.net.URLEncoder


class Authenticate(private val conf: VaultConfiguration) {
  val UTF_8 = Charsets.UTF_8.name()

  /**
   * Authenticate via the "token" authentication method. This authentication
   * method is a bit bizarre because you already have a token, but hey,
   * whatever floats your boat.
   * 
   * This method hits the `/v1/auth/token/lookup-self` endpoint after setting
   * the Vault client's token to the given token parameter. If the self lookup
   * succeeds, the token is returned.
   *
   * @return a [Secret] deserialized from the result.
   */
  fun token(newToken: String): Secret {
    val (request, response, result) = (conf.adddress + "/v1/auth/token/lookup-self")
        .httpGet()
        .header("X-Vault-Token" to newToken)
        .responseObject(Secret.Deserializer())

    return result
  }

  fun appId(appId: String, userId: String, options: List<Pair<String, Any?>> = emptyList()): Secret {
    val (request, response, result) = (conf.adddress + "/v1/auth/app-id/login")
        .httpPost()
        .body(
            jsonObject(
                "app_id" to appId,
                "user_id" to userId,
                *options.toTypedArray()
            ).toString(),
            Charsets.UTF_8
        )
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Secret.Deserializer())

    return result
  }

  fun userPass(username: String, password: String, options: List<Pair<String, Any?>> = emptyList()): Secret {
    val (request, response, result) = (conf.adddress + "/v1/auth/userpass/login/" + URLEncoder.encode(username, UTF_8))
        .httpPost()
        .body(
            jsonObject(
                "password" to password,
                *options.toTypedArray()
            ).toString(),
            Charsets.UTF_8
        )
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Secret.Deserializer())

    return result
  }

  fun ldap(username: String, password: String, options: List<Pair<String, Any?>> = emptyList()): Secret {
    val (request, response, result) = (conf.adddress + "/v1/auth/ldap/login/" + URLEncoder.encode(username, UTF_8))
        .httpPost()
        .body(
            jsonObject(
                "password" to password,
                *options.toTypedArray()
            ).toString(),
            Charsets.UTF_8
        )
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Secret.Deserializer())

    return result
  }

  fun github(githubToken: String): Secret {
    val (request, response, result) = (conf.adddress + "/v1/auth/github/login")
        .httpPost()
        .body(jsonObject("token" to githubToken).toString(), Charsets.UTF_8)
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Secret.Deserializer())

    return result
  }
}