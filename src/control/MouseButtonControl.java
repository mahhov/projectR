package control;

import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

import static org.lwjgl.glfw.GLFW.*;

public class MouseButtonControl implements GLFWMouseButtonCallbackI {
    private Mouse mouses[];

    public MouseButtonControl(long window) {
        glfwSetMouseButtonCallback(window, this);
        mouses = new Mouse[2];
        for (int i = 0; i < mouses.length; i++)
            mouses[i] = new Mouse();
    }

    private void setMouseState(MouseButton mouseButton, State state) {
        mouses[mouseButton.value].setState(state);
    }

    public boolean isMousePressed(MouseButton mouseButton) {
        return mouses[mouseButton.value].getState() == State.PRESSED;
    }

    public boolean isMouseDown(MouseButton mouseButton) {
        return mouses[mouseButton.value].getState() == State.DOWN;
    }

    public boolean isMouseReleased(MouseButton mouseButton) {
        return mouses[mouseButton.value].getState() == State.RELEASED;
    }

    public void next() {
        for (Mouse mouse : mouses)
            mouse.next();
    }

    @Override
    public void invoke(long window, int button, int action, int mods) {
        MouseButton mouseButton = button == GLFW_MOUSE_BUTTON_1 ? MouseButton.PRIMARY : MouseButton.SECONDARY;
        if (action == GLFW_RELEASE)
            setMouseState(mouseButton, State.RELEASED);
        else if (action == GLFW_PRESS)
            setMouseState(mouseButton, State.PRESSED);
    }

    private class Mouse {
        private State state;
        private boolean read;

        private void setState(State state) {
            this.state = state;
            read = false;
        }

        private State getState() {
            read = true;
            return state;
        }

        private void next() {
            if (read)
                if (state == State.PRESSED)
                    state = State.DOWN;
                else if (state == State.RELEASED)
                    state = State.UP;
        }
    }
}   