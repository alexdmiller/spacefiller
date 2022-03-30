package spacefiller.math;


public final class SinCosLUT {

    /**
     * default precision
     */
    public static final float DEFAULT_PRECISION = 0.25f;

    private static SinCosLUT DEFAULT_INSTANCE;

    public static final SinCosLUT getDefaultInstance() {
        if (DEFAULT_INSTANCE == null) {
            DEFAULT_INSTANCE = new SinCosLUT();
        }
        return DEFAULT_INSTANCE;
    }

    /**
     * Lookup table for sine values
     */
    private final float[] sinLUT;

    private final float precision;

    private final int period;
    private final int quadrant;

    private final float deg2rad;
    private final float rad2deg;

    public SinCosLUT() {
        this(DEFAULT_PRECISION);
    }

    public SinCosLUT(float precision) {
        this.precision = precision;
        this.period = (int) (360 / precision);
        this.quadrant = period >> 2;
        this.deg2rad = (float) (Math.PI / 180.0) * precision;
        this.rad2deg = (float) (180.0 / Math.PI) / precision;
        this.sinLUT = new float[period];
        for (int i = 0; i < period; i++) {
            sinLUT[i] = (float) Math.sin(i * deg2rad);
        }
    }

    /**
     * Calculate cosine for the passed in angle in radians.
     *
     * @param theta
     * @return cosine value for theta
     */
    public final float cos(float theta) {
        while (theta < 0) {
            theta += MathUtils.TWO_PI;
        }
        return sinLUT[((int) (theta * rad2deg) + quadrant) % period];
    }

    public int getPeriod() {
        return period;
    }

    public float getPrecision() {
        return precision;
    }

    public float[] getSinLUT() {
        return sinLUT;
    }

    /**
     * Calculates sine for the passed angle in radians.
     *
     * @param theta
     * @return sine value for theta
     */
    public final float sin(float theta) {
        while (theta < 0) {
            theta += MathUtils.TWO_PI;
        }
        return sinLUT[(int) (theta * rad2deg) % period];
    }
}