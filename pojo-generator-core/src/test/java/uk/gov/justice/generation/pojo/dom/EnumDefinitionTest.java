package uk.gov.justice.generation.pojo.dom;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

public class EnumDefinitionTest {

    @Test
    public void shouldConstructEnumDefinition() throws Exception {
        final String fieldName = "fieldName";
        final List<String> enumValues = emptyList();
        final String id = "id";

        final EnumDefinition enumDefinition = new EnumDefinition(fieldName, enumValues, id);

        assertThat(enumDefinition, is(instanceOf(ClassDefinition.class)));
        assertThat(enumDefinition.getFieldName(), is(fieldName));
        assertThat(enumDefinition.getEnumValues(), is(enumValues));
        assertThat(enumDefinition.getId(), is(Optional.of(id)));
    }
}