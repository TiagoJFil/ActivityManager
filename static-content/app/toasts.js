

export function ErrorToast(message) {
    return Toastify({
        text: message,
        backgroundColor: "linear-gradient(to right, #ff6c6c, #f66262)",
        oldestFirst: false,
    })     
}

export function SuccessToast(message) {
    return Toastify({
        text: message,
        backgroundColor: "linear-gradient(to right, #6eff99, #66ff66)",
        oldestFirst: false,
    })     
}

export function InfoToast(message) {
    return Toastify({
        text: message,
        backgroundColor: "linear-gradient(to right, #66b3ff, #6699ff)",
        oldestFirst: false,
    })     
}

