package io.rekast.sdk.sample.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.rekast.sdk.model.authentication.credentials.AccessTokenCredentials;
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
public final class NetworkModule_ProvideAccessTokenCredentialsFactory implements Factory<AccessTokenCredentials> {
  @Override
  public AccessTokenCredentials get() {
    return provideAccessTokenCredentials();
  }

  public static NetworkModule_ProvideAccessTokenCredentialsFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AccessTokenCredentials provideAccessTokenCredentials() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideAccessTokenCredentials());
  }

  private static final class InstanceHolder {
    static final NetworkModule_ProvideAccessTokenCredentialsFactory INSTANCE = new NetworkModule_ProvideAccessTokenCredentialsFactory();
  }
}
