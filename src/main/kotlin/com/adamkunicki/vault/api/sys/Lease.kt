package com.adamkunicki.vault.api.sys

import com.adamkunicki.vault.VaultConfiguration
import com.adamkunicki.vault.VaultError
import com.adamkunicki.vault.VaultException
import com.adamkunicki.vault.api.Secret
import com.github.kittinunf.fuel.httpPut
import com.google.gson.Gson

@Suppress("UNUSED_VARIABLE")
class Lease(private val conf: VaultConfiguration) {

  @Throws(VaultException::class)
  fun renew(leaseId: String): Secret {
    val (request, response, result) = (conf.adddress + "/v1/sys/renew/" + leaseId)
        .httpPut()
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Secret.Deserializer())

    val (secret, error) = result

    if (secret != null) {
      return secret
    }
    val errorMessage = if (error != null) {
      Gson().fromJson(String(error.errorData), VaultError::class.java).errors.joinToString()
    } else {
      ""
    }
    throw VaultException(errorMessage)
  }
}
