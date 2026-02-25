package com.example.myhome.network.auth

import com.example.myhome.local.DataManager
import com.example.myhome.local.DataManager2
import com.example.myhome.local.MySharedPreference
import com.example.myhome.network.ApiConnect
import com.example.myhome.network.AuthEvent
import com.example.myhome.network.AuthEventBus
import com.example.myhome.network.api.AuthService
import dagger.Lazy
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator2 @Inject constructor(
    private val authService: Lazy<AuthService>,
    private val local: DataManager2
) : Authenticator {

    @OptIn(DelicateCoroutinesApi::class)
    override fun authenticate(route: Route?, response: Response): Request? {

        if (responseCount(response) >= 2) {
            return null
        }

        val user = local.getUser()
        // 3️⃣ Gọi API refresh token (SYNC)
        val newToken = runBlocking {
            authService.get().refreshToken(user).apply {
            }
        }
        newToken.code.let{
            when(it){
                "INVALID_REFRESH_TOKEN" -> {
                    GlobalScope.launch {
                        AuthEventBus.authEvent.emit(AuthEvent.InvalidRefreshToken("Phiên đăng nhập không hợp lệ ,vui lòng đăng nhập lại"))
                    }
                    return null
                }
                "REFRESH_TOKEN_EXPIRED" -> {
                    GlobalScope.launch {
                        AuthEventBus.authEvent.emit(AuthEvent.RefreshTokenExpired("Phiên đăng nhập hết hạn ,vui lòng đăng nhập lại"))
                    }
                    return null

                }
                "REFRESH_TOKEN_REVOKED" -> {
                    GlobalScope.launch {
                        AuthEventBus.authEvent.emit(AuthEvent.InvalidRefreshToken("Phiên đăng nhập không hợp lệ ,vui lòng đăng nhập lại"))
                    }
                    return null

                }
                else ->{}
            }
        }
        // 4️⃣ Lưu token mới
        newToken.access_token.let { it->
            local.saveUser(newToken)
        }


        // 5️⃣ Tạo request mới (retry)
        return response.request.newBuilder()
            .header("Authorization", "Bearer ${local.getUser().access_token}")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}

class AuthInterceptor2 @Inject constructor(
    private val local: DataManager2
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.encodedPath

        val noAuthPaths = listOf(
            "/auth/login",
            "/auth/register",
            "/auth/refresh",
            "auth/reset-after-forgot",
            "auth/forgot"
        )

        if (noAuthPaths.any { url.contains(it) }) {
            return chain.proceed(request)
        }

        val token = local.getUser().access_token

        val newRequest = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else request

        return chain.proceed(newRequest)

    }
}
