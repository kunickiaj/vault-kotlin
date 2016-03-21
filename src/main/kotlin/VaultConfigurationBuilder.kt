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

class VaultConfigurationBuilder {
  var address: String = ""
  var token: String = ""
  var openTimeout: String = ""
  var proxyAddress: String = ""
  var proxyPort: Int = 0
  var proxyUsername: String = ""
  var proxyPassword: String = ""
  var readTimeout: Int = 0
  var sslPemFile: String = ""
  var sslPemPassphrase: String = ""
  var sslCaCert: String = ""
  var sslCaPath: String = ""
  var sslVerify: Boolean = true
  var sslTimeout: Int = 0
  var timeout: Int = 0

  companion object {
    fun builder(): VaultConfigurationBuilder {
      return VaultConfigurationBuilder()
    }
  }
  
  fun withAddress(address: String): VaultConfigurationBuilder {
    this.address = address
    return this
  }


}