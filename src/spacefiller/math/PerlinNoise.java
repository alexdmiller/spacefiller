package spacefiller.math;

import java.util.Random;

// TODO: incorporate this into Particle code
public class PerlinNoise {
    protected static final int PERLIN_YWRAPB = 4;

    protected static final int PERLIN_YWRAP = 1 << PERLIN_YWRAPB;

    protected static final int PERLIN_ZWRAPB = 8;

    protected static final int PERLIN_ZWRAP = 1 << PERLIN_ZWRAPB;

    protected static final int PERLIN_SIZE = 4095;

    private static final float PERLIN_MIN_AMPLITUDE = 0.001f;

    protected static int perlin_octaves = 4; // default to medium smooth

    protected static float perlin_amp_falloff = 0.5f; // 50% reduction/octave

    // [toxi 031112]
    // new vars needed due to recent change of cos table in PGraphics
    protected static int perlin_TWOPI, perlin_PI;

    protected static float[] perlin_cosTable;

    protected static float perlin[];

    protected static Random perlinRandom;

    {
        noiseSeed(System.nanoTime());
    }

    /**
     * Computes the Perlin noise function value at point x.
     */
    public static float noise(float x) {
        // is this legit? it's a dumb way to do it (but repair it later)
        return noise(x, 0f, 0f);
    }

    /**
     * Computes the Perlin noise function value at the point x, y.
     */
    public static float noise(float x, float y) {
        return noise(x, y, 0f);
    }

    /**
     * Computes the Perlin noise function value at x, y, z.
     */
    public static float noise(float x, float y, float z) {
        if (perlin == null) {
            if (perlinRandom == null) {
                perlinRandom = new Random();
            }
            perlin = new float[PERLIN_SIZE + 1];
            for (int i = 0; i < PERLIN_SIZE + 1; i++) {
                perlin[i] = perlinRandom.nextFloat(); // (float)Math.random();
            }
            // [toxi 031112]
            // noise broke due to recent change of cos table in PGraphics
            // this will take care of it
            perlin_cosTable = SinCosLUT.getDefaultInstance().getSinLUT();
            perlin_TWOPI = perlin_PI = SinCosLUT.getDefaultInstance()
                    .getPeriod();
            perlin_PI >>= 1;
        }

        if (x < 0) {
            x = -x;
        }
        if (y < 0) {
            y = -y;
        }
        if (z < 0) {
            z = -z;
        }

        int xi = (int) x, yi = (int) y, zi = (int) z;
        float xf = (x - xi);
        float yf = (y - yi);
        float zf = (z - zi);
        float rxf, ryf;

        float r = 0;
        float ampl = 0.5f;

        float n1, n2, n3;

        for (int i = 0; i < perlin_octaves; i++) {
            int of = xi + (yi << PERLIN_YWRAPB) + (zi << PERLIN_ZWRAPB);

            rxf = noise_fsc(xf);
            ryf = noise_fsc(yf);

            n1 = perlin[of & PERLIN_SIZE];
            n1 += rxf * (perlin[(of + 1) & PERLIN_SIZE] - n1);
            n2 = perlin[(of + PERLIN_YWRAP) & PERLIN_SIZE];
            n2 += rxf * (perlin[(of + PERLIN_YWRAP + 1) & PERLIN_SIZE] - n2);
            n1 += ryf * (n2 - n1);

            of += PERLIN_ZWRAP;
            n2 = perlin[of & PERLIN_SIZE];
            n2 += rxf * (perlin[(of + 1) & PERLIN_SIZE] - n2);
            n3 = perlin[(of + PERLIN_YWRAP) & PERLIN_SIZE];
            n3 += rxf * (perlin[(of + PERLIN_YWRAP + 1) & PERLIN_SIZE] - n3);
            n2 += ryf * (n3 - n2);

            n1 += noise_fsc(zf) * (n2 - n1);

            r += n1 * ampl;
            ampl *= perlin_amp_falloff;

            // break if amp has no more impact
            if (ampl < PERLIN_MIN_AMPLITUDE) {
                break;
            }

            xi <<= 1;
            xf *= 2;
            yi <<= 1;
            yf *= 2;
            zi <<= 1;
            zf *= 2;

            if (xf >= 1.0f) {
                xi++;
                xf--;
            }
            if (yf >= 1.0f) {
                yi++;
                yf--;
            }
            if (zf >= 1.0f) {
                zi++;
                zf--;
            }
        }
        return r;
    }

    // [toxi 031112]
    // now adjusts to the size of the cosLUT used via
    // the new variables, defined above
    private static float noise_fsc(float i) {
        // using bagel's cosine table instead
        return 0.5f * (1.0f - perlin_cosTable[(int) ((i + 0.5f) * perlin_PI)
                % perlin_TWOPI]);
    }

    // [toxi 040903]
    // make perlin noise quality user controlled to allow
    // for different levels of detail. lower values will produce
    // smoother results as higher octaves are surpressed

    public static void noiseDetail(int lod) {
        if (lod > 0) {
            perlin_octaves = lod;
        }
    }

    public static void noiseDetail(int lod, float falloff) {
        if (lod > 0) {
            perlin_octaves = lod;
        }
        if (falloff > 0) {
            perlin_amp_falloff = falloff;
        }
    }

    public static void noiseSeed(long what) {
        if (perlinRandom == null) {
            perlinRandom = new Random();
        }
        perlinRandom.setSeed(what);
        perlin = null;
    }
}
