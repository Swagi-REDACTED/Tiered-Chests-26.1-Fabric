package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the {@code libs} extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final FabricLibraryAccessors laccForFabricLibraryAccessors = new FabricLibraryAccessors(owner);
    private final FletchingLibraryAccessors laccForFletchingLibraryAccessors = new FletchingLibraryAccessors(owner);
    private final FoojayLibraryAccessors laccForFoojayLibraryAccessors = new FoojayLibraryAccessors(owner);
    private final KikugieLibraryAccessors laccForKikugieLibraryAccessors = new KikugieLibraryAccessors(owner);
    private final ModLibraryAccessors laccForModLibraryAccessors = new ModLibraryAccessors(owner);
    private final MoulberryLibraryAccessors laccForMoulberryLibraryAccessors = new MoulberryLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Group of libraries at <b>fabric</b>
     */
    public FabricLibraryAccessors getFabric() {
        return laccForFabricLibraryAccessors;
    }

    /**
     * Group of libraries at <b>fletching</b>
     */
    public FletchingLibraryAccessors getFletching() {
        return laccForFletchingLibraryAccessors;
    }

    /**
     * Group of libraries at <b>foojay</b>
     */
    public FoojayLibraryAccessors getFoojay() {
        return laccForFoojayLibraryAccessors;
    }

    /**
     * Group of libraries at <b>kikugie</b>
     */
    public KikugieLibraryAccessors getKikugie() {
        return laccForKikugieLibraryAccessors;
    }

    /**
     * Group of libraries at <b>mod</b>
     */
    public ModLibraryAccessors getMod() {
        return laccForModLibraryAccessors;
    }

    /**
     * Group of libraries at <b>moulberry</b>
     */
    public MoulberryLibraryAccessors getMoulberry() {
        return laccForMoulberryLibraryAccessors;
    }

    /**
     * Group of versions at <b>versions</b>
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Group of bundles at <b>bundles</b>
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Group of plugins at <b>plugins</b>
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class FabricLibraryAccessors extends SubDependencyFactory {

        public FabricLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>loader</b> with <b>net.fabricmc:fabric-loader</b> coordinates and
         * with version reference <b>fabric.loader</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getLoader() {
            return create("fabric.loader");
        }

    }

    public static class FletchingLibraryAccessors extends SubDependencyFactory {

        public FletchingLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>table</b> with <b>dev.kikugie:fletching-table</b> coordinates and
         * with version reference <b>fletching.table</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getTable() {
            return create("fletching.table");
        }

    }

    public static class FoojayLibraryAccessors extends SubDependencyFactory {

        public FoojayLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>resolver</b> with <b>org.gradle.toolchains:foojay-resolver</b> coordinates and
         * with version reference <b>foojay</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getResolver() {
            return create("foojay.resolver");
        }

    }

    public static class KikugieLibraryAccessors extends SubDependencyFactory {

        public KikugieLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>postprocess</b> with <b>dev.kikugie:postprocess</b> coordinates and
         * with version reference <b>stonecutter.postprocess</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getPostprocess() {
            return create("kikugie.postprocess");
        }

        /**
         * Dependency provider for <b>stonecutter</b> with <b>dev.kikugie:stonecutter</b> coordinates and
         * with version reference <b>stonecutter</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getStonecutter() {
            return create("kikugie.stonecutter");
        }

    }

    public static class ModLibraryAccessors extends SubDependencyFactory {
        private final ModPublishLibraryAccessors laccForModPublishLibraryAccessors = new ModPublishLibraryAccessors(owner);

        public ModLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>mod.publish</b>
         */
        public ModPublishLibraryAccessors getPublish() {
            return laccForModPublishLibraryAccessors;
        }

    }

    public static class ModPublishLibraryAccessors extends SubDependencyFactory {

        public ModPublishLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>plugin</b> with <b>me.modmuss50:mod-publish-plugin</b> coordinates and
         * with version reference <b>mod.publish</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getPlugin() {
            return create("mod.publish.plugin");
        }

    }

    public static class MoulberryLibraryAccessors extends SubDependencyFactory {

        public MoulberryLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>mixinconstraints</b> with <b>com.moulberry:mixinconstraints</b> coordinates and
         * with version reference <b>moulberry.mixinconstraints</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMixinconstraints() {
            return create("moulberry.mixinconstraints");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        private final DevtoolsVersionAccessors vaccForDevtoolsVersionAccessors = new DevtoolsVersionAccessors(providers, config);
        private final DotenvVersionAccessors vaccForDotenvVersionAccessors = new DotenvVersionAccessors(providers, config);
        private final FabricVersionAccessors vaccForFabricVersionAccessors = new FabricVersionAccessors(providers, config);
        private final FletchingVersionAccessors vaccForFletchingVersionAccessors = new FletchingVersionAccessors(providers, config);
        private final KotlinVersionAccessors vaccForKotlinVersionAccessors = new KotlinVersionAccessors(providers, config);
        private final ModVersionAccessors vaccForModVersionAccessors = new ModVersionAccessors(providers, config);
        private final MoulberryVersionAccessors vaccForMoulberryVersionAccessors = new MoulberryVersionAccessors(providers, config);
        private final NeoforgeVersionAccessors vaccForNeoforgeVersionAccessors = new NeoforgeVersionAccessors(providers, config);
        private final StonecutterVersionAccessors vaccForStonecutterVersionAccessors = new StonecutterVersionAccessors(providers, config);
        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>foojay</b> with value <b>1.0.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getFoojay() { return getVersion("foojay"); }

        /**
         * Group of versions at <b>versions.devtools</b>
         */
        public DevtoolsVersionAccessors getDevtools() {
            return vaccForDevtoolsVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.dotenv</b>
         */
        public DotenvVersionAccessors getDotenv() {
            return vaccForDotenvVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.fabric</b>
         */
        public FabricVersionAccessors getFabric() {
            return vaccForFabricVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.fletching</b>
         */
        public FletchingVersionAccessors getFletching() {
            return vaccForFletchingVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.kotlin</b>
         */
        public KotlinVersionAccessors getKotlin() {
            return vaccForKotlinVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.mod</b>
         */
        public ModVersionAccessors getMod() {
            return vaccForModVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.moulberry</b>
         */
        public MoulberryVersionAccessors getMoulberry() {
            return vaccForMoulberryVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.neoforge</b>
         */
        public NeoforgeVersionAccessors getNeoforge() {
            return vaccForNeoforgeVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.stonecutter</b>
         */
        public StonecutterVersionAccessors getStonecutter() {
            return vaccForStonecutterVersionAccessors;
        }

    }

    public static class DevtoolsVersionAccessors extends VersionFactory  {

        public DevtoolsVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>devtools.ksp</b> with value <b>2.2.10-2.0.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getKsp() { return getVersion("devtools.ksp"); }

    }

    public static class DotenvVersionAccessors extends VersionFactory  {

        public DotenvVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>dotenv.gradle</b> with value <b>4.0.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getGradle() { return getVersion("dotenv.gradle"); }

    }

    public static class FabricVersionAccessors extends VersionFactory  {

        public FabricVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>fabric.loader</b> with value <b>0.18.5</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getLoader() { return getVersion("fabric.loader"); }

        /**
         * Version alias <b>fabric.loom</b> with value <b>1.15-SNAPSHOT</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getLoom() { return getVersion("fabric.loom"); }

    }

    public static class FletchingVersionAccessors extends VersionFactory  {

        public FletchingVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>fletching.table</b> with value <b>0.1.0-alpha.22</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getTable() { return getVersion("fletching.table"); }

    }

    public static class KotlinVersionAccessors extends VersionFactory  {

        public KotlinVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>kotlin.jvm</b> with value <b>2.2.10</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJvm() { return getVersion("kotlin.jvm"); }

    }

    public static class ModVersionAccessors extends VersionFactory  {

        public ModVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>mod.publish</b> with value <b>1.1.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getPublish() { return getVersion("mod.publish"); }

    }

    public static class MoulberryVersionAccessors extends VersionFactory  {

        public MoulberryVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>moulberry.mixinconstraints</b> with value <b>1.1.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getMixinconstraints() { return getVersion("moulberry.mixinconstraints"); }

    }

    public static class NeoforgeVersionAccessors extends VersionFactory  {

        public NeoforgeVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>neoforge.moddev</b> with value <b>2.0.141</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getModdev() { return getVersion("neoforge.moddev"); }

    }

    public static class StonecutterVersionAccessors extends VersionFactory  implements VersionNotationSupplier {

        public StonecutterVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>stonecutter</b> with value <b>0.9</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> asProvider() { return getVersion("stonecutter"); }

        /**
         * Version alias <b>stonecutter.postprocess</b> with value <b>2.1-beta.8</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getPostprocess() { return getVersion("stonecutter.postprocess"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {
        private final DevtoolsPluginAccessors paccForDevtoolsPluginAccessors = new DevtoolsPluginAccessors(providers, config);
        private final FabricPluginAccessors paccForFabricPluginAccessors = new FabricPluginAccessors(providers, config);
        private final FletchingPluginAccessors paccForFletchingPluginAccessors = new FletchingPluginAccessors(providers, config);
        private final JsonlangPluginAccessors paccForJsonlangPluginAccessors = new JsonlangPluginAccessors(providers, config);
        private final KotlinPluginAccessors paccForKotlinPluginAccessors = new KotlinPluginAccessors(providers, config);
        private final ModPluginAccessors paccForModPluginAccessors = new ModPluginAccessors(providers, config);
        private final NeoforgedPluginAccessors paccForNeoforgedPluginAccessors = new NeoforgedPluginAccessors(providers, config);

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>dotenv</b> with plugin id <b>co.uzzu.dotenv.gradle</b> and
         * with version reference <b>dotenv.gradle</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getDotenv() { return createPlugin("dotenv"); }

        /**
         * Plugin provider for <b>stonecutter</b> with plugin id <b>dev.kikugie.stonecutter</b> and
         * with version reference <b>stonecutter</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getStonecutter() { return createPlugin("stonecutter"); }

        /**
         * Group of plugins at <b>plugins.devtools</b>
         */
        public DevtoolsPluginAccessors getDevtools() {
            return paccForDevtoolsPluginAccessors;
        }

        /**
         * Group of plugins at <b>plugins.fabric</b>
         */
        public FabricPluginAccessors getFabric() {
            return paccForFabricPluginAccessors;
        }

        /**
         * Group of plugins at <b>plugins.fletching</b>
         */
        public FletchingPluginAccessors getFletching() {
            return paccForFletchingPluginAccessors;
        }

        /**
         * Group of plugins at <b>plugins.jsonlang</b>
         */
        public JsonlangPluginAccessors getJsonlang() {
            return paccForJsonlangPluginAccessors;
        }

        /**
         * Group of plugins at <b>plugins.kotlin</b>
         */
        public KotlinPluginAccessors getKotlin() {
            return paccForKotlinPluginAccessors;
        }

        /**
         * Group of plugins at <b>plugins.mod</b>
         */
        public ModPluginAccessors getMod() {
            return paccForModPluginAccessors;
        }

        /**
         * Group of plugins at <b>plugins.neoforged</b>
         */
        public NeoforgedPluginAccessors getNeoforged() {
            return paccForNeoforgedPluginAccessors;
        }

    }

    public static class DevtoolsPluginAccessors extends PluginFactory {

        public DevtoolsPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>devtools.ksp</b> with plugin id <b>com.google.devtools.ksp</b> and
         * with version reference <b>devtools.ksp</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getKsp() { return createPlugin("devtools.ksp"); }

    }

    public static class FabricPluginAccessors extends PluginFactory {

        public FabricPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>fabric.loom</b> with plugin id <b>net.fabricmc.fabric-loom</b> and
         * with version reference <b>fabric.loom</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getLoom() { return createPlugin("fabric.loom"); }

    }

    public static class FletchingPluginAccessors extends PluginFactory {

        public FletchingPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>fletching.table</b> with plugin id <b>dev.kikugie.fletching-table</b> and
         * with version reference <b>fletching.table</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getTable() { return createPlugin("fletching.table"); }

    }

    public static class JsonlangPluginAccessors extends PluginFactory {

        public JsonlangPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>jsonlang.postprocess</b> with plugin id <b>dev.kikugie.postprocess.jsonlang</b> and
         * with version reference <b>stonecutter.postprocess</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getPostprocess() { return createPlugin("jsonlang.postprocess"); }

    }

    public static class KotlinPluginAccessors extends PluginFactory {

        public KotlinPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>kotlin.jvm</b> with plugin id <b>org.jetbrains.kotlin.jvm</b> and
         * with version reference <b>kotlin.jvm</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getJvm() { return createPlugin("kotlin.jvm"); }

    }

    public static class ModPluginAccessors extends PluginFactory {
        private final ModPublishPluginAccessors paccForModPublishPluginAccessors = new ModPublishPluginAccessors(providers, config);

        public ModPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of plugins at <b>plugins.mod.publish</b>
         */
        public ModPublishPluginAccessors getPublish() {
            return paccForModPublishPluginAccessors;
        }

    }

    public static class ModPublishPluginAccessors extends PluginFactory {

        public ModPublishPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>mod.publish.plugin</b> with plugin id <b>me.modmuss50.mod-publish-plugin</b> and
         * with version reference <b>mod.publish</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getPlugin() { return createPlugin("mod.publish.plugin"); }

    }

    public static class NeoforgedPluginAccessors extends PluginFactory {

        public NeoforgedPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>neoforged.moddev</b> with plugin id <b>net.neoforged.moddev</b> and
         * with version reference <b>neoforge.moddev</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getModdev() { return createPlugin("neoforged.moddev"); }

    }

}
