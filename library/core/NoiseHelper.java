package library.core;

public class NoiseHelper implements PConstants {

    public static long noiseSeed = 0;
    public static int noiseFunction = SIMPLEX;
    public static int noiseType = STANDARD;
    public static int noiseOctaves = 1;
    public static float noiseLacunarity = 2;
    public static float noisePersistence = 0.5f;

    // ----------------------Backend Simplex Noise----------------------
    public static float snoise(double x, double y, double z, double w) {
        float n = SimplexNoise.noise4_ImproveXYZ_ImproveXY(noiseSeed, x, y, z, w); // -1 to 1
        n = (n + 1) / 2; // 0 to 1
        return n;
    }

    public static float snoise(double x, double y, double z) {
        float n = SimplexNoise.noise3_ImproveXY(noiseSeed, x, y, z);
        n = (n + 1) / 2; // 0 to 1
        return n;
    }

    public static float snoise(double x, double y) {
        float n = SimplexNoise.noise2(noiseSeed, x, y);
        n = (n + 1) / 2; // 0 to 1
        return n;
    }

    public static float snoise(double x) {
        float n = SimplexNoise.noise2(noiseSeed, x, 0);
        n = (n + 1) / 2; // 0 to 1
        return n;
    }

    // ----------------------Simplex noise----------------------
    public static float simplexNoise(double x, double y, double z, double w) {
        if (noiseType == STANDARD) {
            return snoise(x, y, z, w);
        } else if (noiseType == FRACTAL) {
            float total = 0;
            float frequency = 1;
            float amplitude = 1;

            for (int i = 0; i < noiseOctaves; i++) {
                total += snoise(x * frequency, y * frequency, z * frequency, w * frequency) * amplitude;
                frequency *= noiseLacunarity;
                amplitude *= noisePersistence;
            }

            return total;
        } else {
            return snoise(x, y, z, w); // Shouldn't happen but just in case
        }
    }

    public static float simplexNoise(double x, double y, double z) {
        if (noiseType == STANDARD) {
            return snoise(x, y, z);
        } else if (noiseType == FRACTAL) {
            float total = 0;
            float frequency = 1;
            float amplitude = 1;

            for (int i = 0; i < noiseOctaves; i++) {
                total += snoise(x * frequency, y * frequency, z * frequency) * amplitude;
                frequency *= noiseLacunarity;
                amplitude *= noisePersistence;
            }

            return total;
        } else {
            return snoise(x, y, z); // Shouldn't happen but just in case
        }
    }

    public static float simplexNoise(double x, double y) {
        if (noiseType == STANDARD) {
            return snoise(x, y);
        } else if (noiseType == FRACTAL) {
            float total = 0;
            float frequency = 1;
            float amplitude = 1;

            for (int i = 0; i < noiseOctaves; i++) {
                total += snoise(x * frequency, y * frequency) * amplitude;
                frequency *= noiseLacunarity;
                amplitude *= noisePersistence;
            }

            return total;
        } else {
            return snoise(x, y); // Shouldn't happen but just in case
        }
    }

    public static float simplexNoise(double x) {
        if (noiseType == STANDARD) {
            return snoise(x);
        } else if (noiseType == FRACTAL) {
            float total = 0;
            float frequency = 1;
            float amplitude = 1;

            for (int i = 0; i < noiseOctaves; i++) {
                total += snoise(x * frequency) * amplitude;
                frequency *= noiseLacunarity;
                amplitude *= noisePersistence;
            }

            return total;
        } else {
            return snoise(x); // Shouldn't happen but just in case
        }
    }

    public static float simplexNoise(PVector p) {
        if (p.z == 0) {
            return simplexNoise(p.x, p.y);
        } else {
            return simplexNoise(p.x, p.y, p.z);
        }
    }

}
