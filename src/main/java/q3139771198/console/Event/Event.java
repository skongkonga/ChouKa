package q3139771198.console.Event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import q3139771198.console.BungeeUtil;
import q3139771198.console.ChouKa;

import java.text.MessageFormat;

import static q3139771198.console.ChouKa.*;

public class Event implements Listener {

    public Plugin plugin;

    public Event(Plugin plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void onLogin(PlayerJoinEvent e){
        if (e.getPlayer().hasPermission("ChouKa.Admin")){
            for (String s : Reward_info.keySet()){
                if (itemReward.get(s) == null && (commands_info.get(s) == null || commands_info.get(s).isEmpty())){
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        e.getPlayer().sendMessage(ChouKa.prefix + ChatColor.RED + "还未设置【" + s + "】的集齐物品奖励或集齐执行指令，请尽快设置");
                    }, 20);
                }
            }
        }
        if (remind)
            for (String name : enabled){
                if (Integer.parseInt(ChouKa.getTimes(e.getPlayer().getName(), name)) > 0){
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        BungeeUtil.sendPartCommandMessage(e.getPlayer(), ChouKa.prefix +
                                        MessageFormat.format(getMsg("lang_47"), name),
                                "点击", getMsg("lang_45"), "/ck " + name, true);
                    }, 20);
                }
            }
    }
}
