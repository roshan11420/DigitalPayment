const API_BASE = '/api';

function setToken(token) {
    localStorage.setItem('digital-wallet-token', token);
}

function getToken() {
    return localStorage.getItem('digital-wallet-token');
}

function clearToken() {
    localStorage.removeItem('digital-wallet-token');
}

function authHeaders() {
    const token = getToken();
    return {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
    };
}

function formatCurrency(value) {
    const amount = Number(value || 0);
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        maximumFractionDigits: 2
    }).format(amount);
}

function showAlert(elementId, type, message) {
    const alertBox = document.getElementById(elementId);
    if (!alertBox) return;
    alertBox.className = `alert ${type}`;
    alertBox.textContent = message;
}

function logout() {
    clearToken();
    window.location.href = '/login.html';
}
