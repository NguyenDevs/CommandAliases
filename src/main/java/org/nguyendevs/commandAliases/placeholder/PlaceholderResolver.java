package org.nguyendevs.commandAliases.placeholder;

import java.util.Map;

public class PlaceholderResolver {

    public String resolve(String template, Map<String, String> placeholders) {
        var result = template;
        for (var entry : placeholders.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
