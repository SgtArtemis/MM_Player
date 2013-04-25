import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javax.swing.*;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;

public class MusicFileByFrames extends JFrame {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// The MPEG audio bitstream.
	private Bitstream bitstream;
	// The MPEG audio decoder.
	private Decoder decoder;
	// The AudioDevice the audio samples are written to.
	private AudioDevice audio;

	private boolean playing;
	private int frameCount;
	private int frameNumber;
	private int resumePosition;
	private String directory;

	public MusicFileByFrames(String directory) throws JavaLayerException {
		frameNumber = 0;
		frameCount = getFrameCount(directory);
		openBitstream(directory);
		resumePosition = -1;
		playing = false;
		this.directory = directory;
	}

	// Plays the file from the first frame to the last frame (the whole file)
	public void play() throws JavaLayerException {
		playFrames(0, frameCount);
	}

	// Get the number of frames of the file
	public int getLength() {
		return frameCount;
	}

	// Get the current position of the file in frames
	public int getPosition() {
		return frameNumber;
	}

	// Sets the position where the file should start playing from when prompted
	public void setPosition(int position) throws JavaLayerException {
		pause();
		resumePosition = position;
	}

	public void pause() throws JavaLayerException {
		synchronized (this) {
			playing = false;
			resumePosition = frameNumber;
		}
	}

	public void resume() throws JavaLayerException {
		if (playing != true) {
			int start; // Where the track should continue playing at
			if (resumePosition >= 0) {
				start = resumePosition;
			} else {
				start = frameNumber;
			}
			resumePosition = -1;
			playFrames(start, frameCount);
		}
	}

	private boolean playFrames(int start, int end) throws JavaLayerException {
		resumePosition = -1;

		if (end > frameCount) {
			end = frameCount;
		}

		checkFramePosition(start);

		// Play until finished, paused, or a problem.
		boolean ok = true;
		while (frameNumber < end && playing && ok) {
			ok = decodeFrame();
			if (ok) {
				frameNumber++;
			}
		}

		handleApparentStop();
		return ok;
	}

	// Make sure the player is in the correct position
	private synchronized void checkFramePosition(int startingPosition)
			throws JavaLayerException {
		moveTo(startingPosition);
		playing = true;
	}

	private synchronized void handleApparentStop() {
		playing = false;
		// last frame, ensure all data flushed to the audio device.
		AudioDevice out = audio;
		if (out != null) {
			out.flush();
		}
	}

	// Sets the playing position
	private void moveTo(int position) throws JavaLayerException {
		if (position < frameNumber) {
			// Played too far
			handlePlayingPosition();
		}

		while (frameNumber < position) {
			skipFrame();
			frameNumber++;
		}
	}

	private synchronized void handlePlayingPosition() throws JavaLayerException {
		if (bitstream != null) {
			try {
				bitstream.close();
			} catch (BitstreamException ex) {
			}
		}
		if (audio != null) {
			audio.close();
		}
		openAudio();
		openBitstream(directory);
		frameNumber = 0;
	}

	protected int getFrameCount(String filename) throws JavaLayerException {
		openBitstream(filename);
		int count = 0;
		while (skipFrame()) {
			count++;
		}
		bitstream.close();
		return count;
	}

	protected Header readFrame() throws JavaLayerException {
		if (audio != null) {
			return bitstream.readFrame();
		} else {
			return null;
		}
	}

	protected void openAudio() throws JavaLayerException {
		audio = FactoryRegistry.systemRegistry().createAudioDevice();
		decoder = new Decoder();
		audio.open(decoder);
	}

	protected void openBitstream(String filename) throws JavaLayerException {
		try {
			bitstream = new Bitstream(new BufferedInputStream(
					new FileInputStream(filename)));
		} catch (java.io.IOException ex) {
			throw new JavaLayerException(ex.getMessage(), ex);
		}

	}

	// Returns true if there are no more frames to decode
	protected boolean decodeFrame() throws JavaLayerException {
		try {
			boolean hfd = handleFrameDecoding();
			if (hfd == false) {
				return false;
			}
			bitstream.closeFrame();
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw new JavaLayerException("Exception decoding audio frame", ex);
		}
		return true;
	}

	private synchronized boolean handleFrameDecoding()
			throws JavaLayerException {
		if (audio == null) {
			return false;
		}

		Header h = readFrame();
		if (h == null) {
			return false;
		}

		// Sample buffer set when decoder constructed
		SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h, bitstream);

		if (audio != null) {
			audio.write(output.getBuffer(), 0, output.getBufferLength());
		}
		return true;
	}

	// Skips one frame
	protected boolean skipFrame() throws JavaLayerException {
		Header h = readFrame();
		if (h == null) {
			return false;
		}
		frameNumber++;
		bitstream.closeFrame();
		return true;
	}
}
