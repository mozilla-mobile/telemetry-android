/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.telemetry.util;

import android.support.annotation.RestrictTo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class FileUtils {
    public static void assertDirectory(File directory) {
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException(
                    "Directory doesn't exist and can't be created: " + directory.getAbsolutePath());
        }

        if (!directory.isDirectory() || !directory.canWrite()) {
            throw new IllegalStateException(
                    "Directory is not writable directory: " + directory.getAbsolutePath());
        }
    }

    public static class FilenameRegexFilter implements FilenameFilter {
        private final Pattern mPattern;

        // Each time `Pattern.matcher` is called, a new matcher is created. We can avoid the excessive object creation
        // by caching the returned matcher and calling `Matcher.reset` on it. Since Matcher's are not thread safe,
        // this assumes `FilenameFilter.accept` is not run in parallel (which, according to the source, it is not).
        private Matcher mCachedMatcher;

        public FilenameRegexFilter(final Pattern pattern) {
            mPattern = pattern;
        }

        public FilenameRegexFilter(final String pattern) {
            mPattern = Pattern.compile(pattern);
        }

        @Override
        public boolean accept(final File dir, final String filename) {
            if (mCachedMatcher == null) {
                mCachedMatcher = mPattern.matcher(filename);
            } else {
                mCachedMatcher.reset(filename);
            }
            return mCachedMatcher.matches();
        }
    }
}