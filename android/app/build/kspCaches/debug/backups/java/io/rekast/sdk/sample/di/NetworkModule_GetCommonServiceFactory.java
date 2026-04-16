package io.rekast.sdk.sample.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.rekast.sdk.network.service.products.CommonService;
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
public final class NetworkModule_GetCommonServiceFactory implements Factory<CommonService> {
  private final Provider<Retrofit> retrofitProvider;

  private NetworkModule_GetCommonServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public CommonService get() {
    return getCommonService(retrofitProvider.get());
  }

  public static NetworkModule_GetCommonServiceFactory create(Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_GetCommonServiceFactory(retrofitProvider);
  }

  public static CommonService getCommonService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.getCommonService(retrofit));
  }
}
