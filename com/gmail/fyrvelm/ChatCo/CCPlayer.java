package com.gmail.fyrvelm.ChatCo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class CCPlayer
{
  public Player player;
  public String playerName;
  public boolean chatDisabled;
  public boolean tellsDisabled;
  public String LastMessenger;
  public long timeUnset;
  private File IgnoreList;
  private List<String> ignores;
  
  public CCPlayer(Player p, String pN) throws IOException {
    this.player = p;
    this.playerName = pN;
    this.chatDisabled = false;
    this.tellsDisabled = false;
    this.LastMessenger = null;
    saveIgnoreList("");
  }














  
  public void saveIgnoreList(String p) throws IOException {
    this.IgnoreList = new File(ChatCo.dataFolder, "/ignorelists/" + this.playerName + ".txt");
    if (!this.IgnoreList.exists()) {
      this.IgnoreList.getParentFile().mkdir();
      FileWriter fwo = new FileWriter(this.IgnoreList, true);
      BufferedWriter bwo = new BufferedWriter(fwo);

      
      bwo.close();
    } 
    if (!p.equals("")) {
      if (!isIgnored(p)) {
        FileWriter fwo = new FileWriter(this.IgnoreList, true);
        BufferedWriter bwo = new BufferedWriter(fwo);
        bwo.write(p);
        bwo.newLine();
        bwo.close();
      } else {
        
        this.ignores.remove(p);
        this.ignores.remove("");
        FileWriter fwo = new FileWriter(this.IgnoreList);
        BufferedWriter bwo = new BufferedWriter(fwo);
        for (String print : this.ignores) {
          bwo.write(print);
          bwo.newLine();
        } 
        bwo.close();
      } 
    }
    updateIgnoreList();
  }

  
  public void setLastMessenger(Player sender) { this.LastMessenger = sender.getName(); }

  
  public Player getLastMessenger() {
    if (this.LastMessenger != null)
      return Bukkit.getPlayerExact(this.LastMessenger); 
    return null;
  }
  
  private void updateIgnoreList() throws IOException {
    FileInputStream file = new FileInputStream(this.IgnoreList);
    InputStreamReader fileReader = new InputStreamReader(file);
    BufferedReader inIgnores = new BufferedReader(fileReader);
    String data = inIgnores.readLine();
    this.ignores = new ArrayList();
    while (data != null) {
      this.ignores.add(data);
      data = inIgnores.readLine();
    } 
    file.close();
  }


  
  public boolean isIgnored(String p) { return this.ignores.contains(p); }


  
  public List<String> getIgnoreList() { return this.ignores; }
}
