package item.gear;

import character.Crafting;
import character.Equipment;
import item.Item;
import util.math.MathNumbers;
import util.math.MathRandom;

public class Gear extends Item {
    public static final int GEAR_MAX_PROPERTIES = 7;
    private static final int MIN_ENCHANTABILITY = 10;

    private Property.PropertyType[] primaryProperties, secondaryProperties;

    private int enchantability;
    private Property[] properties;
    private int numProperties;

    Gear(int id, Property.PropertyType[] primaryProperties, Property.PropertyType[] secondaryProperties) {
        super(id, MathRandom.randomString(), false);
        this.primaryProperties = primaryProperties;
        this.secondaryProperties = secondaryProperties;
        enchantability = 100;
        properties = new Property[GEAR_MAX_PROPERTIES];
    }

    public static Gear create(Equipment.GearType gearType) {
        switch (gearType) {
            case BODY:
                return new Body();
            case HELMET:
                return new Helmet();
            case GLOVE:
                return new Glove();
            case BOOT:
                return new Boot();
            case WEAPON:
                return new Weapon();
            case MODULE:
                return new Module();
            default:
                throw new RuntimeException("gear type not caught in Gear.create");
        }
    }

    public void addProperty(Property property) {
        properties[numProperties++] = property;
    }

    public void removeProperties(int keep) {
        numProperties = keep;
    }

    public Crafting.Source getPropertySource(int index) {
        return index < numProperties ? properties[index].propertyType.source : null;
    }

    public void decreaseEnchantability(int amount) {
        enchantability = MathNumbers.max(enchantability - amount, MIN_ENCHANTABILITY);
    }

    public int getEnchantability() {
        return enchantability;
    }

    public int getEquipmentBonus(Property.PropertyType propertyType) {
        int sum = 0;
        for (int i = 0; i < numProperties; i++)
            if (properties[i].isOfType(propertyType))
                sum += properties[i].value();
        return sum;
    }

    public int getNumProperties() {
        return numProperties;
    }

    public String getPropertyText(int property) {
        return property < numProperties ? properties[property].getText() : "";
    }

    public Property.PropertyType getPrimaryProperty(Crafting.Source source) {
        return primaryProperties[source.value];
    }

    public Property.PropertyType getSecondaryProperty(Crafting.Source source) {
        return secondaryProperties[source.value];
    }

    public Property.PropertyType getRandomPrimaryProperty() {
        return primaryProperties[MathRandom.random(0, primaryProperties.length)];
    }

    public Property.PropertyType getRandomSecondaryProperty() {
        return secondaryProperties[MathRandom.random(0, secondaryProperties.length)];
    }

    public static boolean isGear(int id) {
        for (Equipment.GearType gearType : Equipment.getGearTypes())
            if (gearType.gearId == id)
                return true;
        return false;
    }

    public static boolean isModule(int id) {
        return Equipment.GearType.MODULE.gearId == id;
    }
}