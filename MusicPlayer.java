/**
 * PROJEKT INDA 2013 
 * Marcus Heine och Mark Hobro
 */


import javazoom.jl.decoder.JavaLayerException;

public class MusicPlayer 
{
  private MusicFileByFrames player;
	private String directory;
	
	public MusicPlayer(){
		player = null;
		directory = "";
	}
	
	//Makes the player ready to play stuff
	private void preparePlayer(String directory){
		try {
            if(player != null) {
                killPlayer();
            }
            player = new MusicFileByFrames(directory);
        }
        catch(JavaLayerException exc) {
            exc.printStackTrace();
            playerError();
            killPlayer();
        }
	}
	
	public int getLengthInFrames(){
		if(player == null){
			return 0;
		}
		else{
			return player.getLength();
		}
	}
	
	public void play(String directory) throws JavaLayerException{
		preparePlayer(directory);
		player.play();
	}
	
	/*
	private void playFrom(final int start) throws JavaLayerException
    {
        Thread playerThread = new Thread() {
            public void run()
            {
                try {
                    player.play(start);
                }
                catch(JavaLayerException e) {
                    playerError();
                }
                finally{
                	killPlayer();
                }
            }
        };
        playerThread.start();
    }
	*/
	public void pause(){
		if(player != null) {
            try {
                player.pause();
            }
            catch(JavaLayerException exc){
            	exc.printStackTrace();
                playerError();
                killPlayer();
            }
        }
	}
	
	public void resumePlaying(){
		if(player != null) {
            Thread playerThread = new Thread() {
                public void run()
                {
                    try {
                        player.resume();
                    }
                    catch(JavaLayerException e) {
                        playerError();
                    }
                    finally{
                    	 killPlayer();
                    }
                }
            };
            playerThread.start();
        }
	}
	
	//TODO 
	public static void main(String[]args){

	}
	
	private void playerError(){
        System.out.println("Error playing: " + directory);
    }
	
	private void killPlayer()
    {
        kill();
    }
	
	private synchronized void kill(){
		 if(player != null) {
             player.stop();
             player = null;
             directory = "";
         }
	}
}

