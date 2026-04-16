package io.rekast.sdk.sample.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.rekast.sdk.model.authentication.credentials.AccessTokenCredentials;
import io.rekast.sdk.model.authentication.credentials.BasicAuthCredentials;
import io.rekast.sdk.utils.MomoApiConfig;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class NetworkModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<HttpLoggingInterceptor> httpLoggingInterceptorProvider;

  private final Provider<BasicAuthCredentials> basicAuthCredentialsProvider;

  private final Provider<AccessTokenCredentials> accessTokenCredentialsProvider;

  private final Provider<MomoApiConfig> configProvider;

  private NetworkModule_ProvideOkHttpClientFactory(
      Provider<HttpLoggingInterceptor> httpLoggingInterceptorProvider,
      Provider<BasicAuthCredentials> basicAuthCredentialsProvider,
      Provider<AccessTokenCredentials> accessTokenCredentialsProvider,
      Provider<MomoApiConfig> configProvider) {
    this.httpLoggingInterceptorProvider = httpLoggingInterceptorProvider;
    this.basicAuthCredentialsProvider = basicAuthCredentialsProvider;
    this.accessTokenCredentialsProvider = accessTokenCredentialsProvider;
    this.configProvider = configProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(httpLoggingInterceptorProvider.get(), basicAuthCredentialsProvider.get(), accessTokenCredentialsProvider.get(), configProvider.get());
  }

  public static NetworkModule_ProvideOkHttpClientFactory create(
      Provider<HttpLoggingInterceptor> httpLoggingInterceptorProvider,
      Provider<BasicAuthCredentials> basicAuthCredentialsProvider,
      Provider<AccessTokenCredentials> accessTokenCredentialsProvider,
      Provider<MomoApiConfig> configProvider) {
    return new NetworkModule_ProvideOkHttpClientFactory(httpLoggingInterceptorProvider, basicAuthCredentialsProvider, accessTokenCredentialsProvider, configProvider);
  }

  public static OkHttpClient provideOkHttpClient(HttpLoggingInterceptor httpLoggingInterceptor,
      BasicAuthCredentials basicAuthCredentials, AccessTokenCredentials accessTokenCredentials,
      MomoApiConfig config) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttpClient(httpLoggingInterceptor, basicAuthCredentials, accessTokenCredentials, config));
  }
}
