package com.eurail.app

import android.app.Application
import com.eurail.app.data.remote.ArticleApiService
import com.eurail.app.data.remote.KtorArticleApiService
import com.eurail.app.data.remote.interceptor.MockInterceptor
import com.eurail.app.data.ArticleRepositoryImpl
import com.eurail.app.domain.repository.ArticleRepository
import com.eurail.app.work.ArticlePrefetchWorker
import com.eurail.shared.cache.ArticleCache
import com.eurail.shared.cache.InMemoryArticleCache
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

class EurailApplication : Application() {

    lateinit var articleCache: ArticleCache
        private set

    lateinit var articleApiService: ArticleApiService
        private set

    lateinit var articleRepository: ArticleRepository
        private set

    lateinit var httpClient: HttpClient
        private set

    override fun onCreate() {
        super.onCreate()

        articleCache = InMemoryArticleCache()
        
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(MockInterceptor(errorProbability = 0.15f))
            .build()

        httpClient = HttpClient(OkHttp) {
            engine {
                preconfigured = okHttpClient
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
        }

        articleApiService = KtorArticleApiService(
            httpClient = httpClient,
            simulateNetworkDelay = true
        )
        articleRepository = ArticleRepositoryImpl(articleApiService, articleCache)

        ArticlePrefetchWorker.schedule(this)
    }
}
