package q3139771198.console;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BungeeUtil {
    public BungeeUtil() {
    }

    public static void sendShowMessage(Player player, String message, String showmessage) {
        TextComponent send = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
        send.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showmessage))).create()));
        if (player == null) {
            Bukkit.spigot().broadcast(send);
        } else {
            player.spigot().sendMessage(send);
        }
    }

    public static void sendShowItemMessage(Player player, String message, String showmessage) {
        TextComponent send = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
        send.setHoverEvent(new HoverEvent(Action.SHOW_ITEM, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showmessage))).create()));
        if (player == null) {
            Bukkit.spigot().broadcast(send);
        } else {
            player.spigot().sendMessage(send);
        }
    }

    public static void sendPartShowMessage(Player player, String message, String part, String changemessage, String showmessage) {
        if (message.contains(part)) {
            TextComponent all = new TextComponent("");

            for(boolean first = true; message.contains(part); message = message.replaceFirst(part, "")) {
                if (first) {
                    try {
                        all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[0])));
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }
                }

                TextComponent parts = new TextComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', part.replace(part, changemessage))));
                parts.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showmessage))).create()));
                all.addExtra(parts);

                try {
                    all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[1])));
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }

                first = false;
            }

            if (player == null) {
                Bukkit.spigot().broadcast(all);
                return;
            }

            player.spigot().sendMessage(all);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

    }

    public static void sendPartShowItemMessage(Player player, String message, String part, String changemessage, String showitem) {
        if (message.contains(part)) {
            TextComponent all = new TextComponent("");

            for(boolean first = true; message.contains(part); message = message.replaceFirst(part, "")) {
                if (first) {
                    try {
                        all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[0])));
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }
                }

                TextComponent parts = new TextComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', part.replace(part, changemessage))));
                parts.setHoverEvent(new HoverEvent(Action.SHOW_ITEM, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showitem))).create()));
                all.addExtra(parts);

                try {
                    all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[1])));
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }

                first = false;
            }

            if (player == null) {
                Bukkit.spigot().broadcast(all);
                return;
            }

            player.spigot().sendMessage(all);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

    }

    public static void sendCommandMessage(Player player, String message, String showmessage, String command, Boolean usecommand) {
        TextComponent send = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
        String value = null;
        if (usecommand) {
            value = "RUN_COMMAND";
        } else {
            value = "SUGGEST_COMMAND";
        }

        send.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.valueOf(value), command));
        send.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showmessage))).create()));
        if (player == null) {
            Bukkit.spigot().broadcast(send);
        } else {
            player.spigot().sendMessage(send);
        }
    }

    public static void sendCommandItemMessage(Player player, String message, String showitem, String command, Boolean usecommand) {
        TextComponent send = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
        String value = null;
        if (usecommand) {
            value = "RUN_COMMAND";
        } else {
            value = "SUGGEST_COMMAND";
        }

        send.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.valueOf(value), command));
        send.setHoverEvent(new HoverEvent(Action.SHOW_ITEM, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showitem))).create()));
        if (player == null) {
            Bukkit.spigot().broadcast(send);
        } else {
            player.spigot().sendMessage(send);
        }
    }

    public static void sendPartShowCommandMessage(Player player, String message, String part, String changemessage, String showmessage, String command, Boolean usecommand) {
        if (message.contains(part)) {
            TextComponent all = new TextComponent("");

            for(boolean first = true; message.contains(part); message = message.replaceFirst(part, "")) {
                if (first) {
                    try {
                        all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[0])));
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }
                }

                TextComponent parts = new TextComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', part.replace(part, changemessage))));
                parts.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showmessage))).create()));
                String value = null;
                if (usecommand) {
                    value = "RUN_COMMAND";
                } else {
                    value = "SUGGEST_COMMAND";
                }

                parts.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.valueOf(value), command));
                all.addExtra(parts);

                try {
                    all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[1])));
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }

                first = false;
            }

            if (player == null) {
                Bukkit.spigot().broadcast(all);
                return;
            }

            player.spigot().sendMessage(all);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

    }

    public static void sendPartCommandMessage(Player player, String message, String part, String changemessage, String command, Boolean usecommand) {
        if (message.contains(part)) {
            TextComponent all = new TextComponent("");

            for(boolean first = true; message.contains(part); message = message.replaceFirst(part, "")) {
                if (first) {
                    try {
                        all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[0])));
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }
                }

                TextComponent parts = new TextComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', part.replace(part, changemessage))));
                String value = null;
                if (usecommand) {
                    value = "RUN_COMMAND";
                } else {
                    value = "SUGGEST_COMMAND";
                }

                parts.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.valueOf(value), command));
                all.addExtra(parts);

                try {
                    all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[1])));
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }

                first = false;
            }

            if (player == null) {
                Bukkit.spigot().broadcast(all);
                return;
            }

            player.spigot().sendMessage(all);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

    }

    public static void sendPartShowCommandItemMessage(Player player, String message, String part, String changemessage, String showitem, String command, Boolean usecommand) {
        if (message.contains(part)) {
            TextComponent all = new TextComponent("");

            for(boolean first = true; message.contains(part); message = message.replaceFirst(part, "")) {
                if (first) {
                    try {
                        all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[0])));
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }
                }

                TextComponent parts = new TextComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', part.replace(part, changemessage))));
                parts.setHoverEvent(new HoverEvent(Action.SHOW_ITEM, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showitem))).create()));
                String value = null;
                if (usecommand) {
                    value = "RUN_COMMAND";
                } else {
                    value = "SUGGEST_COMMAND";
                }

                parts.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.valueOf(value), command));
                all.addExtra(parts);

                try {
                    all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[1])));
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }

                first = false;
            }

            if (player == null) {
                Bukkit.spigot().broadcast(all);
                return;
            }

            player.spigot().sendMessage(all);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

    }

    public static BaseComponent TransformPartShowCommandMessage(String message, String part, String changemessage, String showmessage, String command, Boolean usecommand) {
        if (message.contains(part)) {
            TextComponent all = new TextComponent("");

            for(boolean first = true; message.contains(part); message = message.replaceFirst(part, "")) {
                if (first) {
                    try {
                        all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[0])));
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }
                }

                TextComponent parts = new TextComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', part.replace(part, changemessage))));
                parts.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showmessage))).create()));
                String value = null;
                if (usecommand) {
                    value = "RUN_COMMAND";
                } else {
                    value = "SUGGEST_COMMAND";
                }

                parts.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.valueOf(value), command));
                all.addExtra(parts);

                try {
                    all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[1])));
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }

                first = false;
            }

            return all;
        } else {
            return new TextComponent(message);
        }
    }
}