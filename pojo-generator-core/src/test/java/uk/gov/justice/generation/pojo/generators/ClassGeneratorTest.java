package uk.gov.justice.generation.pojo.generators;

import static com.squareup.javapoet.ClassName.get;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.generation.pojo.dom.ClassDefinition;
import uk.gov.justice.generation.pojo.generators.plugin.PluginProvider;
import uk.gov.justice.generation.pojo.generators.plugin.classgenerator.ClassGeneratorPlugin;
import uk.gov.justice.generation.pojo.generators.plugin.classgenerator.PluginContext;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClassGeneratorTest {

    @Mock
    private ClassDefinition classDefinition;

    @Mock
    private PluginProvider pluginProvider;

    @Mock
    private ClassNameFactory classNameFactory;

    @Mock
    private PluginContext pluginContext;

    @InjectMocks
    private ClassGenerator classGenerator;

    @Test
    public void shouldReturnCorrectSimpleClassNameForGenerator() throws Exception {
        final ClassName className = get(AlcubierreDrive.class);

        when(pluginProvider.pluginClassGenerators()).thenReturn(emptyList());
        when(classNameFactory.createClassNameFrom(classDefinition)).thenReturn(className);

        assertThat(classGenerator.getSimpleClassName(), is("AlcubierreDrive"));
    }

    @Test
    public void shouldGenerateAnEmptyClassAndUseThePluginsToGenerateTheClassInternals() throws Exception {
        final ClassName className = get(AlcubierreDrive.class);

        final ClassGeneratorPlugin plugin_1 = mock(ClassGeneratorPlugin.class, "plugin_1");
        final ClassGeneratorPlugin plugin_2 = mock(ClassGeneratorPlugin.class, "plugin_2");
        final ClassGeneratorPlugin plugin_3 = mock(ClassGeneratorPlugin.class, "plugin_3");

        when(pluginProvider.pluginClassGenerators()).thenReturn(asList(plugin_1, plugin_2, plugin_3));
        when(classNameFactory.createClassNameFrom(classDefinition)).thenReturn(className);

        final TypeSpec classSpec = classGenerator.generate();

        assertThat(classSpec.toString(), is("public class AlcubierreDrive {\n}\n"));

        verify(plugin_1).generateWith(
                any(TypeSpec.Builder.class),
                eq(classDefinition),
                eq(pluginContext));
        verify(plugin_2).generateWith(
                any(TypeSpec.Builder.class),
                eq(classDefinition),
                eq(pluginContext));
        verify(plugin_3).generateWith(
                any(TypeSpec.Builder.class),
                eq(classDefinition),
                eq(pluginContext));
    }

    private class AlcubierreDrive {
    }
}
