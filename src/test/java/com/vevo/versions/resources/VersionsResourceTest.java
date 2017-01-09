package com.vevo.versions.resources;

import com.google.common.net.HttpHeaders;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.vevo.genesis.Genesis;
import com.vevo.genesis.IntegrationTest;
import com.vevo.versions.Config;
import com.vevo.versions.VersionsIntegrationTest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class VersionsResourceTest extends VersionsIntegrationTest {
    private static final String PATH_ARG = "whatever";
    private static final String PATH_PATH = "/foo/path/" + PATH_ARG;
    private static final String PATH_V2_PATH = "/foo/path/v2/" + PATH_ARG;
    private static final String HEADER_PATH = "/foo/header/" + PATH_ARG;
    private static final String BODY_CONTENT = "the body";
    private static final String EXPECTED_POST_JSON = "{\"value\":\"V2: " + PATH_ARG + " / " + BODY_CONTENT + "\"}";
    private static final String EXPECTED_POST_TEXT = "V1: " + PATH_ARG + " / " + BODY_CONTENT;
    private static final String EXPECTED_GET_JSON = "{\"value\":\"V2: " + PATH_ARG + "\"}";
    private static final String EXPECTED_GET_TEXT = "V1: " + PATH_ARG;

    private Config config;

    @BeforeClass(groups = IntegrationTest.INTEGRATION_GROUP)
    public void beforeClass() {
        Genesis genesis = verifyStarted();
        config = genesis.getLocator().get(Config.class);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // POST Tests
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    // Path v1
    @Test(groups = IntegrationTest.INTEGRATION_GROUP)
    public void postPathV1ShouldReturnPlainText200() throws Exception {
        HttpResponse<String> result = Unirest.post(config.getUri() + PATH_PATH)  // v1 path path
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .body(BODY_CONTENT)
            .asObject(String.class);
        Assert.assertEquals(result.getStatus(), Response.Status.OK.getStatusCode(),"Expected OK response!");
        Assert.assertEquals(result.getBody(), EXPECTED_POST_TEXT,"Expected '" + EXPECTED_POST_TEXT + "'!");
    }

    // Path v2
    @Test(groups = IntegrationTest.INTEGRATION_GROUP)
    public void postPathV2ShouldReturnJSON200() throws Exception {
        HttpResponse<String> result = Unirest.post(config.getUri() + PATH_V2_PATH)  // v2 path path
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .body(BODY_CONTENT)
            .asObject(String.class);
        Assert.assertEquals(result.getStatus(), Response.Status.OK.getStatusCode(),"Expected OK response!");
        Assert.assertEquals(result.getBody(), EXPECTED_POST_JSON, "Expected '" + EXPECTED_POST_JSON + "'!");
    }

    // Header default (v1)
    @Test(groups = IntegrationTest.INTEGRATION_GROUP)
    public void postHeaderDefaultVersionShouldReturnPlainText200() throws Exception {
        HttpResponse<String> result = Unirest.post(config.getUri() + HEADER_PATH)  // header path
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON) // Default version
            .body(BODY_CONTENT)
            .asObject(String.class);
        Assert.assertEquals(result.getStatus(), Response.Status.OK.getStatusCode(),"Expected OK response!");
        Assert.assertEquals(result.getBody(), EXPECTED_POST_TEXT,"Expected '" + EXPECTED_POST_TEXT + "'!");
    }

    // Header v1
    @Test(groups = IntegrationTest.INTEGRATION_GROUP)
    public void postHeaderV1ShouldReturnPlainText200() throws Exception {
        HttpResponse<String> result = Unirest.post(config.getUri() + HEADER_PATH)  // header path
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, VersionsResource.V1_TYPE)   // application/vnd.vevo.header-v1+json
            .body(BODY_CONTENT)
            .asObject(String.class);
        Assert.assertEquals(result.getStatus(), Response.Status.OK.getStatusCode(),"Expected OK response!");
        Assert.assertEquals(result.getBody(), EXPECTED_POST_TEXT, "Expected '" + EXPECTED_POST_TEXT + "'!");
    }

    // Header v2
    @Test(groups = IntegrationTest.INTEGRATION_GROUP)
    public void postHeaderV2ShouldReturnJSON200() throws Exception {
        HttpResponse<String> result = Unirest.post(config.getUri() + HEADER_PATH)  // header path
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, VersionsResource.V2_TYPE)   // v2
            .body(BODY_CONTENT)
            .asObject(String.class);
        Assert.assertEquals(result.getStatus(), Response.Status.OK.getStatusCode(),"Expected OK response!");
        Assert.assertEquals(result.getBody(), EXPECTED_POST_JSON,"Expected '" + EXPECTED_POST_JSON + "'!");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // GET Tests
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    // Path v1
    @Test(groups = IntegrationTest.INTEGRATION_GROUP)
    public void getPathV1ShouldReturnPlainText200() throws Exception {
        HttpResponse<String> result = Unirest.get(config.getUri() + PATH_PATH)  // v1 path path
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .asObject(String.class);
        Assert.assertEquals(result.getStatus(), Response.Status.OK.getStatusCode(),"Expected OK response!");
        Assert.assertEquals(result.getBody(), EXPECTED_GET_TEXT,"Expected '" + EXPECTED_GET_TEXT + "'!");
    }

    // Path v2
    @Test(groups = IntegrationTest.INTEGRATION_GROUP)
    public void getPathV2ShouldReturnJSON200() throws Exception {
        HttpResponse<String> result = Unirest.get(config.getUri() + PATH_V2_PATH)  // v2 path path
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .asObject(String.class);
        Assert.assertEquals(result.getStatus(), Response.Status.OK.getStatusCode(),"Expected OK response!");
        Assert.assertEquals(result.getBody(), EXPECTED_GET_JSON, "Expected '" + EXPECTED_GET_JSON + "'!");
    }

    // Header default (v1)
    @Test(groups = IntegrationTest.INTEGRATION_GROUP)
    public void getHeaderDefaultVersionShouldReturnPlainText200() throws Exception {
        HttpResponse<String> result = Unirest.get(config.getUri() + HEADER_PATH)  // header path
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)  // Default version
            .asObject(String.class);
        Assert.assertEquals(result.getStatus(), Response.Status.OK.getStatusCode(),"Expected OK response!");
        Assert.assertEquals(result.getBody(), EXPECTED_GET_TEXT,"Expected '" + EXPECTED_GET_TEXT + "'!");
    }

    // Header v1
    @Test(groups = IntegrationTest.INTEGRATION_GROUP)
    public void getHeaderV1ShouldReturnPlainText200() throws Exception {
        HttpResponse<String> result = Unirest.get(config.getUri() + HEADER_PATH)  // header path
            .header(HttpHeaders.ACCEPT, VersionsResource.V1_TYPE)  // application/vnd.vevo.header-v1+json
            .asObject(String.class);
        Assert.assertEquals(result.getStatus(), Response.Status.OK.getStatusCode(),"Expected OK response!");
        Assert.assertEquals(result.getBody(), EXPECTED_GET_TEXT, "Expected '" + EXPECTED_GET_TEXT + "'!");
    }

    // Header v2
    @Test(groups = IntegrationTest.INTEGRATION_GROUP)
    public void getHeaderV2ShouldReturnJSON200() throws Exception {
        HttpResponse<String> result = Unirest.get(config.getUri() + HEADER_PATH)  // header path
            .header(HttpHeaders.ACCEPT, VersionsResource.V2_TYPE)  // v2
            .asObject(String.class);
        Assert.assertEquals(result.getStatus(), Response.Status.OK.getStatusCode(),"Expected OK response!");
        Assert.assertEquals(result.getBody(), EXPECTED_GET_JSON,"Expected '" + EXPECTED_GET_JSON + "'!");
    }
}
