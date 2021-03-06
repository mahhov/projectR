package model.segment;

import shape.CubeInstancedFaces;
import util.math.MathAngles;

public class Segment {
    private Segment parent;
    float scaleX, scaleY, scaleZ, color[];
    private CubeInstancedFaces cubeInstancedFaces;
    Transformation transformation;
    private Transformation animationTranformation, animatedTransformation, compositeTransformation;
    private float scale;
    private boolean stale;

    public Segment(float[] color) {
        this.color = color;
        transformation = new Transformation();
        animationTranformation = new Transformation();
        animatedTransformation = new Transformation();
        compositeTransformation = new Transformation();
        stale = true;
    }

    public Segment(SegmentData segmentData, float scale) {
        this(segmentData.color);
        scaleX = segmentData.scaleX * scale;
        scaleY = segmentData.scaleY * scale;
        scaleZ = segmentData.scaleZ * scale;
        transformation.x = segmentData.transformationX * scale;
        transformation.y = segmentData.transformationY * scale;
        transformation.z = segmentData.transformationZ * scale;
        transformation.theta = segmentData.transformationTheta;
        this.scale = scale;
    }

    public void init(Segment parent, CubeInstancedFaces cubeInstancedFaces) {
        this.parent = parent;
        this.cubeInstancedFaces = cubeInstancedFaces;
    }

    public void setScale(float scale) {
        scaleX = scaleY = scaleZ = scale;
    }

    public void setScale(float scaleX, float scaleY, float scaleZ) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
    }

    public void setTranslation(float x, float y, float z) {
        transformation.x = x;
        transformation.y = y;
        transformation.z = z;
        stale = true;
    }

    public void setRotation(float theta) {
        transformation.theta = theta;
        stale = true;
    }

    public void setAnimation(float x, float y, float z, float theta) {
        animationTranformation.x = x * scale;
        animationTranformation.y = y * scale;
        animationTranformation.z = z * scale;
        animationTranformation.theta = theta;
        stale = true;
    }

    private Transformation getCompositeTransformation() {
        if (parent == null) {
            compositeTransformation.sum(animationTranformation, transformation);
            MathAngles.norm(compositeTransformation.theta, compositeTransformation.norm);
            return compositeTransformation;
        }

        if (isStale()) {
            Transformation parentTransformation = parent.getCompositeTransformation();
            animatedTransformation.sum(animationTranformation, transformation);
            compositeTransformation.x = parentTransformation.x + parentTransformation.norm[0] * animatedTransformation.y + parentTransformation.norm[1] * animatedTransformation.x;
            compositeTransformation.y = parentTransformation.y + parentTransformation.norm[1] * animatedTransformation.y - parentTransformation.norm[0] * animatedTransformation.x;
            compositeTransformation.z = parentTransformation.z + animatedTransformation.z;
            compositeTransformation.theta = parentTransformation.theta + animatedTransformation.theta;
            MathAngles.norm(compositeTransformation.theta, compositeTransformation.norm);
            stale = false;
        }

        return compositeTransformation;
    }

    private boolean isStale() {
        return stale || (parent != null && parent.isStale());
    }

    public void draw() {
        getCompositeTransformation();
        cubeInstancedFaces.add(compositeTransformation.x, compositeTransformation.z, -compositeTransformation.y, compositeTransformation.theta, 0, scaleX, scaleZ, scaleY, color, false);
    }

    class Transformation {
        float x, y, z, theta;
        private float[] norm;

        private Transformation() {
            norm = new float[2];
        }

        private void sum(Transformation transformation1, Transformation transformation2) {
            x = transformation1.x + transformation2.x;
            y = transformation1.y + transformation2.y;
            z = transformation1.z + transformation2.z;
            theta = transformation1.theta + transformation2.theta;
        }
    }

    public Segment getParent() {
        return parent;
    }

    public SegmentData getSegmentData() {
        SegmentData segmentData = new SegmentData();

        segmentData.scaleX = scaleX;
        segmentData.scaleY = scaleY;
        segmentData.scaleZ = scaleZ;
        segmentData.color = color;
        segmentData.transformationX = transformation.x;
        segmentData.transformationY = transformation.y;
        segmentData.transformationZ = transformation.z;
        segmentData.transformationTheta = transformation.theta;

        return segmentData;
    }
}