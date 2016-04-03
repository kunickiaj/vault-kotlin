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
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPut
import com.github.salomonbrys.kotson.jsonObject


class Logical(private val conf: VaultConfiguration) {
  val UTF_8 = Charsets.UTF_8.name()

  fun list(path: String): Secret {
    val (request, response, result) = (conf.adddress + "/v1/" + path)
        .httpGet(listOf(Pair("list", true)))
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Secret.Deserializer())

    return result
  }

  fun read(path: String): Secret {
    val (request, response, result) = (conf.adddress + "/v1/" + path)
        .httpGet()
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Secret.Deserializer())

    return result
  }

  fun write(path: String, data: List<Pair<String, Any?>>): Boolean {
    val (request, response, result) = (conf.adddress + "/v1/" + path)
        .httpPut()
        .body(jsonObject(*data.toTypedArray()).toString(), Charsets.UTF_8)
        .header(Pair("X-Vault-Token", conf.token))
        .response()

    return true
  }

  fun delete(path: String): Boolean {
    val (request, response, result) = (conf.adddress + "/v1/" + path)
        .httpDelete()
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Secret.Deserializer())

    return true
  }
}