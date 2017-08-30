package uk.gov.justice.generation.io.files.loader;

import static java.lang.String.format;
import static org.everit.json.schema.loader.SchemaLoader.load;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.everit.json.schema.Schema;
import org.json.JSONObject;

public class SchemaLoader {

    public Schema loadFrom(final File file) {
        final JSONObject schemaJsonObject = new JSONObject(loadAsJsonString(file));
        return load(schemaJsonObject);
    }

    private String loadAsJsonString(final File file) {
        try (final FileReader reader = new FileReader(file)) {
            return IOUtils.toString(reader);
        } catch (final IOException ioe) {
            throw new SchemaLoaderException(format("File failed to load: %s", file.toPath()), ioe);
        }
    }
}