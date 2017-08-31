package uk.gov.justice.generation.pojo.integration.test;

import static com.jayway.jsonassert.JsonAssert.with;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.io.FileUtils.cleanDirectory;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import uk.gov.justice.generation.io.files.loader.SchemaLoader;
import uk.gov.justice.generation.pojo.core.ClassNameProvider;
import uk.gov.justice.generation.pojo.core.NameGenerator;
import uk.gov.justice.generation.pojo.generators.JavaGeneratorFactory;
import uk.gov.justice.generation.pojo.generators.plugin.DefaultPluginProvider;
import uk.gov.justice.generation.pojo.integration.utils.ClassCompiler;
import uk.gov.justice.generation.pojo.visitable.VisitableSchema;
import uk.gov.justice.generation.pojo.visitable.VisitableSchemaFactory;
import uk.gov.justice.generation.pojo.visitable.acceptor.DefaultAcceptorFactory;
import uk.gov.justice.generation.pojo.visitor.DefaultDefinitionFactory;
import uk.gov.justice.generation.pojo.visitor.DefinitionBuilderVisitor;
import uk.gov.justice.generation.pojo.write.SourceWriter;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.everit.json.schema.Schema;
import org.junit.Before;
import org.junit.Test;

public class OptionalFieldsIT {

    private final SourceWriter sourceWriter = new SourceWriter();
    private final ClassCompiler classCompiler = new ClassCompiler();

    private final JavaGeneratorFactory javaGeneratorFactory = new JavaGeneratorFactory();
    private final NameGenerator nameGenerator = new NameGenerator();
    private final SchemaLoader schemaLoader = new SchemaLoader();
    private final DefaultDefinitionFactory definitionFactory = new DefaultDefinitionFactory(new ClassNameProvider());
    private final ObjectMapper objectMapper = new ObjectMapperProducer().objectMapper();

    private File sourceOutputDirectory;
    private File classesOutputDirectory;

    @Before
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void setup() throws Exception {
        sourceOutputDirectory = new File("./target/test-generation/optional-schema");
        classesOutputDirectory = new File("./target/test-classes");

        sourceOutputDirectory.mkdirs();
        classesOutputDirectory.mkdirs();

        if (sourceOutputDirectory.exists()) {
            cleanDirectory(sourceOutputDirectory);
        }
    }

    @Test
    public void shouldParseAVeryComplexSchemaDocument() throws Exception {

        final File jsonSchemaFile = new File("src/test/resources/schemas/person-schema-optional.json");
        final Schema schema = schemaLoader.loadFrom(jsonSchemaFile);
        final String fieldName = nameGenerator.rootFieldNameFrom(jsonSchemaFile);

        final DefinitionBuilderVisitor definitionBuilderVisitor = new DefinitionBuilderVisitor("uk.gov.justice.pojo.optional.schema", definitionFactory);
        final VisitableSchemaFactory visitableSchemaFactory = new VisitableSchemaFactory();
        final VisitableSchema visitableSchema = visitableSchemaFactory.createWith(schema, new DefaultAcceptorFactory(visitableSchemaFactory));

        visitableSchema.accept(fieldName, definitionBuilderVisitor);

        final ArrayList<Class<?>> classes = new ArrayList<>();
        javaGeneratorFactory
                .createClassGeneratorsFor(definitionBuilderVisitor.getDefinitions(), new DefaultPluginProvider())
                .forEach(classGeneratable -> {
                    sourceWriter.write(classGeneratable, sourceOutputDirectory.toPath());
                    final Class<?> newClass = classCompiler.compile(classGeneratable, sourceOutputDirectory, classesOutputDirectory);
                    classes.add(newClass);
                });

        assertThat(classes.size(), is(1));

        final Class<?> personClass = classes.get(0);
        final String firstName = "Fred";
        final String lastName = "Bloggs";
        final Optional<String> favouriteFood = of("Curry and Chips");
        final Optional<String> hairStyle = empty();


        final Constructor<?> personConstructor = personClass.getConstructor(Optional.class, String.class, Optional.class, String.class);

        final Object person = personConstructor.newInstance(favouriteFood, firstName, hairStyle, lastName);

        final String json = objectMapper.writeValueAsString(person);

        with(json)
                .assertThat("$.firstName", is(firstName))
                .assertThat("$.lastName", is(lastName))
                .assertThat("$.favouriteFood", is("Curry and Chips"))
                .assertNotDefined("$.hairStyle")
        ;

    }
}