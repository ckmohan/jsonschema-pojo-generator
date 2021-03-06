package uk.gov.justice.generation.provider;

import uk.gov.justice.generation.pojo.core.ClassNameParser;
import uk.gov.justice.generation.pojo.core.GenerationContext;
import uk.gov.justice.generation.pojo.core.PackageNameParser;
import uk.gov.justice.generation.pojo.generators.ClassNameFactory;
import uk.gov.justice.generation.pojo.generators.JavaGeneratorFactory;
import uk.gov.justice.generation.pojo.generators.TypeNameProvider;
import uk.gov.justice.generation.pojo.plugin.PluginProvider;
import uk.gov.justice.generation.pojo.plugin.TypeNamePluginProcessor;

public class PojoGeneratorFactoriesProvider {

    public JavaGeneratorFactory createJavaGeneratorFactory(final ClassNameFactory classNameFactory) {
        return new JavaGeneratorFactory(classNameFactory);
    }

    public ClassNameFactory createClassNameFactory(final GenerationContext generationContext, final PluginProvider pluginProvider) {
        final TypeNameProvider typeNameProvider = new TypeNameProvider(
                generationContext,
                new PackageNameParser(),
                new ClassNameParser());

        final TypeNamePluginProcessor typeNamePluginProcessor = new TypeNamePluginProcessor(pluginProvider);

        return new ClassNameFactory(typeNameProvider, typeNamePluginProcessor);
    }
}
