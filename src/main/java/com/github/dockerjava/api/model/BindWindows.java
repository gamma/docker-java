package com.github.dockerjava.api.model;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BindWindows implements Serializable {

    private static final long serialVersionUID = 1L;

    private String        source;

    private String        destination;

    private String[]      flags;

    // Spec should be in the format [source:]destination[:mode]
    //
    // Examples: c:\foo bar:d:rw
    //           c:\foo:d:\bar
    //           myname:d:
    //           d:\
    //
    // Explanation of this regex! Thanks @thaJeztah on IRC and gist for help. See
    // https://gist.github.com/thaJeztah/6185659e4978789fb2b2. A good place to
    // test is https://regex-golang.appspot.com/assets/html/index.html
    //
    // Useful link for referencing named capturing groups:
    // http://stackoverflow.com/questions/20750843/using-named-matches-from-go-regex
    //
    // There are three match groups: source, destination and mode.
    //

    // RXHostDir is the first option of a source
    private static String rxHostDir       = "[a-z]:\\\\(?:[^\\\\/:*?\"<>|\\r\\n]+\\\\?)*";

    // RXName is the second option of a source
    private static String rxName          = "[^\\\\/:*?\"<>|\\r\\n]+";

    // RXReservedNames are reserved names not possible on Windows
    // private static String rxReservedNames = "(con)|(prn)|(nul)|(aux)|(com[1-9])|(lpt[1-9])";

    // RXSource is the combined possibilities for a source
    private static String rxSource        = "((?<source>((" + rxHostDir + ")|(" + rxName + "))):)?";

    // Source. Can be either a host directory, a name, or omitted:
    //  HostDir:
    //    -  Essentially using the folder solution from
    //       https://www.safaribooksonline.com/library/view/regular-expressions-cookbook/9781449327453/ch08s18.html
    //       but adding case insensitivity.
    //    -  Must be an absolute path such as c:\path
    //    -  Can include spaces such as `c:\program files`
    //    -  And then followed by a colon which is not in the capture group
    //    -  And can be optional
    //  Name:
    //    -  Must not contain invalid NTFS filename characters (https://msdn.microsoft.com/en-us/library/windows/desktop/aa365247(v=vs.85).aspx)
    //    -  And then followed by a colon which is not in the capture group
    //    -  And can be optional

    // RXDestination is the regex expression for the mount destination
    private static String rxDestination   = "(?<destination>([a-z]):((?:\\\\[^\\\\/:*?\"<>\\r\\n]+)*\\\\?))";
    // Destination (aka container path):
    //    -  Variation on hostdir but can be a drive followed by colon as well
    //    -  If a path, must be absolute. Can include spaces
    //    -  Drive cannot be c: (explicitly checked in code, not RegEx)

    // RXMode is the regex expression for the mode of the mount
    // Mode (optional):
    //    -  Hopefully self explanatory in comparison to above regex's.
    //    -  Colon is not in the capture group
    private static String rxMode          = "(:(?<mode>(?i)ro|rw))?";

    private static Pattern pattern;

    BindWindows(String source, String destination, String[] flags) {
        this.source = source;
        this.destination = destination;
        this.flags = flags;
    }

    public String[] getFlags() {
        return flags;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public static BindWindows splitRawSpec(String raw) throws IllegalArgumentException {

        if (pattern == null) {
            pattern = Pattern.compile("^" + rxSource + rxDestination + rxMode + "$");
        }

        Matcher matcher = pattern.matcher(raw.toLowerCase());

        if (!matcher.find()) {
            throw new IllegalArgumentException();
        }

        String source = matcher.group("source");
        String destination = matcher.group("destination");
        String mode = matcher.group("mode");

        return new BindWindows(source, destination, (mode == null ? "" : mode).split(","));
    }


    /**
     * Returns a string representation of this {@link Bind} suitable for inclusion in a JSON message.
     * The format is <code>&lt;host path&gt;:&lt;container path&gt;:&lt;access mode&gt;</code>,
     * like the argument in {@link #parse(String)}.
     *
     * @return a string representation of this {@link Bind}
     */
    @Override
    public String toString() {
        return String.format("%s:%s%s",
                source,
                destination,
                String.join(",", flags).length() > 0 ? ":" + String.join(",", flags) : "");
    }
}
