package sounds;

import app.AppPaths;
import com.jcraft.jorbis.VorbisBridge;
import com.jcraft.jorbis.VorbisFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.Timer;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MusicPlayer {
    private static final String MUSIC_PATH = AppPaths.assetString("sounds/dragonscale.ogg");
    private static final int FADE_STEP_MS = 33;
    private static final int DECODE_CHUNK = 8192;
    private static final MusicPlayer INSTANCE = new MusicPlayer();

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean decoding = new AtomicBoolean(false);
    private volatile boolean muted = false;
    private volatile boolean startRequested = false;
    private volatile float fadeGain = 0f;
    private volatile byte[] pcmData;
    private volatile int sampleRate = 44100;
    private volatile int channels = 2;
    private Thread worker;
    private Thread decodeThread;
    private Timer fadeTimer;
    private boolean shutdownHookRegistered = false;

    private MusicPlayer() {
    }

    public static MusicPlayer getInstance() {
        return INSTANCE;
    }

    public synchronized void startWithFadeIn() {
        if (running.get() || decoding.get()) {
            return;
        }
        if (!shutdownHookRegistered) {
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "music-shutdown"));
            shutdownHookRegistered = true;
        }
        startRequested = true;
        if (pcmData == null) {
            decoding.set(true);
            decodeThread = new Thread(this::decodeTrack, "music-decode");
            decodeThread.setDaemon(true);
            decodeThread.start();
        } else {
            beginPlayback();
        }
        animateGainTo(1f, 2000);
    }

    public synchronized void fadeOutAndStop(int durationMs) {
        if (!running.get()) {
            return;
        }
        Thread stopper = new Thread(() -> {
            animateGainTo(0f, durationMs);
            try {
                Thread.sleep(durationMs + 150L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            running.set(false);
            joinQuietly(worker);
        }, "music-stopper");
        stopper.setDaemon(true);
        stopper.start();
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isMuted() {
        return muted;
    }

    public boolean isRunning() {
        return running.get() || decoding.get();
    }

    private void beginPlayback() {
        running.set(true);
        worker = new Thread(this::playLoop, "music-player");
        worker.setDaemon(true);
        worker.start();
    }

    private void decodeTrack() {
        try {
            byte[] ogg = Files.readAllBytes(Paths.get(MUSIC_PATH));
            VorbisFile vorbis = new VorbisFile(new ByteArrayInputStream(ogg), null, 0);
            com.jcraft.jorbis.Info info = vorbis.getInfo(0);
            sampleRate = (int) info.rate;
            channels = info.channels;

            java.io.ByteArrayOutputStream pcmOut = new java.io.ByteArrayOutputStream(1 << 20);
            byte[] chunk = new byte[DECODE_CHUNK];
            while (true) {
                int n = VorbisBridge.readLittleEndian(vorbis, chunk, chunk.length);
                if (n <= 0) {
                    break;
                }
                pcmOut.write(chunk, 0, n);
            }
            vorbis.close();
            pcmData = pcmOut.toByteArray();
            if (pcmData.length == 0) {
                System.err.println("Music unavailable: decoded zero PCM bytes");
                return;
            }
        } catch (Throwable t) {
            System.err.println("Music unavailable: " + t);
            return;
        } finally {
            decoding.set(false);
        }
        if (startRequested) {
            beginPlayback();
        }
    }

    private void playLoop() {
        SourceDataLine line = null;
        try {
            byte[] data = pcmData;
            if (data == null || data.length == 0) {
                return;
            }
            AudioFormat format = new AudioFormat(sampleRate, 16, channels, true, false);
            DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine) AudioSystem.getLine(lineInfo);
            int frameSize = format.getFrameSize();
            int bufferBytes = Math.max(sampleRate * frameSize, frameSize * 1024);
            line.open(format, bufferBytes);
            line.start();

            int chunkSize = Math.max(frameSize * 256, DECODE_CHUNK);
            chunkSize -= chunkSize % frameSize;
            byte[] out = new byte[chunkSize];
            int pos = 0;

            while (running.get()) {
                int n = Math.min(chunkSize, data.length - pos);
                System.arraycopy(data, pos, out, 0, n);
                pos += n;
                if (pos >= data.length) {
                    pos = 0;
                }
                applyGain(out, n, muted ? 0f : fadeGain);
                line.write(out, 0, n);
            }
            line.drain();
        } catch (Throwable t) {
            if (running.get()) {
                System.err.println("Music unavailable: " + t);
            }
        } finally {
            running.set(false);
            if (line != null) {
                try {
                    line.stop();
                    line.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void shutdown() {
        if (!running.get() && !decoding.get()) {
            return;
        }
        running.set(false);
        long end = System.currentTimeMillis() + 2000;
        float start = fadeGain;
        while (System.currentTimeMillis() < end && running.get()) {
            float t = (2000f - (end - System.currentTimeMillis())) / 2000f;
            fadeGain = Math.max(0f, start * (1f - t));
            try {
                Thread.sleep(FADE_STEP_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        fadeGain = 0f;
        running.set(false);
        joinQuietly(worker);
        joinQuietly(decodeThread);
    }

    private void animateGainTo(float target, int durationMs) {
        if (fadeTimer != null) {
            fadeTimer.stop();
        }
        final float start = fadeGain;
        final long t0 = System.nanoTime();
        fadeTimer = new Timer(FADE_STEP_MS, e -> {
            float t = Math.min(1f, (System.nanoTime() - t0) / (durationMs * 1_000_000f));
            fadeGain = start + (target - start) * t;
            if (t >= 1f) {
                ((Timer) e.getSource()).stop();
            }
        });
        fadeTimer.start();
    }

    private static void applyGain(byte[] buffer, int length, float gain) {
        if (gain >= 0.999f) {
            return;
        }
        for (int i = 0; i + 1 < length; i += 2) {
            short sample = (short) ((buffer[i] & 0xff) | (buffer[i + 1] << 8));
            sample = (short) (sample * gain);
            buffer[i] = (byte) (sample & 0xff);
            buffer[i + 1] = (byte) ((sample >> 8) & 0xff);
        }
    }

    private static void joinQuietly(Thread t) {
        if (t != null) {
            try {
                t.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
