package io.rekast.sdk.sample.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.rekast.sdk.sample.utils.SampleConfig;
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
public final class AppModule_ProvideSampleConfigFactory implements Factory<SampleConfig> {
  @Override
  public SampleConfig get() {
    return provideSampleConfig();
  }

  public static AppModule_ProvideSampleConfigFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SampleConfig provideSampleConfig() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSampleConfig());
  }

  private static final class InstanceHolder {
    static final AppModule_ProvideSampleConfigFactory INSTANCE = new AppModule_ProvideSampleConfigFactory();
  }
}
