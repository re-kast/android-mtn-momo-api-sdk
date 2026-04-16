package io.rekast.sdk.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.rekast.sdk.model.authentication.credentials.AccessTokenCredentials;
import io.rekast.sdk.model.authentication.credentials.BasicAuthCredentials;
import io.rekast.sdk.network.service.products.CollectionService;
import io.rekast.sdk.network.service.products.DisbursementsService;
import io.rekast.sdk.utils.MomoApiConfig;
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
public final class DefaultRepository_Factory implements Factory<DefaultRepository> {
  private final Provider<DefaultSource> defaultSourceProvider;

  private final Provider<DisbursementsService> disbursementsServiceProvider;

  private final Provider<CollectionService> collectionProvider;

  private final Provider<BasicAuthCredentials> basicAuthCredentialsTProvider;

  private final Provider<AccessTokenCredentials> accessTokenCredentialsTProvider;

  private final Provider<MomoApiConfig> configProvider;

  private DefaultRepository_Factory(Provider<DefaultSource> defaultSourceProvider,
      Provider<DisbursementsService> disbursementsServiceProvider,
      Provider<CollectionService> collectionProvider,
      Provider<BasicAuthCredentials> basicAuthCredentialsTProvider,
      Provider<AccessTokenCredentials> accessTokenCredentialsTProvider,
      Provider<MomoApiConfig> configProvider) {
    this.defaultSourceProvider = defaultSourceProvider;
    this.disbursementsServiceProvider = disbursementsServiceProvider;
    this.collectionProvider = collectionProvider;
    this.basicAuthCredentialsTProvider = basicAuthCredentialsTProvider;
    this.accessTokenCredentialsTProvider = accessTokenCredentialsTProvider;
    this.configProvider = configProvider;
  }

  @Override
  public DefaultRepository get() {
    return newInstance(defaultSourceProvider.get(), disbursementsServiceProvider.get(), collectionProvider.get(), basicAuthCredentialsTProvider.get(), accessTokenCredentialsTProvider.get(), configProvider.get());
  }

  public static DefaultRepository_Factory create(Provider<DefaultSource> defaultSourceProvider,
      Provider<DisbursementsService> disbursementsServiceProvider,
      Provider<CollectionService> collectionProvider,
      Provider<BasicAuthCredentials> basicAuthCredentialsTProvider,
      Provider<AccessTokenCredentials> accessTokenCredentialsTProvider,
      Provider<MomoApiConfig> configProvider) {
    return new DefaultRepository_Factory(defaultSourceProvider, disbursementsServiceProvider, collectionProvider, basicAuthCredentialsTProvider, accessTokenCredentialsTProvider, configProvider);
  }

  public static DefaultRepository newInstance(DefaultSource defaultSource,
      DisbursementsService disbursementsService, CollectionService collection,
      BasicAuthCredentials basicAuthCredentialsT, AccessTokenCredentials accessTokenCredentialsT,
      MomoApiConfig config) {
    return new DefaultRepository(defaultSource, disbursementsService, collection, basicAuthCredentialsT, accessTokenCredentialsT, config);
  }
}
