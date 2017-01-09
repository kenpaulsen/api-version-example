package com.vevo.versions.resources;

import java.util.Collections;


public interface VersionContext {

    Object doSomeVersionSpecificWork(String path, String body);

    VersionContext V1 = new Version1Context();
    VersionContext V2 = new Version2Context();

    static VersionContext getVersionContext(int version) {
        VersionContext result;
        switch (version) {
            case 1:
                result = V1;
                break;
            case 2:
                result = V2;
                break;
            default:
                throw new IllegalArgumentException("Bad version number: " + version);
        }
        return result;
    }

    /**
     * Parses a String such as "application/vnd.vevo.header-v2" and extracts the version to create the appropriate
     * VersionContext.  If the version is not specified, {@code defaultContext} is returned.
     */
    static VersionContext getVersionContext(String mediaType, String prefix, VersionContext defaultContext) {
        int version = -1;
        int start = mediaType.indexOf(prefix);
        if (start != -1) {
            start += prefix.length();
            int end = mediaType.lastIndexOf('+');
            if (end == -1) {
                throw new IllegalArgumentException("Bad type: " + mediaType);
            }
            version = Integer.parseInt(mediaType.substring(start, end));
        }

        return (version == -1) ? defaultContext : getVersionContext(version);
    }

    class Version1Context implements VersionContext {
        public Object doSomeVersionSpecificWork(String path, String body) {
            return "V1: " + path + ((body == null) ? "" : " / " + body);
        }
    }

    class Version2Context extends Version1Context {
        public Object doSomeVersionSpecificWork(String path, String body) {
            return Collections.singletonMap(
                "value",
                super.doSomeVersionSpecificWork(path, body).toString().replace("V1", "V2"));
        }
    }
}
