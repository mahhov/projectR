package character.model;

import control.KeyButton;
import control.KeyControl;
import modelviewer.Selector;
import util.LList;

public class ViewModel {
    private static final float TRANSLATION_SPEED = .5f, ROTATION_SPEED = .1f, SIZE_SPEED = .1f; // todo rename size -> scale

    private LList<SegmentEditable> segments;
    private LList<SegmentEditable>.Node selectedSegmentNode;

    public ViewModel() {
        segments = new LList<>();
    }

    public void addSegment(SegmentEditable segment) {
        selectedSegmentNode = segments.addTail(segment);
    }

    public void update(int selectedSegmentDelta, Selector.Tool tool, KeyControl keyControl) {
        if (selectedSegmentDelta > 0 && selectedSegmentNode.getNext() != null)
            selectedSegmentNode = selectedSegmentNode.getNext();
        else if (selectedSegmentDelta < 0 && selectedSegmentNode.getPrev() != null)
            selectedSegmentNode = selectedSegmentNode.getPrev();

        switch (tool) {
            case POSITION:
                updatePosition(keyControl);
                break;
            case SIZE:
                updateSize(keyControl);
                break;
        }
    }

    private void updatePosition(KeyControl keyControl) {
        float dx = 0, dy = 0, dz = 0, dtheta = 0;

        if (keyControl.isKeyDown(KeyButton.KEY_W))
            dy += TRANSLATION_SPEED;

        if (keyControl.isKeyDown(KeyButton.KEY_S))
            dy -= TRANSLATION_SPEED;

        if (keyControl.isKeyDown(KeyButton.KEY_A))
            dx -= TRANSLATION_SPEED;

        if (keyControl.isKeyDown(KeyButton.KEY_D))
            dx += TRANSLATION_SPEED;

        if (keyControl.isKeyDown(KeyButton.KEY_SHIFT))
            dz -= TRANSLATION_SPEED;

        if (keyControl.isKeyDown(KeyButton.KEY_SPACE))
            dz += TRANSLATION_SPEED;

        if (keyControl.isKeyDown(KeyButton.KEY_Q))
            dtheta += ROTATION_SPEED;

        if (keyControl.isKeyDown(KeyButton.KEY_E))
            dtheta -= ROTATION_SPEED;

        selectedSegmentNode.getValue().addTranslation(dx, dy, dz);
        selectedSegmentNode.getValue().addRotation(dtheta);
    }

    private void updateSize(KeyControl keyControl) {
        float dx = 0, dy = 0, dz = 0;

        if (keyControl.isKeyDown(KeyButton.KEY_W))
            dy += SIZE_SPEED;

        if (keyControl.isKeyDown(KeyButton.KEY_S))
            dy -= SIZE_SPEED;

        if (keyControl.isKeyDown(KeyButton.KEY_A))
            dx -= SIZE_SPEED;

        if (keyControl.isKeyDown(KeyButton.KEY_D))
            dx += SIZE_SPEED;

        if (keyControl.isKeyDown(KeyButton.KEY_SHIFT))
            dz -= SIZE_SPEED;

        if (keyControl.isKeyDown(KeyButton.KEY_SPACE))
            dz += SIZE_SPEED;

        if (keyControl.isKeyDown(KeyButton.KEY_Q)) {
            dx += SIZE_SPEED;
            dy += SIZE_SPEED;
            dz += SIZE_SPEED;
        }

        if (keyControl.isKeyDown(KeyButton.KEY_E)) {
            dx -= SIZE_SPEED;
            dy -= SIZE_SPEED;
            dz -= SIZE_SPEED;
        }

        selectedSegmentNode.getValue().addScale(dx, dy, dz);
    }

    public void draw() {
        for (Segment segment : segments)
            segment.draw();
    }
}