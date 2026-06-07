
const fs = require("fs");
const path = require("path");

const basePath = "src/main/resources/data/tiered_chests/recipe";

fs.readdirSync(basePath).forEach(file => {
  if (file.includes("duplication")) {
    const filePath = path.join(basePath, file);
    const content = JSON.parse(fs.readFileSync(filePath, "utf-8"));
    
    // Change type to standard crafting
    content.type = "minecraft:crafting_shaped";
    
    fs.writeFileSync(filePath, JSON.stringify(content, null, 2));
  }
});

console.log("Duplication recipes fixed.");

