package com.github2136.base

import com.orhanobut.logger.Logger
import okhttp3.Interceptor
import okhttp3.Response
import java.nio.charset.Charset

/**
 * Created by YB on 2019/5/5
 * OKHTTP拦截器
 */
class OkHttpInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestUrl = request.url()
        val method = request.method()
        val requestHeads = request.headers()
        val response = chain.proceed(request)
        val code = response.code()
        val responseHeads = response.headers()
        val responseBody = response.body()
        var body = ""
        responseBody?.apply {
            val contentLength = contentLength()
            val source = source()
            source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.+  Charset charset = UTF8;
            var buffer = source.buffer()

            var charset: Charset? = Charset.forName("UTF-8")
            val contentType = contentType()

            if (contentType != null) {
                charset = contentType.charset(Charset.forName("UTF-8"))
            }
            if (contentLength != 0L) {
                body = buffer.clone().readString(charset!!)
            }
        }

        Logger.t("HTTP")
            .d(
                """
                    |$method $requestUrl
                    |${if (requestHeads.size() > 0) {
                    "Header\n$requestHeads"
                } else {
                    ""
                }}
                    |Code $code
                    |${if (responseHeads.size() > 0) {
                    "Header\n$responseHeads"
                } else {
                    ""
                }}
                    |Body $body
                """.trimMargin()
            )
        return response
    }
}