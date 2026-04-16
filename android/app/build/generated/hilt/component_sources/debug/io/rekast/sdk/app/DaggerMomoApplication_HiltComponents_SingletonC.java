package io.rekast.sdk.app;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import io.rekast.sdk.model.authentication.credentials.AccessTokenCredentials;
import io.rekast.sdk.model.authentication.credentials.BasicAuthCredentials;
import io.rekast.sdk.network.service.AuthenticationService;
import io.rekast.sdk.network.service.products.CollectionService;
import io.rekast.sdk.network.service.products.CommonService;
import io.rekast.sdk.network.service.products.DisbursementsService;
import io.rekast.sdk.repository.DefaultRepository;
import io.rekast.sdk.repository.DefaultSource;
import io.rekast.sdk.sample.di.AppModule_ProvideMomoApiConfigFactory;
import io.rekast.sdk.sample.di.AppModule_ProvideSampleConfigFactory;
import io.rekast.sdk.sample.di.NetworkModule_GetAuthenticationFactory;
import io.rekast.sdk.sample.di.NetworkModule_GetCollectionFactory;
import io.rekast.sdk.sample.di.NetworkModule_GetCommonServiceFactory;
import io.rekast.sdk.sample.di.NetworkModule_GetDisbursementFactory;
import io.rekast.sdk.sample.di.NetworkModule_ProvideAccessTokenCredentialsFactory;
import io.rekast.sdk.sample.di.NetworkModule_ProvideBasicAuthCredentialsFactory;
import io.rekast.sdk.sample.di.NetworkModule_ProvideJsonFactory;
import io.rekast.sdk.sample.di.NetworkModule_ProvideOkHttpClientFactory;
import io.rekast.sdk.sample.di.NetworkModule_ProvideRetrofitFactory;
import io.rekast.sdk.sample.di.NetworkModule_ProvidesHttpLoggingInterceptorFactory;
import io.rekast.sdk.sample.utils.DefaultDispatcherProvider;
import io.rekast.sdk.sample.utils.SampleConfig;
import io.rekast.sdk.sample.views.AppMainActivity;
import io.rekast.sdk.sample.views.AppMainViewModel;
import io.rekast.sdk.sample.views.AppMainViewModel_HiltModules;
import io.rekast.sdk.sample.views.AppMainViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import io.rekast.sdk.sample.views.AppMainViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import io.rekast.sdk.sample.views.collection.pay.CollectionPayScreenFragment;
import io.rekast.sdk.sample.views.collection.pay.CollectionPayScreenViewModel;
import io.rekast.sdk.sample.views.collection.pay.CollectionPayScreenViewModel_HiltModules;
import io.rekast.sdk.sample.views.collection.pay.CollectionPayScreenViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import io.rekast.sdk.sample.views.collection.pay.CollectionPayScreenViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import io.rekast.sdk.sample.views.collection.withdraw.CollectionWithdrawScreenFragment;
import io.rekast.sdk.sample.views.collection.withdraw.CollectionWithdrawScreenViewModel;
import io.rekast.sdk.sample.views.collection.withdraw.CollectionWithdrawScreenViewModel_HiltModules;
import io.rekast.sdk.sample.views.collection.withdraw.CollectionWithdrawScreenViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import io.rekast.sdk.sample.views.collection.withdraw.CollectionWithdrawScreenViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import io.rekast.sdk.sample.views.disbursement.deposit.DisbursementDepositScreenFragment;
import io.rekast.sdk.sample.views.disbursement.deposit.DisbursementDepositScreenViewModel;
import io.rekast.sdk.sample.views.disbursement.deposit.DisbursementDepositScreenViewModel_HiltModules;
import io.rekast.sdk.sample.views.disbursement.deposit.DisbursementDepositScreenViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import io.rekast.sdk.sample.views.disbursement.deposit.DisbursementDepositScreenViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import io.rekast.sdk.sample.views.disbursement.refund.DisbursementRefundScreenFragment;
import io.rekast.sdk.sample.views.disbursement.refund.DisbursementRefundScreenViewModel;
import io.rekast.sdk.sample.views.disbursement.refund.DisbursementRefundScreenViewModel_HiltModules;
import io.rekast.sdk.sample.views.disbursement.refund.DisbursementRefundScreenViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import io.rekast.sdk.sample.views.disbursement.refund.DisbursementRefundScreenViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import io.rekast.sdk.sample.views.home.HomeScreenFragment;
import io.rekast.sdk.sample.views.home.HomeScreenFragment_MembersInjector;
import io.rekast.sdk.sample.views.home.HomeScreenViewModel;
import io.rekast.sdk.sample.views.home.HomeScreenViewModel_HiltModules;
import io.rekast.sdk.sample.views.home.HomeScreenViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import io.rekast.sdk.sample.views.home.HomeScreenViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import io.rekast.sdk.sample.views.remittance.RemittanceScreenFragment;
import io.rekast.sdk.sample.views.remittance.RemittanceScreenViewModel;
import io.rekast.sdk.sample.views.remittance.RemittanceScreenViewModel_HiltModules;
import io.rekast.sdk.sample.views.remittance.RemittanceScreenViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import io.rekast.sdk.sample.views.remittance.RemittanceScreenViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import io.rekast.sdk.sample.views.splash.SplashScreenActivity;
import io.rekast.sdk.utils.MomoApiConfig;
import io.rekast.sdk.utils.Settings;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import kotlinx.serialization.json.Json;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

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
public final class DaggerMomoApplication_HiltComponents_SingletonC {
  private DaggerMomoApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public MomoApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements MomoApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public MomoApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements MomoApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public MomoApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements MomoApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public MomoApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements MomoApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MomoApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements MomoApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MomoApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements MomoApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public MomoApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements MomoApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public MomoApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends MomoApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends MomoApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    FragmentCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }

    @Override
    public void injectCollectionPayScreenFragment(CollectionPayScreenFragment arg0) {
    }

    @Override
    public void injectCollectionWithdrawScreenFragment(CollectionWithdrawScreenFragment arg0) {
    }

    @Override
    public void injectDisbursementDepositScreenFragment(DisbursementDepositScreenFragment arg0) {
    }

    @Override
    public void injectDisbursementRefundScreenFragment(DisbursementRefundScreenFragment arg0) {
    }

    @Override
    public void injectHomeScreenFragment(HomeScreenFragment arg0) {
      injectHomeScreenFragment2(arg0);
    }

    @Override
    public void injectRemittanceScreenFragment(RemittanceScreenFragment arg0) {
    }

    @CanIgnoreReturnValue
    private HomeScreenFragment injectHomeScreenFragment2(HomeScreenFragment instance) {
      HomeScreenFragment_MembersInjector.injectDispatcherProvider(instance, new DefaultDispatcherProvider());
      HomeScreenFragment_MembersInjector.injectDefaultRepository(instance, singletonCImpl.defaultRepositoryProvider.get());
      return instance;
    }
  }

  private static final class ViewCImpl extends MomoApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends MomoApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    ActivityCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    Map keySetMapOfClassOfAndBooleanBuilder() {
      MapBuilder mapBuilder = MapBuilder.<String, Boolean>newMapBuilder(7);
      mapBuilder.put(AppMainViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AppMainViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(CollectionPayScreenViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, CollectionPayScreenViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(CollectionWithdrawScreenViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, CollectionWithdrawScreenViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(DisbursementDepositScreenViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, DisbursementDepositScreenViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(DisbursementRefundScreenViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, DisbursementRefundScreenViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(HomeScreenViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, HomeScreenViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(RemittanceScreenViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, RemittanceScreenViewModel_HiltModules.KeyModule.provide());
      return mapBuilder.build();
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(keySetMapOfClassOfAndBooleanBuilder());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public void injectAppMainActivity(AppMainActivity arg0) {
    }

    @Override
    public void injectSplashScreenActivity(SplashScreenActivity arg0) {
    }
  }

  private static final class ViewModelCImpl extends MomoApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    Provider<AppMainViewModel> appMainViewModelProvider;

    Provider<CollectionPayScreenViewModel> collectionPayScreenViewModelProvider;

    Provider<CollectionWithdrawScreenViewModel> collectionWithdrawScreenViewModelProvider;

    Provider<DisbursementDepositScreenViewModel> disbursementDepositScreenViewModelProvider;

    Provider<DisbursementRefundScreenViewModel> disbursementRefundScreenViewModelProvider;

    Provider<HomeScreenViewModel> homeScreenViewModelProvider;

    Provider<RemittanceScreenViewModel> remittanceScreenViewModelProvider;

    ViewModelCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        SavedStateHandle savedStateHandleParam, ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    Map hiltViewModelMapMapOfClassOfAndProviderOfViewModelBuilder() {
      MapBuilder mapBuilder = MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(7);
      mapBuilder.put(AppMainViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (appMainViewModelProvider)));
      mapBuilder.put(CollectionPayScreenViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (collectionPayScreenViewModelProvider)));
      mapBuilder.put(CollectionWithdrawScreenViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (collectionWithdrawScreenViewModelProvider)));
      mapBuilder.put(DisbursementDepositScreenViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (disbursementDepositScreenViewModelProvider)));
      mapBuilder.put(DisbursementRefundScreenViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (disbursementRefundScreenViewModelProvider)));
      mapBuilder.put(HomeScreenViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (homeScreenViewModelProvider)));
      mapBuilder.put(RemittanceScreenViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (remittanceScreenViewModelProvider)));
      return mapBuilder.build();
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.appMainViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.collectionPayScreenViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.collectionWithdrawScreenViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.disbursementDepositScreenViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.disbursementRefundScreenViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.homeScreenViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.remittanceScreenViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(hiltViewModelMapMapOfClassOfAndProviderOfViewModelBuilder());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // io.rekast.sdk.sample.views.AppMainViewModel
          return (T) new AppMainViewModel(singletonCImpl.defaultRepositoryProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), new Settings(), new DefaultDispatcherProvider(), singletonCImpl.provideSampleConfigProvider.get());

          case 1: // io.rekast.sdk.sample.views.collection.pay.CollectionPayScreenViewModel
          return (T) new CollectionPayScreenViewModel(singletonCImpl.defaultRepositoryProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideSampleConfigProvider.get());

          case 2: // io.rekast.sdk.sample.views.collection.withdraw.CollectionWithdrawScreenViewModel
          return (T) new CollectionWithdrawScreenViewModel(singletonCImpl.defaultRepositoryProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideSampleConfigProvider.get());

          case 3: // io.rekast.sdk.sample.views.disbursement.deposit.DisbursementDepositScreenViewModel
          return (T) new DisbursementDepositScreenViewModel(singletonCImpl.defaultRepositoryProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideSampleConfigProvider.get());

          case 4: // io.rekast.sdk.sample.views.disbursement.refund.DisbursementRefundScreenViewModel
          return (T) new DisbursementRefundScreenViewModel(singletonCImpl.defaultRepositoryProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideSampleConfigProvider.get());

          case 5: // io.rekast.sdk.sample.views.home.HomeScreenViewModel
          return (T) new HomeScreenViewModel(singletonCImpl.defaultRepositoryProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), new Settings(), new DefaultDispatcherProvider(), singletonCImpl.provideSampleConfigProvider.get());

          case 6: // io.rekast.sdk.sample.views.remittance.RemittanceScreenViewModel
          return (T) new RemittanceScreenViewModel(singletonCImpl.defaultRepositoryProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideSampleConfigProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends MomoApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends MomoApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends MomoApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    Provider<HttpLoggingInterceptor> providesHttpLoggingInterceptorProvider;

    Provider<BasicAuthCredentials> provideBasicAuthCredentialsProvider;

    Provider<AccessTokenCredentials> provideAccessTokenCredentialsProvider;

    Provider<MomoApiConfig> provideMomoApiConfigProvider;

    Provider<OkHttpClient> provideOkHttpClientProvider;

    Provider<Json> provideJsonProvider;

    Provider<Retrofit> provideRetrofitProvider;

    Provider<AuthenticationService> getAuthenticationProvider;

    Provider<CommonService> getCommonServiceProvider;

    Provider<DisbursementsService> getDisbursementProvider;

    Provider<CollectionService> getCollectionProvider;

    Provider<DefaultRepository> defaultRepositoryProvider;

    Provider<SampleConfig> provideSampleConfigProvider;

    SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    DefaultSource defaultSource() {
      return new DefaultSource(getAuthenticationProvider.get(), getCommonServiceProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.providesHttpLoggingInterceptorProvider = DoubleCheck.provider(new SwitchingProvider<HttpLoggingInterceptor>(singletonCImpl, 4));
      this.provideBasicAuthCredentialsProvider = DoubleCheck.provider(new SwitchingProvider<BasicAuthCredentials>(singletonCImpl, 5));
      this.provideAccessTokenCredentialsProvider = DoubleCheck.provider(new SwitchingProvider<AccessTokenCredentials>(singletonCImpl, 6));
      this.provideMomoApiConfigProvider = DoubleCheck.provider(new SwitchingProvider<MomoApiConfig>(singletonCImpl, 7));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 3));
      this.provideJsonProvider = DoubleCheck.provider(new SwitchingProvider<Json>(singletonCImpl, 8));
      this.provideRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 2));
      this.getAuthenticationProvider = DoubleCheck.provider(new SwitchingProvider<AuthenticationService>(singletonCImpl, 1));
      this.getCommonServiceProvider = DoubleCheck.provider(new SwitchingProvider<CommonService>(singletonCImpl, 9));
      this.getDisbursementProvider = DoubleCheck.provider(new SwitchingProvider<DisbursementsService>(singletonCImpl, 10));
      this.getCollectionProvider = DoubleCheck.provider(new SwitchingProvider<CollectionService>(singletonCImpl, 11));
      this.defaultRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<DefaultRepository>(singletonCImpl, 0));
      this.provideSampleConfigProvider = DoubleCheck.provider(new SwitchingProvider<SampleConfig>(singletonCImpl, 12));
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @Override
    public void injectMomoApplication(MomoApplication arg0) {
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // io.rekast.sdk.repository.DefaultRepository
          return (T) new DefaultRepository(singletonCImpl.defaultSource(), singletonCImpl.getDisbursementProvider.get(), singletonCImpl.getCollectionProvider.get(), singletonCImpl.provideBasicAuthCredentialsProvider.get(), singletonCImpl.provideAccessTokenCredentialsProvider.get(), singletonCImpl.provideMomoApiConfigProvider.get());

          case 1: // io.rekast.sdk.network.service.AuthenticationService
          return (T) NetworkModule_GetAuthenticationFactory.getAuthentication(singletonCImpl.provideRetrofitProvider.get());

          case 2: // retrofit2.Retrofit
          return (T) NetworkModule_ProvideRetrofitFactory.provideRetrofit(singletonCImpl.provideOkHttpClientProvider.get(), singletonCImpl.provideJsonProvider.get(), singletonCImpl.provideMomoApiConfigProvider.get());

          case 3: // okhttp3.OkHttpClient
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient(singletonCImpl.providesHttpLoggingInterceptorProvider.get(), singletonCImpl.provideBasicAuthCredentialsProvider.get(), singletonCImpl.provideAccessTokenCredentialsProvider.get(), singletonCImpl.provideMomoApiConfigProvider.get());

          case 4: // okhttp3.logging.HttpLoggingInterceptor
          return (T) NetworkModule_ProvidesHttpLoggingInterceptorFactory.providesHttpLoggingInterceptor();

          case 5: // io.rekast.sdk.model.authentication.credentials.BasicAuthCredentials
          return (T) NetworkModule_ProvideBasicAuthCredentialsFactory.provideBasicAuthCredentials();

          case 6: // io.rekast.sdk.model.authentication.credentials.AccessTokenCredentials
          return (T) NetworkModule_ProvideAccessTokenCredentialsFactory.provideAccessTokenCredentials();

          case 7: // io.rekast.sdk.utils.MomoApiConfig
          return (T) AppModule_ProvideMomoApiConfigFactory.provideMomoApiConfig();

          case 8: // kotlinx.serialization.json.Json
          return (T) NetworkModule_ProvideJsonFactory.provideJson();

          case 9: // io.rekast.sdk.network.service.products.CommonService
          return (T) NetworkModule_GetCommonServiceFactory.getCommonService(singletonCImpl.provideRetrofitProvider.get());

          case 10: // io.rekast.sdk.network.service.products.DisbursementsService
          return (T) NetworkModule_GetDisbursementFactory.getDisbursement(singletonCImpl.provideRetrofitProvider.get());

          case 11: // io.rekast.sdk.network.service.products.CollectionService
          return (T) NetworkModule_GetCollectionFactory.getCollection(singletonCImpl.provideRetrofitProvider.get());

          case 12: // io.rekast.sdk.sample.utils.SampleConfig
          return (T) AppModule_ProvideSampleConfigFactory.provideSampleConfig();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
