/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;

/**
 *
 * @author slimane
 */
public class Receive {
    
      public static void main(String[] args) {
        try {
         Player Player;     
            // medialocator to receive data from this url : includes the sender that we want to receive data from
            MediaLocator url = new MediaLocator("rtp://172.20.10.5:10000/audio");
            
            //creating a player to receive data
            Player = Manager.createRealizedPlayer(url);
            Player.start();
        } catch (Exception ex) {
        }
    }
    
    
    
}
