package net.levelz.gui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import net.fabricmc.fabric.api.util.TriState;
import net.levelz.data.LevelLists;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ListGui extends LightweightGuiDescription {

    public ListGui(String name, MinecraftClient client) {
        WPlainPanel root = new WPlainPanel();
        setRootPanel(root);
        root.setSize(200, 215);

        root.add(new WLabel(new TranslatableText("text.levelz.locked_list", StringUtils.capitalize(name))), 6, 7);

        ZWSprite infoIcon = new ZWSprite(name, client, 2);
        root.add(infoIcon, 180, 7, 12, 9);

        WPlainPanel plainPanel = new WPlainPanel();

        WScrollPanel scrollPanel = new WScrollPanel(plainPanel);
        scrollPanel.setScrollingHorizontally(TriState.FALSE);

        List<Integer> levelList = new ArrayList<>();
        List<List<Integer>> ObjectList = new ArrayList<>();
        boolean isBlock = false;
        if (name == "mining") {
            levelList = LevelLists.miningLevelList;
            ObjectList = LevelLists.miningBlockList;
            isBlock = true;
        } else if (name == "alchemy") {
            levelList = LevelLists.brewingLevelList;
            ObjectList = LevelLists.brewingItemList;
        }

        // 9 objects next to each other
        int gridYSpace = 10;
        for (int u = 0; u < levelList.size(); u++) {
            if (ObjectList.get(u).isEmpty()) {
                continue;
            }
            plainPanel.add(new WLabel(new TranslatableText("text.levelz.level", levelList.get(u))), 0, gridYSpace);
            int listSplitter = 0;
            int gridXSpace = 0;
            gridYSpace += 16;

            for (int k = 0; k < ObjectList.get(u).size(); k++) {
                ZWItemSprite zwSprite;
                if (isBlock) {
                    Block block = Registry.BLOCK.get(ObjectList.get(u).get(k));
                    Identifier identifier = Registry.BLOCK.getId(block);
                    zwSprite = new ZWItemSprite(identifier, client);
                    zwSprite.addText(block.getName().getString());
                } else {
                    Item item = Registry.ITEM.get(ObjectList.get(u).get(k));
                    Identifier identifier = Registry.ITEM.getId(item);
                    zwSprite = new ZWItemSprite(identifier, client);
                    zwSprite.addText(item.getName().getString());

                    if (BrewingRecipeRegistry.isValidIngredient(item.getDefaultStack()) && LevelLists.potionList.contains(item)) {
                        int index = LevelLists.potionList.indexOf(item);
                        Potion potion = (Potion) LevelLists.potionList.get(index + 1);
                        ItemStack stack = PotionUtil.setPotion(new ItemStack(Items.POTION), potion);
                        zwSprite.addText("Ingredient for " + new TranslatableText(((PotionItem) PotionUtil.setPotion(stack, potion).getItem()).getTranslationKey(stack)).getString());
                    }

                }
                plainPanel.add(zwSprite, gridXSpace, gridYSpace, 16, 16);
                gridXSpace += 18;
                listSplitter++;
                if (listSplitter % 9 == 0 || k == ObjectList.get(u).size() - 1) {
                    gridYSpace += 18;
                    gridXSpace = 0;
                }
            }
            gridYSpace += 8;
        }
        if (gridYSpace < 200) {
            scrollPanel.setScrollingVertically(TriState.FALSE);
        }
        plainPanel.setSize(200, gridYSpace);

        root.add(scrollPanel, 10, 20, 180, 185);
        root.validate(this);
    }
}
