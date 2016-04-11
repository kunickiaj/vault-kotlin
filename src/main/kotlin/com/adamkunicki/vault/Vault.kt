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

package com.adamkunicki.vault

import com.adamkunicki.vault.api.AuthToken
import com.adamkunicki.vault.api.Authenticate
import com.adamkunicki.vault.api.Logical
import com.adamkunicki.vault.api.Sys

class Vault(private val conf: VaultConfiguration) {
  val auth = Authenticate(conf)
  val authToken = AuthToken(conf)

  val logical = Logical(conf)

  val sys = Sys(conf)
}

