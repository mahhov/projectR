package world;

import geometry.CoordinateI3;
import shape.CubeInstancedFaces;
import util.LList;
import world.worldmap.WorldMap;
import world.worldmap.WorldMapGenerator;

class WorldChunk {
    private CubeInstancedFaces cubeInstancedFaces;
    final int offsetX, offsetY, offsetZ;
    private DynamicCell dynamicCells;
    private byte[][][] dynamicMap;
    private WorldMap worldMap;
    private boolean generated;
    private boolean drawEmpty;

    WorldChunk(CoordinateI3 coordinate) {
        offsetX = coordinate.x * World.CHUNK_SIZE;
        offsetY = coordinate.y * World.CHUNK_SIZE;
        offsetZ = coordinate.z * World.CHUNK_SIZE;
        drawEmpty = true;

        dynamicCells = new DynamicCell();
        dynamicMap = new byte[World.CHUNK_SIZE][World.CHUNK_SIZE][World.CHUNK_SIZE];
    }

    void generate(CubeInstancedFaces cubeInstancedFaces, WorldMapGenerator generator) {
        if (generated)
            return;
        this.cubeInstancedFaces = cubeInstancedFaces;
        worldMap = generator.generate(offsetX, offsetY, offsetZ);
        fill();
        generated = true;
    }

    private void fill() {
        boolean[] sides;

        for (int x = 0; x < World.CHUNK_SIZE; x++)
            for (int y = 0; y < World.CHUNK_SIZE; y++) {
                WorldMap.Terrain terrain = worldMap.getTerrain(x, y);
                for (int z = 0; z < World.CHUNK_SIZE; z++)
                    if (worldMap.map[x + 1][y + 1][z + 1] == 1)
                        if ((sides = cubeSides(x, y, z)) != null) {
                            drawEmpty = false;
                            cubeInstancedFaces.add(x + .5f + offsetX, z + .5f + offsetZ, -(y + .5f + offsetY), sides, terrain.color);
                        }
            }
    }

    void doneFilling() {
        if (!drawEmpty)
            cubeInstancedFaces.doneAdding();
    }

    private boolean[] cubeSides(int x, int y, int z) {
        boolean[] sides = new boolean[6];

        sides[CubeInstancedFaces.LEFT_SIDE] = !hasCube(x - 1, y, z);
        sides[CubeInstancedFaces.RIGHT_SIDE] = !hasCube(x + 1, y, z);
        sides[CubeInstancedFaces.FRONT_SIDE] = !hasCube(x, y - 1, z);
        sides[CubeInstancedFaces.BACK_SIDE] = !hasCube(x, y + 1, z);
        sides[CubeInstancedFaces.TOP_SIDE] = !hasCube(x, y, z + 1);
        sides[CubeInstancedFaces.BOTTOM_SIDE] = !hasCube(x, y, z - 1);

        for (int i = 0; i < 6; i++)
            if (sides[i])
                return sides;

        return null;
    }

    private boolean hasCube(int x, int y, int z) {
        return worldMap.map[x + 1][y + 1][z + 1] != 0 || x >= 0 && y >= 0 && z >= 0 && x < World.CHUNK_SIZE && y < World.CHUNK_SIZE && z < World.CHUNK_SIZE && dynamicMap[x][y][z] != 0;
    }

    boolean hasCube(CoordinateI3 cubeCoordinate) {
        return hasCube(cubeCoordinate.x, cubeCoordinate.y, cubeCoordinate.z);
    }

    void incrementCube(CoordinateI3 cubeCoordinate) {
        dynamicMap[cubeCoordinate.x][cubeCoordinate.y][cubeCoordinate.z]++;
    }

    void decrementCube(CoordinateI3 cubeCoordinate) {
        dynamicMap[cubeCoordinate.x][cubeCoordinate.y][cubeCoordinate.z]--;
    }

    LList<WorldElement>.Node addDynamicElement(WorldElement element) {
        return dynamicCells.add(element);
    }

    void removeDynamicElement(LList<WorldElement>.Node elementNode) {
        dynamicCells.remove(elementNode);
    }

    WorldElement checkDynamicElement(float preciseX, float preciseY, float preciseZ, float range) {
        if (dynamicCells.empty())
            return null;
        return dynamicCells.checkHit(preciseX, preciseY, preciseZ, range);
    }

    void draw() {
        if (!drawEmpty)
            cubeInstancedFaces.draw();
    }

    WorldMap getWorldMap() {
        return worldMap;
    }

    boolean isGenerated() {
        return generated;
    }
}