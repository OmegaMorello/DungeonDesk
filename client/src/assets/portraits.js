import p1 from "./portrait1.png";
import p2 from "./portrait2.png";
import p3 from "./portrait3.png";

const portraits = [p1, p2, p3];

// Placeholder portraits, a future feature will allow uploads
// Modulo used to keep the image choice deterministic (there may be more sheets than portraits, id % length allows to scroll the list)
export function portraitFor(sheetId) {
    return portraits[sheetId % portraits.length];
}