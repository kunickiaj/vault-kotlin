import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.JsonParser
import java.io.Reader

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

data class Secret(
    val lease_id: String,
    val renewable: Boolean,
    val lease_duration: Int,
    val data: Map<String, Any?>
) {
  class Deserializer : ResponseDeserializable<Secret> {
    override fun deserialize(reader: Reader): Secret {
      val parser = JsonParser()
      val parsed = parser.parse(reader).asJsonObject
      println(parsed)

      val data = parsed.get("data").asJsonObject
      val dataMap = data.entrySet().fold(mutableMapOf<String, String>()) { acc, next ->
        if (next.value.isJsonPrimitive) {
          acc.put(next.key, next.value.asString)
        } else {
          println(next.value)
        }
        acc
      }
      return Secret(
          parsed.get("lease_id").asString,
          parsed.get("renewable").asBoolean,
          parsed.get("lease_duration").asInt,
          dataMap
      )
    }
  }
}