# API VersioningExample
This project demonstrates two strategies for Versioning API's: path-based and header-based.  See [VersionsResource](src/main/java/com/vevo/versions/resources/VersionsResource.java) for the service implementations.  See [VersionsResourceTest](src/test/java/com/vevo/versions/resources/VersionsResourceTest.java) for example client calls to the services.

## Path-based

The path-based approach uses a version number in the resource path which is meant to change with every incompatible version change.  For example:

```
GET /foo/path/v2/etc
```

In this example, v2 represents the version.  Often, existing resource paths do not include a version (i.e. v1), so the initial version might be missing this path element:

```
GET /foo/path/etc
```

Iplementing this in Jersey requires each separate path be a separate annotated endpoint (@Path).  If the version number should precede a path element in the class's @Path annotation, it may require a separate class as well.

Clients must adjust the endpoint path in order to utilize a different version of the API.  If a client requests an invalid version, the server will likely return a 404 since the path is not a valid (the server doesn't know about "versions" -- only "paths").  This may be solvable by doing {version} in the @Path, but that might collide with the namespace of the legacy path or create other problems in path resolution (i.e. complication swagger annotations).

Changing the path per version, is not REST-like -- which suggests that an objects path should not change.  The version does effect its representation, but should not change its path.

There are some notable advantages of using path-based versioning.  Conceptually, it's simple.  The version is shown directly in the path.  Making requests to a service endpoint does not require a special header.

## Header-based

The header-based approach encodes the version information in the `Accept` or `Content-Type` header.  This REST-based strategy allows the path to remain constant, but requires a modification to one of these headers to obtain the desired version.  Examples:

```
GET /foo/path/etc
Accept: application/vnd.vevo.header-v2+json
```

```
POST /foo/path/etc
Content-Type: application/vnd.vevo.header-v2+json
```

Jersey supports a few different was of implementing this type of appraoch.  One way is to use the @Consumes (for matching Content-Type) and/or @Produces (for matching Accept).  This allows individual methods to be written for each vrersion (same approach as path-based).  Note, you can map multiple versions to the same method with @Consumes or @Produces (this is not possible with @Path).  In addition, the code can branch internally based on the specified version.  Since the version is not part of the path, Jersey easily matches the request and the code can evaluate the version, even if it's not supported (and return 415, 409, 400, or whatever is desired -- 404 is probably not desirable).  Note, Jersey provides @HeaderParam and @Context for easy access to the headers or other needed request-related information.

Clients must pass in an appropriate `Accept` header on `GET` requests.  This specifices the version which the client expects, example: `Accept: application/vnd.vevo.header-v2+json`.  This indicates version 2 for the "header" Vevo application.  It also indicates that the response should be encoded in json.  For `POST` requests, the client must similarly utilize the `Content-Type` header -- which indicates that the data being sent is in the specified version.  In this case the `Accept` header does not typically also need to specify the version, it is implied via the `Content-Type` header.

This approach allows the resource path to remain constant and is more consistent with REST.

## Async Request Processing

This example also illustrates how to use Jersey's support for asynchronous processing. This allows the service to use fewer threads, which potentially can significantly increase the throughput of the service -- particularly for services which are slow or have slow downstream services.  The key parts of the example to take note of are:

* The @Suspended annotation
* Use of CompletableFuture to return immediately
* The response.resume() call

## References

* https://www.narwhl.com/2015/03/the-ultimate-solution-to-versioning-rest-apis-content-negotiation/
* https://www.youtube.com/watch?v=2qBaMsYXtJ4 (around minutes 13:30 to 15:30)

