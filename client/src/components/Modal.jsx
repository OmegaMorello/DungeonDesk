import {useEffect} from "react";
import "./Modal.css";

export default function Modal({title, onClose, children}) {

    useEffect(() => {
        // Closing the modal with esc key
        function handleKey(event) {
            if (event.key === "Escape") onClose();
        }

        // Listener on window because the modal is not focused
        window.addEventListener("keydown", handleKey);
        return () => window.removeEventListener("keydown", handleKey);
    }, []);

    return (
        <div className="modal-backdrop" onClick={onClose}>
            <div className="modal"
                 onClick={(event) => event.stopPropagation()}> {/*Avoid closing the modal if clicking in it*/}
                <header className="modal-bar">
                    <h2>{title}</h2>
                    <button className="modal-close" onClick={onClose}>X</button>
                </header>

                <div className="modal-body">{children}</div>
            </div>
        </div>
    )

}