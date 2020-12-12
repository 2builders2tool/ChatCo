package com.gmail.fyrvelm.ChatCo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatCo
  extends JavaPlugin
{
  private final CCAllChat allChat = new CCAllChat(this);
  private final CCSpoilers spoilerListener = new CCSpoilers(this);
  private final CCWhispers whisperListener = new CCWhispers(this);
  private static File Configuration;
  public static File Configuration2;
  private static File Help;
  public static File WhisperLog;
  public boolean checkForChatDisable = false;
  public boolean checkForIgnores = false;
  public final ArrayList<CCPlayer> playerlist = new ArrayList();
  public static File dataFolder;
  
  public void onDisable() { this.playerlist.clear(); }









  
  public void onEnable() {
    checkFiles();
    readConfig(0);
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvents(this.allChat, this);
    if (getConfig().getBoolean("ChatCo.chatDisableEnabled", true))
      this.checkForChatDisable = true; 
    if (getConfig().getBoolean("ChatCo.ignoresEnabled", true))
      this.checkForIgnores = true; 
    if (getConfig().getBoolean("ChatCo.WhisperChangesEnabled", true))
      pm.registerEvents(this.whisperListener, this); 
    if (getConfig().getBoolean("ChatCo.SpoilersEnabled", false)) {
      pm.registerEvents(this.spoilerListener, this);
    }
  }







  
  private void readConfig(int change) {
    if (change == 3)
      getConfig().set("ChatCo.SpoilersEnabled", Boolean.valueOf(true)); 
    if (change == 4)
      getConfig().set("ChatCo.SpoilersEnabled", Boolean.valueOf(false)); 
    if (change == 5)
      getConfig().set("ChatCo.WhisperChangesEnabled", Boolean.valueOf(true)); 
    if (change == 6)
      getConfig().set("ChatCo.WhisperChangesEnabled", Boolean.valueOf(false)); 
    if (change == 7)
      getConfig().set("ChatCo.NewCommands", Boolean.valueOf(true)); 
    if (change == 8)
      getConfig().set("ChatCo.NewCommands", Boolean.valueOf(false)); 
    if (change == 9)
      getConfig().set("ChatCo.WhisperLog", Boolean.valueOf(true)); 
    if (change == 10) {
      getConfig().set("ChatCo.WhisperLog", Boolean.valueOf(false));
    }
    saveConfig();
    reloadConfig();
  }

  
  private void checkFiles() {
    dataFolder = getDataFolder();
    Configuration = new File(dataFolder, "config.yml");
    Configuration2 = new File(dataFolder, "permissionConfig.yml");
    WhisperLog = new File(dataFolder, "whisperlog.txt");
    Help = new File(dataFolder, "help.txt");
    if (!WhisperLog.exists()) {
      WhisperLog.getParentFile().mkdirs();
      copy(getResource("whisperlog.txt"), WhisperLog);
    } 
    if (!Help.exists()) {
      Help.getParentFile().mkdirs();
      copy(getResource("help.txt"), Help);
    } 
    if (!Configuration.exists()) {
      saveDefaultConfig();
    }
    if (!Configuration2.exists()) {
      Configuration2.getParentFile().mkdirs();
      copy(getResource("permissionConfig.yml"), Configuration2);
    } 
  }

  
  private void copy(InputStream in, File file) {
    try {
      OutputStream out = new FileOutputStream(file);
      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
      out.close();
      in.close();
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }

  
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    if (sender instanceof Player) {
      if (cmd.getName().equalsIgnoreCase("togglechat") && getConfig().getBoolean("toggleChatEnabled", true)) {
        try {
          if (toggleChat((Player)sender))
          { sender.sendMessage(String.valueOf(ChatColor.RED.toString()) + "Your chat is now disabled until you type /togglechat or relog."); }
          else
          { sender.sendMessage(String.valueOf(ChatColor.RED.toString()) + "Your chat has been re-enabled, type /togglechat to disable it again."); } 
        } catch (IOException e) {
          e.printStackTrace();
        } 
        return true;
      } 
      if (cmd.getName().equalsIgnoreCase("toggletells")) {
        try {
          if (toggleTells((Player)sender))
          { sender.sendMessage(String.valueOf(ChatColor.RED.toString()) + "You will no longer receive tells, type /toggletells to see them again."); }
          else
          { sender.sendMessage(String.valueOf(ChatColor.RED.toString()) + "You now receive tells, type /toggletells to disable them again."); } 
        } catch (IOException e) {
          e.printStackTrace();
        } 
        return true;
      } 
      if (cmd.getName().equalsIgnoreCase("ignore") && getConfig().getBoolean("ignoresEnabled", true)) {
        try {
          System.out.println("Attempting to ignore player " + args[0]);
          if (args.length < 1) {
            sender.sendMessage("You forgot to type the name of the player.");
            return true;
          } 
          
          if (args[0].length() > 16) {
            sender.sendMessage("You entered an invalid player name.");
            return true;
          } 
          
          Player ignorable = Bukkit.getServer().getPlayer(args[0]);
          if (ignorable == null) {
            sender.sendMessage("You entered a player who doesn't exist or is offline.");
            return true;
          } 
          
          ignorePlayer((Player)sender, args[0]);
          return true;
        } catch (IOException e) {
          e.printStackTrace();
        } 
      }
      if (cmd.getName().equalsIgnoreCase("ignorelist") && getConfig().getBoolean("ignoresEnabled", true)) {
        try {
          sender.sendMessage(String.valueOf(ChatColor.RED.toString()) + "Ignored players:");
          int i = 0;
          for (String ignores : getCCPlayer((Player)sender).getIgnoreList()) {
            sender.sendMessage(String.valueOf(ChatColor.RED.toString()) + ChatColor.ITALIC + ignores);
            i++;
          } 
          sender.sendMessage(String.valueOf(ChatColor.RED.toString()) + i + " players ignored.");
          return true;
        } catch (IOException e) {
          e.printStackTrace();
        } 
      }
    } 
    if (args.length > 0) {
      Player player = null;
      sender instanceof Player;

      
      if (cmd.getName().equalsIgnoreCase("chatco")) {
        if (args[true] == null && player == null) {
          sender.sendMessage("You forgot to specify whether you wanted to enable or disable the component (chatco component e/ed)");
          return true;
        } 
        
        if (player == null && args[0].equalsIgnoreCase("spoilers")) {
          if (player == null && args[1].equalsIgnoreCase("e")) {
            readConfig(3);
            sender.sendMessage("Spoilers enabled");
          }
          else if (player == null && args[1].equalsIgnoreCase("d")) {
            readConfig(4);
            sender.sendMessage("Spoilers disabled");
          } 
        }
        if (player == null && args[0].equalsIgnoreCase("whispers")) {
          if (player == null && args[1].equalsIgnoreCase("e")) {
            readConfig(5);
            sender.sendMessage("Whisper changes enabled");
          }
          else if (player == null && args[1].equalsIgnoreCase("d")) {
            readConfig(6);
            sender.sendMessage("Whisper changes disabled");
          } 
        }
        if (player == null && args[0].equalsIgnoreCase("newcommands")) {
          if (player == null && args[1].equalsIgnoreCase("e")) {
            readConfig(7);
            sender.sendMessage("New Whisper commands enabled");
          }
          else if (player == null && args[1].equalsIgnoreCase("d")) {
            readConfig(8);
            sender.sendMessage("New whisper commands disabled");
          } 
        }
        if (player == null && args[0].equalsIgnoreCase("whisperlog")) {
          if (player == null && args[1].equalsIgnoreCase("e")) {
            readConfig(9);
            sender.sendMessage("Whisperlog enabled");
          }
          else if (player == null && args[1].equalsIgnoreCase("d")) {
            readConfig(10);
            sender.sendMessage("Whisperlog disabled");
          } 
        }
        return true;
      } 
    } 
    
    return false;
  }
  
  public CCPlayer getCCPlayer(Player p) throws IOException {
    for (CCPlayer cp : this.playerlist) {
      
      if (cp.playerName.equals(p.getName())) {
        return cp;
      }
    } 
    CCPlayer ccp = new CCPlayer(p, p.getName());
    this.playerlist.add(ccp);
    
    return ccp;
  }
  
  private boolean toggleChat(Player p) throws IOException {
    if ((getCCPlayer(p)).chatDisabled) {
      (getCCPlayer(p)).chatDisabled = false;
      return false;
    } 

    
    (getCCPlayer(p)).chatDisabled = true;
    return true;
  }

  
  private boolean toggleTells(Player p) throws IOException {
    if ((getCCPlayer(p)).tellsDisabled) {
      (getCCPlayer(p)).tellsDisabled = false;
      return false;
    } 

    
    (getCCPlayer(p)).tellsDisabled = true;
    return true;
  }


  
  private void ignorePlayer(Player p, String target) throws IOException {
    String message = String.valueOf(ChatColor.RED.toString()) + ChatColor.ITALIC + target + ChatColor.RESET + ChatColor.RED;
    if (getCCPlayer(p).isIgnored(target)) {
      message = String.valueOf(message) + " unignored.";
    } else {
      message = String.valueOf(message) + " ignored.";
    }  p.sendMessage(message);
    getCCPlayer(p).saveIgnoreList(target);
  }
}
