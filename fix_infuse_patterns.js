
const fs = require("fs");
const path = require("path");

const basePath = "src/main/resources/data/tiered_chests/recipe";

fs.readdirSync(basePath).forEach(file => {
  if (file.includes("infuse") && file.endsWith(".json")) {
    const filePath = path.join(basePath, file);
    const content = JSON.parse(fs.readFileSync(filePath, "utf-8"));
    
    // Change pattern from [" S ", " C ", " S "] to ["S", "C", "S"]
    if (content.pattern) {
      content.pattern = content.pattern.map(row => row.trim());
    }
    
    fs.writeFileSync(filePath, JSON.stringify(content, null, 2));
  }
});

console.log("Infuse patterns fixed.");

