package ui;

import character.Human;
import control.KeyControl;
import control.MouseButtonControl;
import control.MousePosControl;
import map.Map;
import shape.Rects;
import shape.Texts;

public class UiDrawer {
    private static float SMALL_MARGIN = .02f, MEDIUM_MARGIN = .1f;

    private static float BAR_WIDTH = .3f, BAR_HEIGHT = .03f;
    private static final float
            BAR_ROW2_BOTTOM = -1 + MEDIUM_MARGIN,
            BAR_ROW2_TOP = BAR_ROW2_BOTTOM + BAR_HEIGHT,
            BAR_ROW1_BOTTOM = BAR_ROW2_TOP + SMALL_MARGIN,
            BAR_ROW1_TOP = BAR_ROW1_BOTTOM + BAR_HEIGHT;
    private static final float
            BAR_COL1_LEFT = -1 + MEDIUM_MARGIN,
            BAR_COL1_RIGHT = BAR_COL1_LEFT + BAR_WIDTH,
            BAR_COL2_RIGHT = 1 - MEDIUM_MARGIN,
            BAR_COL2_LEFT = BAR_COL2_RIGHT - BAR_WIDTH;

    private static final float
            BAR_ROW3_HEIGHT = SMALL_MARGIN,
            BAR_ROW3_TOP = BAR_ROW2_BOTTOM - SMALL_MARGIN,
            BAR_ROW3_BOTTOM = BAR_ROW3_TOP - BAR_ROW3_HEIGHT;

    private static float CENTER_RECT_SIZE = .01f, CENTER_RECT_COLOR[] = new float[] {.1f, .5f, .3f, 1};

    private static final float
            TEXT_BOX_HEIHT = .3f,
            TEXT_BOX_LEFT = BAR_COL1_RIGHT + SMALL_MARGIN,
            TEXT_BOX_RIGHT = BAR_COL2_LEFT - SMALL_MARGIN,
            TEXT_BOX_BOTTOM = BAR_ROW2_BOTTOM,
            TEXT_BOX_TOP = TEXT_BOX_BOTTOM + TEXT_BOX_HEIHT;

    private static final float
            PANE_OFFSET = .13f,
            PANE_BOTTOM = TEXT_BOX_TOP + PANE_OFFSET,
            PANE_TOP = 1 - MEDIUM_MARGIN - PANE_OFFSET;

    private static final float BAR_ALPHA = 1;
    private static final float[] BACK_COLOR = new float[] {.3f, .3f, .3f, .3f};
    private static final float[] RESERVE_COLOR = new float[] {.2f, .6f, .6f, BAR_ALPHA}, STAMINA_COLOR = new float[] {1, .8f, .6f, BAR_ALPHA};
    private static final float[] SHIELD_COLOR = new float[] {.4f, .5f, .7f, BAR_ALPHA}, LIFE_COLOR = new float[] {.8f, .3f, .3f, BAR_ALPHA};
    private static final float[] EXPERIENCE_COLOR = new float[] {.9f, .6f, .1f, BAR_ALPHA};

    private Human human;
    private Rects rects;
    private Texts texts;

    private UiBar reserveBar, staminaBar;  // todo group 4 bars into 1 ui component?
    private UiBar shieldBar, lifeBar;

    private Texts.Text levelText;
    private UiBar experienceBar;

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

        // life & stamina bars
        reserveBar = new UiBar(BAR_COL2_LEFT, BAR_ROW1_TOP, BAR_COL2_RIGHT, BAR_ROW1_BOTTOM, RESERVE_COLOR, BACK_COLOR, rects);
        staminaBar = new UiBar(BAR_COL2_LEFT, BAR_ROW2_TOP, BAR_COL2_RIGHT, BAR_ROW2_BOTTOM, STAMINA_COLOR, BACK_COLOR, rects);
        shieldBar = new UiBar(BAR_COL1_LEFT, BAR_ROW1_TOP, BAR_COL1_RIGHT, BAR_ROW1_BOTTOM, SHIELD_COLOR, BACK_COLOR, rects);
        lifeBar = new UiBar(BAR_COL1_LEFT, BAR_ROW2_TOP, BAR_COL1_RIGHT, BAR_ROW2_BOTTOM, LIFE_COLOR, BACK_COLOR, rects);

        // experience
        levelText = texts.addText();
        levelText.setCoordinates(BAR_COL1_LEFT, BAR_ROW3_TOP, -1 + SMALL_MARGIN);
        experienceBar = new UiBar(BAR_COL1_LEFT, BAR_ROW3_TOP, BAR_COL2_RIGHT, BAR_ROW3_BOTTOM, EXPERIENCE_COLOR, BACK_COLOR, rects);

        // panes
        inventory = new UiInventory(PANE_OFFSET, PANE_TOP, BAR_COL2_RIGHT, PANE_BOTTOM, BACK_COLOR, rects, texts, human.getInventory());
        this.map = new UiMap(BAR_COL1_LEFT, PANE_TOP, -PANE_OFFSET, PANE_BOTTOM, BACK_COLOR, rects, texts, map, mousePosControl, mouseButtonControl);
        textBox = new UiTextBox(TEXT_BOX_LEFT, TEXT_BOX_TOP, TEXT_BOX_RIGHT, TEXT_BOX_BOTTOM, BACK_COLOR, rects, texts, human.getInventory());

        // text test
        fpsText = texts.addText();
        fpsText.setCoordinates(-1, 1, .95f);

        this.keyControl = keyControl;
        this.mousePosControl = mousePosControl;
        this.mouseButtonControl = mouseButtonControl;
    }

    public void update() {
        if (human.isFollowZoom()) {
            reserveBar.hide();
            staminaBar.hide();
            shieldBar.hide();
            lifeBar.hide();
            levelText.disable();
            experienceBar.hide();
        } else {
            reserveBar.show();
            staminaBar.show();
            shieldBar.show();
            lifeBar.show();
            levelText.enable();
            experienceBar.show();
            reserveBar.setPercentFill(human.getStaminaReservePercent());
            staminaBar.setPercentFill(human.getStaminaPercent());
            shieldBar.setPercentFill(human.getShieldPercent());
            lifeBar.setPercentFill(human.getLifePercent());
            levelText.setText(human.getExperienceLevel() + "");
            experienceBar.setPercentFill(human.getExperiencePercent());
        }

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