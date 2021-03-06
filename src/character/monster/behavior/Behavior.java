package character.monster.behavior;

import character.Human;
import character.Monster;
import character.MoveControl;
import character.monster.attack.Attack;
import character.monster.motion.Motion;
import model.Model;
import util.math.MathRandom;

public class Behavior {
    enum State {PASSIVE, CURIOUS, HOSTILE, FRIGHTENED}

    private static final int WANDER_TIME = 100;
    private static final float WANDER_PROB = .7f;

    Monster monster;
    Human human;
    Motion motion;
    Attack attack;
    Model model;

    Timer timer;
    State state;
    boolean damageTaken;

    Behavior(Monster monster, Human human, Motion motion, Attack attack) {
        this.monster = monster;
        this.human = human;
        this.motion = motion;
        this.attack = attack;
        model = monster.getModel();
        timer = new Timer();
        state = State.PASSIVE;
    }

    public void update() {
    }

    void wanderOrStand() {
        timer.reset(MathRandom.random(WANDER_TIME / 2, WANDER_TIME));
        if (MathRandom.random(WANDER_PROB))
            motion.wander();
        else
            motion.stand();
    }

    public void damageTaken() {
        damageTaken = true;
    }

    public MoveControl getMoveControl() {
        return motion.moveControl;
    }

    public void draw() {
    }
}