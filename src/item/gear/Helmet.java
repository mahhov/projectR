package item.gear;

import character.Crafting;

public class Helmet extends Gear {
    private static final Property.PropertyType[] PRIMARY_PROPERTIES = new Property.PropertyType[4];
    private static final Property.PropertyType[] SECONDARY_PROPERTIES = new Property.PropertyType[4];

    static {
        PRIMARY_PROPERTIES[Crafting.Source.EARTH.value] = Property.PropertyType.HEALTH_LIFE;
        PRIMARY_PROPERTIES[Crafting.Source.FIRE.value] = Property.PropertyType.HEALTH_LIFE_REGEN;
        PRIMARY_PROPERTIES[Crafting.Source.WATER.value] = Property.PropertyType.HEALTH_SHIELD;
        PRIMARY_PROPERTIES[Crafting.Source.AIR.value] = Property.PropertyType.HEALTH_SHIELD_REGEN;

        SECONDARY_PROPERTIES[Crafting.Source.EARTH.value] = Property.PropertyType.STAMINA_STAMINA;
        SECONDARY_PROPERTIES[Crafting.Source.FIRE.value] = Property.PropertyType.STAMINA_STAMINA_REGEN;
        SECONDARY_PROPERTIES[Crafting.Source.WATER.value] = Property.PropertyType.STAMINA_RESERVE;
        SECONDARY_PROPERTIES[Crafting.Source.AIR.value] = Property.PropertyType.STAMINA_RESERVE_REGEN;
    }

    public static final int ID = 3;

    Helmet() {
        super(ID, PRIMARY_PROPERTIES, SECONDARY_PROPERTIES);
    }
}