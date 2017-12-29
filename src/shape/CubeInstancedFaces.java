package shape;

import util.MathArrays;

public class CubeInstancedFaces {
    public static final int LEFT_SIDE = 0, RIGHT_SIDE = 1, FRONT_SIDE = 2, BACK_SIDE = 3, TOP_SIDE = 4, BOTTOM_SIDE = 5;

    private static final float S = .5f;
    private static final float[] VERTICIES = {
            -S, S, S, // 0 : (left, front, top)
            -S, -S, S, // 1 : (left, front, bottom)
            -S, S, -S, // 2 : (left, back, top)
            -S, -S, -S, // 3 : (left, back, bottom)
            S, S, S, // 4 : (right, front, top)
            S, -S, S, // 5 : (right, front, bottom)
            S, S, -S, // 6 : (right, back, top)
            S, -S, -S, // 7 : (right, back, bottom)
    };

    private static final float[][] SIDE_VERTICIES = new float[6][], SIDE_COLORS = new float[6][], SIDE_NORMALS = new float[6][];
    private static final byte[] INDICIES = new byte[] {0, 2, 3, 1, 0, 3};

    static {
        SIDE_VERTICIES[LEFT_SIDE] = MathArrays.pluckArray3(VERTICIES, new int[] {2, 0, 3, 1});
        SIDE_VERTICIES[RIGHT_SIDE] = MathArrays.pluckArray3(VERTICIES, new int[] {4, 6, 5, 7});
        SIDE_VERTICIES[FRONT_SIDE] = MathArrays.pluckArray3(VERTICIES, new int[] {0, 4, 1, 5});
        SIDE_VERTICIES[BACK_SIDE] = MathArrays.pluckArray3(VERTICIES, new int[] {6, 2, 7, 3});
        SIDE_VERTICIES[TOP_SIDE] = MathArrays.pluckArray3(VERTICIES, new int[] {2, 6, 0, 4});
        SIDE_VERTICIES[BOTTOM_SIDE] = MathArrays.pluckArray3(VERTICIES, new int[] {1, 5, 3, 7});

        SIDE_COLORS[LEFT_SIDE] = MathArrays.repeatArray(new float[] {.7f, .7f, .7f}, 4);
        SIDE_COLORS[RIGHT_SIDE] = MathArrays.repeatArray(new float[] {.6f, .6f, .6f}, 4);
        SIDE_COLORS[FRONT_SIDE] = MathArrays.repeatArray(new float[] {.8f, .8f, .8f}, 4);
        SIDE_COLORS[BACK_SIDE] = MathArrays.repeatArray(new float[] {.5f, .5f, .5f}, 4);
        SIDE_COLORS[TOP_SIDE] = MathArrays.repeatArray(new float[] {.9f, .9f, .9f}, 4);
        SIDE_COLORS[BOTTOM_SIDE] = MathArrays.repeatArray(new float[] {.4f, .4f, .4f}, 4);

        SIDE_NORMALS[LEFT_SIDE] = MathArrays.repeatArray(new float[] {-1, 0, 0}, 4);
        SIDE_NORMALS[RIGHT_SIDE] = MathArrays.repeatArray(new float[] {1, 0, 0}, 4);
        SIDE_NORMALS[FRONT_SIDE] = MathArrays.repeatArray(new float[] {0, 0, 1}, 4);
        SIDE_NORMALS[BACK_SIDE] = MathArrays.repeatArray(new float[] {0, 0, -1}, 4);
        SIDE_NORMALS[TOP_SIDE] = MathArrays.repeatArray(new float[] {0, 1, 0}, 4);
        SIDE_NORMALS[BOTTOM_SIDE] = MathArrays.repeatArray(new float[] {0, -1, 0}, 4);
    }

    private ShapeInstanced[] sides;

    public CubeInstancedFaces() {
        sides = new ShapeInstanced[6];
        for (int i = 0; i < sides.length; i++)
            sides[i] = new ShapeInstanced(SIDE_VERTICIES[i], SIDE_COLORS[i], SIDE_NORMALS[i], INDICIES);
    }

    public void add(int side, float x, float y, float z) {
        sides[side].add(x, y, z);
    }

    public void doneAdding() {
        for (ShapeInstanced side : sides)
            side.doneAdding();
    }

    public void draw() {
        for (ShapeInstanced side : sides)
            side.draw();
    }
}