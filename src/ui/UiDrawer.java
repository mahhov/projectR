package ui;

import character.Human;
import control.KeyControl;
import control.MouseButtonControl;
import control.MousePosControl;
import map.Map;
import shape.Rects;
import shape.Texts;

public class UiDrawer {
    static float SMALL_MARGIN = .02f, MEDIUM_MARGIN = .1f;

    private static float CENTER_RECT_SIZE = .01f, CENTER_RECT_COLOR[] = new float[] {.1f, .5f, .3f, 1};

    private static final float
            TEXT_BOX_HEIHT = .3f,
            TEXT_BOX_LEFT = UiHud.BAR_COL1_RIGHT + SMALL_MARGIN,
            TEXT_BOX_RIGHT = UiHud.BAR_COL2_LEFT - SMALL_MARGIN,
            TEXT_BOX_BOTTOM = UiHud.BAR_ROW2_BOTTOM,
            TEXT_BOX_TOP = TEXT_BOX_BOTTOM + TEXT_BOX_HEIHT;

    private static final float
            PANE_OFFSET = .13f,
            PANE_BOTTOM = TEXT_BOX_TOP + PANE_OFFSET,
            PANE_TOP = 1 - MEDIUM_MARGIN - PANE_OFFSET;

    static final float[] BACK_COLOR = new float[] {.3f, .3f, .3f, .3f};

    private Human human;
    private Rects rects;
    private Texts texts;

    private UiHud hud;
    private UiInventory inventory;
    private UiMap map;
    private UiTextBox textBox;
    private Texts.Text fpsText;

    // controls
    private KeyControl keyControl;
    private MousePosControl mousePosControl;
    private MouseButtonControl mouseButtonControl;

    public UiDrawer(Human human, Map map, KeyControl keyControl, MousePosControl mousePosControl, MouseButtonControl mouseButtonControl) {
        this.human = human;
        rects = new Rects(20); // todo allow dynamic growing size
        texts = new Texts(1000);

        // center crosshair
        rects.addRect(CENTER_RECT_COLOR).setCoordinates(-CENTER_RECT_SIZE, CENTER_RECT_SIZE, CENTER_RECT_SIZE, -CENTER_RECT_SIZE);

        // hud
        hud = new UiHud(rects, texts, human);

        // panes
        inventory = new UiInventory(PANE_OFFSET, PANE_TOP, UiHud.BAR_COL2_RIGHT, PANE_BOTTOM, BACK_COLOR, rects, texts, human.getInventory());
        this.map = new UiMap(UiHud.BAR_COL1_LEFT, PANE_TOP, -PANE_OFFSET, PANE_BOTTOM, BACK_COLOR, rects, texts, map, mousePosControl, mouseButtonControl);
        textBox = new UiTextBox(TEXT_BOX_LEFT, TEXT_BOX_TOP, TEXT_BOX_RIGHT, TEXT_BOX_BOTTOM, BACK_COLOR, rects, texts, human.getInventory());

        // text test
        fpsText = texts.addText();
        fpsText.setCoordinates(-1, 1, .95f);

        this.keyControl = keyControl;
        this.mousePosControl = mousePosControl;
        this.mouseButtonControl = mouseButtonControl;
    }

    public void update() {
        if (human.isFollowZoom())
            hud.hide();
        else
            hud.show();

        if (keyControl.isKeyPressed(KeyControl.KEY_E))
            inventory.toggle();
        inventory.update();

        if (keyControl.isKeyPressed(KeyControl.KEY_Q))
            map.toggle();
        map.update();

        if (keyControl.isKeyPressed(KeyControl.KEY_ENTER))
            textBox.toggle();
        textBox.update();

        rects.doneAdding();
        texts.doneAdding();
    }

    public void updateFps(int fps) {
        fpsText.setText("fps " + fps);
    }

    public void draw() {
        rects.draw();
    }

    public void drawText() {
        texts.draw();
    }
}