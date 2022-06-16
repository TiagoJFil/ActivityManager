let _isRendering = false;
let _currentHash = "";

export function renderContent(mainContent, content) {
    if (Array.isArray(content)) {
        mainContent.replaceChildren(...content)
    } else {
        mainContent.replaceChildren(content)
    }
}

export function isRendering() {
    return _isRendering;
}

export function setIsRendering(value) {
    _isRendering = value
    _currentHash = window.location.hash
}

export function previousHash() {
    return _currentHash
}

export function forceReloadHash() {
    const currentHash = window.location.hash
    window.location.hash = ""
    window.location.hash = currentHash
}
