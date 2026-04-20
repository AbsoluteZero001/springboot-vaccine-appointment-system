// Global state
let currentUser = null;
let currentAdmin = null;
const API_BASE = 'http://localhost:8080/api';

// Helper functions
function showAlert(message, type = 'success', containerId = 'alert-container') {
    const container = document.getElementById(containerId);
    if (!container) return;
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;
    container.appendChild(alert);
    setTimeout(() => alert.remove(), 5000);
}

function clearAlerts(containerId = 'alert-container') {
    const container = document.getElementById(containerId);
    if (container) container.innerHTML = '';
}

async function apiCall(endpoint, options = {}) {
    const url = API_BASE + endpoint;
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
        },
    };
    const finalOptions = { ...defaultOptions, ...options };
    try {
        const response = await fetch(url, finalOptions);
        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.error || `HTTP ${response.status}`);
        }
        return data;
    } catch (error) {
        console.error('API call failed:', error);
        throw error;
    }
}

// Auth functions
function setUser(user) {
    currentUser = user;
    localStorage.setItem('user', JSON.stringify(user));
}

function setAdmin(admin) {
    currentAdmin = admin;
    localStorage.setItem('admin', JSON.stringify(admin));
}

function getUser() {
    if (currentUser) return currentUser;
    const stored = localStorage.getItem('user');
    if (stored) {
        currentUser = JSON.parse(stored);
        return currentUser;
    }
    return null;
}

function getAdmin() {
    if (currentAdmin) return currentAdmin;
    const stored = localStorage.getItem('admin');
    if (stored) {
        currentAdmin = JSON.parse(stored);
        return currentAdmin;
    }
    return null;
}

function logout() {
    currentUser = null;
    currentAdmin = null;
    localStorage.removeItem('user');
    localStorage.removeItem('admin');
    window.location.href = '/';
}

function isLoggedIn() {
    return getUser() !== null || getAdmin() !== null;
}

function isUser() {
    return getUser() !== null;
}

function isAdmin() {
    return getAdmin() !== null;
}

// Initialize auth state on page load
document.addEventListener('DOMContentLoaded', () => {
    const user = getUser();
    const admin = getAdmin();
    if (user || admin) {
        // Update UI accordingly
        const authElements = document.querySelectorAll('.auth-only');
        authElements.forEach(el => el.classList.remove('hidden'));
        const guestElements = document.querySelectorAll('.guest-only');
        guestElements.forEach(el => el.classList.add('hidden'));
    }
});

// Tab switching
function switchTab(tabName) {
    document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
    document.querySelector(`.tab[data-tab="${tabName}"]`).classList.add('active');
    document.getElementById(`tab-${tabName}`).classList.add('active');
}