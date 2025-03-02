package yuuria.stackupper.configlibrary;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import yuuria.stackupper.configlibrary.ast.AssignOperator;
import yuuria.stackupper.configlibrary.ast.CompareOperator;

import java.util.regex.Pattern;

public class Property {
    public String modId = "";
    public String itemId = "";
    public AssignOperator assignOperator;
    public Number assignedBy;
    public ItemStack itemStack;
    public Number origStackSize;

    public Property(String modId, String itemId, AssignOperator assignOperator, Number assignedBy, ItemStack itemStack) {
        this.modId = modId;
        this.itemId = itemId;
        this.itemStack = itemStack;
        this.assignOperator = assignOperator;
        this.assignedBy = assignedBy;

        this.origStackSize = itemStack.getMaxStackSize();
    }
    public Property(AssignOperator assignOperator, Number assignedBy, ItemStack itemStack) {
        this.itemStack = itemStack;
        this.assignOperator = assignOperator;
        this.assignedBy = assignedBy;

        this.origStackSize = itemStack.getMaxStackSize();
    }

    @Override
    public String toString()
    {
        return String.format("Property{modId=%s, itemId=%s, itemStack=%s }", modId, itemId, itemStack.toString());
    }

    public static class RegexProperty {
        public String regexString;
        public AssignOperator assignOperator;
        public Number assignedSize;
        public boolean isTag = false;
        public boolean isTagRegex = false;
        public boolean isSize = false;
        public Number comparedSize;
        public CompareOperator compareOperator;

        public RegexProperty(String regexString, AssignOperator assignOperator, Number assignedSize)
        {
            this.assignedSize =  assignedSize;
            this.regexString = regexString;
            this.assignOperator = assignOperator;
        }

        public RegexProperty(String regexString, AssignOperator assignOperator, Number assignedSize, Boolean isTag)
        {
            this.assignedSize =  assignedSize;
            this.regexString = regexString;
            this.assignOperator = assignOperator;
            this.isTag = isTag;
        }
        public RegexProperty(String regexString, AssignOperator assignOperator, Number assignedSize, Boolean isTag, Boolean isTagRegex)
        {
            this.assignedSize =  assignedSize;
            this.regexString = regexString;
            this.assignOperator = assignOperator;
            this.isTag = isTag;
            this.isTagRegex = isTagRegex;
        }

        public RegexProperty(AssignOperator assignOperator, CompareOperator compareOperator, Number assignedSize, Number comparedSize)
        {
            this.assignedSize =  assignedSize;
            this.assignOperator = assignOperator;
            this.comparedSize = comparedSize;
            this.compareOperator = compareOperator;
            this.isSize = true;
        }
    }

    public static void processRegexProperty(RegexProperty regexProperty)
    {
        ConfigLibrary.logger.info("processRegex info: isSize={}, comparedSize={}, isTag={}, regexString={}, assignOperator={}, compareOperator={}", regexProperty.isSize, regexProperty.comparedSize, regexProperty.isTag, regexProperty.regexString, regexProperty.assignOperator, regexProperty.compareOperator);
        BuiltInRegistries.ITEM.stream()
                .filter(i -> !i.equals(Items.AIR))
                .forEach(item -> {
                    ItemStack itemStack = new ItemStack(item);

                    if (regexProperty.isSize && regexProperty.compareOperator.test(regexProperty.comparedSize, itemStack.getMaxStackSize())) {
                        Constant.ItemCollection.put(
                                item,
                                new Property(
                                        regexProperty.assignOperator,
                                        regexProperty.assignedSize,
                                        itemStack
                                )
                        );
                    }

                    if (regexProperty.isTag) {
                        if (regexProperty.isTagRegex)  {
                            itemStack.getTags().forEach(tag -> {
                                if (tag.location().toString().matches(regexProperty.regexString)) {
                                    Constant.ItemCollection.put(
                                            item,
                                            new Property(
                                                    regexProperty.assignOperator,
                                                    regexProperty.assignedSize,
                                                    itemStack
                                            )
                                    );
                                }
                            });
                            return;
                        }

                        itemStack.getTags().forEach(tag -> {
                            if (tag.equals(new TagKey<>(Registries.ITEM, ResourceLocation.parse(regexProperty.regexString)))) {
                                Constant.ItemCollection.put(
                                        item,
                                        new Property(
                                                regexProperty.assignOperator,
                                                regexProperty.assignedSize,
                                                itemStack
                                        )
                                );
                            }
                        });
                    }

                    if (!regexProperty.isTag && !regexProperty.isSize) {
                        if (!Pattern.matches(regexProperty.regexString, item.toString())) return;
                        Constant.ItemCollection.put(
                                item,
                                new Property(
                                        regexProperty.assignOperator,
                                        regexProperty.assignedSize,
                                        itemStack
                                )
                        );
                    }
                });
    }
}