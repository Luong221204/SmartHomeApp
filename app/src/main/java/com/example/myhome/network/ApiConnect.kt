package com.example.myhome.network
import android.util.Log
import com.example.myhome.domain.User
import com.example.myhome.domain.enum.RefreshTokenCode
import com.example.myhome.local.DataManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AuthInterceptor() : Interceptor {
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

        val token = DataManager.getUser().access_token

        val newRequest = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else request

        return chain.proceed(newRequest)

    }
}
class TokenAuthenticator(
) : Authenticator {

    @OptIn(DelicateCoroutinesApi::class)
    override fun authenticate(route: Route?, response: Response): Request? {

        // 1️⃣ Nếu request đã retry quá nhiều → bỏ
        if (responseCount(response) >= 2) {
            return null
        }

        val user = DataManager.getUser()
        // 3️⃣ Gọi API refresh token (SYNC)
        val newToken = runBlocking {
            ApiConnect.service?.refreshToken(user).apply {
            }
        }
        newToken?.body()?.code.let{
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
        newToken?.body()?.access_token.let {it->
            DataManager.saveUser(newToken?.body()!!)
        }


        // 5️⃣ Tạo request mới (retry)
        return response.request.newBuilder()
            .header("Authorization", "Bearer ${DataManager.getUser().access_token}")
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

object ApiConnect {

    private var token: String? = null

    private fun getClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .authenticator(TokenAuthenticator()) // <-- interceptor gắn token
            .build()
    }

    var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.122:5435/")
        .client(getClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var service: Service? = retrofit.create(Service::class.java)
}

object AuthEventBus {
    val authEvent = MutableSharedFlow<AuthEvent>()
}

sealed class AuthEvent(val message: String) {
    class RefreshTokenExpired( message: String) : AuthEvent(message)
    class InvalidRefreshToken( message: String) : AuthEvent(message)
    class TokenRevoked( message: String) : AuthEvent(message)
}

