package yuuria.stackupper.configlibrary;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class Constant {
    public static final HashMap<Item, Property> ItemCollection = new HashMap<Item, Property>();

    public static final ArrayList<Property.RegexProperty> UnprocessedCollection = new ArrayList<>();

    public static final ArrayList<File> FilesArray = new ArrayList<>();

    private static Function<ResourceLocation, Item> BuiltinRegistriesGetter;
    public static void setBuiltinRegistriesGetter(Function<ResourceLocation, Item> func) { Constant.BuiltinRegistriesGetter = func; }
    public static Item getItemFromBuiltinRegistries(ResourceLocation resourceLocation) { return Constant.BuiltinRegistriesGetter.apply(resourceLocation); }
}
