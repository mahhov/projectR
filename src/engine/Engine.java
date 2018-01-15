package engine;

import camera.Camera;
import character.Human;
import control.KeyControl;
import control.MouseButtonControl;
import control.MousePosControl;
import map.Map;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import shader.ShaderManager;
import ui.UiDrawer;
import world.World;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Engine {
    private static final int WINDOW_SIZE = 1000;
    public static final int SCALE = 16, SCALE_Z = 16;

    private static final long NANOSECONDS_IN__SECOND = 1000000000L;
    private long window;
    private Camera camera;
    private Human human;
    private UiDrawer uiDrawer;
    private KeyControl keyControl;
    private MousePosControl mousePosControl;
    private MouseButtonControl mouseButtonControl;
    private World world;
    private Map map;

    private Engine() {
        initLwjgl();
        ShaderManager.setRenderShader();
        camera = new Camera(ShaderManager.getRenderShaderProgramId());
        keyControl = new KeyControl(window);
        mousePosControl = new MousePosControl(window);
        mouseButtonControl = new MouseButtonControl(window);
        world = new World(64 * SCALE, 64 * SCALE, 16 * SCALE_Z, camera);
        human = new Human(32 * Engine.SCALE, 0, 8 * Engine.SCALE_Z, 0, 0, world.getIntersectionMover(), world.getIntersectionPicker(), keyControl, mousePosControl, mouseButtonControl);
        world.setHuman(human);
        world.addRandomMonsters(100);
        map = new Map();
        ShaderManager.setTextShader();
        uiDrawer = new UiDrawer(human, map, keyControl, mousePosControl, mouseButtonControl);
        camera.setFollow(human);
    }

    private void initLwjgl() {
        System.out.println("LWJGL " + Version.getVersion());

        GLFWErrorCallback.createPrint(System.err).set();
        glfwInit();
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        window = glfwCreateWindow(WINDOW_SIZE, WINDOW_SIZE, "Project R", NULL, NULL);

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void destroyLwjgl() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void run() {
        int drawFrame = 0, engineFrame = 0;
        long beginTime = 0, endTime;

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            ShaderManager.setRenderShader();
            camera.update(keyControl);
            world.setCameraCoordinate(camera.getWorldCoordinate());
            world.update();
            world.draw();

            uiDrawer.update();
            ShaderManager.setUiShader();
            uiDrawer.draw();
            ShaderManager.setTextShader();
            uiDrawer.drawText();

            glfwSwapBuffers(window);
            glfwPollEvents();

            engineFrame++;
            endTime = System.nanoTime() + 1;
            if (endTime - beginTime > NANOSECONDS_IN__SECOND) {
                uiDrawer.updateFps(engineFrame);
                drawFrame = 0;
                engineFrame = 0;
                beginTime = endTime;
            }
        }

        world.shutDownGeneratorExecutors();
    }

    public static void main(String[] args) {
        new Engine().run();
    }
}

// todo
// ~~ high priority ~~
// combat
// enemies
// progression
// instances
// inventory
// harvesting
// crafting (best gear: 1. craftable, 2. choices of pros/cons, 3. materials required rare drops from touch monsters)
// replace string concat with builder

// ~~ low priority ~~
// camera culling
// multi-light support
// multithread world chunk fill
// blur distant
// polygon outline
// more efficient is(draw/world)empty tracking
// shadows
// investigate if should retain STATIC_DRAW
// support cubes of different colors in same CubeInstancedFaces
// multi thread chunk loading
// particles