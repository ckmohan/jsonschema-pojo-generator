package uk.gov.justice.generation.pojo.integration.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.generation.pojo.plugin.classmodifying.AddToStringMethodToClassPlugin.newAddToStringMethodToClassPlugin;

import uk.gov.justice.generation.pojo.integration.utils.ClassInstantiator;
import uk.gov.justice.generation.pojo.integration.utils.GeneratorUtil;
import uk.gov.justice.generation.pojo.integration.utils.OutputDirectories;
import uk.gov.justice.generation.pojo.plugin.classmodifying.AddAdditionalPropertiesToClassPlugin;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ToStringMethodHandlesAdditionalPropertiesIT {

    private final GeneratorUtil generatorUtil = new GeneratorUtil();
    private final ClassInstantiator classInstantiator = new ClassInstantiator();
    private final OutputDirectories outputDirectories = new OutputDirectories();

    @Before
    public void setup() throws Exception {
        outputDirectories.makeDirectories("./target/test-generation/tests/to/string");
    }

    @Test
    public void shouldAddToStringMethodUsingAllFieldsPlusAdditionalProperties() throws Exception {

        final File jsonSchemaFile = new File("src/test/resources/schemas/tests/student-to-string-additional-properties-true.json");
        final String packageName = "uk.gov.justice.pojo.string";


        final List<Class<?>> newClasses = generatorUtil
                .withClassModifyingPlugin(newAddToStringMethodToClassPlugin())
                .withClassModifyingPlugin(new AddAdditionalPropertiesToClassPlugin())
                .generateAndCompileJavaSource(
                        jsonSchemaFile,
                        packageName,
                        outputDirectories);

        assertThat(newClasses.size(), is(1));

        final Class<?> studentClass = newClasses.get(0);

        final Object student = classInstantiator.newInstance(studentClass, 23, "Fred", "Bloggs");
        final Method additionalPropertySetter = studentClass.getDeclaredMethod("setAdditionalProperty", String.class, Object.class);
        additionalPropertySetter.invoke(student, "name", "value");

        assertThat(student.toString(), is("StudentToStringAdditionalPropertiesTrue{age='23',firstName='Fred',lastName='Bloggs',additionalProperties='{name=value}'}"));
    }
}