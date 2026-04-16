package io.rekast.sdk.sample.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.rekast.sdk.network.service.products.DisbursementsService;
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
public final class NetworkModule_GetDisbursementFactory implements Factory<DisbursementsService> {
  private final Provider<Retrofit> retrofitProvider;

  private NetworkModule_GetDisbursementFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public DisbursementsService get() {
    return getDisbursement(retrofitProvider.get());
  }

  public static NetworkModule_GetDisbursementFactory create(Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_GetDisbursementFactory(retrofitProvider);
  }

  public static DisbursementsService getDisbursement(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.getDisbursement(retrofit));
  }
}
