package io.rekast.sdk.sample.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.rekast.sdk.model.authentication.credentials.BasicAuthCredentials;
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
public final class NetworkModule_ProvideBasicAuthCredentialsFactory implements Factory<BasicAuthCredentials> {
  @Override
  public BasicAuthCredentials get() {
    return provideBasicAuthCredentials();
  }

  public static NetworkModule_ProvideBasicAuthCredentialsFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static BasicAuthCredentials provideBasicAuthCredentials() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideBasicAuthCredentials());
  }

  private static final class InstanceHolder {
    static final NetworkModule_ProvideBasicAuthCredentialsFactory INSTANCE = new NetworkModule_ProvideBasicAuthCredentialsFactory();
  }
}
