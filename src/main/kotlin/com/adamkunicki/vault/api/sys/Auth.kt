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

package com.adamkunicki.vault.api.sys

import com.adamkunicki.vault.VaultConfiguration
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonParser
import java.io.Reader
import java.net.URLEncoder

class Auth(private val conf: VaultConfiguration) {
  val UTF_8 = Charsets.UTF_8.name()

  class Deserializer : ResponseDeserializable<Map<String, Any?>> {
    override fun deserialize(reader: Reader): Map<String, Any?>? {
      val parser = JsonParser()
      val parsed = parser.parse(reader).asJsonObject

      return parsed.entrySet().fold(mutableMapOf<String, String>()) { acc, next ->
        if (next.value.isJsonPrimitive) {
          acc.put(next.key, next.value.asString)
        } else {
          println(next.value)
        }
        acc
      }
    }
  }

  fun auths(): Map<String, Any?> {
    val (request, response, result) = (conf.adddress + "/v1/sys/auth")
        .httpGet()
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Deserializer())

    return result
  }

  fun enable_auth(path: String, type: String, description: String = ""): Boolean {
    val body = jsonObject("type" to type)
    if (description.isNotBlank()) {
      body.addProperty("description", description)
    }
    (conf.adddress + "/v1/sys/auth/" + URLEncoder.encode(path, UTF_8))
        .httpPost()
        .body(body.toString(), Charsets.UTF_8)
        .header(Pair("X-Vault-Token", conf.token))
        .response()

    return true
  }

  fun disable_auth(path: String): Boolean {
    (conf.adddress + "/v1/sys/auth/" + URLEncoder.encode(path, UTF_8))
        .httpDelete()
        .header(Pair("X-Vault-Token", conf.token))
        .response()

    return true
  }
}