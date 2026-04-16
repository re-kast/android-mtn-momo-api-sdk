package io.rekast.sdk.sample.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.rekast.sdk.model.authentication.credentials.AccessTokenCredentials;
import io.rekast.sdk.model.authentication.credentials.BasicAuthCredentials;
import io.rekast.sdk.network.interfaces.auth.AuthInterface;
import javax.annotation.processing.Generated;

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
public final class NetworkModule_ProvideApiAuthenticatorFactory implements Factory<AuthInterface> {
  private final Provider<BasicAuthCredentials> basicAuthCredentialsProvider;

  private final Provider<AccessTokenCredentials> accessTokenCredentialsProvider;

  private NetworkModule_ProvideApiAuthenticatorFactory(
      Provider<BasicAuthCredentials> basicAuthCredentialsProvider,
      Provider<AccessTokenCredentials> accessTokenCredentialsProvider) {
    this.basicAuthCredentialsProvider = basicAuthCredentialsProvider;
    this.accessTokenCredentialsProvider = accessTokenCredentialsProvider;
  }

  @Override
  public AuthInterface get() {
    return provideApiAuthenticator(basicAuthCredentialsProvider.get(), accessTokenCredentialsProvider.get());
  }

  public static NetworkModule_ProvideApiAuthenticatorFactory create(
      Provider<BasicAuthCredentials> basicAuthCredentialsProvider,
      Provider<AccessTokenCredentials> accessTokenCredentialsProvider) {
    return new NetworkModule_ProvideApiAuthenticatorFactory(basicAuthCredentialsProvider, accessTokenCredentialsProvider);
  }

  public static AuthInterface provideApiAuthenticator(BasicAuthCredentials basicAuthCredentials,
      AccessTokenCredentials accessTokenCredentials) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideApiAuthenticator(basicAuthCredentials, accessTokenCredentials));
  }
}
