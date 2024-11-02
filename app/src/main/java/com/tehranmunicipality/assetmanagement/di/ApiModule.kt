package com.tehranmunicipality.assetmanagement.di


import com.google.gson.GsonBuilder
import com.tehranmunicipality.assetmanagement.data.REQUEST_CONNECT_TIMEOUT
import com.tehranmunicipality.assetmanagement.data.REQUEST_READ_TIMEOUT
import com.tehranmunicipality.assetmanagement.data.api.AssetManagementApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.KeyStore
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.*

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val BASE_URL = "https://srv165-apigateway.tehran.ir/ApiContainer.Finanace.RCL1/"

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {

        //Prevent SSL handshake timeout =================================================//

        val trustManagerFactory: TrustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        )
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers: Array<TrustManager> = trustManagerFactory.getTrustManagers()
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
            ("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers))
            also {  }
        }
        val trustManager: X509TrustManager = trustManagers[0] as X509TrustManager

        val sslContext: SSLContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
        val sslSocketFactory: SSLSocketFactory = sslContext.getSocketFactory()

        //=============================================================================//

        val client: OkHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustManager)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(REQUEST_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(REQUEST_READ_TIMEOUT, TimeUnit.SECONDS)
            .build()
        return client
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideAssetManagementApi(retrofit: Retrofit): AssetManagementApi =
        retrofit.create(AssetManagementApi::class.java)

}