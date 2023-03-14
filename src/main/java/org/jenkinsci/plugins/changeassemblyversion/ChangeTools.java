package org.jenkinsci.plugins.changeassemblyversion;

import hudson.FilePath;
import hudson.model.TaskListener;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;

public class ChangeTools {

    private final FilePath file;
    private final String regexPattern;
    private final String replacementPattern;

    ChangeTools(FilePath f, String regexPattern, String replacementPattern) {
        this.file = f;
        if (regexPattern != null && !regexPattern.equals("")) {
            this.regexPattern = regexPattern;
        } else {
            this.regexPattern = "Version[(]\"[\\d\\.]+\"[)]";
        }

        if (replacementPattern != null && !replacementPattern.equals("")) {
            this.replacementPattern = replacementPattern;
        } else {
            this.replacementPattern = "Version(\"%s\")";
        }
    }

    public void replace(String replacement, TaskListener listener) throws IOException, InterruptedException {
        if (replacement != null && !replacement.isEmpty()) {
            BOMInputStream inputStream = new BOMInputStream(file.read());
            String content;
            ByteOrderMark bom;
            Charset fileEncoding = Charset.defaultCharset();
            try {
                bom = inputStream.getBOM();
                if (bom != null) {
                    fileEncoding = Charset.forName(bom.getCharsetName());
                }

                content = IOUtils.toString(inputStream, fileEncoding);
            } finally {
                inputStream.close();
            }
            listener.getLogger().println(String.format("Updating file : %s, Replacement : %s", file.getRemote(), replacement));
            content = content.replaceAll(regexPattern, String.format(replacementPattern, replacement));
            try (OutputStream os = file.write()) {
                if (bom != null) {
                    os.write(bom.getBytes());
                }
                os.write(content.getBytes(fileEncoding));
            }
        } else {
            listener.getLogger().println(String.format("Skipping replacement because value is empty."));
        }
    }
}
