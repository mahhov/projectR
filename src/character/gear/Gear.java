package character.gear;

import character.Equipment;
import item.Item;

public class Gear extends Item {
    public static final int GEAR_MAX_PROPERTIES = 7;
    private static final float SOURCE_LEVEL_VALUE_BONUS = 10;
    private static final float PRIMARY_ENCHANT_SECOND_TIER_VALUE_BONUS = .5f;
    private static final float SECONDARY_ENCHANT_ADDITIONAL_GLOW_VALUE_BONUS = .1f;
    private static final float ENCHANT_VALUE_MULTIPLIER = 1;

    private float enchantability;
    private Property[] properties;
    private int numProperties;

    Gear(int id) {
        super(id, "", false);
        enchantability = 100;
        properties = new Property[GEAR_MAX_PROPERTIES];
    }

    public void addProperty(Property property) {
        properties[numProperties++] = property;
    }

    public int getEquipmentBonus(Equipment.PropertyType propertyType) {
        int sum = 0;
        for (int i = 0; i < numProperties; i++)
            if (properties[i].isOfType(propertyType))
                sum += properties[i].value();
        return sum;
    }

    @Override
    public String getText() {
        return "juba"; // todo placeholder
    }

    public String getText(int property) {
        return property < numProperties ? properties[property].getText() : "";
    }
}