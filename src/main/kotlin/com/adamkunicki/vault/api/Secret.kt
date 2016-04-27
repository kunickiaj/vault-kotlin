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

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import java.io.Reader

data class Secret(
    val lease_id: String,
    val renewable: Boolean,
    val lease_duration: Int,
    val data: Map<String, Any?>,
    val auth: SecretAuth,
    val errors: Array<String>
) {
  class Deserializer : ResponseDeserializable<Secret> {
    override fun deserialize(reader: Reader): Secret = Gson().fromJson<Secret>(reader)
  }
}