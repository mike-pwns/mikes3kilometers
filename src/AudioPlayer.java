
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {
    private Clip clip;
    private boolean muted = false;
    private boolean paused = false;
    private long pausePosition = 0;
    private boolean loop = true;

    public void setLoop(boolean loop) {
        this.loop = loop;
    }
    public void playAudio(String filename) {
        stopAudio();

        File file = new File("res/audio/" + filename);

        if (!file.isFile()) {
            System.err.println("Audio file not found: " + file.getAbsolutePath());
            return;
        }

        try (AudioInputStream stream =
                     AudioSystem.getAudioInputStream(file)) {

            clip = AudioSystem.getClip();
            clip.open(stream);

            // Loop indefinitely
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }

            // Apply mute setting
            applyMute();

            paused = false;
            pausePosition = 0;

        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio format. Use PCM WAV: " + filename);
        } catch (IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void pauseAudio() {
        if (clip != null && clip.isRunning()) {
            pausePosition = clip.getMicrosecondPosition();
            clip.stop();
            paused = true;
        }
    }

    public void resumeAudio() {
        if (clip != null && paused) {
            clip.setMicrosecondPosition(pausePosition);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            paused = false;
        }
    }

    public void stopAudio() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }

        paused = false;
        pausePosition = 0;
    }

    public boolean toggleMuted() {
        muted = !muted;
        applyMute();
        return muted;
    }

    private void applyMute() {
        if (clip == null || !clip.isControlSupported(
                BooleanControl.Type.MUTE)) {
            return;
        }

        BooleanControl muteControl = (BooleanControl)
                clip.getControl(BooleanControl.Type.MUTE);

        muteControl.setValue(muted);
    }

    public boolean isMuted() {
        return muted;
    }
}
