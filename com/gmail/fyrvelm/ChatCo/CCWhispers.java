package com.gmail.fyrvelm.ChatCo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CCWhispers
  implements Listener {
  public ChatCo plugin;
  public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
  
  public CCWhispers(ChatCo plugin) { this.plugin = plugin; }
  
  public static String now() {
    cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.format(cal.getTime());
  }

  
  public void whisperLog(String text, String sender) {
    try {
      FileWriter fwo = new FileWriter(ChatCo.WhisperLog, true);
      BufferedWriter bwo = new BufferedWriter(fwo);
      bwo.write(String.valueOf(now()) + " " + sender + ": " + text);
      bwo.newLine();
      bwo.close();
    
    }
    catch (IOException ioexception) {
      
      ioexception.printStackTrace();
    } 
  }

  
  public String whisperFormat(String who, Player sender, Player target) {
    if (who.equalsIgnoreCase("sender")) {
      data = this.plugin.getConfig().getString("ChatCo.WhisperFormat.Send");
    } else {
      data = this.plugin.getConfig().getString("ChatCo.WhisperFormat.Receive");
    }  data = data.replace("*UNDERLINE*", ChatColor.UNDERLINE.toString());
    data = data.replace("*ITALIC*", ChatColor.ITALIC.toString());
    data = data.replace("*STRIKETHROUGH*", ChatColor.STRIKETHROUGH.toString());
    data = data.replace("*BOLD*", ChatColor.BOLD.toString());
    data = data.replace("*WHITE*", ChatColor.WHITE.toString());
    data = data.replace("*RED*", ChatColor.RED.toString());
    data = data.replace("*BLACK*", ChatColor.BLACK.toString());
    data = data.replace("*DARK_RED*", ChatColor.DARK_RED.toString());
    data = data.replace("*DARK_GRAY*", ChatColor.DARK_GRAY.toString());
    data = data.replace("*DARK_BLUE*", ChatColor.DARK_BLUE.toString());
    data = data.replace("*DARK_PURPLE*", ChatColor.DARK_PURPLE.toString());
    data = data.replace("*BLUE*", ChatColor.BLUE.toString());
    data = data.replace("*LIGHT_PURPLE*", ChatColor.LIGHT_PURPLE.toString());
    data = data.replace("*DARK_GREEN*", ChatColor.DARK_GREEN.toString());
    data = data.replace("*GOLD*", ChatColor.GOLD.toString());
    data = data.replace("*GREEN*", ChatColor.GREEN.toString());
    data = data.replace("*YELLOW*", ChatColor.YELLOW.toString());
    data = data.replace("*DARK_AQUA*", ChatColor.DARK_AQUA.toString());
    data = data.replace("*AQUA*", ChatColor.AQUA.toString());
    data = data.replace("*GRAY*", ChatColor.GRAY.toString());
    data = data.replace("*NORM*", ChatColor.RESET.toString());
    data = data.replace("RECEIVER", target.getName());
    return data.replace("SENDER", sender.getName());
  }

  
  @EventHandler(ignoreCancelled = true)
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) throws IOException {
    boolean doNotSend = false;
    boolean isIgnoring = false;
    String inputText = event.getMessage();
    String[] args = inputText.split("\\ ", 0);
    Player sender = event.getPlayer();

    
    if (this.plugin.getConfig().getBoolean("ChatCo.ReplyCommands", true) && (
      args[0].equals("/r") || args[0].equals("/reply"))) {
      Player target = null;
      if (args.length == 1)
        sender.sendMessage("Usage: /r <message>"); 
      if (this.plugin.getCCPlayer(sender).getLastMessenger() != null)
        target = this.plugin.getCCPlayer(sender).getLastMessenger(); 
      if (target == null && (this.plugin.getCCPlayer(sender)).LastMessenger != null) {
        sender.sendMessage("The last person who whispered you(" + (this.plugin.getCCPlayer(sender)).LastMessenger + ") is offline.");
      }
      else if (target == null) {
        sender.sendMessage("You have received no whispers this session.");
      
      }
      else if (target != null) {
        try {
          if ((this.plugin.getCCPlayer(target)).tellsDisabled) {
            doNotSend = true;
          
          }
        }
        catch (Exception exception) {}
        if (this.plugin.checkForIgnores && this.plugin.getCCPlayer(target).isIgnored(sender.getName()))
          isIgnoring = true; 
        StringBuilder message = new StringBuilder();
        for (int z = 1; z < args.length; z++) {
          if (z > 1)
            message.append(" "); 
          message.append(args[z]);
        } 
        String senderMessage = whisperFormat("sender", sender, target);
        String receiverMessage = whisperFormat("target", sender, target);
        String result = String.valueOf(receiverMessage) + message;
        sender.sendMessage(String.valueOf(senderMessage) + message);
        if (isIgnoring && this.plugin.getConfig().getBoolean("ChatCo.ignoreMessageEnabled", true))
          sender.sendMessage(String.valueOf(ChatColor.RED.toString()) + target.getName() + " is ignoring you."); 
        if (doNotSend && this.plugin.getConfig().getBoolean("ChatCo.chatDisabledMessageEnabled", true))
          sender.sendMessage(String.valueOf(ChatColor.RED.toString()) + target.getName() + "'s chat is disabled."); 
        if (!doNotSend && !isIgnoring) {
          target.sendMessage(result);
          this.plugin.getCCPlayer(target).setLastMessenger(sender);
        } 
        if (doNotSend || isIgnoring)
          inputText = "***WAS NOT SENT*** " + inputText; 
        if (this.plugin.getConfig().getBoolean("ChatCo.WhisperLog", false))
          whisperLog(inputText, sender.getName()); 
        if (this.plugin.getConfig().getBoolean("ChatCo.WhisperMonitoring", true))
          this.plugin.getLogger().info(String.valueOf(sender.getName()) + ": " + inputText); 
      } 
      event.setCancelled(true);
    } 


    
    if (this.plugin.getConfig().getBoolean("ChatCo.NewCommands", true)) {
      if (inputText.toLowerCase().startsWith("/tell ") || inputText.toLowerCase().startsWith("/msg ") || inputText.toLowerCase().startsWith("/t ") || inputText.toLowerCase().startsWith("/w ") || inputText.toLowerCase().startsWith("/whisper ") || inputText.toLowerCase().startsWith("/pm ")) {
        if (args.length == 1)
          sender.sendMessage("Usage: /t <player> <message>"); 
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null)
          return; 
        try {
          if ((this.plugin.getCCPlayer(target)).tellsDisabled) {
            doNotSend = true;
          }
        }
        catch (Exception exception) {}
        if (this.plugin.checkForIgnores && this.plugin.getCCPlayer(target).isIgnored(sender.getName()))
          isIgnoring = true; 
        event.setCancelled(true);
        StringBuilder message = new StringBuilder();
        for (int z = 2; z < args.length; z++) {
          if (z > 2)
            message.append(" "); 
          message.append(args[z]);
        } 
        String senderMessage = whisperFormat("sender", sender, target);
        String receiverMessage = whisperFormat("target", sender, target);
        String result = String.valueOf(receiverMessage) + message;
        sender.sendMessage(String.valueOf(senderMessage) + message);
        if (isIgnoring && this.plugin.getConfig().getBoolean("ChatCo.ignoreMessageEnabled", true))
          sender.sendMessage(String.valueOf(ChatColor.RED.toString()) + target.getName() + " is ignoring you."); 
        if (doNotSend && this.plugin.getConfig().getBoolean("ChatCo.chatDisabledMessageEnabled", true))
          sender.sendMessage(String.valueOf(ChatColor.RED.toString()) + target.getName() + "'s chat is disabled."); 
        if (!doNotSend && !isIgnoring) {
          target.sendMessage(result);
          this.plugin.getCCPlayer(target).setLastMessenger(sender);
        } 
        if (doNotSend || isIgnoring)
          inputText = "***WAS NOT SENT*** " + inputText; 
        if (this.plugin.getConfig().getBoolean("ChatCo.WhisperLog", false))
          whisperLog(inputText, sender.getName()); 
        if (this.plugin.getConfig().getBoolean("ChatCo.WhisperMonitoring", true)) {
          this.plugin.getLogger().info(String.valueOf(sender.getName()) + ": " + inputText);
        }
      }
    
    } else {
      
      Player target = Bukkit.getPlayerExact(args[1]);
      if (target == null) {
        return;
      }
      
      if ((this.plugin.getCCPlayer(target)).tellsDisabled) {
        doNotSend = true;
      }
      if (this.plugin.checkForIgnores && this.plugin.getCCPlayer(target).isIgnored(sender.getName()))
        isIgnoring = true; 
      if (inputText.toLowerCase().startsWith("/tell ") || inputText.toLowerCase().startsWith("/w ") || inputText.toLowerCase().startsWith("/msg ")) {
        event.setCancelled(true);
        
        StringBuilder message = new StringBuilder();
        for (int z = 2; z < args.length; z++) {
          if (z > 2)
            message.append(" "); 
          message.append(args[z]);
        } 
        String senderMessage = whisperFormat("sender", sender, target);
        String receiverMessage = whisperFormat("target", sender, target);
        String result = String.valueOf(receiverMessage) + message;
        sender.sendMessage(String.valueOf(senderMessage) + message);
        if (isIgnoring && this.plugin.getConfig().getBoolean("ChatCo.ignoreMessageEnabled", true))
          sender.sendMessage(String.valueOf(ChatColor.RED.toString()) + target + " is ignoring you."); 
        if (doNotSend && this.plugin.getConfig().getBoolean("ChatCo.chatDisabledMessageEnabled", true))
          sender.sendMessage(String.valueOf(ChatColor.RED.toString()) + target + "'s chat is disabled."); 
        if (!doNotSend && !isIgnoring) {
          target.sendMessage(result);
          this.plugin.getCCPlayer(target).setLastMessenger(sender);
        } 
        if (doNotSend || isIgnoring)
          inputText = "***WAS NOT SENT*** " + inputText; 
        if (this.plugin.getConfig().getBoolean("ChatCo.WhisperLog", false))
          whisperLog(inputText, sender.getName()); 
        if (this.plugin.getConfig().getBoolean("ChatCo.WhisperMonitoring", true))
          this.plugin.getLogger().info(String.valueOf(sender.getName()) + ": " + inputText); 
      } 
    } 
  }
}
