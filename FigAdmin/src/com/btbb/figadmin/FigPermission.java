package com.btbb.figadmin;

import java.util.logging.Level;

import net.milkbowl.vault.permission.Permission;
import com.nijikokun.bukkit.Permissions.Permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Permissions class to support different permission systems
 * 
 * @author Serge Humphrey
 * 
 */
public class FigPermission {

    public FigAdmin plugin;

    private Permission vPerm = null;
    private Permissions nPerm = null;

    public FigPermission(FigAdmin fig) {
        this.plugin = fig;

        getPermissions();

    }

    public boolean has(CommandSender sender, String perm) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.isOp()) {
                return true;
            }
            if (nPerm != null) {
                return Permissions.Security.permission(p, perm);
            }
            if (vPerm != null) {
                return vPerm.has(p, perm);
            }
            return false;
        } else {
            // must be console
            return true;
        }
    }

    private void getPermissions() {
        
        Plugin p = plugin.getServer().getPluginManager().getPlugin("Permissions");
        if (p != null && p instanceof Permissions) {
            nPerm = (Permissions) p;
            FigAdmin.log.log(Level.INFO, "[FigAdmin] Using Permissions");
            return;
        }
        p = plugin.getServer().getPluginManager().getPlugin("Vault");
        if (p != null) {
            try {
                RegisteredServiceProvider<Permission> permissionProvider = p.getServer().getServicesManager()
                        .getRegistration(net.milkbowl.vault.permission.Permission.class);
                if (permissionProvider != null) {
                    vPerm = permissionProvider.getProvider();
                }
                FigAdmin.log.log(Level.INFO, "[FigAdmin] Using Vault");
                return;
            } catch (Exception exc) {
                FigAdmin.log.log(Level.WARNING, "[FigAdmin] Can't enable Vault, oh well");
            }
        }
        FigAdmin.log.log(Level.WARNING, "[FigAdmin] Can't find Permissions or Vault, using no permissions");
    }
}
