/**
 * PROJEKT INDA 2013 
 * Marcus Heine och Mark Hobro
 */


import javazoom.jl.decoder.JavaLayerException;

public class MusicPlayer 
{
	private MusicFileByFrames framesPlayer;
	private String directory;
	
	public MusicPlayer(){
		framesPlayer = null;
		directory = "";
	}
	
	//Makes the player ready to play stuff
	private void preparePlayer(String directory){
		try {
            if(framesPlayer != null) {
                killPlayer();
            }
            framesPlayer = new MusicFileByFrames(directory);
        }
        catch(JavaLayerException exc) {
            exc.printStackTrace();
            playerError();
            killPlayer();
        }
	}
	
	public int getLengthInFrames(){
		if(framesPlayer == null){
			return 0;
		}
		else{
			return framesPlayer.getLength();
		}
	}
	
	public boolean hasSongEnded() {
		return framesPlayer.hasSongEnded();
	}
	
	public int getPlayingPosition(){
		
		if(framesPlayer == null){
			return 0;
		}
		else{
			return framesPlayer.getPosition();
		}
	}
	
	public void play(String directory) throws JavaLayerException{
		preparePlayer(directory);
		playFrom(0);
	}
	
	
	private void playFrom(final int start) throws JavaLayerException
    {
        Thread playerThread = new Thread() {
            public void run()
            {
                    try {
                    	framesPlayer.playFrom(start);
						
					} catch (JavaLayerException e) { e.printStackTrace(); }
            }
        };
        playerThread.setPriority(Thread.MIN_PRIORITY);
        playerThread.start();
    }
	public void pause(){
		if(framesPlayer != null) {
            try {
                framesPlayer.pause();
            }
            catch(JavaLayerException exc){
            	exc.printStackTrace();
                playerError();
                killPlayer();
            }
        }
	}
	
	public void stop(){
		framesPlayer.stop();
	}
	
	public void resumePlaying(){
		if(framesPlayer != null) {
            Thread playerThread = new Thread() {
                public void run()
                {
                    try {
                        framesPlayer.resume();
                    }
                    catch(JavaLayerException e) {
                        playerError();
                        killPlayer();
                    }
                }
            };
            playerThread.setPriority(Thread.MIN_PRIORITY);
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
		 if(framesPlayer != null) {
             framesPlayer.stop();
             framesPlayer = null;
             directory = "";
         }
	}
}
