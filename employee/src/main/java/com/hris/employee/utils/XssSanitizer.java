package com.hris.employee.utils;


import org.owasp.encoder.Encode;
import org.springframework.stereotype.Component;

@Component
public class XssSanitizer {
    public String sanitizeForHtml(String input) {
        return input == null ? null : Encode.forHtml(input);
    }

    public String sanitizeForJavaScript(String input) {
        return input == null ? null : Encode.forJavaScript(input);
    }

    public String sanitizeForCss(String input) {
        return input == null ? null : Encode.forCssString(input);
    }

    public String sanitizeForHtmlAttribute(String input) {
        return input == null ? null : Encode.forHtmlAttribute(input);
    }
}
