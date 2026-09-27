# Simpleflex REST

`simpleflex-rest` is the REST extension for the [simpleflex-base](https://github.com/software-atelier/simpleflex-base) web server. It adds resource-oriented routing, a request and response API, consistent REST errors, and application-relative API documentation generated from the resources that an application registers.

It is intended to be used in a Simpleflex Base `WebApp`. Extend `RestApp` instead of implementing the routing yourself: it dispatches a request to a `RestResource`, exposes the documentation endpoints, and lets the base server handle the HTTP connection and application lifecycle.

## Installation

The latest published Maven Central version is `ch.software-atelier:simpleflex-rest:2.3.1`. This branch prepares `2.3.2`, which will bring `simpleflex-base:2.3.2` as a transitive dependency once both are published. Use `2.3.1` until the new release is verified on Maven Central. The following snippet is for the forthcoming release:

```xml
<dependency>
    <groupId>ch.software-atelier</groupId>
    <artifactId>simpleflex-rest</artifactId>
    <version>2.3.2</version>
</dependency>
```

The project requires Maven 3.9 or newer and is compiled for Java 17.

## Quick start

The following is a complete, small application. It serves `GET /greetings/Ada?formal=true` and returns JSON. `DefaultRestResource` is the usual base class: every method is initially answered with `405 Method Not Allowed`, so a resource only needs to override the methods it supports.

```java
package example;

import ch.software_atelier.simpleflex.SimpleFlexAccesser;
import ch.software_atelier.simpleflex.SimpleFlexBase;
import ch.software_atelier.simpleflex.rest.DefaultRestResource;
import ch.software_atelier.simpleflex.rest.RestApp;
import ch.software_atelier.simpleflex.rest.RestRequest;
import ch.software_atelier.simpleflex.rest.RestResponse;
import ch.software_atelier.simpleflex.rest.swagger.MethodDocumentation;
import ch.software_atelier.simpleflex.rest.swagger.PathParameter;
import ch.software_atelier.simpleflex.rest.swagger.QueryParameter;
import java.util.HashMap;
import org.json.JSONObject;

public class GreetingApp extends RestApp {
    public static void main(String[] args) {
        SimpleFlexBase.serveOnLocalhost(GreetingApp.class.getName(), new HashMap(), 18001);
    }

    @Override
    public void start(String name, HashMap<String, Object> config, SimpleFlexAccesser sfa) {
        super.start(name, config, sfa);
        setDocInfo("1.0.0", "Greeting API", "A minimal Simpleflex REST application.");
        setDocPath("localhost:18001", "/");
        addResource("/greetings/{name}", new GreetingResource());
    }
}

class GreetingResource extends DefaultRestResource {
    @Override
    public RestResponse onGET(RestRequest request) {
        String name = request.getResourcePlaceholder("name");
        boolean formal = "true".equals(request.getRequestArgument("formal"));
        return RestResponse.json_200(new JSONObject()
                .put("message", (formal ? "Good day, " : "Hello, ") + name));
    }

    @Override
    public void docGET(MethodDocumentation doc) {
        doc.setTitle("Get a greeting");
        doc.setDescription("Builds a greeting for the requested name.");
        doc.addTag("greetings");
        doc.addProduces("application/json");
        doc.addParameter(new PathParameter("name", "Name to greet"));
        doc.addParameter(new QueryParameter("formal", "Use a formal greeting", false));
        doc.addResponse("200", "Greeting returned", new JSONObject().put("type", "object"));
    }
}
```

Start it with Maven or your application launcher and call:

```text
http://localhost:18001/greetings/Ada?formal=true
```

## Resources and routing

Register a resource with `RestApp.addResource(String path, RestResource resource)`. Leading slashes are optional on registration. A segment in braces is a named path placeholder, which is read with `RestRequest.getResourcePlaceholder`.

```java
addResource("/people/{id}", new PersonResource());

String id = request.getResourcePlaceholder("id");
String include = request.getRequestArgument("include"); // query argument
```

Use `{name*}` as the final route segment when it must capture the remaining path. The captured value includes leading slashes.

```java
addResource("/files/{path*}", new FileResource());

String path = request.getResourcePlaceholder("path");
// A request to /files/a/b/report.pdf produces "/a/b/report.pdf".
```

`RestRequest.getPath()` returns the request path relative to the application. Headers are available through `getHeaderValue(String)` and `getheaders()`.

## HTTP methods

`RestResource` defines `onGET`, `onPOST`, `onPUT`, `onPATCH`, `onDELETE`, `onOPTIONS`, and `onHEAD`. Implement the methods your endpoint accepts; inheriting `DefaultRestResource` makes all unimplemented methods return `RestResponse.methodNotAllewed_405()`.

```java
public class PersonResource extends DefaultRestResource {
    @Override public RestResponse onGET(RestRequest request) {
        return RestResponse.json_200(new JSONObject().put("id",
                request.getResourcePlaceholder("id")));
    }

    @Override public RestResponse onPOST(RestRequest request) {
        return RestResponse.json_201_created(request.getJSON());
    }

    @Override public RestResponse onPUT(RestRequest request) {
        return RestResponse.json_200(request.getJSON());
    }

    @Override public RestResponse onPATCH(RestRequest request) {
        return RestResponse.json_200(request.getJSON());
    }

    @Override public RestResponse onDELETE(RestRequest request) {
        return RestResponse.noContent_204();
    }

    @Override public RestResponse onOPTIONS(RestRequest request) {
        RestResponse response = RestResponse.noContent_204();
        response.addHeader("Allow", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        return response;
    }
}
```

`onHEAD` and `docHEAD` are part of the public `RestResource` interface and are included in generated documentation. In version 2.3.0, however, `RestHandler.handle` does not dispatch a `HEAD` request to `onHEAD`; use one of the dispatched methods above for executable endpoints (or handle `HEAD` outside this handler).

## Request bodies and uploads

`RestRequest` delegates parsing to Simpleflex Base. Check the matching predicate before reading a structured representation.

```java
@Override
public RestResponse onPOST(RestRequest request) {
    if (request.isJSON()) {
        JSONObject body = request.getJSON();
        return RestResponse.json_201_created(body);
    }
    if (request.isJSONArray()) {
        return RestResponse.json_200(request.getJSONArray());
    }
    if (request.isXML()) {
        // xmlwise.XmlElement is returned by getXML().
        return RestResponse.noContent_204();
    }
    if (request.isSinglePart()) {
        // One uploaded part: file, metadata, and raw data are available.
        java.io.File file = request.getSinglePartFile();
        String filename = request.getSinglePartFilename();
        String mimeType = request.getSinglePartMimeType();
        byte[] bytes = request.getRawData();
        return RestResponse.noContent_204();
    }
    if (request.isMultiPart()) {
        // Multipart/form-data: enumerate fields, then read a file or field data.
        for (String field : request.getFieldNames()) {
            java.io.File uploadedFile = request.getFile(field); // null for text fields
            byte[] data = request.getData(field);               // file or URL-decoded text
            String filename = request.getFileName(field);       // null for text fields
        }
        return RestResponse.noContent_204();
    }
    return RestResponse.badRequest_400("Expected JSON, XML, or an upload");
}
```

For an unstructured request body, call `getRawData()`. Query/form arguments exposed by Simpleflex Base are read using `getRequestArgument(String)`.

## Responses and errors

Use the JSON helpers for the common success responses:

```java
return RestResponse.json_200(new JSONObject().put("status", "ok"));
return RestResponse.json_200(new org.json.JSONArray().put("one"));
return RestResponse.json_201_created(new JSONObject().put("id", 42));
return RestResponse.noContent_204();
```

`RestResponse` also supports arbitrary byte, stream, and file responses. Temporary files are deleted when the response is closed.

```java
byte[] csv = "id,name\\n1,Ada\\n".getBytes(java.nio.charset.StandardCharsets.UTF_8);
RestResponse download = new RestResponse("people.csv", "text/csv", csv);
download.addHeader("Content-Disposition", "attachment; filename=people.csv");
return download;

// For a stream with a known length:
return new RestResponse("export.zip", "application/zip", inputStream, contentLength);

// For a file; pass true only when it may be removed after the response is sent:
return new RestResponse("export.zip", "application/zip", file, true);
```

Error helpers are `badRequest_400`, `unauthorized_401`, `notFound_404`, `methodNotAllewed_405`, and `internalServerError_500`; `RestResponse.error(int, String, Object)` creates any other status response. Error bodies use `{"msg":"..."}` for string messages and embed a `JSONObject` as `{"msg":{...}}`.

Throw `RestException` from a resource to have `RestHandler` turn it into that error response:

```java
if (!request.isJSON()) {
    throw new RestException(415, "Unsupported Media Type", "Expected application/json");
}

try {
    return RestResponse.json_200(process(request.getJSON()));
} catch (IllegalArgumentException e) {
    throw new RestException(422, "Unprocessable Entity", e.getMessage());
} catch (Exception e) {
    throw RestException.internalServerError500(e.getMessage());
}
```

Only `RestException` is translated to its configured error response. Other uncaught throwables are logged by the handler and result in its fallback `404` response, so convert expected application failures to `RestException`.

## Swagger / OpenAPI documentation

`RestApp` builds a documentation object when `addResource` is called. Each resource documents supported operations by overriding its matching `docGET`, `docPOST`, and similar methods. The default implementations in `DefaultRestResource` invalidate the method documentation, so only explicitly documented operations appear.

```java
import ch.software_atelier.simpleflex.rest.swagger.BodyParameter;
import ch.software_atelier.simpleflex.rest.swagger.FileUploadParameter;
import ch.software_atelier.simpleflex.rest.swagger.HeaderParameter;
import ch.software_atelier.simpleflex.rest.swagger.MethodDocumentation;
import ch.software_atelier.simpleflex.rest.swagger.ObjectSchemaBuilder;
import ch.software_atelier.simpleflex.rest.swagger.PathParameter;
import ch.software_atelier.simpleflex.rest.swagger.QueryParameter;
import org.json.JSONObject;

@Override
public void docPOST(MethodDocumentation doc) {
    JSONObject person = ObjectSchemaBuilder.create("Person to create")
            .addSimpleProperty("name", "string", "Display name", true)
            .addSimpleProperty("age", "integer", "Age in years", false)
            .toJSON();
    BodyParameter body = new BodyParameter("person", person);
    body.setDescription("JSON person payload");

    QueryParameter dryRun = new QueryParameter("dryRun", "Validate without saving", false);
    dryRun.setType("boolean");

    doc.setTitle("Create a person");
    doc.setDescription("Creates a person record.");
    doc.addTag("people");
    doc.addProduces("application/json");
    doc.addParameter(new PathParameter("id", "Parent identifier"));
    doc.addParameter(dryRun);
    doc.addParameter(new HeaderParameter("X-Request-Id", "Tracing identifier"));
    doc.addParameter(body);
    doc.addResponse("201", "Person created", person);
    doc.addResponse("400", "Invalid request", new JSONObject());
}
```

Available parameter builders are `PathParameter`, `QueryParameter`, `HeaderParameter`, `BodyParameter`, and `FileUploadParameter`. Adding a `FileUploadParameter` marks the operation as consuming `multipart/form-data`:

```java
doc.addParameter(new FileUploadParameter("avatar", "Avatar image", true));
```

Build response and nested schemas with `ObjectSchemaBuilder` and `ArraySchemaBuilder`:

```java
JSONObject people = ch.software_atelier.simpleflex.rest.swagger.ArraySchemaBuilder
        .create("People")
        .setObject(ObjectSchemaBuilder.create("Person")
                .addSimpleProperty("name", "string", "Display name", true)
                .toJSON())
        .toJSON();
doc.addResponse("200", "People returned", people);
```

Set document metadata in `start` with `setDocInfo(version, title, description)` and `setDocPath(host, basePath)`. At the application root, the generated specification is available at `/api-doc/swagger.json`; `/api-doc` redirects to `/api-doc/`, which serves the bundled ReDoc UI. For an application mounted below a path, prefix these paths with that application path.

The root document declares `swagger: "2.0"`. Note that `BodyParameter` is serialized as a `requestBody` object, while file uploads are serialized as Swagger 2.0 `formData`; validate the generated document with the documentation tooling used by your deployment if strict specification compatibility matters.

## Testing

The repository uses JUnit Jupiter 5.10.2. Run the tests with:

```bash
mvn test
```

`RestResponseTest` checks JSON, error, stream, and temporary-file responses. `SwaggerDocumentationTest` checks object and array schemas, parameter serialization, body/upload documentation, responses, and invalidated operations. Add focused JUnit 5 tests next to these classes for resources and response behaviour you introduce.

## Ecosystem

- [simpleflex-base](https://github.com/software-atelier/simpleflex-base) provides the web server, `WebApp` lifecycle, HTTP request parsing, and document transport used by this extension.
- `simpleflex-rest-auth` complements this module where REST authentication and authorization are required.

## License

Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

## Publishing

See [Release procedure](docs/RELEASING.md) for the Maven Central publishing flow.
