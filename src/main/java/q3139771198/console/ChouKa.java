package q3139771198.console;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import q3139771198.console.Event.Event;
import q3139771198.console.database.MySQL;
import q3139771198.console.database.SQLite;
import q3139771198.console.bStats.Metrics;
import q3139771198.console.database.database;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ChouKa extends JavaPlugin {

    public static final String version = "1.12";
    public static final String configVersion = "1.12";
    public Plugin Main = this;
    public static File fileConfig = null;
    public static FileConfiguration config = null;
    public static FileConfiguration Cards = null;
    public static FileConfiguration Reward = null;
    public static FileConfiguration database = null;
    public static FileConfiguration lang = null;
    public static File DataFolder = null;
    public static int mcVersion = 0;
    public static database ciShuDatabase = null;
    public static database cardDatabase = null;
    public static List<String> enabled = new ArrayList<>();
    public static List<String> cardName = new ArrayList<>();
    public static HashMap<String, List<String>> cards_rare_cards = new HashMap<>();
    public HashMap<String, HashMap<String, Integer>> cards_rare_cards_rates = new HashMap<>();
    public static HashMap<String, String> cards_rare_cards_broadcast = new HashMap<>();
    public static HashMap<String, String> cards_rare_cards_title = new HashMap<>();
    public static HashMap<String, String> cards_rare_cards_subtitle = new HashMap<>();
    public static HashMap<String, Integer> cards_guarantee = new HashMap<>();
    public static HashMap<String, List<String>> cards_sounds = new HashMap<>();
    public HashMap<String, HashMap<String, Integer>> cards_info = new HashMap<>();
    public static HashMap<String, String> cards_display_name = new HashMap<>();
    public static HashMap<String, List<String>> Reward_info = new HashMap<>();
    public static HashMap<String, List<String>> commands_info = new HashMap<>();
    public static HashMap<String, String> broadcast_info = new HashMap<>();
    public static HashMap<String, List<String>> reward_sounds = new HashMap<>();
    public static HashMap<String, Boolean> reward_remind = new HashMap<>();
    public static HashMap<String, String> reward_display_name = new HashMap<>();
    public HashMap<String, List<String>> list = new HashMap<>();
    public HashMap<String, Integer> rate = new HashMap<>();
    public HashMap<String, List<String>> rare_list = new HashMap<>();
    public HashMap<String, Integer> rare_rate = new HashMap<>();
    public static HashMap<String, ItemStack> itemReward = new HashMap<>();
    public static boolean isDebug;
    public static String prefix;
    public boolean useNBT = false;
    public static boolean remind = false;
    public static boolean asyn = false;

    public static int cd = 30;

    public String type;
    public String mysql_ip;
    public String mysql_port;
    public String mysql_username;
    public String mysql_password;
    public String mysql_database;
    public String mysql_option;

    public HashMap<String, Long> cds = new HashMap<>();

    private static final Logger log = Logger.getLogger("ChouKa");

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        DataFolder = this.getDataFolder();

        String ver = Bukkit.getVersion();
        ver = ver.substring(ver.indexOf(".") + 1,ver.length() - 1);
        if (ver.contains(" ")){
            ver = ver.substring(0,ver.indexOf(" "));
        }
        if (ver.contains(".")){
            ver = ver.substring(0,ver.indexOf("."));
        }
        if (ver.contains("-")){
            ver = ver.substring(0,ver.indexOf("-"));
        }
        mcVersion = Integer.parseInt(ver);
        getLogger().info("识别到游戏版本为 1." + mcVersion);

        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            getLogger().info("检测到前置正确安装，启用物品nbt保存");
            useNBT = true;
            NBT.init();
        } else {
            getLogger().info("未检测到前置正确安装，关闭物品nbt保存");
            useNBT = false;
        }

        getServer().getPluginManager().registerEvents(new Event(this), this);
        fileConfig = new File(DataFolder, "config.yml");
        getServer().getPluginCommand("ChouKa").setExecutor(new Command());
        getServer().getPluginCommand("ChouKa").setTabCompleter(new Command());
        new Metrics(this, 13223);
        if(load()){
            getLogger().info("插件启动完毕！版本:" +version + " 制作:skongkonga QQ3139771198");
        }else{
            getLogger().log(Level.SEVERE,"插件启动异常！版本:" +version + " 制作:skongkonga QQ3139771198");
        }
    }
    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        getLogger().info("插件已经关闭了！版本:" +version + " 制作:skongkonga QQ3139771198");
    }

    public boolean load() {
        try {
            load0();
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE,"读取配置文件信息出错！");
            this.getServer().getPluginManager().disablePlugin(this);
            e.printStackTrace();
            return false;
        }
        this.getLogger().info("读取配置文件信息成功！");
        return true;
    }
    private void load0() throws IOException {
        this.reloadConfig();
        config = this.getConfig();
        String version = config.getString("version");
        if(!verifyVersion(version)){
            throw new IllegalArgumentException(
                    "config.yml版本无法读取或不符合当前版本！请使用最新版本并不要改动config.yml的version项！");
        }
        lang = YamlConfiguration.loadConfiguration(getYml("lang.yml"));
        enabled = config.getStringList("enabled");
        isDebug = config.getBoolean("DEBUG");
        if (isDebug) log.info("DEBUG模式已开启");
        prefix = config.getString("prefix");
        remind = config.getBoolean("remind");
        asyn = config.getBoolean("asyn");
        cd = config.getInt("cd");
        log.info("登录提醒有次数的玩家抽卡："+remind);
        log.info("一键兑换指令异步执行："+asyn);
        log.info("当前开启的卡组：");
        log.info(enabled.toString());
        log.info("----------------------------------------------------------------");
        this.saveDefaultConfig();
        Cards = YamlConfiguration.loadConfiguration(getYml("Cards.yml"));//加载卡组配置
        cardName.clear();
        cards_info.clear();
        cards_rare_cards.clear();
        cards_rare_cards_broadcast.clear();
        cards_guarantee.clear();
        cards_sounds.clear();
        cards_display_name.clear();
        cards_rare_cards_title.clear();
        cards_rare_cards_subtitle.clear();
        cards_rare_cards_rates.clear();
        for (String s : Cards.getKeys(false)) {
            HashMap<String, Integer> names_rates = new HashMap<>();
            List<String> names = Cards.getStringList(s + "..names");
            List<Integer> rates = Cards.getIntegerList(s + "..rates");
            for(int i = 0; i < names.size(); i++){
                try{
                    names_rates.put(names.get(i),rates.get(i));
                } catch (Exception e) {
                    throw new IllegalArgumentException("请检查卡片名字与概率是否一一对应");
                }

            }
            for (String name : names)
                if (!cardName.contains(name))
                    cardName.add(name);
            cards_info.put(s, names_rates);
            if (Cards.getStringList(s + "..rare_cards") == null) {
                Cards.set(s + "..rare_cards", new ArrayList<>());
            }
            if (Cards.getString(s + "..rare_cards_broadcast") == null) {
                Cards.set(s + "..rare_cards_broadcast", "none");
            }
            if (Cards.getString(s + "..guarantee") == null) {
                Cards.set(s + "..guarantee", 0);
            }
            if (Cards.getStringList(s + "..sounds") == null) {
                Cards.set(s + "..sounds", new ArrayList<>());
            }
            if (Cards.getString(s + "..display_name") == null) {
                Cards.set(s + "..display_name", s);
            }
            if (Cards.getString(s + "..rare_cards_title") == null) {
                Cards.set(s + "..rare_cards_title", "");
            }
            if (Cards.getString(s + "..rare_cards_subtitle") == null) {
                Cards.set(s + "..rare_cards_subtitle", "");
            }
            if (Cards.getString(s + "..rare_cards_rates") == null) {
                Cards.set(s + "..rare_cards_rates", new ArrayList<>());
            }
            cards_rare_cards.put(s, Cards.getStringList(s + "..rare_cards"));
            cards_rare_cards_broadcast.put(s, Cards.getString(s + "..rare_cards_broadcast"));
            cards_guarantee.put(s, Cards.getInt(s + "..guarantee"));
            cards_sounds.put(s, Cards.getStringList(s + "..sounds"));
            cards_display_name.put(s, Cards.getString(s + "..display_name"));
            cards_rare_cards_title.put(s, Cards.getString(s + "..rare_cards_title"));
            cards_rare_cards_subtitle.put(s, Cards.getString(s + "..rare_cards_subtitle"));
            HashMap<String, Integer> rare_names_rates = new HashMap<>();
            List<String> rare_names = Cards.getStringList(s + "..rare_cards");
            List<Integer> rare_rates = Cards.getIntegerList(s + "..rare_cards_rates");
            for(int i = 0; i < rare_names.size(); i++){
                try{
                    rare_names_rates.put(rare_names.get(i),rare_rates.get(i));
                } catch (Exception e) {
                    throw new IllegalArgumentException("请检查卡片名字与概率是否一一对应");
                }

            }
            cards_rare_cards_rates.put(s, rare_names_rates);
        }
        Cards.save(getYml("Cards.yml"));
        log.info("当前设置的卡组有：");
        for (String name : cards_info.keySet()){
            log.info(name);
            log.info("  "+cards_info.get(name).toString());
            log.info("  "+"聊天栏中显示的名字：");
            log.info("    "+cards_display_name.get(name));
            log.info("  "+"稀有卡：");
            log.info("    "+cards_rare_cards_rates.get(name).toString());
            log.info("  "+"抽中稀有卡广播：");
            if (cards_rare_cards_broadcast.get(name).equals("none"))
                log.info("    未开启广播");
            else
                log.info("    "+cards_rare_cards_broadcast.get(name));
            log.info("  "+"抽到稀有卡后发送标题内容：");
            log.info("    "+cards_rare_cards_title.get(name));
            log.info("    "+cards_rare_cards_subtitle.get(name));
            log.info("  "+"稀有卡保底张数：");
            log.info("    "+cards_guarantee.get(name));
            log.info("  "+"抽该卡组播放的音效：");
            log.info("    "+cards_sounds.get(name).toString());
        }
        log.info("----------------------------------------------------------------");
        log.info("当前设置的卡名有：");
        log.info(cardName.toString());
        log.info("----------------------------------------------------------------");
        Reward = YamlConfiguration.loadConfiguration(getYml("Reward.yml"));//加载集齐配置
        Reward_info.clear();
        commands_info.clear();
        broadcast_info.clear();
        reward_sounds.clear();
        itemReward.clear();
        reward_remind.clear();
        reward_display_name.clear();
        for (String s : Reward.getKeys(false)) {
            Reward_info.put(s, Reward.getStringList(s + "..names"));
            try{
                if (!useNBT)
                    itemReward.put(s, Reward.getItemStack(s + "..Item"));
                else{
                    ItemStack item = NBT.streamSerializer.deserializeItemStack(Reward.getString(s + "..Item"));
                    itemReward.put(s, item);
                }
            } catch (Exception ignored) { }
            if (Reward.getString(s + "..commands") == null) {
                Reward.set(s + "..commands", new ArrayList<>());
            }
            if (Reward.getString(s + "..broadcast") == null) {
                Reward.set(s + "..broadcast", "none");
            }
            commands_info.put(s, Reward.getStringList(s + "..commands"));
            broadcast_info.put(s, Reward.getString(s + "..broadcast"));
            if (Reward.getStringList(s + "..sounds") == null) {
                Reward.set(s + "..sounds", new ArrayList<>());
            }
            reward_sounds.put(s, Reward.getStringList(s + "..sounds"));
            if (Reward.getString(s + "..remind") == null) {
                Reward.set(s + "..remind", true);
            }
            reward_remind.put(s, Reward.getBoolean(s + "..remind"));
            if (Reward.getString(s + "..display_name") == null) {
                Reward.set(s + "..display_name", s);
            }
            reward_display_name.put(s, Reward.getString(s + "..display_name"));
        }
        Reward.save(getYml("Reward.yml"));
        log.info("当前设置的集齐配置有：");
        for (String name : Reward_info.keySet()){
            log.info(name);
            log.info("  "+Reward_info.get(name).toString());
            try{
                log.info("  "+itemReward.get(name).toString());
            } catch (Exception e) {
                getServer().getConsoleSender().sendMessage(ChatColor.RED + "未设置【" + name + "】的集齐物品奖励");
            }
            log.info("  "+"聊天栏中显示的名字：");
            log.info("    "+reward_display_name.get(name));
            log.info("  集齐后执行指令：");
            log.info("    "+commands_info.get(name).toString());
            log.info("  集齐后广播：");
            if (broadcast_info.get(name).equals("none"))
                log.info("    未开启广播");
            else
                log.info("    "+broadcast_info.get(name));
            log.info("  "+"集齐后播放的音效：");
            log.info("    "+reward_sounds.get(name).toString());
            log.info("  "+"是否提醒兑换：");
            log.info("    "+reward_remind.get(name));
        }
        log.info("----------------------------------------------------------------");
        rate.clear();
        list.clear();
        rare_rate.clear();
        rare_list.clear();
        for (String name : enabled){
            try {
                HashMap<String, Integer> names_rates = cards_info.get(name);//卡名—几率
                Set<String> names = names_rates.keySet();
                for (String n : names) {
                    if (!rate.containsKey(name)) {
                        rate.put(name, names_rates.get(n));
                    } else
                        rate.put(name, rate.get(name) + names_rates.get(n));
                    for (int i = 1; i <= names_rates.get(n); i++) {
                        if (!list.containsKey(name)) {
                            list.put(name, new ArrayList<>(Collections.singleton(n)));
                        } else
                            list.get(name).add(n);
                    }
                }
            } catch (Exception e) {
                log.log(Level.SEVERE, name + "卡组配置出错，请检查Cards.yml中是否存在该卡组");
            }
            try {
                HashMap<String, Integer> names_rates = cards_rare_cards_rates.get(name);//卡名—几率
                Set<String> names = names_rates.keySet();
                for (String n : names) {
                    if (!rare_rate.containsKey(name)) {
                        rare_rate.put(name, names_rates.get(n));
                    } else
                        rare_rate.put(name, rare_rate.get(name) + names_rates.get(n));
                    for (int i = 1; i <= names_rates.get(n); i++) {
                        if (!rare_list.containsKey(name)) {
                            rare_list.put(name, new ArrayList<>(Collections.singleton(n)));
                        } else
                            rare_list.get(name).add(n);
                    }
                }
            } catch (Exception e) {
                log.log(Level.SEVERE, name + "卡组配置出错，请检查Cards.yml中是否存在该稀有卡卡组");
            }
        }
        database = YamlConfiguration.loadConfiguration(getYml("database.yml"));//加载数据库配置
        type = database.getString("type");
        log.info("type: " + type);
        if (type.equalsIgnoreCase("MySQL")){
            mysql_ip = database.getString("mysql_ip");
            mysql_port = database.getString("mysql_port");
            mysql_username = database.getString("mysql_username");
            mysql_password = database.getString("mysql_password");
            mysql_database = database.getString("mysql_database");
            mysql_option = database.getString("mysql_option");
            log.info("mysql_ip: " + mysql_ip);
            log.info("mysql_port: " + mysql_port);
            log.info("mysql_username: " + mysql_username);
            log.info("mysql_password: " + mysql_password);
            log.info("mysql_database: " + mysql_database);
            log.info("mysql_option: " + mysql_option);
        }

        if (ciShuDatabase != null) ciShuDatabase.closeConnection();
        if (cardDatabase != null) cardDatabase.closeConnection();

        if (type.equalsIgnoreCase("MySQL")) {
            ciShuDatabase = new MySQL(mysql_ip+":"+mysql_port+"/"+mysql_database+"?"+mysql_option,
                    mysql_username, mysql_password, enabled, "cishu");
            if (ciShuDatabase.checkForAddColumn(enabled, "cishu")) {
                log.info("MySQL成功连接次数数据库");
            } else {
                log.log(Level.SEVERE, "MySQL未能连接次数数据库");
            }
            cardDatabase = new MySQL(mysql_ip+":"+mysql_port+"/"+mysql_database+"?"+mysql_option,
                    mysql_username, mysql_password, cardName, "card");
            if (cardDatabase.checkForAddColumn(cardName, "card")) {
                log.info("MySQL成功连接仓库数据库");
            } else {
                log.log(Level.SEVERE, "MySQL未能连接仓库数据库，卡名不能有重复的哦");
            }
        } else if (type.equalsIgnoreCase("SQLite")) {
            ciShuDatabase = new SQLite(new File(getDataFolder(), "database.db"), enabled, "cishu");
            if (ciShuDatabase.checkForAddColumn(enabled, "cishu")) {
                log.info("SQLite成功连接次数数据库");
            } else {
                log.log(Level.SEVERE, "SQLite未能连接次数数据库");
            }
            cardDatabase = new SQLite(new File(getDataFolder(), "database.db"), cardName, "card");
            if (cardDatabase.checkForAddColumn(cardName, "card")) {
                log.info("SQLite成功连接仓库数据库");
            } else {
                log.log(Level.SEVERE, "SQLite未能连接仓库数据库，卡名不能有重复的哦");
            }
        } else {
            log.log(Level.SEVERE, "数据库设置填写有误！");
        }
        ciShuDatabase.closeConnection();
        cardDatabase.closeConnection();
    }

    public static String getMsg(String s) {
        return Objects.<String>requireNonNull(lang.getString(s));
    }

    private File getYml(String path) {
        File file = new File(DataFolder, path);
        if (!file.exists())
            if (this.getResource(path) != null) {
                this.saveResource(path, true);
            }
        return file;
    }

    private boolean verifyVersion(String version) {
        if(version==null)return false;
        return version.equalsIgnoreCase(configVersion);
    }

    public static String getTimes(String playername, String name){
        ciShuDatabase.openConnection(enabled, "cishu");
        String times = ciShuDatabase.getCardTimes(playername, name);
        ciShuDatabase.closeConnection();
        return times;
    }

    public class Command implements TabExecutor {

        @Override
        public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label,
                                 String[] args) {
            int leng=args.length;
            if(leng == 0) {
                if (sender instanceof ConsoleCommandSender){
                    help(sender);
                    return true;
                }
                sender.sendMessage(prefix + getMsg("lang_01"));
                for (String name : enabled){
                    if (cards_info.containsKey(name)) {
                        BungeeUtil.sendCommandMessage((Player) sender, cards_display_name.get(name),
                                getMsg("lang_02"), "/ck " + name, true);
                        BungeeUtil.sendCommandMessage((Player) sender, cards_display_name.get(name)+"*10",
                                getMsg("lang_48"), "/ck " + name+"*10", true);
                    }
                }
                sender.sendMessage(prefix + getMsg("lang_03"));
                sender.sendMessage(prefix + getMsg("lang_04"));
                return true;
            }
            String arg1=args[0].toLowerCase();
            switch(leng){
                case 1:
                    switch(arg1){
                        case "help":
                            help(sender);
                            return true;
                        case "reload":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            this.load(sender);
                            return true;
                        case "mycards":
                            if (sender instanceof ConsoleCommandSender) {
                                sender.sendMessage("后台不可以查看拥有的卡");
                                return true;
                            }
                            mycards(sender, sender.getName());
                            return true;
                        case "mytimes":
                            if (sender instanceof ConsoleCommandSender) {
                                sender.sendMessage("后台不可以查看次数");
                                return true;
                            }
                            mytimes(sender, sender.getName());
                            return true;
                        case "setitem":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            sender.sendMessage(prefix + " /ChouKa setitem [集齐卡组名] 设置手上物品为集齐卡组的奖励物品");
                            return true;
                        case "duihuan":
                            if (sender instanceof ConsoleCommandSender) {
                                sender.sendMessage("后台不可以进行兑换");
                                return true;
                            }
                            sender.sendMessage(prefix + getMsg("lang_06"));
                            for (String name : Reward_info.keySet()){
                                BungeeUtil.sendCommandMessage((Player)sender, reward_display_name.get(name),
                                        getMsg("lang_07"), "/ck duihuan " + name, true);
                            }
                            sender.sendMessage(prefix + " /ChouKa duihuan [集齐卡组名] 兑换集齐奖励");
                            BungeeUtil.sendCommandMessage((Player)sender, prefix + getMsg("lang_51"),
                                    getMsg("lang_51"), "/ck yijianduihuan", true);
                            return true;
                        case "yijianduihuan":
                            if (sender instanceof ConsoleCommandSender) {
                                sender.sendMessage("后台不可以进行兑换");
                                return true;
                            }
                            if (checkCd(sender)) {
                                return true;
                            }
                            if (asyn) {
                                Bukkit.getScheduler().runTaskAsynchronously(Main, () -> {
                                    try {
                                        yijianduihuan(sender);
                                    } catch (IOException e) {
                                        sender.sendMessage(prefix + getMsg("lang_11"));
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                try {
                                    yijianduihuan(sender);
                                } catch (IOException e) {
                                    sender.sendMessage(prefix + getMsg("lang_11"));
                                    e.printStackTrace();
                                }
                            }
                            return true;
                        case "addtime":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            sender.sendMessage(prefix + " /ChouKa addtime [玩家名（注意大小写）] [卡组名] [次数] 添加玩家抽卡次数");
                            return true;
                        case "deltime":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            sender.sendMessage(prefix + " /ChouKa deltime [玩家名（注意大小写）] [卡组名] [次数] 减少玩家抽卡次数");
                            return true;
                        case "setcard":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            sender.sendMessage(prefix + " /ChouKa setcard [玩家名（注意大小写）] [卡名] [数量] 设置玩家某卡的数量");
                            return true;
                        case "addcard":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            sender.sendMessage(prefix + " /ChouKa addcard [玩家名（注意大小写）] [卡名] [数量] 添加玩家某卡数量");
                            return true;
                        case "delcard":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            sender.sendMessage(prefix + " /ChouKa delcard [玩家名（注意大小写）] [卡名] [数量] 删除玩家某卡数量");
                            return true;
                        default:
                            if (sender instanceof ConsoleCommandSender) {
                                sender.sendMessage("后台不可以进行抽卡");
                                return true;
                            }
                            if (asyn) {
                                Bukkit.getScheduler().runTaskAsynchronously(Main, () -> {
                                    chou(arg1, sender);
                                });
                            } else {
                                chou(arg1, sender);
                            }
                            return true;
                    }
                case 2:
                    switch(arg1){
                        case "help":
                            help(sender);
                            return true;
                        case "reload":
                            this.load(sender);
                            return true;
                        case "setitem":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            if (sender instanceof ConsoleCommandSender) {
                                sender.sendMessage("后台不可以设置物品奖励");
                                return true;
                            }
                            try {
                                setItem(sender, args[1]);
                            } catch (IOException e) {
                                sender.sendMessage(prefix + " 设置物品奖励失败，请查看后台");
                                e.printStackTrace();
                            }
                            return true;
                        case "duihuan":
                            if (sender instanceof ConsoleCommandSender) {
                                sender.sendMessage("后台不可以进行兑换");
                                return true;
                            }
                            if (asyn) {
                                Bukkit.getScheduler().runTaskAsynchronously(Main, () -> {
                                    try {
                                        duihuan(sender, args[1]);
                                    } catch (IOException e) {
                                        sender.sendMessage(prefix + getMsg("lang_11"));
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                try {
                                    duihuan(sender, args[1]);
                                } catch (IOException e) {
                                    sender.sendMessage(prefix + getMsg("lang_11"));
                                    e.printStackTrace();
                                }
                            }
                            return true;
                        case "addtime":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            sender.sendMessage(prefix + " /ChouKa addtime [玩家名（注意大小写）] [卡组名] [次数] 添加玩家抽卡次数");
                            return true;
                        case "deltime":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            sender.sendMessage(prefix + " /ChouKa deltime [玩家名（注意大小写）] [卡组名] [次数] 减少玩家抽卡次数");
                            return true;
                        case "mycards":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                mycards(sender, sender.getName());
                                return true;
                            }
                            mycards(sender, args[1]);
                            return true;
                        case "mytimes":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                mytimes(sender, sender.getName());
                                return true;
                            }
                            mytimes(sender, args[1]);
                            return true;
                        case "setcard":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            sender.sendMessage(prefix + " /ChouKa setcard [玩家名（注意大小写）] [卡名] [数量] 设置玩家某卡的数量");
                            return true;
                        case "addcard":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            sender.sendMessage(prefix + " /ChouKa addcard [玩家名（注意大小写）] [卡名] [数量] 添加玩家某卡数量");
                            return true;
                        case "delcard":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            sender.sendMessage(prefix + " /ChouKa delcard [玩家名（注意大小写）] [卡名] [数量] 删除玩家某卡数量");
                            return true;
                        default:
                            sender.sendMessage(prefix + getMsg("lang_13"));
                            help(sender);
                            return true;
                    }
                case 3:
                    switch (arg1){
                        case "addtime":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            sender.sendMessage(prefix + " /ChouKa addtime [玩家名（注意大小写）] [卡组名] [次数] 添加玩家抽卡次数");
                            return true;
                        case "deltime":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            sender.sendMessage(prefix + " /ChouKa deltime [玩家名（注意大小写）] [卡组名] [次数] 减少玩家抽卡次数");
                            return true;
                        case "setcard":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            sender.sendMessage(prefix + " /ChouKa setcard [玩家名（注意大小写）] [卡名] [数量] 设置玩家某卡的数量");
                            return true;
                        case "addcard":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            sender.sendMessage(prefix + " /ChouKa addcard [玩家名（注意大小写）] [卡名] [数量] 添加玩家某卡数量");
                            return true;
                        case "delcard":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            sender.sendMessage(prefix + " /ChouKa delcard [玩家名（注意大小写）] [卡名] [数量] 删除玩家某卡数量");
                            return true;
                        default:
                            sender.sendMessage(prefix + getMsg("lang_13"));
                            help(sender);
                            return true;
                    }
                case 4:
                    switch (arg1){
                        case "addtime":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            if (!enabled.contains(args[2])){
                                sender.sendMessage(prefix + getMsg("lang_14"));
                                return true;
                            }
                            try{
                                if (Integer.parseInt(args[3]) == 0){
                                    sender.sendMessage(prefix + getMsg("lang_15"));
                                }
                                ciShuDatabase.openConnection(enabled, "cishu");
                                int oldtimes = Integer.parseInt(ciShuDatabase.getCardTimes(args[1], args[2]));
                                int times = oldtimes + Integer.parseInt(args[3]);
                                if (ciShuDatabase.updateTime(args[1], args[2], times)){
                                    sender.sendMessage(prefix + MessageFormat.format(getMsg("lang_16"),
                                            args[1], args[3], args[2], times));
                                }
                            } catch (NumberFormatException e) {
                                sender.sendMessage(prefix + getMsg("lang_17"));
                                sender.sendMessage(prefix + " /ChouKa addtime [玩家名（注意大小写）] [卡组名] [次数] 添加玩家抽卡次数");
                            }
                            ciShuDatabase.closeConnection();
                            return true;

                        case "deltime":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            if (!enabled.contains(args[2])){
                                sender.sendMessage(prefix + getMsg("lang_14"));
                                return true;
                            }
                            try{
                                if (Integer.parseInt(args[3]) == 0){
                                    sender.sendMessage(prefix + getMsg("lang_15"));
                                }
                                ciShuDatabase.openConnection(enabled, "cishu");
                                int oldtimes = Integer.parseInt(ciShuDatabase.getCardTimes(args[1], args[2]));
                                int times = oldtimes - Integer.parseInt(args[3]);
                                if (times < 0){
                                    sender.sendMessage(prefix + MessageFormat.format(getMsg("lang_18"),
                                            oldtimes));
                                    ciShuDatabase.closeConnection();
                                    return true;
                                }
                                if (ciShuDatabase.updateTime(args[1], args[2], times)){
                                    sender.sendMessage(prefix + MessageFormat.format(getMsg("lang_19"),
                                            args[1], args[3], args[2], times));
                                }
                            } catch (NumberFormatException e) {
                                sender.sendMessage(prefix + getMsg("lang_17"));
                                sender.sendMessage(prefix + " /ChouKa deltime [玩家名（注意大小写）] [卡组名] [次数] 减少玩家抽卡次数");
                            }
                            ciShuDatabase.closeConnection();
                            return true;
                        case "setcard":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            cardDatabase.openConnection(cardName, "card");
                            String name = args[2].replace("&", "§");
                            if (cardName.contains(name)) {
                                try {
                                    cardDatabase.updateCard(args[1], name, Integer.parseInt(args[3]));
                                    sender.sendMessage(prefix + MessageFormat.format(getMsg("lang_20"),
                                            args[1], name, args[3]));
                                } catch (NumberFormatException e){
                                    sender.sendMessage(prefix + getMsg("lang_21"));
                                    sender.sendMessage(prefix + " /ChouKa setcard [玩家名（注意大小写）] [卡名] [数量] 设置玩家某卡的数量");
                                }
                            } else {
                                sender.sendMessage(prefix + getMsg("lang_22"));
                                sender.sendMessage(prefix + " /ChouKa setcard [玩家名（注意大小写）] [卡名] [数量] 设置玩家某卡的数量");
                            }
                            cardDatabase.closeConnection();
                            return true;
                        case "addcard":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            cardDatabase.openConnection(cardName, "card");
                            String addname = args[2].replace("&", "§");
                            if (cardName.contains(addname)) {
                                try {
                                    if (Integer.parseInt(args[3]) == 0){
                                        sender.sendMessage(prefix + getMsg("lang_15"));
                                    }
                                    int times = Integer.parseInt(cardDatabase.getCard(sender.getName(), addname)) +
                                            Integer.parseInt(args[3]);
                                    cardDatabase.updateCard(args[1], addname, times);
                                    sender.sendMessage(prefix + MessageFormat.format(getMsg("lang_52"),
                                            args[1], addname, args[3], times));
                                } catch (NumberFormatException e){
                                    sender.sendMessage(prefix + getMsg("lang_21"));
                                    sender.sendMessage(prefix + " /ChouKa addcard [玩家名（注意大小写）] [卡名] [数量] 添加玩家某卡数量");
                                }
                            } else {
                                sender.sendMessage(prefix + getMsg("lang_22"));
                                sender.sendMessage(prefix + " /ChouKa addcard [玩家名（注意大小写）] [卡名] [数量] 添加玩家某卡数量");
                            }
                            cardDatabase.closeConnection();
                            return true;
                        case "delcard":
                            if (!sender.hasPermission("ChouKa.Admin")){
                                help(sender);
                                return true;
                            }
                            cardDatabase.openConnection(cardName, "card");
                            String delname = args[2].replace("&", "§");
                            if (cardName.contains(delname)) {
                                try {
                                    if (Integer.parseInt(args[3]) == 0){
                                        sender.sendMessage(prefix + getMsg("lang_15"));
                                    }
                                    int oldtimes = Integer.parseInt(cardDatabase.getCard(sender.getName(), delname));
                                    int times = oldtimes - Integer.parseInt(args[3]);
                                    if (times < 0){
                                        sender.sendMessage(prefix + MessageFormat.format(getMsg("lang_54"),
                                                oldtimes, delname));
                                        cardDatabase.closeConnection();
                                        return true;
                                    }
                                    cardDatabase.updateCard(args[1], delname, times);
                                    sender.sendMessage(prefix + MessageFormat.format(getMsg("lang_53"),
                                            args[1], delname, args[3], times));
                                } catch (NumberFormatException e){
                                    sender.sendMessage(prefix + getMsg("lang_21"));
                                    sender.sendMessage(prefix + " /ChouKa delcard [玩家名（注意大小写）] [卡名] [数量] 删除玩家某卡数量");
                                }
                            } else {
                                sender.sendMessage(prefix + getMsg("lang_22"));
                                sender.sendMessage(prefix + " /ChouKa delcard [玩家名（注意大小写）] [卡名] [数量] 删除玩家某卡数量");
                            }
                            cardDatabase.closeConnection();
                            return true;
                    }
                default:
                    sender.sendMessage(prefix + getMsg("lang_13"));
                    help(sender);
                    return true;
            }
        }

        private boolean checkCd(CommandSender sender) {
            String name = sender.getName();
            if (cds.containsKey(name)) {
                long oldTime = cds.get(name);
                long nowTime = System.currentTimeMillis();
                if (nowTime - oldTime < cd * 1000L) {
                    sender.sendMessage(prefix + MessageFormat.format(getMsg("lang_55"), String.valueOf(cd - (nowTime - oldTime) / 1000)));
                    return true;
                } else {
                    cds.put(name, System.currentTimeMillis());
                    return false;
                }
            } else {
                cds.put(name, System.currentTimeMillis());
                return false;
            }
        }

        private void mytimes(CommandSender sender, String playerName) {
            if (sender.getName().equals(playerName))
                sender.sendMessage(prefix + getMsg("lang_23"));
            else
                sender.sendMessage(prefix + MessageFormat.format(getMsg("lang_24"), playerName));
            for (String name : enabled){
                sender.sendMessage(cards_display_name.get(name) + " : " + getTimes(playerName, name));
                if (cards_info.containsKey(name) && sender.getName().equals(playerName) && Integer.parseInt(getTimes(playerName, name)) > 0)
                    BungeeUtil.sendCommandMessage((Player)sender, getMsg("lang_25"),
                            MessageFormat.format(getMsg("lang_26"), name), "/ck " + name, true);
            }

        }

        private void mycards(CommandSender sender, String playerName) {
            if (sender.getName().equals(playerName))
                sender.sendMessage(prefix + getMsg("lang_27"));
            else
                sender.sendMessage(prefix + getMsg("lang_28"));
            HashMap<String, Integer> playerCards = getcards(playerName);
            for (String name : cardName){
                sender.sendMessage(name + " : " + playerCards.get(name));
            }
            for (String cardSetName : Reward_info.keySet()) {
                int num = isGetAll(getcards(playerName), cardSetName, sender);
                if (num != 0) {
                    if (sender.getName().equals(playerName) && sender instanceof Player)
                        BungeeUtil.sendPartCommandMessage((Player)sender, prefix +
                                        MessageFormat.format(getMsg("lang_29"), num, cardSetName), "兑换",
                                getMsg("lang_30"), "/ck duihuan " + cardSetName, true);
                    else
                        sender.sendMessage(prefix + MessageFormat.format(getMsg("lang_31"), num, cardSetName));
                }
            }
            if (sender instanceof Player)
                BungeeUtil.sendCommandMessage((Player)sender, prefix + getMsg("lang_51"),
                        getMsg("lang_51"), "/ck yijianduihuan", true);
        }

        private void duihuan(CommandSender sender, String name) throws IOException {
            if (!Reward_info.containsKey(name)){
                sender.sendMessage(prefix + getMsg("lang_32"));
                return;
            }
            boolean flag = false;
            cardDatabase.openConnection(cardName, "card");
            for (String cardname : Reward_info.get(name)){
                if (Integer.parseInt(cardDatabase.getCard(sender.getName(), cardname)) == 0){
                    if (!flag){
                        sender.sendMessage(prefix + getMsg("lang_33"));
                        sender.sendMessage(Reward_info.get(name).toString());
                        sender.sendMessage(prefix + getMsg("lang_34"));
                        sender.sendMessage(cardname);
                        flag = true;
                    }
                    else{
                        sender.sendMessage(cardname);
                    }
                }
            }
            if (!flag){
                if (getItem(sender, name) || !commands_info.get(name).isEmpty()) {
                    for (String cardname : Reward_info.get(name)) {
                        cardDatabase.updateCard(sender.getName(), cardname,
                                Integer.parseInt(cardDatabase.getCard(sender.getName(), cardname)) - 1);
                    }
                    BungeeUtil.sendPartCommandMessage((Player) sender, prefix +
                            MessageFormat.format(getMsg("lang_35"), name),"兑换吧",
                            getMsg("lang_30"), "/ck duihuan " + name, true);
                    if (!commands_info.get(name).isEmpty())
                        for (String cmd : commands_info.get(name)) {
                            if (asyn)
                                Bukkit.getScheduler().scheduleSyncDelayedTask(Main, () -> {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                            cmd.replace("{player}", sender.getName()).replace("{cardname}", name));
                                });
                            else {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                        cmd.replace("{player}", sender.getName()).replace("{cardname}", name));
                            }
                        }
                    if (!broadcast_info.get(name).equals("none"))
                        if (asyn)
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main, () -> {
                                Bukkit.broadcastMessage(broadcast_info.get(name).replace("{player}",
                                        sender.getName()).replace("{cardname}", name));
                            });
                        else
                            Bukkit.broadcastMessage(broadcast_info.get(name).replace("{player}",
                                    sender.getName()).replace("{cardname}", name));
                    if (!reward_sounds.get(name).isEmpty() && reward_sounds.get(name) != null) {
                        Player player = (Player) sender;
                        for (String sounds : reward_sounds.get(name)) {
                            playSound(player, sounds);
                        }
                    }
                }
            } else {
                BungeeUtil.sendCommandMessage((Player)sender, prefix + getMsg("lang_36"), getMsg("lang_36"),
                        "/ck mycards", true);
            }
            cardDatabase.closeConnection();
        }

        private void yijianduihuan(CommandSender sender) throws IOException {
            String playerName = sender.getName();
            boolean isEmpty = true;
            for (String name : Reward_info.keySet()) {
                int num = isGetAll(getcards(playerName), name, sender);
                cardDatabase.openConnection(cardName, "card");
                if (num != 0) {
                    isEmpty = false;
                    int times = getItem(sender, name, num);
                    if (times > 0) {
                        for (String cardname : Reward_info.get(name)) {
                            cardDatabase.updateCard(sender.getName(), cardname,
                                    Integer.parseInt(cardDatabase.getCard(sender.getName(), cardname)) - times);
                        }
                        for (int i = 0; i < times; i++) {
                            if (!commands_info.get(name).isEmpty())
                                for (String cmd : commands_info.get(name)) {
                                    if (asyn)
                                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main, () -> {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                                    cmd.replace("{player}", sender.getName()).replace("{cardname}", name));
                                        });
                                    else {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                                cmd.replace("{player}", sender.getName()).replace("{cardname}", name));
                                    }
                                }
                            if (!broadcast_info.get(name).equals("none"))
                                if (asyn)
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main, () -> {
                                        Bukkit.broadcastMessage(broadcast_info.get(name).replace("{player}",
                                                sender.getName()).replace("{cardname}", name));
                                    });
                                else
                                    Bukkit.broadcastMessage(broadcast_info.get(name).replace("{player}",
                                            sender.getName()).replace("{cardname}", name));
                        }
                        if (!reward_sounds.get(name).isEmpty() && reward_sounds.get(name) != null) {
                            Player player = (Player) sender;
                            for (String sounds : reward_sounds.get(name)) {
                                playSound(player, sounds);
                            }
                        }
                    }
                    sender.sendMessage(prefix + MessageFormat.format(getMsg("lang_50"), times, name));
                }
                cardDatabase.closeConnection();
            }
            if (isEmpty) {
                sender.sendMessage(prefix + getMsg("lang_49"));
            }
        }

        private boolean getItem(CommandSender sender, String name) {
            Player player = (Player) sender;
            ItemStack newItem;
            try {
                if (!useNBT) {
                    newItem = Reward.getItemStack(name + "..Item");
                } else {
                    newItem = NBT.streamSerializer.deserializeItemStack(Reward.getString(name + "..Item"));
                }
            } catch (Exception e){
                //sender.sendMessage(prefix + getMsg("lang_37"));
                return false;
            }
            if (newItem == null){
                //sender.sendMessage(prefix + getMsg("lang_37"));
                return false;
            }
            if (player.getInventory().firstEmpty() == -1){
                sender.sendMessage(prefix + getMsg("lang_38"));
                return false;
            }
            try {
                player.getInventory().addItem(newItem);
                return true;
            } catch (Exception e) {
                sender.sendMessage(prefix + getMsg("lang_11"));
                e.printStackTrace();
            }
            return false;
        }

        private int getItem(CommandSender sender, String name, int num) {//一键兑换使用
            Player player = (Player) sender;
            ItemStack newItem;
            int times = 0;
            try {
                if (!useNBT) {
                    newItem = Reward.getItemStack(name + "..Item");
                } else {
                    newItem = NBT.streamSerializer.deserializeItemStack(Reward.getString(name + "..Item"));
                }
            } catch (Exception e){
                //sender.sendMessage(prefix + getMsg("lang_37"));
                return num;
            }
            if (newItem == null){
                //sender.sendMessage(prefix + getMsg("lang_37"));
                return num;
            }
            for (int i = 0; i < num; i++) {
                try {
                    if (player.getInventory().firstEmpty() == -1){
                        sender.sendMessage(prefix + getMsg("lang_38"));
                        return times;
                    }
                    player.getInventory().addItem(newItem);
                    times++;
                } catch (Exception e) {
                    sender.sendMessage(prefix + getMsg("lang_11"));
                    e.printStackTrace();
                    break;
                }
            }

            return times;
        }

        private void setItem(CommandSender sender, String name) throws IOException {
            if (!Reward_info.containsKey(name)) {
                sender.sendMessage(prefix + getMsg("lang_39"));
                return;
            }
            Player player = (Player) sender;
            ItemStack itemInHand;
            if (mcVersion > 8){
                itemInHand = player.getInventory().getItemInMainHand();
            } else {
                itemInHand = player.getItemInHand();
            }
            if (itemInHand != null && itemInHand.getType() != Material.AIR) {
                if (!useNBT)
                    Reward.set(name+ "..Item", itemInHand);
                else {
                    String item = NBT.streamSerializer.serializeItemStack(itemInHand);
                    Reward.set(name+ "..Item", item);
                }
                Reward.save(new File(DataFolder, "Reward.yml"));
                itemReward.put(name, itemInHand);
                player.sendMessage(prefix + MessageFormat.format(getMsg("lang_40"), name));
            } else{
                sender.sendMessage(prefix + getMsg("lang_41"));
            }
        }

        private String getTimes(String playername, String name){
            ciShuDatabase.openConnection(enabled, "cishu");
            String times = ciShuDatabase.getCardTimes(playername, name);
            ciShuDatabase.closeConnection();
            return times;
        }

        private HashMap<String, Integer> getcards(String playerName) {
            HashMap<String, Integer> playerCards = new HashMap<>();
            cardDatabase.openConnection(cardName, "card");
            for (String name : cardName)
                playerCards.put(name, Integer.parseInt(cardDatabase.getCard(playerName, name)));
            cardDatabase.closeConnection();
            return playerCards;
        }

        private Integer isGetAll(HashMap<String, Integer> playerCards, String cardSetName, CommandSender sender){
            int min;
            try {
                min = playerCards.get(Reward_info.get(cardSetName).get(0));
            } catch (Exception e) {
                sender.sendMessage(prefix + getMsg("lang_42"));
                log.log(Level.SEVERE, "集齐配置出错，请检查Reward.yml与Cards.yml中的卡名是否一致");
                return 0;
            }
            for (String cardname : Reward_info.get(cardSetName)) {
                int num = 0;
                try {
                    num = playerCards.get(cardname);
                } catch (Exception e) {
                    sender.sendMessage(prefix + getMsg("lang_42"));
                    log.log(Level.SEVERE, "集齐配置出错，请检查Reward.yml与Cards.yml中的卡名是否一致");
                }
                if (min > num)
                    min = num;
                if (num == 0)
                    break;
            }
            return min;
        }
        private void chou(String name, CommandSender sender) {
            if (name.endsWith("*10")) {
                name = name.substring(0, name.length() - 3);
                chou10(name, sender);
                return;
            }
            if (!cards_info.containsKey(name) || !enabled.contains(name)) {
                sender.sendMessage(prefix + getMsg("lang_43"));
                sender.sendMessage(prefix + getMsg("lang_04"));
                return;
            }
            ciShuDatabase.openConnection(enabled, "cishu");
            cardDatabase.openConnection(cardName, "card");
            if (Integer.parseInt(ciShuDatabase.getCardTimes(sender.getName(), name)) > 0){
                ciShuDatabase.updateTime(sender.getName(), name,
                        Integer.parseInt(ciShuDatabase.getCardTimes(sender.getName(), name)) - 1);

                Random random = new Random();
                int randomNum = random.nextInt(rate.get(name));
                String result = list.get(name).get(randomNum);
                //sender.sendMessage("恭喜你抽中了 " + result);
                BungeeUtil.sendPartCommandMessage((Player)sender, prefix + MessageFormat.format(getMsg("lang_44"),
                        name, result), "点击", getMsg("lang_45"), "/ck " + name, true);
                cardDatabase.updateCard(sender.getName(), result,
                        Integer.parseInt(cardDatabase.getCard(sender.getName(), result)) + 1);
                if (!cards_rare_cards.get(name).isEmpty() && cards_rare_cards.get(name) != null) {
                    for (String card : cards_rare_cards.get(name)) {
                        if (card.equals(result)) {
                            if (!cards_rare_cards_broadcast.get(name).equals("none")) {
                                if (asyn) {
                                    String finalName = name;
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main, () -> {
                                        Bukkit.broadcastMessage(cards_rare_cards_broadcast.get(finalName).replace("{player}",
                                                sender.getName()).replace("{cardname}", result));
                                    });
                                }
                                else
                                    Bukkit.broadcastMessage(cards_rare_cards_broadcast.get(name).replace("{player}",
                                            sender.getName()).replace("{cardname}", result));
                            }
                            if (!cards_rare_cards_title.get(name).equals("") ||
                                    !cards_rare_cards_subtitle.get(name).equals("")) {
                                sendTitle(sender, name, result);
                            }
                            break;
                        }
                    }
                }
                if (!cards_sounds.get(name).isEmpty() && cards_sounds.get(name) != null) {
                    Player player = (Player) sender;
                    for (String sounds : cards_sounds.get(name)) {
                        playSound(player, sounds);
                    }
                }
                remind(sender);
            } else {
                sender.sendMessage(prefix + getMsg("lang_46"));
            }
            ciShuDatabase.closeConnection();
            cardDatabase.closeConnection();
        }

        private void chou10(String name, CommandSender sender) {
            if (!cards_info.containsKey(name) || !enabled.contains(name)) {
                sender.sendMessage(prefix + getMsg("lang_43"));
                sender.sendMessage(prefix + getMsg("lang_04"));
                return;
            }
            ciShuDatabase.openConnection(enabled, "cishu");
            cardDatabase.openConnection(cardName, "card");
            if (Integer.parseInt(ciShuDatabase.getCardTimes(sender.getName(), name)) >= 10){
                ciShuDatabase.updateTime(sender.getName(), name,
                        Integer.parseInt(ciShuDatabase.getCardTimes(sender.getName(), name)) - 10);
                List<String> results = new ArrayList<>();
                if (cards_guarantee.get(name) != 0 && !cards_rare_cards.get(name).isEmpty()) {
                    real_guarantee(cards_guarantee.get(name), rate.get(name), results, list.get(name),
                            cards_rare_cards.get(name), name, sender);
                } else {
                    for (int i = 0; i < 10; i++) {
                        Random random = new Random();
                        int randomNum = random.nextInt(rate.get(name));
                        String result = list.get(name).get(randomNum);
                        //sender.sendMessage("恭喜你抽中了 " + result);
                        cardDatabase.updateCard(sender.getName(), result,
                                Integer.parseInt(cardDatabase.getCard(sender.getName(), result)) + 1);
                        results.add(result);
                    }
                }
                BungeeUtil.sendPartCommandMessage((Player) sender, prefix + MessageFormat.format(getMsg("lang_44"),
                        name, results.toString().replace("[", "").replace("]", "")),
                        "点击", getMsg("lang_45"), "/ck " + name + "*10", true);
                if (!cards_rare_cards.get(name).isEmpty() && cards_rare_cards.get(name) != null) {
                    List<String> result10 = new ArrayList<>();
                    for (String card : cards_rare_cards.get(name)) {
                        for (String result : results){
                            if (card.equals(result)) {
                                result10.add(result);
                                if (!cards_rare_cards_broadcast.get(name).equals("none")) {
                                    if (asyn)
                                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main, () -> {
                                            Bukkit.broadcastMessage(cards_rare_cards_broadcast.get(name).replace("{player}",
                                                    sender.getName()).replace("{cardname}", result));
                                        });
                                    else
                                        Bukkit.broadcastMessage(cards_rare_cards_broadcast.get(name).replace("{player}",
                                                sender.getName()).replace("{cardname}", result));
                                }
                            }
                        }
                    }
                    if (!cards_rare_cards_title.get(name).equals("") ||
                            !cards_rare_cards_subtitle.get(name).equals("")) {
                        sendTitle(sender, name, result10.toString().replace("[", "").replace("]", ""));
                    }
                }
                if (!cards_sounds.get(name).isEmpty() && cards_sounds.get(name) != null) {
                    Player player = (Player) sender;
                    for (String sounds : cards_sounds.get(name)) {
                        playSound(player, sounds);
                    }
                }
                remind(sender);
            } else {
                sender.sendMessage(prefix + getMsg("lang_46"));
            }
            ciShuDatabase.closeConnection();
            cardDatabase.closeConnection();
        }

        private void real_guarantee(int num, int sum, List<String> results, List<String> cards, List<String> rare_cards, String name, CommandSender sender) {
            int result_rare_cards = 0;
            int size = results.size();
            for (int i = 0; i < 10 - num - size; i++) { //除去保底
                Random random = new Random();
                int randomNum = random.nextInt(sum);
                String result = cards.get(randomNum);
                //sender.sendMessage("恭喜你抽中了 " + result);
                cardDatabase.updateCard(sender.getName(), result,
                        Integer.parseInt(cardDatabase.getCard(sender.getName(), result)) + 1);
                if (rare_cards.contains(result)) result_rare_cards++;
                results.add(result);
            }
//            System.out.println(results.toString());
            if (results.size() == 10) return;
            if (result_rare_cards < num && result_rare_cards != 0) {
                num = num - result_rare_cards;
                real_guarantee(num, sum, results, cards, rare_cards, name, sender);
            } else if (result_rare_cards < num && result_rare_cards == 0) {
                for (int i = 0; i < num; i++) {
                    Random random = new Random();
                    int randomNum = random.nextInt(rare_rate.get(name));
                    String result = rare_list.get(name).get(randomNum);
                    //sender.sendMessage("恭喜你抽中了 " + result);
                    cardDatabase.updateCard(sender.getName(), result,
                            Integer.parseInt(cardDatabase.getCard(sender.getName(), result)) + 1);
                    results.add(result);
                }
            } else {
                num = 0;
                real_guarantee(num, sum, results, cards, rare_cards, name, sender);
            }
        }

        private void sendTitle(CommandSender sender, String name, String result) {
            try {
                ((Player) sender).sendTitle(cards_rare_cards_title.get(name).
                                replace("{player}", sender.getName()).
                                replace("{cardname}", result),
                        cards_rare_cards_subtitle.get(name).
                                replace("{player}", sender.getName()).
                                replace("{cardname}", result),
                        10, 70, 20);
            } catch (Exception e) {
                log.log(Level.SEVERE, "发送标题失败");
            }
        }

        private void playSound(Player player, String sounds) {
            String[] args = sounds.split("-");
            try {
                Sound sound = Sound.valueOf(args[0].toUpperCase());
                float volume = Float.parseFloat(args[1]);
                float pitch = Float.parseFloat(args[2]);
                player.playSound(player.getLocation(), sound, volume, pitch);
            } catch (Exception e) {
                log.log(Level.SEVERE, "播放声音" + sounds + "出错，请检查配置");
            }
        }

        private void remind(CommandSender sender) {
            for (String cardSetName : Reward_info.keySet()) {
                if (!reward_remind.get(cardSetName)) continue;
                int num = isGetAll(getcards(sender.getName()), cardSetName, sender);
                if (num != 0) {
                    BungeeUtil.sendPartCommandMessage((Player)sender, prefix +
                                    MessageFormat.format(getMsg("lang_29"), num, cardSetName), "兑换",
                            getMsg("lang_30"), "/ck duihuan " + cardSetName, true);
                }
            }
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label,
                                          String[] args) {
            if (args.length == 1) {
                List<String> subCommands = new ArrayList<>();
                for (String s : enabled)
                    if (cards_info.containsKey(s)) {
                        subCommands.add(s);
                        subCommands.add(s+"*10");
                    }
                subCommands.add("help");
                subCommands.add("mycards");
                subCommands.add("mytimes");
                subCommands.add("duihuan");
                subCommands.add("yijianduihuan");
                if (sender.hasPermission("ChouKa.Admin")){
                    subCommands.add("reload");
                    subCommands.add("addtime");
                    subCommands.add("deltime");
                    subCommands.add("setitem");
                    subCommands.add("setcard");
                    subCommands.add("addcard");
                    subCommands.add("delcard");
                }
                subCommands.addAll(cards_info.keySet());
                return filterStartsWith(subCommands, args[0]);
            } else if (args.length == 2) {
                List<String> subCommands = new ArrayList<>();
                subCommands.addAll(Reward_info.keySet());
                for (Player player : Bukkit.getOnlinePlayers())
                    subCommands.add(player.getName());
                return filterStartsWith(subCommands, args[1]);
            } else if (args.length == 3) {
                if (!sender.hasPermission("ChouKa.Admin"))
                    return null;
                List<String> subCommands = new ArrayList<>();
                subCommands.addAll(cards_info.keySet());
                subCommands.addAll(cardName);
                return filterStartsWith(subCommands, args[2]);
            }
            return null;
        }
        public List<String> filterStartsWith(List<String> in, String filter) {
            List<String> out = new ArrayList<>();
            for (String s : in) {
                if (s.startsWith(filter)) {
                    out.add(s);
                }
            }
            return out;
        }

        //封装异常，更友好的显示，有参数表示控制台或者玩家执行命令
        public boolean load(CommandSender sender) {
            try {
                load0();
            } catch (Exception e) {
                sender.sendMessage(" §4读取配置文件信息出错！");
                e.printStackTrace();
                return false;
            }
            sender.sendMessage(" §2读取配置文件成功！");
            return true;
        }

        private void help(CommandSender sender) {
            List<String> list = new ArrayList<>();
            sender.sendMessage(prefix + " /ChouKa [卡组名] 进行抽卡");
            for (String name : enabled){
                if (cards_info.containsKey(name))
                    list.add(cards_display_name.get(name));
            }
            if (list.isEmpty())
                sender.sendMessage(prefix + " 当前无可抽卡组，请联系管理员");
            else {
                sender.sendMessage(prefix + " 当前可抽卡组：");
                sender.sendMessage(list.toString().replace("[", "").replace("]", ""));
            }

            sender.sendMessage(prefix + " /ChouKa help 查看抽卡帮助");
            if (sender.hasPermission("ChouKa.Admin")) {
                sender.sendMessage(prefix + " /ChouKa reload 重载配置文件");
                sender.sendMessage(prefix + " /ChouKa addtime [玩家名（注意大小写）] [卡组名] [次数] 添加玩家抽卡次数");
                sender.sendMessage(prefix + " /ChouKa deltime [玩家名（注意大小写）] [卡组名] [次数] 减少玩家抽卡次数");
                sender.sendMessage(prefix + " /ChouKa setitem [集齐卡组名] 设置手上物品为集齐卡组的奖励物品");
                sender.sendMessage(prefix + " /ChouKa mycards [玩家名（注意大小写）] 查看该玩家拥有的卡");
                sender.sendMessage(prefix + " /ChouKa mytimes [玩家名（注意大小写）] 查看该玩家拥有的抽卡次数");
                sender.sendMessage(prefix + " /ChouKa setcard [玩家名（注意大小写）] [卡名] [数量] 设置玩家某卡的数量");
                sender.sendMessage(prefix + " /ChouKa addcard [玩家名（注意大小写）] [卡名] [数量] 添加玩家某卡数量");
                sender.sendMessage(prefix + " /ChouKa delcard [玩家名（注意大小写）] [卡名] [数量] 删除玩家某卡数量");
            } else {
                sender.sendMessage(prefix + " /ChouKa mycards 查看拥有的卡");
                sender.sendMessage(prefix + " /ChouKa mytimes 查看拥有的抽卡次数");

            }
            sender.sendMessage(prefix + " /ChouKa duihuan [集齐卡组名] 兑换集齐奖励");
            sender.sendMessage(prefix + " /ChouKa yijianduihuan 一键兑换所有集齐奖励");
        }
    }
}
