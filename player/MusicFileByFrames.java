/**
 * PROJEKT INDA 2013 
 * Marcus Heine och Mark Hobro
 * 
 */

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;

public class MusicFileByFrames {

	private Bitstream bitstream;
	private Decoder decoder;
	private AudioDevice audio;

	private boolean playing = false;
	private int frameCount;
	private int frameNumber;
	private int resumePosition;
	private String directory;

	private boolean songHasEnded;

	public MusicFileByFrames(String directory) throws JavaLayerException {
		this.directory = directory;
		openAudio();
		frameNumber = 0;
		frameCount = getFrameCount(directory);
		openBitstream(directory);
		resumePosition = -1;
		songHasEnded = false;
	}

	// Play a whole file from a given frame
	public void playFrom(int start) throws JavaLayerException {
		songHasEnded = false;
		playFrames(start, frameCount);
	}

	public boolean hasSongEnded() {
		return songHasEnded;
	}

	// Get the number of frames of the file
	public int getLength() {
		return frameCount;
	}

	// Get the current position of the file in frames
	public int getPosition() {
		return frameNumber;
	}

	public void stop() {
		close();
	}

	private synchronized void close() {
		if (audio != null) {
			AudioDevice out = audio;
			audio = null;
			out.close();
			try {
				bitstream.close();
			} catch (BitstreamException ex) {
			}
			bitstream = null;
			decoder = null;
		}
	}

	public void pause() throws JavaLayerException {
		playing = false;
	}

	public void resume() throws JavaLayerException {
		if (!playing) {
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
				resumePosition = frameNumber;
			}
		}

		if (frameNumber == end) {
			songHasEnded = true;
		}

		handleApparentStop();
		return ok;
	}

	// Make sure the player is in the correct position
	private void checkFramePosition(int startingPosition)
			throws JavaLayerException {
		moveTo(startingPosition);
		playing = true;
	}

	private void handleApparentStop() {
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

	private void handlePlayingPosition() throws JavaLayerException {
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

	private boolean handleFrameDecoding() throws JavaLayerException {
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
		Header header = readFrame();
		if (header == null) {
			return false;
		}
		frameNumber++;
		bitstream.closeFrame();
		return true;
	}
}
