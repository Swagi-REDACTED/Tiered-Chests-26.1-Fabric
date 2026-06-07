
const fs = require("fs");
const path = require("path");

const basePath = "src/main/resources/data/tiered_chests/recipe";

const tiers = [
  { name: "copper", ingredient: "minecraft:copper_ingot" },
  { name: "iron", ingredient: "minecraft:iron_ingot" },
  { name: "golden", ingredient: "minecraft:gold_ingot" },
  { name: "diamond", ingredient: "minecraft:diamond" },
  { name: "netherite", ingredient: "minecraft:netherite_ingot" }
];

const types = ["chest", "barrel"];

// 1. Add Left and Right infuse recipes for tiers
tiers.forEach(tier => {
  types.forEach(type => {
    const leftRecipe = {
      type: "tiered_chests:upgrade",
      pattern: [
        "S  ",
        "C  ",
        "S  "
      ],
      key: {
        "S": "minecraft:shulker_shell",
        "C": `tiered_chests:${tier.name}_${type}`
      },
      result: {
        id: `tiered_chests:shulker_infused_${tier.name}_${type}`
      }
    };
    
    const rightRecipe = {
      type: "tiered_chests:upgrade",
      pattern: [
        "  S",
        "  C",
        "  S"
      ],
      key: {
        "S": "minecraft:shulker_shell",
        "C": `tiered_chests:${tier.name}_${type}`
      },
      result: {
        id: `tiered_chests:shulker_infused_${tier.name}_${type}`
      }
    };

    fs.writeFileSync(path.join(basePath, `shulker_infused_${tier.name}_${type}_infuse_left.json`), JSON.stringify(leftRecipe, null, 2));
    fs.writeFileSync(path.join(basePath, `shulker_infused_${tier.name}_${type}_infuse_right.json`), JSON.stringify(rightRecipe, null, 2));
  });
});

// 2. Fix Netherite upgrade recipes
types.forEach(type => {
  const netheriteUpgrade = {
    type: "tiered_chests:upgrade",
    pattern: [
      "DND",
      "NCN",
      "DND"
    ],
    key: {
      "D": "minecraft:diamond",
      "N": "minecraft:netherite_ingot",
      "C": `tiered_chests:shulker_infused_diamond_${type}`
    },
    result: {
      id: `tiered_chests:shulker_infused_netherite_${type}`
    }
  };
  fs.writeFileSync(path.join(basePath, `shulker_infused_netherite_${type}_upgrade.json`), JSON.stringify(netheriteUpgrade, null, 2));
});

// 3. Add Duplication recipes for all shulker infused variants (including Wood)
const allTiers = [{name: "wood", ingredient: "#minecraft:planks"}, ...tiers];

allTiers.forEach(tier => {
  types.forEach(type => {
    let dupeRecipe;
    
    if (tier.name === "netherite") {
      dupeRecipe = {
        type: "tiered_chests:upgrade",
        pattern: [
          "DSD",
          "NCN",
          "DSD"
        ],
        key: {
          "D": "minecraft:diamond",
          "N": "minecraft:netherite_ingot",
          "S": "minecraft:shulker_shell",
          "C": `tiered_chests:shulker_infused_netherite_${type}`
        },
        result: {
          count: 2,
          id: `tiered_chests:shulker_infused_netherite_${type}`
        }
      };
    } else {
      dupeRecipe = {
        type: "tiered_chests:upgrade",
        pattern: [
          "MSM",
          "MCM",
          "MSM"
        ],
        key: {
          "M": tier.ingredient,
          "S": "minecraft:shulker_shell",
          "C": `tiered_chests:shulker_infused_${tier.name}_${type}`
        },
        result: {
          count: 2,
          id: `tiered_chests:shulker_infused_${tier.name}_${type}`
        }
      };
    }
    
    fs.writeFileSync(path.join(basePath, `shulker_infused_${tier.name}_${type}_duplication.json`), JSON.stringify(dupeRecipe, null, 2));
  });
});

console.log("Recipes generated.");

