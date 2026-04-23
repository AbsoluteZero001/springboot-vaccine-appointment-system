// Global state
let currentUser = null;
let currentAdmin = null;
let currentToken = null;
const API_BASE = 'http://localhost:8080/api';

// Loading state
let isLoading = false;

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

function showLoading(show = true, message = 'Loading...') {
    let loader = document.getElementById('global-loader');
    if (!loader && show) {
        loader = document.createElement('div');
        loader.id = 'global-loader';
        loader.className = 'global-loader';
        loader.innerHTML = `
            <div class="loader-overlay"></div>
            <div class="loader-content">
                <div class="loading"></div>
                <p>${message}</p>
            </div>
        `;
        document.body.appendChild(loader);
        // Add styles if not already present
        if (!document.querySelector('#loader-styles')) {
            const style = document.createElement('style');
            style.id = 'loader-styles';
            style.textContent = `
                .global-loader {
                    position: fixed;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                    z-index: 9999;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }
                .loader-overlay {
                    position: absolute;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                    background: rgba(255, 255, 255, 0.85);
                    backdrop-filter: blur(5px);
                }
                .loader-content {
                    position: relative;
                    z-index: 1;
                    background: white;
                    padding: 40px;
                    border-radius: 20px;
                    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
                    text-align: center;
                    min-width: 200px;
                }
                .loader-content .loading {
                    display: inline-block;
                    width: 50px;
                    height: 50px;
                    border: 3px solid #f3f3f3;
                    border-top: 3px solid #4361ee;
                    border-radius: 50%;
                    animation: spin 1s linear infinite;
                    margin-bottom: 20px;
                }
                @keyframes spin {
                    0% { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }
                .loader-content p {
                    margin: 0;
                    color: #333;
                    font-weight: 500;
                    font-size: 16px;
                }
            `;
            document.head.appendChild(style);
        }
    }
    if (loader) {
        loader.style.display = show ? 'flex' : 'none';
    }
    isLoading = show;
}

async function apiCall(endpoint, options = {}) {
    const url = API_BASE + endpoint;
    const token = getAuthToken();
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
        },
    };
    if (token) {
        defaultOptions.headers['Authorization'] = 'Bearer ' + token;
    }
    const finalOptions = { ...defaultOptions, ...options };
    // Merge headers deeply
    if (options.headers) {
        finalOptions.headers = { ...defaultOptions.headers, ...options.headers };
    }

    // Show loading for non-GET requests or longer operations
    const showLoader = options.method && options.method !== 'GET';
    if (showLoader) {
        showLoading(true, 'Processing...');
    }

    try {
        const response = await fetch(url, finalOptions);

        // Check if response is JSON
        const contentType = response.headers.get('content-type');
        let data;
        if (contentType && contentType.includes('application/json')) {
            data = await response.json();
        } else {
            data = await response.text();
        }

        if (!response.ok) {
            throw new Error(typeof data === 'object' ? (data.error || `HTTP ${response.status}`) : `HTTP ${response.status}`);
        }
        return data;
    } catch (error) {
        console.error('API call failed:', error);
        showAlert(error.message || 'Request failed', 'error');
        throw error;
    } finally {
        if (showLoader) {
            setTimeout(() => showLoading(false), 300);
        }
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

function setAuthToken(token) {
    currentToken = token;
    localStorage.setItem('accessToken', token);
}

function getAuthToken() {
    if (currentToken) return currentToken;
    const stored = localStorage.getItem('accessToken');
    if (stored) {
        currentToken = stored;
        return currentToken;
    }
    return null;
}

function clearAuth() {
    currentUser = null;
    currentAdmin = null;
    currentToken = null;
    localStorage.removeItem('user');
    localStorage.removeItem('admin');
    localStorage.removeItem('accessToken');
}

function logout() {
    clearAuth();
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