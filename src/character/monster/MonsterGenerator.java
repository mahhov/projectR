package character.monster;

import character.Human;
import character.Monster;
import character.monster.attack.Attack;
import character.monster.attack.DegenAttack;
import character.monster.attack.MeleeAttack;
import character.monster.attack.NoneAttack;
import character.monster.behavior.AggressiveBehavior;
import character.monster.behavior.Behavior;
import character.monster.behavior.RetaliateBehavior;
import character.monster.behavior.RunawayBehavior;
import character.monster.motion.Motion;
import model.ModelData;
import shape.CubeInstancedFaces;
import util.Distribution;
import util.math.MathRandom;

public class MonsterGenerator {
    public static MonsterDetails createRandomDetails() {
        return createRhinoDetails();
    }

    private static MonsterDetails createBugDetails() {
        MonsterDetails details = new MonsterDetails();
        details.color = new float[] {0, 1, 1, 1};
        details.size = 3; // 1
        details.movement = MonsterDetails.Movement.WALK;
        details.runAcc = .07f;
        details.wanderSpeed = .1f;
        details.hostilitySpeed = 1;
        details.hostility = MonsterDetails.Hostility.RUNAWAY;
        details.hostilitySightDistance = 1;
        details.hostilityDangerDistance = 10;
        details.armour = MonsterDetails.Armour.NONE;
        details.life = 10;
        details.attack = MonsterDetails.Attack.NONE;
        details.metalReward = new Distribution(0, 0);
        details.glowReward = new Distribution[] {};
        details.coinReward = new Distribution(10, 20);
        details.experienceReward = 10;
        details.modelData = ModelData.ModelType.BUG.modelData;
        return details;
    }

    private static MonsterDetails createBirdDetails() {
        MonsterDetails details = new MonsterDetails();
        details.color = new float[] {0, 1, .5f, 1};
        details.size = 1;
        details.movement = MonsterDetails.Movement.FLY;
        details.runAcc = .07f;
        details.airAcc = .02f;
        details.jetAcc = .11f;
        details.wanderSpeed = .1f;
        details.hostilitySpeed = 1;
        details.hostility = MonsterDetails.Hostility.RUNAWAY;
        details.hostilitySightDistance = 1;
        details.hostilityDangerDistance = 10;
        details.armour = MonsterDetails.Armour.NONE;
        details.life = 10;
        details.attack = MonsterDetails.Attack.NONE;
        details.metalReward = new Distribution(0, 0);
        details.glowReward = new Distribution[] {};
        details.coinReward = new Distribution(15, 25);
        details.experienceReward = 10;
        details.modelData = ModelData.ModelType.BIRD.modelData;
        return details;
    }

    private static MonsterDetails createGoatDetails() {
        MonsterDetails details = new MonsterDetails();
        details.color = new float[] {1, 1, 0, 1};
        details.size = 1.5f;
        details.movement = MonsterDetails.Movement.WALK;
        details.runAcc = .07f;
        details.wanderSpeed = .1f;
        details.hostilitySpeed = 1;
        details.hostility = MonsterDetails.Hostility.RETALIATE;
        details.armour = MonsterDetails.Armour.NONE;
        details.life = 50;
        details.attack = MonsterDetails.Attack.DEGEN;
        details.attackDamage = .5f;
        details.attackAoe = 10;
        details.metalReward = new Distribution(0, 0);
        details.glowReward = new Distribution[] {};
        details.coinReward = new Distribution(20, 30);
        details.experienceReward = 50;
        details.modelData = ModelData.ModelType.GOAT.modelData;
        return details;
    }

    private static MonsterDetails createWolfDetails() {
        MonsterDetails details = new MonsterDetails();
        details.color = new float[] {1, 0, 0, 1};
        details.size = 2;
        details.movement = MonsterDetails.Movement.WALK;
        details.runAcc = .07f;
        details.wanderSpeed = .1f;
        details.hostilitySpeed = 1;
        details.hostility = MonsterDetails.Hostility.AGGRESSIVE;
        details.hostilitySightDistance = 20;
        details.armour = MonsterDetails.Armour.NONE;
        details.life = 50;
        details.attack = MonsterDetails.Attack.MELEE;
        details.attackSpeed = 100;
        details.attackDamage = 50;
        details.attackRange = 10;
        details.metalReward = new Distribution(0, 0);
        details.glowReward = new Distribution[] {};
        details.coinReward = new Distribution(25, 35);
        details.experienceReward = 50;
        details.modelData = ModelData.ModelType.FOUR_LEG.modelData;
        return details;
    }

    private static MonsterDetails createRhinoDetails() {
        MonsterDetails details = new MonsterDetails();
        details.color = new float[] {1, 0, 0, 1};
        details.size = 2;
        details.movement = MonsterDetails.Movement.WALK;
        details.runAcc = .07f;
        details.wanderSpeed = .1f;
        details.hostilitySpeed = 1;
        details.hostility = MonsterDetails.Hostility.AGGRESSIVE;
        details.hostilitySightDistance = 20;
        details.armour = MonsterDetails.Armour.NONE;
        details.life = 50;
        details.attack = MonsterDetails.Attack.MELEE;
        details.attackSpeed = 100;
        details.attackDamage = 50;
        details.attackRange = 10;
        details.metalReward = new Distribution(0, 0);
        details.glowReward = new Distribution[] {};
        details.coinReward = new Distribution(25, 35);
        details.experienceReward = 50;
        details.modelData = ModelData.ModelType.RHINO.modelData;
        return details;
    }

    public static Behavior createBehavior(Monster monster, Human human, CubeInstancedFaces cubeInstancedFaces, MonsterDetails details) {
        Motion motion = createMotion(monster, human, details);
        Attack attack = createAttack(monster, human, cubeInstancedFaces, details);
        return createBehavior(monster, human, motion, attack, details);
    }

    private static Behavior createBehavior(Monster monster, Human human, Motion motion, Attack attack, MonsterDetails details) {
        switch (details.hostility) {
            case RUNAWAY:
                return new RunawayBehavior(monster, human, motion, attack, details.hostilitySightDistance, details.hostilityDangerDistance);
            case RETALIATE:
                return new RetaliateBehavior(monster, human, motion, attack);
            case AGGRESSIVE:
                return new AggressiveBehavior(monster, human, motion, attack, details.hostilitySightDistance);
            default:
                throw new RuntimeException("monster hostility type not caught in MonsterGenerator.baseMotion");
        }
    }

    private static Motion createMotion(Monster monster, Human human, MonsterDetails details) {
        Motion motion = new Motion(monster);
        motion.setSpeeds(details.wanderSpeed, details.hostilitySpeed);
        if (details.movement == MonsterDetails.Movement.FLY)
            motion.setFlyHeight(100);
        return motion;
    }

    private static Attack createAttack(Monster monster, Human human, CubeInstancedFaces cubeInstancedFaces, MonsterDetails details) {
        Attack attack = baseAttack(details);
        attack.setBase(monster, human, cubeInstancedFaces);
        attack.setParams(details.attackSpeed, details.attackDamage, details.attackRange, details.attackSize, details.attackAoe);
        return attack;
    }

    private static Attack baseAttack(MonsterDetails details) {
        switch (details.attack) {
            case NONE:
                return new NoneAttack();
            case DEGEN:
                return new DegenAttack();
            case MELEE:
                return new MeleeAttack();
            default:
                throw new RuntimeException("monster attack type not caught in MonsterGenerator.baseAttack");
        }
    }
}
