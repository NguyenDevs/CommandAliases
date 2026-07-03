package org.nguyendevs.commandAliases.model;

import java.util.List;

public record AliasDefinition(
    String configKey,
    String command,
    String execute,
    String commandName,
    List<String> declaredArgs,
    String permission,
    String permissionMessage,
    String permissionDefault
) {}
