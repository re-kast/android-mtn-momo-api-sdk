package io.rekast.sdk.sample.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.rekast.sdk.network.service.products.CollectionService;
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
public final class NetworkModule_GetCollectionFactory implements Factory<CollectionService> {
  private final Provider<Retrofit> retrofitProvider;

  private NetworkModule_GetCollectionFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public CollectionService get() {
    return getCollection(retrofitProvider.get());
  }

  public static NetworkModule_GetCollectionFactory create(Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_GetCollectionFactory(retrofitProvider);
  }

  public static CollectionService getCollection(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.getCollection(retrofit));
  }
}
