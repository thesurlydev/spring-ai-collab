package dev.surly.ai.collab.util;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.parser.Parser;
import org.apache.commons.text.StringEscapeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConversionUtils {

    private static final Pattern HTML_ENTITY_PATTERN = Pattern.compile("&[a-zA-Z0-9#]+;");

    public static String convertToMarkdown(String html) {
        if (html == null) {
            return null;
        }

        // first, determine if the html is escaped.
        Matcher matcher = HTML_ENTITY_PATTERN.matcher(html);
        if (matcher.find()) {
            html = StringEscapeUtils.unescapeHtml4(html);
        }

        html = html.replaceAll("\\*", "\n");

        return FlexmarkHtmlConverter.builder().build().convert(html);
    }

    public static String convertToHtml(String markdownContent) {
        if (markdownContent == null) {
            return null;
        }
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String rawHtml = renderer.render(parser.parse(markdownContent));
        String out = rawHtml.replaceFirst("```", "<pre><code>").replace("```", "</code></pre>");
        return out;
    }
}
