package model;

import model.segment.Segment;
import model.segment.SegmentData;
import shape.CubeInstancedFaces;
import world.WorldElement;

public class Model {
    private Segment segments[];
    private int segmentCount;
    private WorldElement worldElement;

    public Model(ModelData modelData, CubeInstancedFaces cubeInstancedFaces, WorldElement worldElement) {
        segments = new Segment[modelData.segmentCount];
        for (SegmentData segmentData : modelData.segmentData)
            addSegment(new Segment(segmentData));

        for (int i = 0; i < modelData.segmentCount; i++)
            segments[i].init(modelData.parents[i] != -1 ? segments[modelData.parents[i]] : null, cubeInstancedFaces);

        this.worldElement = worldElement;
    }

    private void addSegment(Segment segment) {
        segments[segmentCount++] = segment;
    }

    public void draw() {
        segments[0].setTranslation(worldElement.getX(), worldElement.getY(), worldElement.getZ());
        segments[0].setRotation(worldElement.getTheta());
        for (Segment segment : segments)
            segment.draw();
    }
}