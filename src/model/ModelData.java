package model;

import model.animation.AnimationData;
import model.segment.SegmentData;
import util.Writer;

import java.io.IOException;
import java.io.Serializable;

public class ModelData implements Serializable {
    public enum ModelType {
        GOAT("goat.model"),
        FOUR_LEG("fourLeg.model"),
        BIRD("bird.model");

        final String file;
        public final ModelData modelData;

        ModelType(String file) {
            this.file = file;
            modelData = ModelData.readModelData(file);
        }
    }

    public static final ModelType[] MODEL_TYPE_VALUES = ModelType.values();

    int segmentCount;
    int[] parents;
    SegmentData[] segmentData;
    AnimationData animationData;

    ModelData(int segmentCount) {
        this.segmentCount = segmentCount;
        parents = new int[segmentCount];
        segmentData = new SegmentData[segmentCount];
    }

    private static ModelData readModelData(String fileName) {
        try {
            return (ModelData) Writer.getReadStream(fileName).readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}