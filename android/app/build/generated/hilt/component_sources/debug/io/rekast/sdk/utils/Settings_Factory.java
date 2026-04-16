package io.rekast.sdk.utils;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class Settings_Factory implements Factory<Settings> {
  @Override
  public Settings get() {
    return newInstance();
  }

  public static Settings_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static Settings newInstance() {
    return new Settings();
  }

  private static final class InstanceHolder {
    static final Settings_Factory INSTANCE = new Settings_Factory();
  }
}
