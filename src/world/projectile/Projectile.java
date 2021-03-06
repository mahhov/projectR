package world.projectile;

import shape.CubeInstancedFaces;
import util.intersection.Intersection;
import util.intersection.IntersectionHitter;
import util.math.MathNumbers;
import util.math.MathRandom;
import world.World;
import world.WorldElement;
import world.particle.TrailParticle;

public class Projectile implements WorldElement {
    private static final int WORLD_ELEMENT_ID = 2;

    private static final float[] COLOR = new float[] {0, 0, 1, 1};

    private static final float SIZE = .3f, AREA = 3, SPEED = 3, DAMAGE = 10;
    private static final float AIR_FRICTION = 1f, GRAVITY = 0f;

    private float x, y, z;
    private float vx, vy, vz;

    private IntersectionHitter intersectionHitter;
    private CubeInstancedFaces cubeInstancedFaces;

    public Projectile(float x, float y, float z, float vx, float vy, float vz) {
        this.x = x;
        this.y = y;
        this.z = z;

        float[] v = MathNumbers.setMagnitude(vx, vy, vz, SPEED);
        this.vx = v[0];
        this.vy = v[1];
        this.vz = v[2];
    }

    public void connectWorld(World world, IntersectionHitter intersectionHitter, CubeInstancedFaces cubeInstancedFaces) {
        this.intersectionHitter = intersectionHitter;
        this.cubeInstancedFaces = cubeInstancedFaces;
    }

    @Override
    public boolean update(World world) {
        vx *= AIR_FRICTION;
        vy *= AIR_FRICTION;
        vz = (vz - GRAVITY) * AIR_FRICTION;

        float t = MathRandom.random(0, 1f);
        world.addParticle(new TrailParticle(x + vx * t, y + vy * t, z + vz * t));

        Intersection intersection = intersectionHitter.find(new float[] {x, y, z}, new float[] {vx, vy, vz}, MathNumbers.magnitude(vx, vy, vz), SIZE);

        x = intersection.coordinate.getX();
        y = intersection.coordinate.getY();
        z = intersection.coordinate.getZ();

        if (intersection.hitElement != null)
            intersection.hitElement.takeDamage(DAMAGE);
        else if (intersection.grounded) {
            WorldElement hit = world.hit(x, y, z, AREA);
            if (hit != null)
                hit.takeDamage(DAMAGE);
        } else
            return false;
        return true;
    }

    @Override
    public void takeDamage(float amount) {
    }

    @Override
    public void draw() {
        cubeInstancedFaces.add(x, z, -y, 0, 0, SIZE, COLOR);
    }

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
        return 0; // todo compute based on vx/vy on construction
    }

    @Override
    public float getSize() {
        return 1;
    }

    @Override
    public int getId() {
        return WORLD_ELEMENT_ID;
    }
}
