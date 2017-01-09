package com.vevo.versions.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

import java.util.concurrent.CompletableFuture;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/foo")
@Api(value = "versions")
@SwaggerDefinition(
    info = @Info(title = "Version test", description = "Some description", version = "1.0")
)
public class VersionsResource {
    // Substring of the header that specifies the version
    private static final String HEADER_RESOURCE_CONTENT_TYPE_PREFIX = "vnd.vevo.header-v";
    private static final String APPLICATION_PREFIX = "application/";
    private static final String JSON_SUFFIX = "+json";

    static final String V1_TYPE = APPLICATION_PREFIX + HEADER_RESOURCE_CONTENT_TYPE_PREFIX + "1" + JSON_SUFFIX;
    static final String V2_TYPE = APPLICATION_PREFIX + HEADER_RESOURCE_CONTENT_TYPE_PREFIX + "2" + JSON_SUFFIX;


    // Scenario: V1 of all these endpoints returns plain/text but labels it as json... v2 corrects this.
    //
    // PATH NOTES:
    //   *  If you want "v2" before "/foo", might need a whole new class... or not use prefix at the class level.
    //
    //   *  The version is part of the path, so if there isn't a path match, you get 404. This might be solvable by
    //      doing {version} in the @Path, but that might collide with the namespace of some legacy paths.  This is an
    //      intrinsic problem w/ renaming the document as a versioning strategy.
    //
    // HEADER NOTES:
    //
    //   *  Resource path doesn't change
    //
    //   *  If you use @Consumes, you have to list all the possible values (it does accept multiple), if you omit
    //      @Consumes, you get everything that is posted.  Being specific allows you to create multiple methods -- nice
    //      for swagger.  You can still delegate to a single method, so you don't have to duplicate much code.  See
    //      headerVersionPostV2 for example.

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // POST Endpoints -- Clients send /v2 in path or "Content-Type: application/vnd.vevo.header-v2+json"
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Path v1 Post.
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
        value = "ApiOperation: postAction value field",
        notes = "ApiOperation: postAction notes field",
        response = String.class)
    @ApiResponses({@ApiResponse(code = 200, response = String.class, message = "postAction 200 response message")})
    @POST
    @Path("/path/{something}")
    public void pathVersionPostV1(@Suspended AsyncResponse response, @PathParam("something") String something, String body) {
        VersionContext versionContext = VersionContext.getVersionContext(1);
        CompletableFuture.supplyAsync(() -> {
                // NOTE: In this example... here's how we branch on version specific stuff
                return versionContext.doSomeVersionSpecificWork(something, body);
            }).
            exceptionally(ex -> { throw new RuntimeException(ex); }).
            thenAccept(output -> response.resume(Response.status(200).entity(output).build()));
    }

    /**
     * Path v2 Post.
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
        value = "ApiOperation: postAction value field",
        notes = "ApiOperation: postAction notes field",
        response = String.class)
    @ApiResponses({@ApiResponse(code = 200, response = String.class, message = "postAction 200 response message")})
    @POST
    @Path("/path/v2/{something}")       // NOTE: /v2
    public void pathVersionPostV2(@Suspended AsyncResponse response, @PathParam("something") String something, String body) {
        VersionContext versionContext = VersionContext.getVersionContext(2);
        CompletableFuture.supplyAsync(() -> {
                // NOTE: In this example... here's how we branch on version specific stuff
                return versionContext.doSomeVersionSpecificWork(something, body);
            }).
            exceptionally(ex -> { throw new RuntimeException(ex); }).
            thenAccept(output -> response.resume(Response.status(200).entity(output).build()));
    }


    /**
     *  Header default and v1 Post.
     */
    @Consumes({MediaType.APPLICATION_JSON, V1_TYPE})
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
        value = "ApiOperation: postAction value field",
        notes = "ApiOperation: postAction notes field",
        response = String.class)
    @ApiResponses({@ApiResponse(code = 200, response = String.class, message = "postAction 200 response message")})
    @POST
    @Path("/header/{something}")
    public void headerVersionPost(@HeaderParam("Content-type") String contentType, @Suspended AsyncResponse response, @PathParam("something") String something, String body) {
        // NOTE: We extract version info from the header here...
        final VersionContext versionContext = VersionContext.getVersionContext(
                contentType.toLowerCase(), HEADER_RESOURCE_CONTENT_TYPE_PREFIX, VersionContext.V1);

        CompletableFuture.supplyAsync(() -> {
                // NOTE: In this example... here's how we branch on version specific stuff
                return versionContext.doSomeVersionSpecificWork(something, body);
            }).
            exceptionally(ex -> { throw new RuntimeException(ex); }).
            thenAccept(output -> response.resume(Response.status(200).entity(output).build()));
    }

    /**
     *  Header v2 Post.  Note, this case exists solely for the documentation (swagger).  Note that it delegates to
     *  {@link #headerVersionPost}.
     */
    @Consumes({V2_TYPE})
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
        value = "ApiOperation: postAction value field",
        notes = "ApiOperation: postAction notes field",
        response = String.class)
    @ApiResponses({@ApiResponse(code = 200, response = String.class, message = "postAction 200 response message")})
    @POST
    @Path("/header/{something}")
    public void headerVersionPostV2(@HeaderParam("Content-type") String contentType, @Suspended AsyncResponse response, @PathParam("something") String something, String body) {
        headerVersionPost(contentType, response, something, body);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GET Endpoints -- Clients send /v2 in path or "Accept: application/vnd.vevo.header-v2+json"
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Path v1 Get.
     */
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
        value = "ApiOperation: path get value",
        notes = "ApiOperation: path get notes",
        response = String.class)
    @ApiResponses({@ApiResponse(code = 200, response = String.class, message = "Path get 200 response message")})
    @GET
    @Path("/path/{something}")
    public void pathVersionGetV1(@Suspended AsyncResponse response, @PathParam("something") String something) {
        // NOTE: Could also calculate version from URL, but can't combine @Path annotations. Hardcoding for now.
        VersionContext versionContext = VersionContext.getVersionContext(1);
        CompletableFuture.supplyAsync(() -> {
            // NOTE: In this example... here's how we branch on version specific stuff
            return versionContext.doSomeVersionSpecificWork(something, null);
        }).
            exceptionally(ex -> { throw new RuntimeException(ex); }).
            thenAccept(output -> response.resume(Response.status(200).entity(output).build()));
    }

    /**
     * Path v2 Get.
     */
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
        value = "ApiOperation: path v2 get value",
        notes = "ApiOperation: path v2 get notes",
        response = String.class)
    @ApiResponses({@ApiResponse(code = 200, response = String.class, message = "Path get v2 200 response message")})
    @GET
    @Path("/path/v2/{something}")
    public void pathVersionGetV2(@Suspended AsyncResponse response, @PathParam("something") String something) {
        // NOTE: Could also calculate version from URL, but can't combine @Path annotations. Hardcoding for now.
        VersionContext versionContext = VersionContext.getVersionContext(2);
        CompletableFuture.supplyAsync(() -> {
            // NOTE: In this example... here's how we branch on version specific stuff
            return versionContext.doSomeVersionSpecificWork(something, null);
        }).
            exceptionally(ex -> { throw new RuntimeException(ex); }).
            thenAccept(output -> response.resume(Response.status(200).entity(output).build()));
    }

    /**
     * Header Get (default (v1), v1, and v2).
     */
    @Produces({MediaType.APPLICATION_JSON, V1_TYPE, V2_TYPE})
    @ApiOperation(
        value = "ApiOperation: header get value",
        notes = "ApiOperation: header get notes",
        response = String.class)
    @ApiResponses({@ApiResponse(code = 200, response = String.class, message = "Header get 200 response message")})
    @GET
    @Path("/header/{something}")
    public void headerVersionGetV1(@HeaderParam("Accept") String accept, @Suspended AsyncResponse response, @PathParam("something") String something) {
        // NOTE: We extract version info from the header here...
        final VersionContext versionContext = VersionContext.getVersionContext(
            accept.toLowerCase(), HEADER_RESOURCE_CONTENT_TYPE_PREFIX, VersionContext.V1);

        CompletableFuture.supplyAsync(() -> {
                // NOTE: In this example... here's how we branch on version specific stuff
                return versionContext.doSomeVersionSpecificWork(something, null);
            }).
            exceptionally(ex -> { throw new RuntimeException(ex); }).
            thenAccept(output -> response.resume(Response.status(200).entity(output).build()));
    }
}
