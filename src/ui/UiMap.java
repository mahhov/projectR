package ui;

import control.MouseButtonControl;
import control.MousePosControl;
import map.Map;
import shape.Rects;
import shape.Texts;

class UiMap extends UiTextListPane {
    private static final int SIZE = 16;
    private Map map;
    private String[] mapTexts;
    private MousePosControl mousePosControl;
    private MouseButtonControl mouseButtonControl;

    UiMap(float left, float top, float right, float bottom, float[] backColor, Rects rects, Texts texts, Map map, MousePosControl mousePosControl, MouseButtonControl mouseButtonControl) {
        super(SIZE, false, left, top, right, bottom, backColor, rects, texts);
        this.map = map;

        mapTexts = map.getTexts();
        for (int i = 0; i < size; i++)
            setText(i, mapTexts[i]);

        this.mousePosControl = mousePosControl;
        this.mouseButtonControl = mouseButtonControl;
    }

    @Override
    void setVisible() {
        super.setVisible();
        if (mousePosControl != null)
            mousePosControl.unlock();
    }

    @Override
    void setInvisible() {
        super.setInvisible();
        if (mousePosControl != null)
            mousePosControl.lock();
    }

    @Override
    void updateTexts() {
        int selected = getIntersecting(mousePosControl.getAbsX(), mousePosControl.getAbsY());
        if (selected < 2)
            selected = -1;
        else if (mouseButtonControl.isMousePressed(MouseButtonControl.PRIMARY))
            map.load(selected);
        setHighlightAndRefreshText(selected);
    }
}