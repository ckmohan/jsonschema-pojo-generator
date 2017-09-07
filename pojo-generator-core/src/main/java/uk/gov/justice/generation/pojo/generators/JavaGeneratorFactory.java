package uk.gov.justice.generation.pojo.generators;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.capitalize;

import uk.gov.justice.generation.pojo.core.GenerationContext;
import uk.gov.justice.generation.pojo.dom.ClassDefinition;
import uk.gov.justice.generation.pojo.dom.Definition;
import uk.gov.justice.generation.pojo.dom.EnumDefinition;
import uk.gov.justice.generation.pojo.dom.FieldDefinition;
import uk.gov.justice.generation.pojo.generators.plugin.PluginProvider;

import java.util.List;

public class JavaGeneratorFactory {

    private final ClassNameFactory classNameFactory;

    public JavaGeneratorFactory(final ClassNameFactory classNameFactory) {
        this.classNameFactory = classNameFactory;
    }

    public ElementGeneratable createGeneratorFor(final Definition definition) {

        if (definition.getClass() == ClassDefinition.class || definition.getClass() == EnumDefinition.class) {
            return new ElementGenerator(definition, classNameFactory);
        }

        return new FieldGenerator((FieldDefinition) definition, classNameFactory);
    }

    public List<ClassGeneratable> createClassGeneratorsFor(final List<Definition> definitions,
                                                           final PluginProvider pluginProvider,
                                                           final GenerationContext generationContext) {
        return definitions.stream()
                .filter(this::isClassOrEnum)
                .filter(definition -> isNotHardCoded(definition, generationContext.getIgnoredClassNames()))
                .map(definition -> getClassGeneratable(pluginProvider, generationContext, definition))
                .collect(toList());
    }

    private boolean isClassOrEnum(final Definition definition) {
        return EnumDefinition.class.isInstance(definition) || ClassDefinition.class.isInstance(definition);
    }

    private boolean isNotHardCoded(final Definition definition, final List<String> hardCodedClassNames) {
        final String className = capitalize(definition.getFieldName());
        return ! hardCodedClassNames.contains(className);
    }

    private ClassGeneratable getClassGeneratable(
            final PluginProvider pluginProvider,
            final GenerationContext generationContext,
            final Definition definition) {

        if (definition.getClass() == EnumDefinition.class) {
            return new EnumGenerator((EnumDefinition) definition);
        }

        return new ClassGenerator((ClassDefinition) definition,
                this,
                pluginProvider,
                classNameFactory,
                generationContext);
    }
}
