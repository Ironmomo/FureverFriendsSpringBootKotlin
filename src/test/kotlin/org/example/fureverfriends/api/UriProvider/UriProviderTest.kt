package org.example.fureverfriends.api.UriProvider

import org.assertj.core.api.Assertions.assertThat
import org.example.fureverfriends.api.uriprovider.UserUriProviderImpl
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class UriProviderTest {

    @Nested
    inner class UserUriProviderTests {
        @Test
        fun testBaseUri() {
            val url = "https://test.com"
            val expectedUri = "/api/v1/user"
            val provider = UserUriProviderImpl(
                urlProperties = mock {
                    on { externalUrl } doReturn url
                }
            )

            val returnedUri = provider.baseUri

            assertAll(
                { assertThat(returnedUri).isEqualTo(expectedUri) },
                { assertThat(provider.toExternalUri(returnedUri)).isEqualTo("$url$expectedUri") }
            )
        }

        @Test
        fun testCreateUserUri() {
            val url = "https://test.com"
            val expectedUri = "/api/v1/user/create"
            val provider = UserUriProviderImpl(
                urlProperties = mock {
                    on { externalUrl } doReturn url
                }
            )

            val returnedUri = provider.getCreateUserUri()

            assertAll(
                { assertThat(returnedUri).isEqualTo(expectedUri) },
                { assertThat(provider.toExternalUri(returnedUri)).isEqualTo("$url$expectedUri") }
            )
        }
    }
}