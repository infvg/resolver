package org.eclipse.aether.impl;


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.internal.impl.DefaultChecksumPolicyProvider;
import org.eclipse.aether.internal.impl.DefaultDeployer;
import org.eclipse.aether.internal.impl.DefaultFileProcessor;
import org.eclipse.aether.internal.impl.DefaultInstaller;
import org.eclipse.aether.internal.impl.DefaultLocalPathComposer;
import org.eclipse.aether.internal.impl.DefaultLocalRepositoryProvider;
import org.eclipse.aether.internal.impl.DefaultMetadataResolver;
import org.eclipse.aether.internal.impl.DefaultOfflineController;
import org.eclipse.aether.internal.impl.DefaultRemoteRepositoryManager;
import org.eclipse.aether.internal.impl.DefaultRepositoryConnectorProvider;
import org.eclipse.aether.internal.impl.DefaultRepositoryEventDispatcher;
import org.eclipse.aether.internal.impl.DefaultRepositoryLayoutProvider;
import org.eclipse.aether.internal.impl.DefaultRepositorySystem;
import org.eclipse.aether.internal.impl.DefaultRepositorySystemLifecycle;
import org.eclipse.aether.internal.impl.DefaultTrackingFileManager;
import org.eclipse.aether.internal.impl.DefaultTransporterProvider;
import org.eclipse.aether.internal.impl.DefaultUpdateCheckManager;
import org.eclipse.aether.internal.impl.DefaultUpdatePolicyAnalyzer;
import org.eclipse.aether.internal.impl.EnhancedLocalRepositoryManagerFactory;
import org.eclipse.aether.internal.impl.LocalPathComposer;
import org.eclipse.aether.internal.impl.Maven2RepositoryLayoutFactory;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.internal.impl.TrackingFileManager;
import org.eclipse.aether.internal.impl.checksum.DefaultChecksumAlgorithmFactorySelector;
import org.eclipse.aether.internal.impl.collect.DefaultDependencyCollector;
import org.eclipse.aether.internal.impl.filter.DefaultRemoteRepositoryFilterManager;
import org.eclipse.aether.internal.impl.slf4j.Slf4jLoggerFactory;
import org.eclipse.aether.internal.impl.synccontext.legacy.DefaultSyncContextFactory;
import org.eclipse.aether.internal.impl.synccontext.named.NamedLockFactoryAdapterFactory;
import org.eclipse.aether.internal.impl.synccontext.named.NamedLockFactoryAdapterFactoryImpl;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmFactorySelector;
import org.eclipse.aether.spi.connector.checksum.ChecksumPolicyProvider;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutFactory;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutProvider;
import org.eclipse.aether.spi.connector.transport.TransporterProvider;
import org.eclipse.aether.spi.io.FileProcessor;
import org.eclipse.aether.spi.localrepo.LocalRepositoryManagerFactory;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.spi.log.LoggerFactory;

public final class ResolverServiceLocator implements ServiceLocator {
    private final Map<Class<?>, Entry<?>> entries = new HashMap();
    private ErrorHandler errorHandler;

    public ResolverServiceLocator() {
        this.addService(RepositorySystem.class, DefaultRepositorySystem.class);
        this.addService(DependencyCollector.class, DefaultDependencyCollector.class);
        this.addService(Deployer.class, DefaultDeployer.class);
        this.addService(Installer.class, DefaultInstaller.class);
        this.addService(MetadataResolver.class, DefaultMetadataResolver.class);
        this.addService(RepositoryLayoutProvider.class, DefaultRepositoryLayoutProvider.class);
        this.addService(RepositoryLayoutFactory.class, Maven2RepositoryLayoutFactory.class);
        this.addService(TransporterProvider.class, DefaultTransporterProvider.class);
        this.addService(ChecksumPolicyProvider.class, DefaultChecksumPolicyProvider.class);
        this.addService(RepositoryConnectorProvider.class, DefaultRepositoryConnectorProvider.class);
        this.addService(RemoteRepositoryManager.class, DefaultRemoteRepositoryManager.class);
        this.addService(UpdateCheckManager.class, DefaultUpdateCheckManager.class);
        this.addService(UpdatePolicyAnalyzer.class, DefaultUpdatePolicyAnalyzer.class);
        this.addService(FileProcessor.class, DefaultFileProcessor.class);
        this.addService(SyncContextFactory.class, DefaultSyncContextFactory.class);
        this.addService(org.eclipse.aether.spi.synccontext.SyncContextFactory.class, org.eclipse.aether.internal.impl.synccontext.DefaultSyncContextFactory.class);
        this.addService(RepositoryEventDispatcher.class, DefaultRepositoryEventDispatcher.class);
        this.addService(OfflineController.class, DefaultOfflineController.class);
        this.addService(LocalRepositoryProvider.class, DefaultLocalRepositoryProvider.class);
        this.addService(LocalRepositoryManagerFactory.class, SimpleLocalRepositoryManagerFactory.class);
        this.addService(LocalRepositoryManagerFactory.class, EnhancedLocalRepositoryManagerFactory.class);
        this.addService(LoggerFactory.class, Slf4jLoggerFactory.class);
        this.addService(TrackingFileManager.class, DefaultTrackingFileManager.class);
        this.addService(ChecksumAlgorithmFactorySelector.class, DefaultChecksumAlgorithmFactorySelector.class);
        this.addService(LocalPathComposer.class, DefaultLocalPathComposer.class);
        this.addService(RemoteRepositoryFilterManager.class, DefaultRemoteRepositoryFilterManager.class);
        this.addService(RepositorySystemLifecycle.class, DefaultRepositorySystemLifecycle.class);
        this.addService(NamedLockFactoryAdapterFactory.class, NamedLockFactoryAdapterFactoryImpl.class);
    }

    private <T> Entry<T> getEntry(Class<T> type, boolean create) {
        Entry<T> entry = (Entry)this.entries.get(Objects.requireNonNull(type, "service type cannot be null"));
        if (entry == null && create) {
            entry = new Entry(type);
            this.entries.put(type, entry);
        }

        return entry;
    }

    public <T> ResolverServiceLocator setService(Class<T> type, Class<? extends T> impl) {
        this.getEntry(type, true).setService(impl);
        return this;
    }

    public <T> ResolverServiceLocator addService(Class<T> type, Class<? extends T> impl) {
        this.getEntry(type, true).addService(impl);
        return this;
    }

    public <T> ResolverServiceLocator setServices(Class<T> type, T... services) {
        this.getEntry(type, true).setServices(services);
        return this;
    }

    public <T> T getService(Class<T> type) {
        Entry<T> entry = this.getEntry(type, false);
        return entry != null ? entry.getInstance() : null;
    }

    public <T> List<T> getServices(Class<T> type) {
        Entry<T> entry = this.getEntry(type, false);
        return entry != null ? entry.getInstances() : null;
    }

    private void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
        if (this.errorHandler != null) {
            this.errorHandler.serviceCreationFailed(type, impl, exception);
        }

    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    private class Entry<T> {
        private final Class<T> type;
        private final Collection<Object> providers;
        private List<T> instances;

        Entry(Class<T> type) {
            this.type = (Class)Objects.requireNonNull(type, "service type cannot be null");
            this.providers = new LinkedHashSet(8);
        }

        public synchronized void setServices(T... services) {
            this.providers.clear();
            if (services != null) {
                Object[] var2 = services;
                int var3 = services.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    T service = (T) var2[var4];
                    this.providers.add(Objects.requireNonNull(service, "service instance cannot be null"));
                }
            }

            this.instances = null;
        }

        public synchronized void setService(Class<? extends T> impl) {
            this.providers.clear();
            this.addService(impl);
        }

        public synchronized void addService(Class<? extends T> impl) {
            this.providers.add(Objects.requireNonNull(impl, "implementation class cannot be null"));
            this.instances = null;
        }

        public T getInstance() {
            List<T> instances = this.getInstances();
            return instances.isEmpty() ? null : instances.get(0);
        }

        public synchronized List<T> getInstances() {
            if (this.instances == null) {
                this.instances = new ArrayList(this.providers.size());
                Iterator var1 = this.providers.iterator();

                while(var1.hasNext()) {
                    Object provider = var1.next();
                    Object instance;
                    if (provider instanceof Class) {
                        instance = this.newInstance((Class)provider);
                    } else {
                        instance = this.type.cast(provider);
                    }

                    if (instance != null) {
                        this.instances.add((T) instance);
                    }
                }

                this.instances = Collections.unmodifiableList(this.instances);
            }

            return this.instances;
        }

        private T newInstance(Class<?> impl) {
            try {
                Constructor<?> constr = impl.getDeclaredConstructor();
                if (!Modifier.isPublic(constr.getModifiers())) {
                    constr.setAccessible(true);
                }

                Object obj = constr.newInstance();
                T instance = this.type.cast(obj);
                if (instance instanceof Service) {
                    ((Service)instance).initService(ResolverServiceLocator.this);
                }

                return instance;
            } catch (LinkageError | Exception var5) {
                Throwable e = var5;
                ResolverServiceLocator.this.serviceCreationFailed(this.type, impl, e);
                return null;
            }
        }
    }

    public abstract static class ErrorHandler {
        public ErrorHandler() {
        }

        public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
        }
    }
}
