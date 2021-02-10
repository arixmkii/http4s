/*
 * Copyright 2013 http4s.org
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

package org.http4s
package parser

import cats.data.NonEmptyList
import org.http4s.headers.Authorization
import org.http4s.syntax.all._

class AuthorizationHeaderSuite extends munit.FunSuite {
  def hparse(value: String) = HttpHeaderParser.AUTHORIZATION(value)

  test("Authorization header should Parse a valid OAuth2 header") {
    val token = (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') ++ "-._~+/".toSeq).mkString
    val h = Authorization(Credentials.Token(AuthScheme.Bearer, token + "="))
    assertEquals(hparse(h.value), Right(h))
  }

  test("Authorization header should Reject an invalid OAuth2 header") {
    val invalidTokens = Seq("f!@", "=abc", "abc d")
    invalidTokens.foreach { token =>
      val h = Authorization(Credentials.Token(AuthScheme.Bearer, token))
      assert(hparse(h.value).isLeft)
    }
  }

  test("Authorization header should Parse a KeyValueCredentials header") {
    val scheme = "foo"
    val params = NonEmptyList("abc" -> "123", Nil)
    val h = Authorization(Credentials.AuthParams(scheme.ci, params))
    assertEquals(hparse(h.value), Right(h))
  }
}