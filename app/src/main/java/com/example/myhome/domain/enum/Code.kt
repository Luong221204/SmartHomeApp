package com.example.myhome.domain.enum

enum class RefreshTokenCode(val message: String) {
    REFRESH_TOKEN_REVOKED("phiên đăng nhập không hợp lệ , vui lòng đăng nhập lại"),
    REFRESH_TOKEN_EXPIRED("phiên đăng nhập đã hết hạn , vui lòng đăng nhập lại"),
    INVALID_REFRESH_TOKEN("phiên đăng nhập không hợp lệ , vui lòng đăng nhập lại"),

}