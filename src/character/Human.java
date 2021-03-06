package character;

import camera.Follow;
import control.*;
import item.Item;
import item.gear.Gear;
import item.gear.Module;
import shape.CubeInstancedFaces;
import util.intersection.Intersection;
import util.intersection.IntersectionMover;
import util.intersection.IntersectionPicker;
import util.math.MathAngles;
import util.math.MathNumbers;
import world.World;
import world.WorldElement;
import world.particle.JetParticle;
import world.projectile.Projectile;

public class Human implements WorldElement, Follow {
    private static final int WORLD_ELEMENT_ID = 0;

    private static final float ROTATE_SPEED_MOUSE = .008f;
    private static final float[] COLOR = new float[] {1, 1, 1, 1};

    // mobility constants
    private static final float FRICTION = 0.8f, AIR_FRICTION = 0.97f, GRAVITY = .1f, JUMP_MULT = 1;
    private static final float RUN_ACC = .07f, JUMP_ACC = .1f, AIR_ACC = .02f, JET_ACC = .11f, BOOST_ACC = .07f, GLIDE_ACC = .05f, GLIDE_DESCENT_ACC = .02f;
    private static final float STAMINA = 20, STAMINA_REGEN = .1f, STAMINA_RESERVE = 150, STAMINA_RESERVE_REGEN = .05f;
    private static final float LIFE = 100, LIFE_REGEN = .1f, SHIELD = 100, SHIELD_REGEN = 1, SHIELD_REGEN_DELAY = 75;
    private static final float VJET_PLUS = .002f, VJET_MINUS = .001f, VJET_JUMP = .1f;

    private Log log;
    private Stats stats;
    private static final int EXPERIENCE_PER_LEVEL = 100;
    private Experience experience;
    private Inventory inventory;
    private Equipment equipment;
    private Glows glows;
    private Crafting crafting;
    private ModuleCrafting moduleCrafting;
    private Forge forge;

    private Stamina stamina;
    private Health health;
    private static final int BOOST_COOLDOWN = 60, BOOST_DURATION = 20, THROW_COOLDOWN = 15;
    private AbilityTimer boostTimer, throwTimer;

    // position
    private static final float SIZE = 1;
    private float x, y, z;
    private float vx, vy, vz, vjet;
    private float theta, thetaZ;
    private float[] norm, right;
    private boolean air;
    private boolean zoom;

    private IntersectionMover intersectionMover;
    private IntersectionPicker intersectionPicker;
    private WorldElement pickElement;

    private CubeInstancedFaces cubeInstancedFaces;

    // controls
    private KeyControl keyControl;
    private MousePosControl mousePosControl;
    private MouseButtonControl mouseButtonControl;

    public Human(float x, float y, float z, float theta, float thetaZ, IntersectionMover intersectionMover, IntersectionPicker intersectionPicker, KeyControl keyControl, MousePosControl mousePosControl, MouseButtonControl mouseButtonControl) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.theta = theta;
        this.thetaZ = thetaZ;
        norm = new float[2];
        right = new float[2];
        air = true;

        log = new Log();
        stats = new Stats(RUN_ACC, JUMP_ACC, AIR_ACC, JET_ACC, BOOST_ACC, GLIDE_ACC, GLIDE_DESCENT_ACC, STAMINA, STAMINA_REGEN, STAMINA_RESERVE, STAMINA_RESERVE_REGEN, LIFE, LIFE_REGEN, SHIELD, SHIELD_REGEN, SHIELD_REGEN_DELAY);
        experience = new Experience(EXPERIENCE_PER_LEVEL, stats);
        equipment = new Equipment(stats);
        stats.setFactors(experience, equipment);
        inventory = new Inventory(16, log);
        glows = new Glows();
        crafting = new Crafting(log, inventory, glows);
        moduleCrafting = new ModuleCrafting(log, inventory, glows);
        forge = new Forge(log, inventory);

        stamina = new Stamina(stats);
        health = new Health(stats);
        boostTimer = new AbilityTimer(BOOST_COOLDOWN, BOOST_DURATION);
        throwTimer = new AbilityTimer(THROW_COOLDOWN, 1);

        this.intersectionMover = intersectionMover;
        this.intersectionPicker = intersectionPicker;

        cubeInstancedFaces = new CubeInstancedFaces();

        this.keyControl = keyControl;
        this.mousePosControl = mousePosControl;
        this.mouseButtonControl = mouseButtonControl;
    }

    @Override
    public boolean update(World world) {
        boolean shiftPress = keyControl.isKeyPressed(KeyButton.KEY_SHIFT);
        zoom ^= mousePosControl.isLocked() && mouseButtonControl.isMousePressed(MouseButton.SECONDARY);

        stamina.regen();
        health.regen();

        doRotations(mousePosControl);
        computeAxis();
        doRunningMove(keyControl);

        if (keyControl.isKeyPressed(KeyButton.KEY_SPACE))
            doJump();
        doJet(world, keyControl.isKeyDown(KeyButton.KEY_SPACE));

        doBoost(shiftPress);

        vz -= GRAVITY;
        doFriction();

        applyVelocity();

        Intersection pick = intersectionPicker.find();
        pickElement = pick.hitElement;

        doThrow(mousePosControl.isLocked() && mouseButtonControl.isMouseDown(MouseButton.PRIMARY), world, pick);

        return false;
    }

    private void doRotations(MousePosControl mousePosControl) {
        theta -= ROTATE_SPEED_MOUSE * mousePosControl.getMoveX();
        thetaZ = MathNumbers.minMax(thetaZ - ROTATE_SPEED_MOUSE * mousePosControl.getMoveY(), -MathAngles.MAX_THETA_Z, MathAngles.MAX_THETA_Z);
    }

    private void computeAxis() {
        MathAngles.norm(theta, norm);
        right[0] = norm[1];
        right[1] = -norm[0];
    }

    private void doRunningMove(KeyControl keyControl) {
        float acc;
        if (boostTimer.active())
            acc = stats.getStat(Stats.StatType.BOOST_ACC);
        else if (!air)
            acc = stats.getStat(Stats.StatType.RUN_ACC);
        else if (keyControl.isKeyDown(KeyButton.KEY_SHIFT) && stamina.available(Stamina.StaminaCost.GLIDE)) {
            stamina.deplete(Stamina.StaminaCost.GLIDE);
            acc = stats.getStat(Stats.StatType.GLIDE_ACC);
            vz -= stats.getStat(Stats.StatType.GLIDE_DESCENT_ACC);
        } else
            acc = stats.getStat(Stats.StatType.AIR_ACC);

        float dx = 0, dy = 0;

        if (keyControl.isKeyDown(KeyButton.KEY_W)) {
            dx += norm[0];
            dy += norm[1];
        }

        if (keyControl.isKeyDown(KeyButton.KEY_S)) {
            dx -= norm[0];
            dy -= norm[1];
        }

        if (keyControl.isKeyDown(KeyButton.KEY_A)) {
            dx -= right[0];
            dy -= right[1];
        }

        if (keyControl.isKeyDown(KeyButton.KEY_D)) {
            dx += right[0];
            dy += right[1];
        }

        float[] dxy = MathNumbers.setMagnitude(dx, dy, 1);

        vx += dxy[0] * acc;
        vy += dxy[1] * acc;
    }

    private void doJump() {
        Stamina.StaminaCost staminaRequired = air ? Stamina.StaminaCost.AIR_JUMP : Stamina.StaminaCost.JUMP;
        if (!stamina.available(staminaRequired))
            return;
        stamina.deplete(staminaRequired);
        vx *= JUMP_MULT;
        vy *= JUMP_MULT;
        vz += stats.getStat(Stats.StatType.JUMP_ACC);
        vjet = MathNumbers.max(vjet, VJET_JUMP);
        air = true;
    }

    private void doJet(World world, boolean active) {
        if (active && stamina.available(Stamina.StaminaCost.JET)) {
            stamina.deplete(Stamina.StaminaCost.JET);
            vjet = MathNumbers.min(vjet + VJET_PLUS, stats.getStat(Stats.StatType.JET_ACC));
            world.addParticle(new JetParticle(x, y, z));
        } else
            vjet = MathNumbers.max(vjet - VJET_MINUS, 0);

        vz += vjet;
    }

    private void doBoost(boolean shiftPress) {
        boostTimer.update();
        if (shiftPress && boostTimer.ready() && stamina.available(Stamina.StaminaCost.BOOST) && !air) {
            stamina.deplete(Stamina.StaminaCost.BOOST);
            boostTimer.activate();
        }
    }

    private void doFriction() {
        float friction;
        if (!air && !boostTimer.active())
            friction = FRICTION;
        else
            friction = AIR_FRICTION;

        vx *= friction;
        vy *= friction;
        vz *= friction;
    }

    private void applyVelocity() {
        Intersection intersection = intersectionMover.find(new float[] {x, y, z}, new float[] {vx, vy, vz}, MathNumbers.magnitude(vx, vy, vz), SIZE);
        x = intersection.coordinate.getX();
        y = intersection.coordinate.getY();
        z = intersection.coordinate.getZ();

        if (intersection.grounded) {
            air = false;
            vz = 0;
        }

        if (intersection.collisionX)
            vx = 0;
        if (intersection.collisionY)
            vy = 0;
    }

    private void doThrow(boolean primaryDown, World world, Intersection pick) {
        throwTimer.update();

        if (primaryDown && throwTimer.ready() && stamina.available(Stamina.StaminaCost.THROW)) {
            stamina.deplete(Stamina.StaminaCost.THROW);
            throwTimer.activate();
            float topZ = z + SIZE / 2;
            float dx = pick.coordinate.getX() - x;
            float dy = pick.coordinate.getY() - y;
            float dz = pick.coordinate.getZ() - topZ;
            world.addProjectile(new Projectile(x, y, topZ, dx, dy, dz));
        }
    }

    @Override
    public void takeDamage(float amount) {
        health.deplete(amount);
    }

    public void experienceAdd(int amount) {
        experience.add(amount);
    }

    public void inventoryAdd(Item item) {
        inventory.addWithLog(item);
    }

    public void swapEquipment(int inventoryIndex, int equipmentIndex) {
        Item inventoryItem = inventory.getItem(inventoryIndex);
        Equipment.GearType gearType = Equipment.getGearType(equipmentIndex);
        Gear equipmentGear = equipment.getGear(gearType);

        if (inventoryItem == null) {
            if (equipmentGear != null && inventory.add(equipmentGear))
                equipment.unequip(gearType);
        } else if (inventoryItem.id == gearType.gearId) {
            inventory.put(inventoryIndex, equipmentGear);
            equipment.equip(gearType, (Gear) inventoryItem);
        }
    }

    public void swapEquipmentModule(int inventoryIndex, int moduleIndex) {
        Item inventoryItem = inventory.getItem(inventoryIndex);
        Module equipmentModule = equipment.getModule(moduleIndex);

        if (inventoryItem == null) {
            if (equipmentModule != null && inventory.add(equipmentModule))
                equipment.unequipModule(moduleIndex);
        } else if (Gear.isModule(inventoryItem.id) && equipment.equipModule(moduleIndex, (Module) inventoryItem))
            inventory.put(inventoryIndex, equipmentModule);
    }

    @Override
    public void draw() {
        if (zoom)
            return;
        cubeInstancedFaces.reset();
        cubeInstancedFaces.add(x, z, -y, theta, thetaZ, SIZE, COLOR);
        cubeInstancedFaces.doneAdding();
        cubeInstancedFaces.draw();
    }

    // camera getters

    @Override
    public float getFollowX() {
        return x;
    }

    @Override
    public float getFollowY() {
        return z + SIZE / 2;
    }

    @Override
    public float getFollowZ() {
        return -y;
    }

    @Override
    public float getFollowTheta() {
        return theta;
    }

    @Override
    public float getFollowThetaZ() {
        return thetaZ;
    }

    @Override
    public float[] getFollowNorm() {
        float thetaZCos = MathAngles.cos(thetaZ);
        return new float[] {norm[0] * thetaZCos, MathAngles.sin(thetaZ), -norm[1] * thetaZCos};
    }

    @Override
    public boolean isFollowZoom() {
        return zoom;
    }

    // world getters

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getZ() {
        return z;
    }

    @Override
    public float getTheta() {
        return theta;
    }

    @Override
    public float getSize() {
        return SIZE;
    }

    @Override
    public int getId() {
        return WORLD_ELEMENT_ID;
    }

    // ui getters

    public float getStaminaPercent() {
        return stamina.percent();
    }

    public float getStaminaReservePercent() {
        return stamina.percentReserve();
    }

    public float getLifePercent() {
        return health.percentLife();
    }

    public float getShieldPercent() {
        return health.percentShield();
    }

    public int getExperienceLevel() {
        return experience.level();
    }

    public float getExperiencePercent() {
        return experience.percent();
    }

    public Log getLog() {
        return log;
    }

    public Stats getStats() {
        return stats;
    }

    public Experience getExperience() {
        return experience;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public Glows getGlows() {
        return glows;
    }

    public Crafting getCrafting() {
        return crafting;
    }

    public ModuleCrafting getModuleCrafting() {
        return moduleCrafting;
    }

    public Forge getForge() {
        return forge;
    }

    public WorldElement getPickElement() {
        return pickElement;
    }

    // other getters

    public boolean isDangerous() {
        return throwTimer.justActiveated();
    }
}