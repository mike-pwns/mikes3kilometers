
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {

    private Clip clip;

    private boolean muted = false;
    private boolean paused = false;
    private boolean loop = true;

    private long pausePosition = 0;
    private float savedGain = 0.0f;

    // The last file this player was asked to play.
    private String currentFilename;


    public synchronized void setLoop(boolean loop) {
        this.loop = loop;

        if (clip == null || !clip.isOpen() || paused) {
            return;
        }

        // Apply the new looping behavior immediately.
        if (loop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            clip.loop(0);
        }
    }

    public synchronized boolean isLooping() {
        return loop;
    }

    /**
     * Plays an audio file from res/audio.
     * Starting a new track closes the previous clip.
     */
    public synchronized void playAudio(String filename) {
        stopAudio();

        if (filename == null || filename.isBlank()) {
            System.err.println("Audio filename cannot be empty.");
            return;
        }

        File file = new File("res/audio", filename);

        if (!file.isFile()) {
            System.err.println(
                    "Audio file not found: " + file.getAbsolutePath()
            );
            return;
        }

        Clip newClip = null;

        try (AudioInputStream input =
                     AudioSystem.getAudioInputStream(file)) {

            newClip = AudioSystem.getClip();
            newClip.open(input);

            clip = newClip;
            currentFilename = filename;
            paused = false;
            pausePosition = 0;

            // Remember this clip's initial volume before muting.
            savedGain = 0.0f;

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl)
                        clip.getControl(FloatControl.Type.MASTER_GAIN);

                savedGain = gain.getValue();
            }

            applyMute();

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }

        } catch (UnsupportedAudioFileException e) {
            cleanupFailedClip(newClip);
            System.err.println("Unsupported audio file: " + filename);
            e.printStackTrace();

        } catch (LineUnavailableException e) {
            cleanupFailedClip(newClip);
            System.err.println("Audio output is unavailable: " + filename);
            e.printStackTrace();

        } catch (IOException e) {
            cleanupFailedClip(newClip);
            System.err.println("Could not read audio file: " + filename);
            e.printStackTrace();

        } catch (IllegalArgumentException e) {
            cleanupFailedClip(newClip);
            System.err.println("Could not initialize audio: " + filename);
            e.printStackTrace();
        }
    }

    private synchronized void cleanupFailedClip(Clip failedClip) {
        if (failedClip != null) {
            failedClip.stop();
            failedClip.close();
        }

        if (clip == failedClip) {
            clip = null;
            currentFilename = null;
        }

        paused = false;
        pausePosition = 0;
    }

    /**
     * Pauses playback and remembers its current position.
     */
    public synchronized void pauseAudio() {
        if (clip == null || !clip.isOpen() || paused) {
            return;
        }

        pausePosition = clip.getMicrosecondPosition();
        clip.stop();
        paused = true;
    }

    /**
     * Resumes the current clip from its saved position.
     */
    public synchronized void resumeAudio() {
        if (clip == null || !clip.isOpen() || !paused) {
            return;
        }

        long duration = clip.getMicrosecondLength();

        if (duration > 0) {
            pausePosition = Math.max(
                    0L,
                    Math.min(pausePosition, duration)
            );

            // Avoid attempting to resume beyond the end.
            if (pausePosition >= duration) {
                pausePosition = 0;
            }

            clip.setMicrosecondPosition(pausePosition);
        }

        paused = false;

        if (loop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            clip.start();
        }
    }

    /**
     * Stops playback and releases the clip.
     */
    public synchronized void stopAudio() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }

        currentFilename = null;
        paused = false;
        pausePosition = 0;
        savedGain = 0.0f;
    }

    /**
     * Sets the mute state explicitly.
     * Useful for keeping several audio players synchronized.
     */
    public synchronized void setMuted(boolean muted) {
        if (this.muted == muted) {
            return;
        }

        // Save the current gain before muting.
        if (muted && clip != null && clip.isOpen()
                && clip.isControlSupported(
                FloatControl.Type.MASTER_GAIN)) {

            FloatControl gain = (FloatControl)
                    clip.getControl(FloatControl.Type.MASTER_GAIN);

            savedGain = gain.getValue();
        }

        this.muted = muted;
        applyMute();
    }

    /**
     * Toggles mute and returns the new state.
     */
    public synchronized boolean toggleMuted() {
        setMuted(!muted);
        return muted;
    }

    public synchronized boolean isMuted() {
        return muted;
    }

    public synchronized boolean isPaused() {
        return paused;
    }

    /**
     * True only when the clip is actively running.
     */
    public synchronized boolean isPlaying() {
        return clip != null
                && clip.isOpen()
                && clip.isRunning()
                && !paused;
    }

    public synchronized String getCurrentFilename() {
        return currentFilename;
    }

    /**
     * Applies the current mute state without changing
     * the mute state itself.
     */
    private synchronized void applyMute() {
        if (clip == null || !clip.isOpen()) {
            return;
        }

        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            System.err.println(
                    "This audio clip does not support MASTER_GAIN."
            );
            return;
        }

        FloatControl gain = (FloatControl)
                clip.getControl(FloatControl.Type.MASTER_GAIN);

        if (muted) {
            gain.setValue(gain.getMinimum());
        } else {
            float restoredGain = Math.max(
                    gain.getMinimum(),
                    Math.min(gain.getMaximum(), savedGain)
            );

            gain.setValue(restoredGain);
        }
    }


    public static String[] loadSoundEffects() {
        File directory = new File("res/audio/sound_effects/");
        File[] files = directory.listFiles();

        if (files == null) {
            return new String[0];
        }

        String[] soundeffects = new String[files.length];

        for (int i = 0; i < files.length; i++) {
            soundeffects[i] = "sound_effects/" + files[i].getName();
        }

        return soundeffects;
    }
}
