import cover1 from "./cover1.png";
import cover2 from "./cover2.png";
import cover3 from "./cover3.png";

const covers = [cover1, cover2, cover3];

// Placeholder covers, a future feature will allow uploads
// Modulo used to keep the image choice deterministic (there may be more sheets than portraits, id % length allows to scroll the list)
export function coverFor(campaignId) {
    return covers[campaignId % covers.length];
}