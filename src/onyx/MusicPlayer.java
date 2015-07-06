package onyx;
/**
 * PROJEKT INDA 2013 
 * Marcus Heine och Mark Hobro
 * 
 */


import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.sound.sampled.*;

import javazoom.jl.decoder.JavaLayerException;

public class MusicPlayer 
{
	private MusicFileByFrames framesPlayer;
	private String directory;
	
	public MusicPlayer(){
		framesPlayer = null;
		directory = "";
	}
	
	//Makes the player ready to play tracks
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
		if(framesPlayer == null){
			return false;
		}
		else{
			return framesPlayer.hasSongEnded();
		}
		
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
	
	public void playFromSetPoint(String directory, int startPoint) throws JavaLayerException{
		preparePlayer(directory);
		playFrom(startPoint);
	}
	
	//TODO
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
	
	public String getTrackLength(String dir) throws UnsupportedAudioFileException, IOException {
		
		File file = new File(dir);

	    AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
	    if (fileFormat instanceof TAudioFileFormat) {
	        Map<?, ?> properties = ((TAudioFileFormat) fileFormat).properties();
	        String key = "duration";
	        Long microseconds = (Long) properties.get(key);
	        int mili = (int) (microseconds / 1000);
	        int sec = (mili / 1000) % 60;
	        int min = (mili / 1000) / 60;
	        
	        String s;
	        
	        if(sec < 10)
	        	s = "" + min +":0" + sec + "";
	        else
	        	s = "" + min +":" + sec + "";
	        
	        
	        return s;
	        //System.out.println("time = " + min + ":" + sec);
	    } else {
	        throw new UnsupportedAudioFileException();
	    }

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
		System.out.println("ATTEMPTINF TO STOP.");
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
