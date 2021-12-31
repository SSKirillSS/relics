package it.hurts.sskirillss.relics.items;

import it.hurts.sskirillss.relics.configs.data.runes.RuneConfigData;
import it.hurts.sskirillss.relics.configs.data.runes.RuneLootData;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;

public class RuneItem extends Item {
    @Getter
    protected final Color color;

    @Getter
    @Setter
    protected RuneLootData loot;
    @Getter
    @Setter
    protected List<String> ingredients;

    @Getter
    protected RuneConfigData configData;

    public RuneItem(Color color) {
        super(new Item.Properties()
                .tab(RelicsTab.RELICS_TAB)
                .stacksTo(8)
                .rarity(Rarity.UNCOMMON));
        this.color = color;
    }

    public void applyAbility(World world, BlockPos pos) {

    }

    public void setConfig(RuneConfigData data) {
        this.loot = data.getLoot();
        this.ingredients = data.getIngredients();
    }
}