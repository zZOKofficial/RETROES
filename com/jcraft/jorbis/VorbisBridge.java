package com.jcraft.jorbis;

public final class VorbisBridge {
    private VorbisBridge() {
    }

    public static int readLittleEndian(VorbisFile vf, byte[] buffer, int length) {
        int index = 0;
        while (true) {
            if (vf.decode_ready) {
                float[][][] pcmHolder = new float[1][][];
                float[][] pcm;
                int[] pcmIndex = new int[vf.getInfo(-1).channels];
                int samples = vf.vd.synthesis_pcmout(pcmHolder, pcmIndex);
                pcm = pcmHolder[0];
                if (samples != 0) {
                    int channels = vf.getInfo(-1).channels;
                    int bytesPerSample = 2 * channels;
                    if (samples > length / bytesPerSample) {
                        samples = length / bytesPerSample;
                    }
                    for (int i = 0; i < channels; i++) {
                        float[] src = pcm[i];
                        int offset = pcmIndex[i];
                        int dest = i * 2;
                        for (int j = 0; j < samples; j++) {
                            int val = (int) (src[offset + j] * 32768.0 + 0.5);
                            if (val > 32767) {
                                val = 32767;
                            } else if (val < -32768) {
                                val = -32768;
                            }
                            buffer[dest] = (byte) val;
                            buffer[dest + 1] = (byte) (val >> 8);
                            dest += bytesPerSample;
                        }
                    }
                    vf.vd.synthesis_read(samples);
                    vf.pcm_offset += samples;
                    return samples * bytesPerSample;
                }
            }
            int result = vf.process_packet(1);
            if (result == 0) {
                return 0;
            }
            if (result == -1) {
                return -1;
            }
        }
    }
}
