package io.rekast.sdk.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.rekast.sdk.network.service.AuthenticationService;
import io.rekast.sdk.network.service.products.CommonService;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class DefaultSource_Factory implements Factory<DefaultSource> {
  private final Provider<AuthenticationService> authenticationServiceProvider;

  private final Provider<CommonService> commonServiceProvider;

  private DefaultSource_Factory(Provider<AuthenticationService> authenticationServiceProvider,
      Provider<CommonService> commonServiceProvider) {
    this.authenticationServiceProvider = authenticationServiceProvider;
    this.commonServiceProvider = commonServiceProvider;
  }

  @Override
  public DefaultSource get() {
    return newInstance(authenticationServiceProvider.get(), commonServiceProvider.get());
  }

  public static DefaultSource_Factory create(
      Provider<AuthenticationService> authenticationServiceProvider,
      Provider<CommonService> commonServiceProvider) {
    return new DefaultSource_Factory(authenticationServiceProvider, commonServiceProvider);
  }

  public static DefaultSource newInstance(AuthenticationService authenticationService,
      CommonService commonService) {
    return new DefaultSource(authenticationService, commonService);
  }
}
