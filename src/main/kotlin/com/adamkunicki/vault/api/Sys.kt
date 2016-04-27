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
import com.adamkunicki.vault.api.sys.Audit
import com.adamkunicki.vault.api.sys.Auth
import com.adamkunicki.vault.api.sys.Lease

class Sys(private val conf: VaultConfiguration) {
  val auth = Auth(conf)
  val audit = Audit(conf)
  val lease = Lease(conf)
}