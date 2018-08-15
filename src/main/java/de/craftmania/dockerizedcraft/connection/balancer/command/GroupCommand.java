package de.craftmania.dockerizedcraft.connection.balancer.command;

import de.craftmania.dockerizedcraft.connection.balancer.ConnectionBalancer;
import de.craftmania.dockerizedcraft.connection.balancer.model.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class GroupCommand extends Command {

    private Group group;

    private ConnectionBalancer connectionBalancer;

    public GroupCommand(String name, Group group, ConnectionBalancer connectionBalancer) {
        super(name);
        this.group = group;
        this.connectionBalancer = connectionBalancer;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(commandSender instanceof ProxiedPlayer){
            ProxiedPlayer player = (ProxiedPlayer) commandSender;

            String currentServer = player.getServer().getInfo().getName();
            if (connectionBalancer.getServerGroup(currentServer).equalsIgnoreCase(group.getName())) {
                player.sendMessage(new ComponentBuilder("You are already connected to this server group!").color(ChatColor.RED).create());
                return;
            }

            player.connect(group.getStrategy().getServer(group.getServers()));

        }else{
            commandSender.sendMessage(new ComponentBuilder("This command can only be run by a player!").color(ChatColor.RED).create());
        }
    }
}
