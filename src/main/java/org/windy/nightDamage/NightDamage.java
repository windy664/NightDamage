package org.windy.nightDamage;


import org.bukkit.Bukkit;
import org.bukkit.GameMode; 
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class NightDamage extends JavaPlugin {

    private FileConfiguration config;
    private final HashMap<UUID, Double> playerHealthMap = new HashMap<>();
    private String message;
    private String message2;
    private boolean debug;
    private Double damage;
    @Override
    public void onEnable() {
        // 载入配置文件
        saveDefaultConfig();
        config = getConfig();
        message = getConfig().getString("message","§o§d黑暗袭击了你");
        message2 = getConfig().getString("message2","§o§d你感到一丝黑暗，心跟碎了一样");
        debug = getConfig().getBoolean("debug",false);
        damage = getConfig().getDouble("damage",1);

        // 如果插件禁用，直接返回
        if (!config.getBoolean("enabled", true)) {
            getLogger().info("§c插件已禁用！");
            return;
        }
        this.getServer().getConsoleSender().sendMessage(texts.logo);

        // 获取检测频率和亮度阈值
        int checkFrequency = config.getInt("check-frequency", 20);
        int lightThreshold = config.getInt("light-threshold", 1);
        List<String> enabledWorlds = config.getStringList("enabled-worlds");

        // 定时任务：每隔指定的 ticks 执行一次
        new BukkitRunnable() {
            @Override
            public void run() {
                log("执行异步任务");
                for (World world : Bukkit.getWorlds()) {
                    if (!enabledWorlds.contains(world.getName())) {
                        continue;
                    }

                    long time = world.getTime();
                    boolean isNight = (time > 13000 && time < 23000);

                    if (isNight) {
                        for (Player player : world.getPlayers()) {
                            if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                                int lightLevel = player.getLocation().getBlock().getLightLevel();
                                if (lightLevel < lightThreshold) {
                                    log("检测到玩家符合条件");
                                    double currentHealth = player.getHealth();
                                    if (!playerHealthMap.containsKey(player.getUniqueId())) {
                                        playerHealthMap.put(player.getUniqueId(), currentHealth);
                                    }

                                    // 持续扣血，直到死亡
                                    if (config.getBoolean("damage-till-death", true)) {
                                        if (currentHealth <= 1.0) {
                                            player.setHealth(0.0); // 直接杀死玩家
                                            player.sendMessage(message);
                                            playerHealthMap.remove(player.getUniqueId());
                                            log("尝试杀死玩家"+player);
                                        } else {
                                            player.damage(damage);
                                            player.sendMessage(message2);
                                            log("尝试扣血"+player);
                                        }
                                    } else {
                                        player.damage(damage); // 仅扣1点血
                                    }
                                } else {
                                    // 玩家离开危险区域后重置健康状态
                                    playerHealthMap.remove(player.getUniqueId());
                                }
                            }
                            log("玩家不符合条件，跳过");
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0, checkFrequency); // 根据配置文件的频率检测
    }

    @Override
    public void onDisable() {
        // 插件被关闭时执行的代码
        playerHealthMap.clear();
        this.getServer().getConsoleSender().sendMessage(texts.logo);
        this.getLogger().info("感谢使用，定制插件联系QQ 366262436");
    }

    public void log(String text){
        if(debug){
            this.getLogger().info(text);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("nd")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("nightDamage.reload")) {
                    sender.sendMessage("§c你没有权限来执行这个指令!");
                    return true;
                }
                this.reloadConfig();
                sender.sendMessage("§a配置已重新加载!");
                return true;
            } else if(args.length > 0 && args[0].equalsIgnoreCase("debug")) {
                if (!sender.hasPermission("nightDamage.debug")) {
                    sender.sendMessage("§c你没有权限来执行这个指令!");
                    return true;
                }
                debug = !debug; // 切换debug模式
                sender.sendMessage("§aDebug模式已临时" + (debug ? "开启" : "关闭"));
                return true;
            }
        }
        return false;
    }

}
