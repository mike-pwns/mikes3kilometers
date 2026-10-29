
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

        AudioFormat[] formats = {
                new AudioFormat(44100f, 16, 2, true, false),
                new AudioFormat(44100f, 16, 1, true, false),
                new AudioFormat(22050f, 16, 1, true, false),
                new AudioFormat(22050f, 16, 2, true, false)
        };

        try {
            AudioInputStream original =
                    AudioSystem.getAudioInputStream(file);

            AudioFormat sourceFormat = original.getFormat();
            original.close();

            // Try each available audio mixer
            for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {

                Mixer mixer = AudioSystem.getMixer(mixerInfo);

                // Try the original format first
                AudioFormat[] attempts = new AudioFormat[formats.length + 1];
                attempts[0] = sourceFormat;
                System.arraycopy(formats, 0, attempts, 1, formats.length);

                for (AudioFormat format : attempts) {

                    DataLine.Info info = new DataLine.Info(Clip.class, format);

                    if (!mixer.isLineSupported(info)) {
                        continue;
                    }

                    try (AudioInputStream input =
                                 AudioSystem.getAudioInputStream(file)) {

                        AudioInputStream playable = input;

                        if (!sourceFormat.matches(format)) {
                            if (!AudioSystem.isConversionSupported(
                                    format, sourceFormat)) {
                                continue;
                            }

                            playable = AudioSystem.getAudioInputStream(
                                    format, input);
                        }

                        Clip newClip = (Clip) mixer.getLine(info);
                        newClip.open(playable);

                        clip = newClip;

                        if (loop) {
                            clip.loop(Clip.LOOP_CONTINUOUSLY);
                        } else {
                            clip.start();
                        }

                        applyMute();
                        paused = false;
                        pausePosition = 0;

                        return;

                    } catch (LineUnavailableException |
                             IOException |
                             IllegalArgumentException e) {
                        // Try another format or mixer
                    }
                }
            }

            System.err.println(
                    "No compatible audio output found for: " + filename);

        } catch (UnsupportedAudioFileException | IOException e) {
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
