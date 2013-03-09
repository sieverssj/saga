package com.github.timurstrekalov.saga.core;

import java.io.File;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;

final class UriUtil {

    private static final Pattern supportedUriSchemeRe = Pattern.compile("(https?|file)");

    private UriUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    static URI toUri(final String s) {
        final URI uri = URI.create(s);

        if (uri.getScheme() != null) {
            final Matcher matcher = supportedUriSchemeRe.matcher(uri.getScheme());
            Preconditions.checkArgument(matcher.find(), "Supported URI schemes are: http, https and file");
            return uri;
        }

        return new File(s).toURI().normalize();
    }

    static boolean isFileUri(final URI uri) {
        return "file".equals(uri.getScheme());
    }

    static Optional<String> getLastSegment(final URI uri) {
        return getSegment(uri, -1);
    }

    private static Optional<String> getSegment(final URI uri, final int index) {
        final String path = uri.getPath();

        if (StringUtils.isBlank(path)) {
            return Optional.absent();
        }

        if (!path.contains("/")) {
            return Optional.of(path);
        }

        final Iterable<String> parts = Splitter.on('/').
                omitEmptyStrings().
                trimResults().
                split(path);

        final int size = Iterables.size(parts);
        final int actualIndex = index < 0 ? size + index : index;
        if (actualIndex < 0 || actualIndex > size - 1) {
            return Optional.absent();
        }

        return Optional.of(Iterables.get(parts, actualIndex));
    }

    static String getLastSegmentOrHost(final URI uri) {
        final Optional<String> segment = getLastSegment(uri);
        if (segment.isPresent()) {
            return segment.get();
        }

        return uri.getHost();
    }

    static String getParent(final URI uri) {
        final String path = uri.getPath();

        if (StringUtils.isBlank(path)) {
            return "/";
        }

        final String parent = path.substring(0, path.lastIndexOf('/')).trim();
        return StringUtils.isBlank(parent) ? "/" : parent;
    }

    static String getPath(final URI uri) {
        return isFileUri(uri) ? uri.getPath() : uri.toString();
    }

}
