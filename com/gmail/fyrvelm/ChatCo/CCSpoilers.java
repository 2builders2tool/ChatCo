package com.gmail.fyrvelm.ChatCo;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CCSpoilers implements Listener {
  ChatCo plugin;
  
  public CCSpoilers(ChatCo plugin) {
    this.Spoiler = new String[5];

    
    this.plugin = plugin;
  }
  String[] Spoiler;
  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent event) {
    if (event.isCancelled())
      return;  String check = event.getMessage();
    
    if (check.startsWith("[SPOILER]") && check.endsWith("[/SPOILER]")) {
      for (int i = 4; i > 0; i--)
        this.Spoiler[i] = this.Spoiler[i - 1]; 
      int Length = event.getMessage().length();
      Player name = event.getPlayer();
      this.Spoiler[0] = "[ " + event.getMessage().substring(9, Length - 10) + " ] by R%)[".replace("R%)", ChatColor.RED.toString()) + name.getDisplayName() + "]";
      event.setMessage(ChatColor.BLACK + "SPOILER");
    } else {
      
      event.setMessage(check);
    } 
  } @EventHandler
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    Player player = event.getPlayer();
    String inputText = event.getMessage();
    if (inputText.startsWith("/show spoiler")) {
      int length = inputText.length();
      event.setCancelled(true);
      if (length > 14) {
        char parser = inputText.charAt(14);
        int numberOfPrints = Character.digit(parser, 15);
        if (numberOfPrints > 5 || numberOfPrints < 1) {
          player.sendMessage("The server only stores the last 5 spoilers to have been made.");
        }
        else if (numberOfPrints < 5 && numberOfPrints > 1) {
          for (int i = 0; i < numberOfPrints; i++) {
            player.sendMessage("Spoiler [" + (i + 1) + "]: " + this.Spoiler[i]);
          }
        } 
      } else {
        player.sendMessage(this.Spoiler[0]);
      } 
    } 
  }
}
