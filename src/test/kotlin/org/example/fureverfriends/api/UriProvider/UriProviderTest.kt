package org.example.fureverfriends.api.UriProvider

import org.assertj.core.api.Assertions.assertThat
import org.example.fureverfriends.api.uriprovider.UserFollowingUriProviderImpl
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

    @Nested
    inner class UserFollowingUriProviderTests {
        @Test
        fun testBaseUri() {
            val url = "https://test.com"
            val expectedUri = "/api/v1/relation"
            val provider = UserFollowingUriProviderImpl(
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
        fun testFollowingRequestUri() {
            val url = "https://test.com"
            val expectedUri = "/api/v1/relation/follow"
            val provider = UserFollowingUriProviderImpl(
                urlProperties = mock {
                    on { externalUrl } doReturn url
                }
            )

            val returnedUri = provider.getFollowingRequestUri()

            assertAll(
                { assertThat(returnedUri).isEqualTo(expectedUri) },
                { assertThat(provider.toExternalUri(returnedUri)).isEqualTo("$url$expectedUri") }
            )
        }

        @Test
        fun testFollowersUri() {
            val url = "https://test.com"
            val expectedUri = "/api/v1/relation/followers"
            val provider = UserFollowingUriProviderImpl(
                urlProperties = mock {
                    on { externalUrl } doReturn url
                }
            )

            val returnedUri = provider.getFollowersUri()

            assertAll(
                { assertThat(returnedUri).isEqualTo(expectedUri) },
                { assertThat(provider.toExternalUri(returnedUri)).isEqualTo("$url$expectedUri") }
            )
        }

        @Test
        fun testFollowingsUri() {
            val url = "https://test.com"
            val expectedUri = "/api/v1/relation/followings"
            val provider = UserFollowingUriProviderImpl(
                urlProperties = mock {
                    on { externalUrl } doReturn url
                }
            )

            val returnedUri = provider.getFollowingsUri()

            assertAll(
                { assertThat(returnedUri).isEqualTo(expectedUri) },
                { assertThat(provider.toExternalUri(returnedUri)).isEqualTo("$url$expectedUri") }
            )
        }

        @Test
        fun testAcceptingRequestUri() {
            val url = "https://test.com"
            val expectedUri = "/api/v1/relation/accept"
            val provider = UserFollowingUriProviderImpl(
                urlProperties = mock {
                    on { externalUrl } doReturn url
                }
            )

            val returnedUri = provider.getAcceptingRequestUri()

            assertAll(
                { assertThat(returnedUri).isEqualTo(expectedUri) },
                { assertThat(provider.toExternalUri(returnedUri)).isEqualTo("$url$expectedUri") }
            )
        }

        @Test
        fun testRejectingRequestUri() {
            val url = "https://test.com"
            val expectedUri = "/api/v1/relation/reject"
            val provider = UserFollowingUriProviderImpl(
                urlProperties = mock {
                    on { externalUrl } doReturn url
                }
            )

            val returnedUri = provider.getRejectingRequestUri()

            assertAll(
                { assertThat(returnedUri).isEqualTo(expectedUri) },
                { assertThat(provider.toExternalUri(returnedUri)).isEqualTo("$url$expectedUri") }
            )
        }
    }
}