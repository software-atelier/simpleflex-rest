package ch.software_atelier.simpleflex.rest.swagger;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SwaggerDocumentationTest {

    @Test
    void objectAndArrayBuildersProduceNestedSchemasAndRequiredFields() {
        JSONObject address = ObjectSchemaBuilder.create("Address")
                .addSimpleProperty("city", "string", "City name", true)
                .toJSON();
        JSONObject person = ObjectSchemaBuilder.create("Person")
                .addSimpleProperty("name", "string", "Display name", true)
                .addObjectProperty("address", address, false)
                .toJSON();
        JSONObject people = ArraySchemaBuilder.create("People")
                .setObject(person)
                .toJSON();

        assertEquals("array", people.getString("type"));
        assertEquals("object", people.getJSONObject("items").getString("type"));
        assertEquals("Display name", people.getJSONObject("items").getJSONObject("properties")
                .getJSONObject("name").getString("description"));
        assertEquals("name", people.getJSONObject("items").getJSONArray("required").getString(0));
        assertEquals("city", address.getJSONArray("required").getString(0));
    }

    @Test
    void parameterTypesSerializeToSwaggerLocations() {
        QueryParameter query = new QueryParameter("limit", "Maximum results", false);
        query.setType("integer");
        HeaderParameter header = new HeaderParameter("X-Request-Id", "Tracing id");
        PathParameter path = new PathParameter("id", "Resource id");
        FileUploadParameter file = new FileUploadParameter("upload", "A file", false);

        assertEquals("query", query.toJSON().getString("in"));
        assertEquals("integer", query.toJSON().getString("type"));
        assertTrue(header.toJSON().getBoolean("required"));
        assertEquals("path", path.toJSON().getString("in"));
        assertEquals("file", file.toJSON().getString("type"));
        assertEquals("formData", file.toJSON().getString("in"));
    }

    @Test
    void methodDocumentationSeparatesBodyAndFileUploadDetails() {
        MethodDocumentation documentation = new MethodDocumentation();
        documentation.setTitle("Create person");
        documentation.setDescription("Creates a person record");
        documentation.addTag("people");
        documentation.addProduces("application/json");
        documentation.addParameter(new BodyParameter("person", ObjectSchemaBuilder.create("Person").toJSON()));
        documentation.addParameter(new FileUploadParameter("avatar", "Avatar image", true));
        documentation.addResponse("201", "Created", new JSONObject().put("type", "object"));

        JSONObject json = documentation.toJSON();
        assertEquals("Create person", json.getString("summary"));
        assertEquals("application/json", json.getJSONArray("produces").getString(0));
        assertEquals("multipart/form-data", json.getJSONArray("consumes").getString(0));
        assertEquals("object", json.getJSONObject("requestBody").getJSONObject("content")
                .getJSONObject("application/json").getJSONObject("schema").getString("type"));
        assertEquals("avatar", json.getJSONArray("parameters").getJSONObject(0).getString("name"));
        assertEquals("Created", json.getJSONObject("responses").getJSONObject("201").getString("description"));
    }

    @Test
    void pathDocumentationOnlyRetainsValidMethods() {
        MethodDocumentation valid = new MethodDocumentation();
        valid.setTitle("Read person");
        MethodDocumentation invalid = new MethodDocumentation();
        invalid.invalidate();
        PathDocumentation path = new PathDocumentation();

        path.addMethodDocumentationIfValid("get", valid);
        path.addMethodDocumentationIfValid("post", invalid);

        assertTrue(path.hasMethodDocumentations());
        assertTrue(path.toJSON().has("get"));
        assertFalse(path.toJSON().has("post"));
    }
}
