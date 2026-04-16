package io.rekast.sdk.sample.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class AppModule_ProvideMomoApiConfigFactory implements Factory<MomoApiConfig> {
  @Override
  public MomoApiConfig get() {
    return provideMomoApiConfig();
  }

  public static AppModule_ProvideMomoApiConfigFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MomoApiConfig provideMomoApiConfig() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideMomoApiConfig());
  }

  private static final class InstanceHolder {
    static final AppModule_ProvideMomoApiConfigFactory INSTANCE = new AppModule_ProvideMomoApiConfigFactory();
  }
}
