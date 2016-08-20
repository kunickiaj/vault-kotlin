# vault-kotlin
[ ![Download](https://api.bintray.com/packages/kunickiaj/maven/vault-kotlin/images/download.svg) ](https://bintray.com/kunickiaj/maven/vault-kotlin/_latestVersion)
[ ![Kotlin](https://img.shields.io/badge/Kotlin-1.0.3-blue.svg)](http://kotlinlang.org)
[![Build Status](https://travis-ci.org/kunickiaj/vault-kotlin.svg?branch=master)](https://travis-ci.org/kunickiaj/vault-kotlin)
[![License](https://img.shields.io/github/license/kunickiaj/vault-kotlin.svg)](https://github.com/kunickiaj/vault-kotlin/blob/master/LICENSE)


Hashicorp Vault client library written in Kotlin
================================================

Install
-------

Releases
========

Releases are published to [jCenter](https://bintray.com/bintray/jcenter).

Building From Source
====================

Gradle:

```./gradlew install```

Basic Usage
-----------

```kotlin
val conf = VaultConfiguration("https://vault:8200", "vault token")
val vault = Vault(conf)

vault.logical.write("/secret/hello", listOf("value" to "world"))
val secret = vault.logical.read("/secret")

if (secret.data["value"] == "world") {
  println("It works!")
}
```

For more examples please take a look at the tests.

__Note:__ This library is still under heavy development and the API is not yet stable. Use at your own risk! Once it has stabilized I'll begin publishing artifacts.

Contributions are welcome.
