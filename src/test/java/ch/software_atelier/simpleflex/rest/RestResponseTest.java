package ch.software_atelier.simpleflex.rest;

import ch.software_atelier.simpleflex.docs.WebDoc;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class RestResponseTest {

    @Test
    void json200ReturnsByteResponseWithJsonPayload() {
        JSONObject payload = new JSONObject().put("id", 7).put("name", "Ada");

        RestResponse response = RestResponse.json_200(payload);

        assertEquals(200, response.getHttpCode().code);
        assertEquals("OK", response.getHttpCode().message);
        assertEquals("data.json", response.name());
        assertEquals("application/json", response.mime());
        assertEquals(WebDoc.DATA_BYTE, response.dataType());
        assertEquals(payload.toString(), new String(response.byteData(), StandardCharsets.UTF_8));
        assertEquals(response.byteData().length, response.size());
        assertNull(response.streamData());
    }

    @Test
    void json201CreatedUsesCreatedStatus() {
        RestResponse response = RestResponse.json_201_created(new JSONObject().put("created", true));

        assertEquals(201, response.getHttpCode().code);
        assertEquals("Created", response.getHttpCode().message);
        assertEquals(WebDoc.DATA_BYTE, response.dataType());
    }

    @Test
    void errorWrapsStringAndJsonMessagesAndLeavesNullPayloadEmpty() {
        RestResponse stringError = RestResponse.error(400, "Bad Request", "invalid input");
        RestResponse jsonError = RestResponse.error(422, "Unprocessable", new JSONObject().put("field", "email"));
        RestResponse emptyError = RestResponse.error(204, "No Content", null);

        assertEquals(400, stringError.getHttpCode().code);
        assertEquals("{\"msg\":\"invalid input\"}", new String(stringError.byteData(), StandardCharsets.UTF_8));
        assertEquals("{\"msg\":{\"field\":\"email\"}}", new String(jsonError.byteData(), StandardCharsets.UTF_8));
        assertEquals(0, emptyError.size());
        assertEquals("", new String(emptyError.byteData(), StandardCharsets.UTF_8));
    }

    @Test
    void streamResponseExposesProvidedStreamAndLength() throws Exception {
        byte[] content = "stream body".getBytes(StandardCharsets.UTF_8);
        RestResponse response = new RestResponse("body.txt", "text/plain", new ByteArrayInputStream(content), content.length);

        assertEquals(WebDoc.DATA_STREAM, response.dataType());
        assertEquals(content.length, response.size());
        assertNull(response.byteData());
        byte[] read = new byte[content.length];
        assertEquals(content.length, response.streamData().read(read));
        assertArrayEquals(content, read);
        response.close();
    }

    @Test
    void closingTemporaryFileResponseDeletesItsFile() throws Exception {
        File file = File.createTempFile("rest-response-", ".txt");
        try (FileOutputStream output = new FileOutputStream(file)) {
            output.write("temporary".getBytes(StandardCharsets.UTF_8));
        }

        RestResponse response = new RestResponse("temporary.txt", "text/plain", file, true);
        assertEquals(file.length(), response.size());
        response.close();

        assertFalse(file.exists());
    }
}
