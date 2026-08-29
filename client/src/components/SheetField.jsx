import {useState} from "react";
import "./SheetView.css";

export default function SheetField({label, value, onChange, type = "text", derived = false}) {
    const readOnly = derived || !onChange;
    const isNumber = type === "number";

    // What is being typed, kept as text
    const [draft, setDraft] = useState(null);

    function handleChange(event) {
        const text = event.target.value;

        if (!isNumber) {
            onChange(text);
            return;
        }

        setDraft(text);

        // An empty or half typed field leaves the sheet untouched until it parses
        const parsed = Number(text);
        if (text !== "" && Number.isFinite(parsed)) onChange(parsed);
    }

    // Leaving an empty field restores the last valid number instead of zeroing a stat
    function handleBlur() {
        setDraft(null);
    }

    return (
        <label className={`sheet-field ${derived ? "is-derived" : ""}`}>
            <span>{label}</span>
            <input
                type={type}
                value={draft ?? value ?? ""}
                readOnly={readOnly}
                onChange={readOnly ? undefined : handleChange}
                onBlur={isNumber ? handleBlur : undefined}
            />
        </label>
    )
}
