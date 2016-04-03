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

data class VaultConfiguration(
    val adddress: String = VAULT_ADDRESS,
    val token: String = "",
    val openTimeout: Int = 0,
    val proxyAddress: String = "",
    val proxyPort: Int = 8080,
    val proxyUsername: String = "",
    val proxyPassword: String = "",
    val readTimeout: Int = 0,
    val sslCiphers: String = SSL_CIPHERS,
    val sslPemFile: String = "",
    val sslPemPassphrase: String = "",
    val sslCaCert: String = "",
    val sslCaPath: String = "",
    val sslVerify: Boolean = true,
    val sslTimeout: Int = 0,
    val timeout: Int = 0
) {
  companion object {
    // The default vault address.
    val VAULT_ADDRESS = "https://127.0.0.1:8200"

    //    val VAULT_DISK_TOKEN

    // SSL ciphers to allow. This should not be changed.
    val SSL_CIPHERS = "TLSv1.2"

    // The default number of request retry attempts.
    val RETRY_ATTEMPTS = 2

    // The default backoff interval for retries.
    val RETRY_BASE = 0.05

    // The maximum amount of time for a single exponential backoff to sleep.
    val RETRY_MAX_WAIT = 2.0
  }
}