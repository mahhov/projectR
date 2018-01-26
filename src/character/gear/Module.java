package character.gear;

import character.Crafting;

public class Module extends Gear {
    public static final int MODULE_MAX_PROPERTIES = 6;

    private static final Property.PropertyType[] PRIMARY_PROPERTIES = new Property.PropertyType[4];

    static {
        PRIMARY_PROPERTIES[Crafting.Source.EARTH.value] = Property.PropertyType.ATTACK_POWER;
        PRIMARY_PROPERTIES[Crafting.Source.FIRE.value] = Property.PropertyType.ATTACK_SPEED;
        PRIMARY_PROPERTIES[Crafting.Source.WATER.value] = Property.PropertyType.ACCURACY;
        PRIMARY_PROPERTIES[Crafting.Source.AIR.value] = Property.PropertyType.RANGE;
    }

    public static final int ID = 2;

    Module() {
        super(ID, PRIMARY_PROPERTIES, null);
    }

    @Override
    public String getText() {
        return "im a module";
    }
}