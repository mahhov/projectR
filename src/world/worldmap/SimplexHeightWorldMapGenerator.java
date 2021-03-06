package world.worldmap;

import util.math.MathNumbers;
import util.math.MathRandom;
import world.worldmap.simplex.SimplexNoiseHelper;

public class SimplexHeightWorldMapGenerator implements WorldMapGenerator {
    private final SimplexNoiseHelper noiseHeight;
    private final SimplexNoiseHelper noiseEnvironment;
    private final SimplexNoiseHelper noiseRockyness;
    private final int width, length, height;
    private final float heightMult;

    public SimplexHeightWorldMapGenerator(int width, int length, int height, int maxHeight) {
        noiseHeight = new SimplexNoiseHelper(1000, .5, MathRandom.random(0, Integer.MAX_VALUE));
        noiseEnvironment = new SimplexNoiseHelper(1000, .5, MathRandom.random(0, Integer.MAX_VALUE));
        noiseRockyness = new SimplexNoiseHelper(1000, .5, MathRandom.random(0, Integer.MAX_VALUE));
        this.width = width;
        this.length = length;
        this.height = height;
        heightMult = maxHeight - 1;
    }

    @Override
    public WorldMap generate(int startX, int startY, int startZ) {
        byte[][][] map = new byte[width + 2][length + 2][height + 2];
        int[][] heightMap = new int[width + 2][length + 2];
        byte[][] environment = new byte[width + 2][length + 2];

        for (int x = 0; x < width + 2; x++)
            for (int y = 0; y < length + 2; y++) {
                float noiseHeight = getNoiseHeight(x + startX - 1, y + startY - 1);
                float noiseRockyness = getNoiseRockyness(x + startX - 1, y + startY - 1);
                int mapHeight = (int) ((noiseHeight + 1) * heightMult * noiseRockyness + 1);
                int relativeMapHeight = mapHeight - startZ + 1;
                int maxZ = MathNumbers.min(relativeMapHeight, height + 1);
                for (int z = 0; z <= maxZ; z++)
                    map[x][y][z] = 1;

                heightMap[x][y] = mapHeight;
                environment[x][y] = getNoiseEnvironment(x + startX - 1, y + startY - 1);
            }

        return new WorldMap(map, heightMap, environment);
    }

    private float getNoiseHeight(int x, int y) {
        return MathNumbers.minMax((float) noiseHeight.getNoise(x, y), -1, 1);
    }

    private byte getNoiseEnvironment(int x, int y) {
        float noise = MathNumbers.minMax((float) noiseEnvironment.getNoise(x, y), -1, 1);
        return (byte) ((noise + 1) * 50);
    }

    private float getNoiseRockyness(int x, int y) {
        float noise = MathNumbers.minMax((float) noiseRockyness.getNoise(x, y), -1, 1);
        return (noise + 1) * .375f + .25f;
    }
}