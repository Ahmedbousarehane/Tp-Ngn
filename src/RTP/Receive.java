package RTP;

import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;

public class Receive {
    private Player player;

    public Receive(String ipAdress) {
        try {
            // medialocator to receive data from this url : includes the sender that we want to receive data from
            MediaLocator url = new MediaLocator("rtp://"+ ipAdress +":10000/audio");

            //creating a player to receive data
            player = Manager.createRealizedPlayer(url);
        } catch (Exception ex) {
            ex.getStackTrace();
        }
    }

    public void start() {
        player.start();
    }

    public void close() {
        player.close();
    }
}
