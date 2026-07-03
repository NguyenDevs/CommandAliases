package org.nguyendevs.commandAliases.permission;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import org.nguyendevs.commandAliases.model.AliasDefinition;

import java.util.HashSet;
import java.util.Set;

public class PermissionManager {

    private static final String PERMISSION_DEFAULT_TRUE = "true";
    private static final String PERMISSION_DEFAULT_FALSE = "false";
    private static final String PERMISSION_DEFAULT_OP = "op";

    private final Set<String> registeredPermissions = new HashSet<>();

    public void register(AliasDefinition def, String fallbackDefaultLevel) {
        if (def.permission() == null) return;

        var node = def.permission();
        if (Bukkit.getPluginManager().getPermission(node) != null) return;

        var permDefault = resolveDefault(def.permissionDefault(), fallbackDefaultLevel);
        var permission = new Permission(node, permDefault);
        Bukkit.getPluginManager().addPermission(permission);
        registeredPermissions.add(node);
    }

    public void unregisterAll() {
        for (var node : registeredPermissions) {
            Bukkit.getPluginManager().removePermission(node);
        }
        registeredPermissions.clear();
    }

    private static PermissionDefault resolveDefault(String aliasDefault, String configDefault) {
        var value = aliasDefault != null ? aliasDefault : configDefault;
        if (value == null) value = PERMISSION_DEFAULT_OP;

        return switch (value.toLowerCase()) {
            case PERMISSION_DEFAULT_TRUE -> PermissionDefault.TRUE;
            case PERMISSION_DEFAULT_FALSE -> PermissionDefault.FALSE;
            case PERMISSION_DEFAULT_OP -> PermissionDefault.OP;
            default -> PermissionDefault.OP;
        };
    }
}
