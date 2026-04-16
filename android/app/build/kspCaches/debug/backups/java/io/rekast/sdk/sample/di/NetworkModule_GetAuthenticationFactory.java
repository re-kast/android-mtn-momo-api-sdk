package io.rekast.sdk.sample.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.rekast.sdk.network.service.AuthenticationService;
import javax.annotation.processing.Generated;
import retrofit2.Retrofit;

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
public final class NetworkModule_GetAuthenticationFactory implements Factory<AuthenticationService> {
  private final Provider<Retrofit> retrofitProvider;

  private NetworkModule_GetAuthenticationFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public AuthenticationService get() {
    return getAuthentication(retrofitProvider.get());
  }

  public static NetworkModule_GetAuthenticationFactory create(Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_GetAuthenticationFactory(retrofitProvider);
  }

  public static AuthenticationService getAuthentication(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.getAuthentication(retrofit));
  }
}
