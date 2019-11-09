package com.github.jake.repos

import android.app.Application
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.github.jake.repos.data.repositories.IRepository
import com.github.jake.repos.data.repositories.RepositoryImpl
import com.github.jake.repos.data.sources.Database
import com.github.jake.repos.data.sources.IRemoteDataSource
import com.github.jake.repos.domain.GetRepositoriesUseCase
import com.github.jake.repos.presentation.RepositoriesViewModel
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RepositoriesApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RepositoriesApplication)

            modules(module {

                single<Interceptor> {
                    HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
                }

                single {
                    OkHttpClient.Builder()
                        .addInterceptor(get<Interceptor>())
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .build()
                }

                single { GsonBuilder().serializeNulls().create() }

                single<Retrofit> {
                    Retrofit.Builder()
                        .baseUrl("https://api.github.com/")
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(get()))
                        .client(get())
                        .build()
                }

                single {
                    get<Retrofit>().create(IRemoteDataSource::class.java) as IRemoteDataSource
                }

                single {
                    Room.databaseBuilder(
                        applicationContext,
                        Database::class.java,
                        BuildConfig.APPLICATION_ID
                    ).build()
                }

                single {
                    get<Database>().repoDao()
                }

                single {
                    RepositoryImpl(get(), get()) as IRepository
                }

                single {
                    GetRepositoriesUseCase(get())
                }

                viewModel {
                    RepositoriesViewModel(get())
                }
            })
        }
    }
}