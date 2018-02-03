package character.monster.attack;

import character.Human;
import character.Monster;

public class MonsterAttack {
    Monster monster;
    Human human;

    float attackSpeed, attackDamage, attackRange, attackSize, attackAoe;

    MonsterAttack() {
    }

    public void update() {
    }

    public void setBase(Monster monster, Human human) {
        this.monster = monster;
        this.human = human;
    }

    public void setParams(float attackSpeed, float attackDamage, float attackRange, float attackSize, float attackAoe) {
        this.attackSpeed = attackSpeed;
        this.attackDamage = attackDamage;
        this.attackRange = attackRange;
        this.attackSize = attackSize;
        this.attackAoe = attackAoe;
    }
}