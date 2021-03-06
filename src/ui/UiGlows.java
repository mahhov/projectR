package ui;

import character.Glows;
import control.MouseButton;
import control.MouseButtonControl;
import control.MousePosControl;
import shape.Rects;
import shape.Texts;

class UiGlows extends UiInteractivePane {
    private Glows glows;

    UiGlows(float[] backColor, Rects rects, Texts texts, MousePosControl mousePosControl, MouseButtonControl mouseButtonControl, Glows glows) {
        super(Glows.getGlowCount() + 3, 2, false, Location.LEFT, backColor, rects, texts, mousePosControl, mouseButtonControl);
        setText(-2, "Glows");
        setText(Glows.getGlowCount() + 1, "Select All");
        setText(Glows.getGlowCount() + 2, "Unselect All");
        this.glows = glows;
    }

    Glows.Glow[] getGlowsSelected() {
        int[] selectedList = getSelectedList();
        Glows.Glow[] glows = new Glows.Glow[selectedList[0]];
        for (int i = 0; i < glows.length; i++)
            glows[i] = Glows.getGlow(selectedList[i + 1]);
        return glows;
    }

    void refreshSelectedGlows() {
        for (int i = 0; i < Glows.getGlowCount(); i++)
            if (isSelected(i) && !glows.available(Glows.getGlow(i)))
                toggleSelect(i);
    }

    @Override
    void updateTexts() {
        int highlighted = getHighlighted();
        if (highlighted != -1 && highlighted < Glows.getGlowCount() && !glows.available(Glows.getGlow(highlighted)))
            highlighted = -1;
        setHighlight(highlighted);

        if (getClick() == MouseButton.PRIMARY && highlighted != -1)
            if (highlighted < Glows.getGlowCount())
                toggleSelect(highlighted);

            else if (highlighted == Glows.getGlowCount() + 1) {
                for (int i = 0; i < Glows.getGlowCount(); i++)
                    if (!isSelected(i) && glows.available(Glows.getGlow(i)))
                        toggleSelect(i);

            } else if (highlighted == Glows.getGlowCount() + 2)
                for (int i = 0; i < Glows.getGlowCount(); i++)
                    if (isSelected(i))
                        toggleSelect(i);

        for (int i = 0; i < size - 3; i++)
            setText(i, glows.getText(i));
    }
}