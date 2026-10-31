
import java.util.concurrent.atomic.AtomicInteger;

public class AudioPlayer {

    private static final AtomicInteger NEXT_ID =
            new AtomicInteger(1);

    private final int playerId = NEXT_ID.getAndIncrement();

    private boolean loop = false;
    private boolean muted = true;
    private boolean paused = false;

    private String currentTrack;

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void playAudio(String resourcePath) {

        currentTrack = resourcePath;
        paused = false;

        playBrowserAudio(
                playerId,
                resourcePath,
                loop,
                muted
        );
    }

    public void stopAudio() {

        stopBrowserAudio(playerId);

        currentTrack = null;
        paused = false;
    }

    public void pauseAudio() {

        pauseBrowserAudio(playerId);
        paused = true;
    }

    public void resumeAudio() {

        if (currentTrack != null && paused) {

            resumeBrowserAudio(playerId);
            paused = false;
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void setMuted(boolean muted) {

        this.muted = muted;

        setBrowserMuted(muted);
    }

    public boolean isMuted() {
        return muted;
    }

    public static String[] loadSoundEffects() {
        return new String[0];
    }

    // Browser audio methods

    private native void playBrowserAudio(
            int id,
            String path,
            boolean loop,
            boolean muted
    );

    private native void stopBrowserAudio(int id);

    private native void pauseBrowserAudio(int id);

    private native void resumeBrowserAudio(int id);

    private native void setBrowserMuted(boolean muted);
}
