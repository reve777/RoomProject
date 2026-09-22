/**
 * Modern Hotel Booking Frontend Controller (REST API Client)
 */
const API_BASE = '/api';

const app = {
    state: {
        token: localStorage.getItem('token') || null,
        user: JSON.parse(localStorage.getItem('user') || 'null'),
        currentView: 'roomsView',
        rooms: [],
        filteredRooms: [],
        currentRegion: 'ALL',
        currentRoom: null,
        diningRestaurants: [],
        tickets: [],
        shopProducts: [],
        filteredShopProducts: [],
        currentShopCategory: 'ALL',
        shopCart: JSON.parse(localStorage.getItem('shopCart') || '[]'),
        temp2faToken: null,
        currentGoogleAuthUrl: null,
        currentLineQrSessionId: null,
        currentLineAuthUrl: null,
        linePollingInterval: null,
        isSubmittingBooking: false,
        isSubmittingDining: false,
        isSubmittingTicket: false,
        isSubmittingShopOrder: false,
        isSubmittingLogin: false,
        isSubmittingRegister: false,
        isSubmittingOtp: false,
        isSubmitting2FA: false,
        appliedDiscount: 0,
        promoDiscountRate: 0,
        selectedTicket: null
    },

    // --- Helper utilities for room models ---
    getRoomCoverImage(room) {
        if (!room) return 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800';
        if (room.primaryImageUrl && typeof room.primaryImageUrl === 'string') {
            return room.primaryImageUrl;
        }
        if (room.images && room.images.length > 0) {
            const first = room.images[0];
            if (typeof first === 'string') return first;
            if (first && typeof first === 'object' && first.imageUrl) return first.imageUrl;
        }
        return 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800';
    },

    getRoomImageUrls(room) {
        if (!room) return ['https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800'];
        if (!room.images || room.images.length === 0) {
            if (room.primaryImageUrl) return [room.primaryImageUrl];
            return ['https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800'];
        }
        return room.images.map(img => {
            if (typeof img === 'string') return img;
            if (img && typeof img === 'object' && img.imageUrl) return img.imageUrl;
            return '';
        }).filter(url => url.length > 0);
    },

    getRoomPrice(room) {
        if (!room) return 0;
        if (room.pricePerNight !== undefined && room.pricePerNight !== null) {
            return room.pricePerNight;
        }
        return room.price || 0;
    },

    async init() {
        await this.loadComponents();
        this.updateAuthUI();
        this.updateShopCartUI();
        this.checkOAuthCallback();
        this.checkUrlAuthParams();
        await this.loadRooms();
    },

    checkUrlAuthParams() {
        try {
            const params = new URLSearchParams(window.location.search);
            const view = params.get('view');
            const action = params.get('action');
            if (view) {
                setTimeout(() => this.switchView(view), 200);
            } else if (action === 'login' || params.get('login') === '1') {
                setTimeout(() => this.showModal('loginModal'), 300);
            } else if (action === 'register' || params.get('register') === '1') {
                setTimeout(() => this.switchView('registerView'), 300);
            } else if (action === 'guest') {
                setTimeout(() => this.handleGuestLogin(), 300);
            }
        } catch (e) {
            console.error('URL Auth Params error:', e);
        }
    },

    quickFillLogin(username, password) {
        const u = document.getElementById('loginUsername');
        const p = document.getElementById('loginPassword');
        if (u) u.value = username;
        if (p) p.value = password;
    },

    async loadComponents() {
        try {
            const [headerHtml, menuHtml, footerHtml] = await Promise.all([
                fetch('/components/header.html').then(r => r.text()),
                fetch('/components/menu.html').then(r => r.text()),
                fetch('/components/footer.html').then(r => r.text())
            ]);

            document.getElementById('header-placeholder').innerHTML = headerHtml;
            document.getElementById('menu-placeholder').innerHTML = menuHtml;
            document.getElementById('footer-placeholder').innerHTML = footerHtml;
            this.updateAuthUI();
            this.syncSidebarMode(this.state.currentView);
        } catch (e) {
            console.error('載入組件失敗:', e);
        }
    },

    updateAuthUI() {
        const guestSection = document.getElementById('authGuestSection');
        const userSection = document.getElementById('authUserSection');
        const greeting = document.getElementById('userGreeting');
        const adminElements = document.querySelectorAll('.admin-only');
        const guestElements = document.querySelectorAll('.guest-only');

        if (this.state.token && this.state.user) {
            if (guestSection) guestSection.classList.add('d-none');
            if (userSection) userSection.classList.remove('d-none');
            if (greeting) greeting.innerText = `歡迎, ${this.state.user.fullName || this.state.user.username}`;
            guestElements.forEach(el => el.classList.add('d-none'));

            const isAdmin = this.state.user.roles && (
                Array.isArray(this.state.user.roles) 
                    ? this.state.user.roles.includes('ROLE_ADMIN') 
                    : (typeof this.state.user.roles === 'string' && this.state.user.roles.includes('ROLE_ADMIN'))
            );

            adminElements.forEach(el => {
                if (isAdmin) el.classList.remove('d-none');
                else el.classList.add('d-none');
            });
        } else {
            if (guestSection) guestSection.classList.remove('d-none');
            if (userSection) userSection.classList.add('d-none');
            guestElements.forEach(el => el.classList.remove('d-none'));
            adminElements.forEach(el => el.classList.add('d-none'));
        }
    },

    async fetchApi(endpoint, options = {}) {
        const headers = {
            'Content-Type': 'application/json',
            ...(this.state.token ? { 'Authorization': `Bearer ${this.state.token}` } : {}),
            ...options.headers
        };

        if (options.body instanceof FormData) {
            delete headers['Content-Type'];
        }

        const response = await fetch(`${API_BASE}${endpoint}`, {
            ...options,
            headers
        });

        if (response.status === 401) {
            if (this.state.token) {
                console.warn('Session expired or invalid, clearing authentication.');
                this.state.token = null;
                this.state.user = null;
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                this.updateAuthUI();
            }
        }

        const data = await response.json().catch(() => ({}));
        if (!response.ok) {
            throw new Error(data.message || '請求發生錯誤');
        }
        return data;
    },

    // --- Navigation & View Switching ---

    syncSidebarMode(viewId) {
        const navBookingBtn = document.getElementById('headerNavBookingBtn');
        const navShopBtn = document.getElementById('headerNavShopBtn');

        if (viewId === 'shopView') {
            if (navBookingBtn) {
                navBookingBtn.style.background = 'transparent';
                navBookingBtn.style.color = '#475569';
            }
            if (navShopBtn) {
                navShopBtn.style.background = '#2563eb';
                navShopBtn.style.color = '#ffffff';
            }
            this.syncSidebarShopCategory(this.state.currentShopCategory);
        } else {
            if (navBookingBtn) {
                navBookingBtn.style.background = '#2563eb';
                navBookingBtn.style.color = '#ffffff';
            }
            if (navShopBtn) {
                navShopBtn.style.background = 'transparent';
                navShopBtn.style.color = '#475569';
            }
        }
    },

    switchView(viewId) {
        document.querySelectorAll('.view-section').forEach(el => el.classList.add('d-none'));
        const targetView = document.getElementById(viewId);
        if (targetView) targetView.classList.remove('d-none');

        this.syncSidebarMode(viewId);

        document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
        const menuMap = {
            'roomsView': 'roomsMenuItem',
            'shopView': 'shopMenuItem',
            'diningView': 'diningMenuItem',
            'ticketsView': 'ticketsMenuItem',
            'registerView': 'registerMenuItem',
            'myBookingsView': 'myBookingsMenuItem',
            'profileView': 'profileMenuItem',
            'adminRoomsView': 'adminRoomsMenuItem',
            'adminUsersView': 'adminUsersMenuItem',
            'adminBookingsView': 'adminBookingsMenuItem'
        };

        const activeMenuItem = document.getElementById(menuMap[viewId]);
        if (activeMenuItem) activeMenuItem.classList.add('active');

        this.state.currentView = viewId;

        if (viewId === 'roomsView') this.loadRooms();
        if (viewId === 'shopView') this.loadShop();
        if (viewId === 'diningView') this.loadDining();
        if (viewId === 'ticketsView') this.loadTickets();
        if (viewId === 'myBookingsView') this.loadMyBookings();
        if (viewId === 'profileView') this.loadProfile();
        if (viewId === 'adminRoomsView') this.loadAdminRooms();
        if (viewId === 'adminUsersView') this.loadAdminUsers();
        if (viewId === 'adminBookingsView') this.loadAdminOverview();
    },

    showModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) modal.classList.add('show');
    },

    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) modal.classList.remove('show');

        if (modalId === 'lineQrModal') {
            this.stopLineQrPolling();
        }
    },

    // --- Authentication ---

    async handleLogin(e) {
        if (e) e.preventDefault();
        if (this.state.isSubmittingLogin) return;

        const usernameOrEmail = document.getElementById('loginUsername').value.trim();
        const password = document.getElementById('loginPassword').value;

        if (!usernameOrEmail || !password) {
            alert('請輸入帳號或電子信箱與密碼！');
            return;
        }

        const form = document.querySelector('#loginModal form');
        const submitBtn = form ? form.querySelector('button[type="submit"]') : null;
        this.state.isSubmittingLogin = true;
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerText = '登入中...';
        }

        try {
            const res = await this.fetchApi('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ usernameOrEmail, password })
            });

            if (res.data.requires2FA) {
                this.state.temp2faToken = res.data.temporaryToken;
                this.closeModal('loginModal');
                this.showModal('twoFactorVerifyModal');
                return;
            }

            this.loginSuccess(res.data);
            this.closeModal('loginModal');
            alert(`歡迎回來，${res.data.user.fullName || res.data.user.username}！`);
        } catch (err) {
            alert('登入失敗: ' + err.message);
        } finally {
            this.state.isSubmittingLogin = false;
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerText = '登入';
            }
        }
    },

    async handleGuestLogin() {
        try {
            const res = await this.fetchApi('/auth/guest-login', { method: 'POST' });
            this.loginSuccess(res.data);
            this.closeModal('loginModal');
            alert('訪客體驗登入成功！您現在可以預訂房型、美饌與各項休閒票券。');
        } catch (err) {
            alert('訪客登入失敗: ' + err.message);
        }
    },

    showGoogleMailOtpModal() {
        this.closeModal('loginModal');
        const statusMsg = document.getElementById('otpStatusMsg');
        if (statusMsg) statusMsg.innerText = '';
        this.showModal('googleMailOtpModal');
    },

    async handleSendGoogleMailOtp() {
        const emailInput = document.getElementById('otpEmailInput');
        const btnSend = document.getElementById('btnSendOtp');
        const statusMsg = document.getElementById('otpStatusMsg');
        const email = emailInput?.value?.trim();

        if (!email) {
            alert('請先輸入 Google Mail 信箱地址！');
            emailInput?.focus();
            return;
        }

        btnSend.disabled = true;
        btnSend.innerText = '發送中...';
        if (statusMsg) {
            statusMsg.innerText = '正在向 ' + email + ' 發送 6 位數驗證碼...';
            statusMsg.style.color = '#2563eb';
        }

        try {
            const res = await this.fetchApi('/auth/email-otp/send', {
                method: 'POST',
                body: JSON.stringify({ email })
            });

            if (statusMsg) {
                statusMsg.innerText = '✅ 驗證碼已成功寄出！請至信箱查收並於 5 分鐘內輸入。';
                statusMsg.style.color = '#10b981';
            }
            alert('驗證碼已寄出至: ' + email + '\n' + (res.data?.message || '請查收信箱！'));
            document.getElementById('otpCodeInput')?.focus();
        } catch (err) {
            if (statusMsg) {
                statusMsg.innerText = '❌ 發送失敗: ' + err.message;
                statusMsg.style.color = '#ef4444';
            }
            alert('驗證碼發送失敗: ' + err.message);
        } finally {
            btnSend.disabled = false;
            btnSend.innerText = '重新發送';
        }
    },

    async handleVerifyGoogleMailOtp(e) {
        if (e) e.preventDefault();
        if (this.state.isSubmittingOtp) return;

        const email = document.getElementById('otpEmailInput')?.value?.trim();
        const otp = document.getElementById('otpCodeInput')?.value?.trim();

        if (!email || !otp) {
            alert('請輸入完整 Email 與 6 位數驗證碼！');
            return;
        }

        const form = document.querySelector('#googleMailOtpModal form');
        const submitBtn = form ? form.querySelector('button[type="submit"]') : null;
        this.state.isSubmittingOtp = true;
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerText = '驗證中...';
        }

        try {
            const res = await this.fetchApi('/auth/email-otp/verify', {
                method: 'POST',
                body: JSON.stringify({ email, otp })
            });

            this.loginSuccess(res.data);
            this.closeModal('googleMailOtpModal');
            alert('Google Mail 信箱驗證成功，歡迎登入！');
        } catch (err) {
            alert('驗證失敗: ' + err.message);
        } finally {
            this.state.isSubmittingOtp = false;
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerText = '驗證並登入';
            }
        }
    },

    async showLineQrCodeModal() {
        this.closeModal('loginModal');
        this.showModal('lineQrModal');

        const qrImg = document.getElementById('lineQrImg');
        const statusText = document.getElementById('lineQrStatusText');
        if (qrImg) qrImg.src = 'https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=https://access.line.me/oauth2/v2.1/authorize';
        if (statusText) {
            statusText.innerText = '🟢 等待手機端掃描授權中...';
            statusText.style.color = '#059669';
        }

        try {
            const res = await this.fetchApi('/auth/line/login-session', { method: 'POST' });
            if (res.data) {
                this.state.currentLineQrSessionId = res.data.sessionId;
                this.state.currentLineAuthUrl = res.data.authUrl;
                if (qrImg && res.data.qrCodeDataUrl) {
                    qrImg.src = res.data.qrCodeDataUrl;
                }
                this.startLineQrPolling(res.data.sessionId);
            }
        } catch (e) {
            console.error('取得 LINE QR Session 失敗:', e);
        }
    },

    openLineAuthUrl() {
        if (this.state.currentLineAuthUrl) {
            window.open(this.state.currentLineAuthUrl, '_blank');
        } else {
            alert('目前無法取得 LINE 授權網址');
        }
    },

    startLineQrPolling(sessionId) {
        this.stopLineQrPolling();
        this.state.linePollingInterval = setInterval(async () => {
            try {
                const res = await this.fetchApi(`/auth/line/check-session?sessionId=${sessionId}`);
                if (res.data && res.data.authenticated) {
                    this.stopLineQrPolling();
                    this.loginSuccess(res.data);
                    this.closeModal('lineQrModal');
                    alert('LINE 掃碼授權登入成功！歡迎使用。');
                }
            } catch (e) {
                console.error('Polling LINE status error:', e);
            }
        }, 2000);
    },

    stopLineQrPolling() {
        if (this.state.linePollingInterval) {
            clearInterval(this.state.linePollingInterval);
            this.state.linePollingInterval = null;
        }
    },

    async handleGoogleOAuthLogin() {
        try {
            const res = await this.fetchApi('/auth/oauth2/google/url');
            if (res.data && res.data.authUrl) {
                this.state.currentGoogleAuthUrl = res.data.authUrl;
                const display = document.getElementById('googleAuthUrlDisplay');
                if (display) display.innerText = res.data.authUrl;
                this.closeModal('loginModal');
                this.showModal('googleAuthUrlModal');
            }
        } catch (err) {
            alert('無法獲取 Google 授權網址: ' + err.message);
        }
    },

    redirectToGoogleAuth() {
        if (this.state.currentGoogleAuthUrl) {
            window.location.href = this.state.currentGoogleAuthUrl;
        }
    },

    async checkOAuthCallback() {
        const urlParams = new URLSearchParams(window.location.search);
        const code = urlParams.get('code');
        const state = urlParams.get('state');

        if (code) {
            window.history.replaceState({}, document.title, window.location.pathname);
            try {
                let res;
                if (state && state.startsWith('line_')) {
                    res = await this.fetchApi(`/auth/line/callback?code=${code}&state=${state}`);
                } else {
                    res = await this.fetchApi(`/auth/oauth2/google/callback?code=${code}`);
                }

                if (res.data && res.data.token) {
                    this.loginSuccess(res.data);
                    alert('第三方授權登入成功！');
                }
            } catch (err) {
                console.error('OAuth callback failed:', err);
                alert('第三方登入授權失敗: ' + err.message);
            }
        }
    },

    toggleAdminKeyField() {
        const role = document.querySelector('input[name="regRole"]:checked')?.value;
        const group = document.getElementById('adminKeyGroup') || document.getElementById('adminSecretKeyGroup');
        if (group) {
            if (role === 'ROLE_ADMIN') {
                group.classList.remove('d-none');
            } else {
                group.classList.add('d-none');
            }
        }
    },

    checkPasswordMatch() {
        const p1 = document.getElementById('pageRegPassword')?.value;
        const p2 = document.getElementById('pageRegPasswordConfirm')?.value;
        const msg = document.getElementById('pwdMatchMsg');
        if (!msg) return;
        if (!p2) { msg.innerText = ''; return; }
        if (p1 === p2) {
            msg.innerText = '✅ 密碼一致';
            msg.style.color = '#10b981';
        } else {
            msg.innerText = '❌ 兩次輸入的密碼不一致';
            msg.style.color = '#ef4444';
        }
    },

    async handlePageRegister(e) {
        if (e) e.preventDefault();
        if (this.state.isSubmittingRegister) return;

        const username = document.getElementById('pageRegUsername').value.trim();
        const email = document.getElementById('pageRegEmail').value.trim();
        const password = document.getElementById('pageRegPassword').value;
        const passwordConfirm = document.getElementById('pageRegPasswordConfirm').value;
        const fullName = document.getElementById('pageRegFullName').value.trim();
        const phone = document.getElementById('pageRegPhone').value.trim();
        const selectedRole = document.querySelector('input[name="regRole"]:checked')?.value || 'ROLE_USER';
        const adminSecretKey = document.getElementById('pageRegAdminSecretKey')?.value?.trim();

        if (password !== passwordConfirm) {
            alert('兩次輸入的密碼不一致，請重新檢查！');
            return;
        }

        const payload = {
            username,
            email,
            password,
            fullName: fullName || username,
            phone,
            roles: [selectedRole],
            adminSecretKey: selectedRole === 'ROLE_ADMIN' ? adminSecretKey : null
        };

        const form = document.querySelector('#registerView form');
        const submitBtn = form ? form.querySelector('button[type="submit"]') : null;
        this.state.isSubmittingRegister = true;
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerText = '註冊中並寄發迎賓信...';
        }

        try {
            const res = await this.fetchApi('/auth/register', {
                method: 'POST',
                body: JSON.stringify(payload)
            });

            this.loginSuccess(res.data);
            alert('🎉 恭喜您註冊成功！系統已發送迎賓信至: ' + email);
            this.switchView('roomsView');
        } catch (err) {
            alert('註冊失敗: ' + err.message);
        } finally {
            this.state.isSubmittingRegister = false;
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerText = '✨ 確認註冊並登入';
            }
        }
    },

    async handle2FAVerify(e) {
        if (e) e.preventDefault();
        if (this.state.isSubmitting2FA) return;

        const code = document.getElementById('twoFactorCode').value.trim();
        const form = document.querySelector('#twoFactorVerifyModal form');
        const submitBtn = form ? form.querySelector('button[type="submit"]') : null;

        this.state.isSubmitting2FA = true;
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerText = '驗證中...';
        }

        try {
            const res = await this.fetchApi('/auth/verify-2fa', {
                method: 'POST',
                body: JSON.stringify({
                    temporaryToken: this.state.temp2faToken,
                    code: code
                })
            });

            this.loginSuccess(res.data);
            this.closeModal('twoFactorVerifyModal');
            alert('2FA 身分驗證成功！');
        } catch (err) {
            alert('2FA 驗證失敗: ' + err.message);
        } finally {
            this.state.isSubmitting2FA = false;
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerText = '驗證並登入';
            }
        }
    },

    loginSuccess(data) {
        this.state.token = data.token;
        this.state.user = data.user;
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(data.user));
        this.updateAuthUI();
    },

    logout() {
        this.state.token = null;
        this.state.user = null;
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        this.updateAuthUI();
        this.switchView('roomsView');
        alert('您已安全登出。');
    },

    // --- 1. Rooms Discovery & Booking ---

    async loadRooms() {
        try {
            const res = await this.fetchApi('/rooms');
            this.state.rooms = res.data || [];
            this.filterRooms();
        } catch (err) {
            console.error('載入房型失敗:', err);
        }
    },

    setRegionFilter(region) {
        this.state.currentRegion = region;
        document.querySelectorAll('.city-tag-btn').forEach(btn => {
            if (btn.id.startsWith('regionBtn-')) btn.classList.remove('active');
        });
        const map = {
            'ALL': 'regionBtn-all',
            'NORTH': 'regionBtn-north',
            'CENTRAL': 'regionBtn-central',
            'SOUTH': 'regionBtn-south',
            'EAST': 'regionBtn-east',
            'ISLANDS': 'regionBtn-islands'
        };
        const activeBtn = document.getElementById(map[region]);
        if (activeBtn) activeBtn.classList.add('active');

        this.filterRooms();
    },

    filterRooms() {
        const keyword = (document.getElementById('roomSearchKeyword')?.value || '').toLowerCase().trim();
        const cityFilter = document.getElementById('roomFilterCity')?.value || '';
        const typeFilter = document.getElementById('roomFilterType')?.value || '';
        const capFilter = parseInt(document.getElementById('roomFilterCapacity')?.value || '0', 10);
        const sortOrder = document.getElementById('roomSortOrder')?.value || 'default';
        const region = this.state.currentRegion;

        const regionMap = {
            'NORTH': ['台北市', '新北市', '基隆市', '桃園市', '新竹市', '新竹縣'],
            'CENTRAL': ['台中市', '苗栗縣', '彰化縣', '南投縣', '雲林縣'],
            'SOUTH': ['高雄市', '台南市', '嘉義市', '嘉義縣', '屏東縣'],
            'EAST': ['宜蘭縣', '花蓮縣', '台東縣'],
            'ISLANDS': ['澎湖縣', '金門縣', '連江縣']
        };

        let list = this.state.rooms.filter(r => {
            if (region !== 'ALL') {
                const allowedCities = regionMap[region] || [];
                if (!allowedCities.some(c => (r.city || '').includes(c))) return false;
            }
            if (cityFilter && !(r.city || '').includes(cityFilter)) return false;
            if (typeFilter && r.roomType !== typeFilter) return false;
            if (capFilter > 0 && r.capacity < capFilter) return false;
            if (keyword) {
                const nameMatch = (r.name || '').toLowerCase().includes(keyword);
                const descMatch = (r.description || '').toLowerCase().includes(keyword);
                const amenMatch = (r.amenities || '').toLowerCase().includes(keyword);
                const cityMatch = (r.city || '').toLowerCase().includes(keyword);
                if (!nameMatch && !descMatch && !amenMatch && !cityMatch) return false;
            }
            return true;
        });

        if (sortOrder === 'price_asc') {
            list.sort((a, b) => this.getRoomPrice(a) - this.getRoomPrice(b));
        } else if (sortOrder === 'price_desc') {
            list.sort((a, b) => this.getRoomPrice(b) - this.getRoomPrice(a));
        }

        this.state.filteredRooms = list;
        this.renderRooms(list);
    },

    renderRooms(rooms) {
        const container = document.getElementById('roomsListContainer');
        if (!container) return;

        if (rooms.length === 0) {
            container.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 48px; color: #64748b;">🔍 查無符合條件的奢華房型，請嘗試調整篩選條件！</div>';
            return;
        }

        container.innerHTML = rooms.map(r => {
            const coverImg = this.getRoomCoverImage(r);
            const price = this.getRoomPrice(r);
            return `
                <div class="room-card" onclick="app.openRoomDetailModal(${r.id})">
                    <div class="room-image-container">
                        <img src="${coverImg}" alt="${r.name}" loading="lazy">
                        <div class="room-badge">${r.roomType || '尊爵套房'}</div>
                    </div>
                    <div class="room-body">
                        <span class="region-badge" style="width: fit-content; margin-bottom: 6px;">📍 ${r.city || '台灣精選'} • 容納 ${r.capacity} 位</span>
                        <div class="room-title">${r.name}</div>
                        <div class="room-details">${(r.amenities || '頂級設施, 私人露台, 景觀浴缸').substring(0, 45)}...</div>
                        <div class="room-price-row">
                            <div>
                                <span class="room-price-unit">每晚尊榮價</span>
                                <div class="room-price">NT$ ${Number(price).toLocaleString()}</div>
                            </div>
                            <button class="btn btn-primary btn-sm" onclick="event.stopPropagation(); app.openRoomDetailModal(${r.id})">
                                立即預訂
                            </button>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    },

    openRoomDetailModal(roomId) {
        const room = this.state.rooms.find(r => r.id === roomId);
        if (!room) return;
        this.state.currentRoom = room;

        const price = this.getRoomPrice(room);
        const images = this.getRoomImageUrls(room);

        document.getElementById('modalRoomTitle').innerText = room.name;
        document.getElementById('modalRoomCity').innerText = `📍 ${room.city || '台灣精選'} | 規格: ${room.roomType}`;
        document.getElementById('modalRoomPrice').innerText = `NT$ ${Number(price).toLocaleString()} / 晚`;
        document.getElementById('modalRoomCapacity').innerText = `最多可容納 ${room.capacity} 位貴賓`;
        document.getElementById('modalRoomAmenities').innerText = room.amenities || '全景觀落地窗, 私人景觀溫泉池, Nespresso 膠囊咖啡機, 席夢思名床, 專屬管家服務';
        document.getElementById('modalRoomDesc').innerText = room.description || '這間豪華套房專為追求頂級品質與放鬆身心的尊貴旅客所設計，提供無微不至的奢華體驗。';

        // Set Main Image and Carousel Thumbnails
        const mainImg = document.getElementById('galleryMainImg');
        const thumbsContainer = document.getElementById('galleryThumbsContainer');

        if (mainImg) mainImg.src = images[0] || 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800';

        if (thumbsContainer) {
            thumbsContainer.innerHTML = images.map((imgUrl, idx) => `
                <img src="${imgUrl}" class="gallery-thumb ${idx === 0 ? 'active' : ''}" onclick="app.setGalleryMainImage('${imgUrl}', this)">
            `).join('');
        }

        // Set Default Booking Dates
        const today = new Date();
        const tomorrow = new Date();
        tomorrow.setDate(today.getDate() + 1);

        document.getElementById('bookCheckIn').value = today.toISOString().split('T')[0];
        document.getElementById('bookCheckOut').value = tomorrow.toISOString().split('T')[0];
        document.getElementById('bookGuests').value = Math.min(2, room.capacity);

        this.calculateBookingTotal();
        this.showModal('roomDetailModal');
    },

    setGalleryMainImage(url, thumbEl) {
        const main = document.getElementById('galleryMainImg');
        if (main) main.src = url;

        document.querySelectorAll('.gallery-thumb').forEach(t => t.classList.remove('active'));
        if (thumbEl) thumbEl.classList.add('active');
    },

    calculateBookingTotal() {
        const inDateVal = document.getElementById('bookCheckIn')?.value;
        const outDateVal = document.getElementById('bookCheckOut')?.value;
        const nightsSpan = document.getElementById('bookSummaryNights');
        const discountSpan = document.getElementById('bookSummaryDiscount');
        const totalSpan = document.getElementById('bookSummaryTotal');

        if (!inDateVal || !outDateVal) return 0;

        const inDate = new Date(inDateVal);
        const outDate = new Date(outDateVal);

        if (outDate <= inDate) {
            if (nightsSpan) nightsSpan.innerText = '0 晚';
            if (totalSpan) totalSpan.innerText = 'NT$ 0';
            return 0;
        }

        const nights = Math.ceil((outDate - inDate) / (1000 * 60 * 60 * 24));
        const roomPrice = this.state.currentRoom ? this.getRoomPrice(this.state.currentRoom) : 0;
        const subtotal = nights * roomPrice;
        const discount = subtotal * this.state.promoDiscountRate;
        const finalTotal = Math.max(0, subtotal - discount);

        if (nightsSpan) nightsSpan.innerText = `${nights} 晚`;
        if (discountSpan) discountSpan.innerText = `- NT$ ${Math.round(discount).toLocaleString()}`;
        if (totalSpan) totalSpan.innerText = `NT$ ${Math.round(finalTotal).toLocaleString()}`;

        return finalTotal;
    },

    applyPromoCode() {
        const code = document.getElementById('bookPromoCode')?.value.trim().toUpperCase();
        if (code === 'LUXURY2026' || code === 'VIP888') {
            this.state.promoDiscountRate = 0.20;
            alert('🎉 促銷折扣碼套用成功！享有 8 折 (20% OFF) 尊榮優惠！');
        } else if (code === 'SUMMER') {
            this.state.promoDiscountRate = 0.10;
            alert('🎉 促銷折扣碼套用成功！享有 9 折優惠！');
        } else {
            this.state.promoDiscountRate = 0;
            alert('❌ 無效的折扣碼');
        }
        this.calculateBookingTotal();
    },

    async handleBookingSubmit(e) {
        if (e) e.preventDefault();
        if (this.state.isSubmittingBooking) return;

        if (!this.state.token) {
            alert('請先登入或註冊會員後再進行預訂！');
            this.closeModal('roomDetailModal');
            this.showModal('loginModal');
            return;
        }

        const checkInDate = document.getElementById('bookCheckIn').value;
        const checkOutDate = document.getElementById('bookCheckOut').value;
        const guests = parseInt(document.getElementById('bookGuests').value, 10);
        const specialRequests = document.getElementById('bookSpecialRequests')?.value || '';

        const payload = {
            roomId: this.state.currentRoom.id,
            checkInDate,
            checkOutDate,
            guests,
            specialRequests
        };

        const btn = document.getElementById('btnSubmitBooking');
        this.state.isSubmittingBooking = true;
        if (btn) {
            btn.disabled = true;
            btn.innerText = '正在處理預訂與寄發確認信...';
        }

        try {
            const res = await this.fetchApi('/bookings', {
                method: 'POST',
                body: JSON.stringify(payload)
            });

            alert('🎉 預訂成功！訂單確認信已發送至您的電子信箱。');
            this.closeModal('roomDetailModal');
            this.switchView('myBookingsView');
        } catch (err) {
            alert('預訂失敗: ' + err.message);
        } finally {
            this.state.isSubmittingBooking = false;
            if (btn) {
                btn.disabled = false;
                btn.innerText = '💳 確認下單並發送確認信件';
            }
        }
    },

    // --- 🛍️ 電商購物商城 (E-Commerce Mall) ---

    async loadShop() {
        try {
            const [productsRes, statsRes] = await Promise.all([
                this.fetchApi('/shop/products'),
                this.fetchApi('/shop/stats').catch(() => null)
            ]);

            this.state.shopProducts = productsRes.data || [];
            if (statsRes && statsRes.data) {
                const statCount = document.getElementById('spaStatProductCount');
                if (statCount) statCount.innerText = `${statsRes.data.totalProducts || this.state.shopProducts.length}+ 款`;
            }

            this.filterShopProducts();
        } catch (err) {
            console.error('載入商城商品失敗:', err);
        }
    },

    filterShopCategory(category) {
        if (this.state.currentView !== 'shopView') {
            this.switchView('shopView');
        }

        this.state.currentShopCategory = category;
        const pills = document.querySelectorAll('#spaCategoryPillsContainer .cat-pill');
        pills.forEach(p => p.classList.remove('active'));

        const map = {
            'ALL': 'spaShopCat-all',
            '全部商品': 'spaShopCat-all',
            '📱 3C 數位旗艦': 'spaShopCat-3c',
            '👗 日韓流行服飾': 'spaShopCat-fashion',
            '💄 專櫃美妝保養': 'spaShopCat-beauty',
            '🏠 智能生活家電': 'spaShopCat-appliances',
            '🍷 產地頂級美饌': 'spaShopCat-gourmet'
        };

        const targetId = map[category] || 'spaShopCat-all';
        const targetBtn = document.getElementById(targetId);
        if (targetBtn) targetBtn.classList.add('active');

        this.syncSidebarShopCategory(category);
        this.filterShopProducts();
    },

    syncSidebarShopCategory(category) {
        const sidebarItems = document.querySelectorAll('#shopCategoriesNavMenu .nav-item');
        sidebarItems.forEach(item => item.classList.remove('active'));

        const sidebarMap = {
            'ALL': 'sidebarShopCat-all',
            '全部商品': 'sidebarShopCat-all',
            '📱 3C 數位旗艦': 'sidebarShopCat-3c',
            '👗 日韓流行服飾': 'sidebarShopCat-fashion',
            '💄 專櫃美妝保養': 'sidebarShopCat-beauty',
            '🏠 智能生活家電': 'sidebarShopCat-appliances',
            '🍷 產地頂級美饌': 'sidebarShopCat-gourmet'
        };

        const targetSidebarId = sidebarMap[category] || 'sidebarShopCat-all';
        const targetSidebarItem = document.getElementById(targetSidebarId);
        if (targetSidebarItem) targetSidebarItem.classList.add('active');
    },

    matchShopCategory(product, cat) {
        if (!cat || cat === 'ALL' || cat === '全部商品') return true;
        const pCat = (product.category || '').toLowerCase();
        const pTitle = (product.title || '').toLowerCase();

        if (cat.includes('3C') || cat.includes('3c')) {
            return pCat.includes('3c') || pCat.includes('smartphones') || pCat.includes('laptops') || pCat.includes('tablets') || pCat.includes('electronics') || pCat.includes('audio') || pCat.includes('gaming') || pCat.includes('camera') || pCat.includes('phone') || pCat.includes('watch') || pCat.includes('monitor') || pTitle.includes('iphone') || pTitle.includes('macbook') || pTitle.includes('asus') || pTitle.includes('sony') || pTitle.includes('switch') || pTitle.includes('ipad') || pTitle.includes('耳機') || pTitle.includes('相機') || pTitle.includes('螢幕');
        }
        if (cat.includes('服飾') || cat.includes('fashion')) {
            return pCat.includes('fashion') || pCat.includes('clothing') || pCat.includes('mens-shirts') || pCat.includes('womens-dresses') || pCat.includes('shoes') || pCat.includes('bags') || pTitle.includes('外套') || pTitle.includes('洋裝') || pTitle.includes('風衣') || pTitle.includes('包') || pTitle.includes('鞋') || pTitle.includes('衣');
        }
        if (cat.includes('美妝') || cat.includes('beauty')) {
            return pCat.includes('beauty') || pCat.includes('skincare') || pCat.includes('fragrance') || pCat.includes('jewelery') || pTitle.includes('精華') || pTitle.includes('乳霜') || pTitle.includes('香水') || pTitle.includes('口紅') || pTitle.includes('面膜') || pTitle.includes('眼影');
        }
        if (cat.includes('家電') || cat.includes('appliances')) {
            return pCat.includes('appliances') || pCat.includes('home-decoration') || pCat.includes('furniture') || pCat.includes('lighting') || pTitle.includes('吸塵器') || pTitle.includes('咖啡機') || pTitle.includes('吹風機') || pTitle.includes('清淨機') || pTitle.includes('水波爐') || pTitle.includes('電風扇');
        }
        if (cat.includes('美饌') || cat.includes('gourmet') || cat.includes('food') || cat.includes('groceries')) {
            return pCat.includes('gourmet') || pCat.includes('groceries') || pCat.includes('food') || pCat.includes('wine') || pTitle.includes('和牛') || pTitle.includes('紅酒') || pTitle.includes('松露') || pTitle.includes('干貝') || pTitle.includes('魚子醬') || pTitle.includes('火腿') || pTitle.includes('茶') || pTitle.includes('抹茶');
        }
        return pCat.includes(cat.toLowerCase()) || pTitle.includes(cat.toLowerCase());
    },

    filterShopProducts() {
        const keyword = (document.getElementById('spaShopSearchInput')?.value || '').toLowerCase().trim();
        const sortVal = document.getElementById('spaShopSortSelect')?.value || 'default';
        const sourceVal = document.getElementById('spaShopSourceFilter')?.value || 'ALL';
        const cat = this.state.currentShopCategory;

        let list = this.state.shopProducts.filter(p => {
            if (!this.matchShopCategory(p, cat)) return false;
            if (sourceVal !== 'ALL' && p.source !== sourceVal) return false;
            if (keyword) {
                const mTitle = (p.title || '').toLowerCase().includes(keyword);
                const mDesc = (p.description || '').toLowerCase().includes(keyword);
                const mCat = (p.category || '').toLowerCase().includes(keyword);
                if (!mTitle && !mDesc && !mCat) return false;
            }
            return true;
        });

        if (sortVal === 'price_asc') {
            list.sort((a, b) => (a.price || 0) - (b.price || 0));
        } else if (sortVal === 'price_desc') {
            list.sort((a, b) => (b.price || 0) - (a.price || 0));
        } else if (sortVal === 'rating') {
            list.sort((a, b) => (b.rating || 0) - (a.rating || 0));
        }

        this.state.filteredShopProducts = list;
        this.renderShopProducts(list);
    },

    renderShopProducts(products) {
        const container = document.getElementById('spaProductGrid');
        if (!container) return;

        if (products.length === 0) {
            container.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 60px 20px; color: #64748b; font-size: 1.1rem;">🔍 抱歉，查無符合條件的熱銷商品，請切換搜尋分類或關鍵字！</div>';
            return;
        }

        container.innerHTML = products.map(p => {
            const img = p.image || 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600';
            const price = Number(p.price || 0).toLocaleString();
            const origPrice = Number(p.originalPrice || (p.price * 1.25)).toLocaleString();
            const badgeBg = p.source === 'MOMO' ? '#ec4899' : (p.source === 'SHOPEE' ? '#f97316' : '#2563eb');
            const sourceName = p.source === 'MOMO' ? 'MOMO 直營' : (p.source === 'SHOPEE' ? '蝦皮旗艦' : '國際選物');

            return `
                <div class="product-card">
                    <div class="product-img-box">
                        <img src="${img}" alt="${p.title}" loading="lazy">
                        <div class="discount-tag" style="background:${badgeBg};">${sourceName}</div>
                    </div>
                    <div class="product-info">
                        <div class="product-cat">${p.category || '精選推薦'}</div>
                        <div class="product-name" title="${p.title}">${p.title}</div>
                        <div style="font-size: 0.8rem; color: #f59e0b; margin-bottom: 6px;">
                            ⭐ ${p.rating ? p.rating.toFixed(1) : '4.9'} (${p.ratingCount || 120}+ 已售出)
                        </div>
                        <div class="product-price-row">
                            <div>
                                <span style="font-size: 0.75rem; color: #94a3b8; text-decoration: line-through;">NT$ ${origPrice}</span>
                                <div style="font-size: 1.25rem; font-weight: 800; color: #dc2626;">NT$ ${price}</div>
                            </div>
                            <button class="btn btn-primary btn-sm" onclick="app.addToShopCart('${p.id}', event)">
                                <i class="fa-solid fa-cart-plus"></i> 加入
                            </button>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    },

    addToShopCart(productId, event) {
        const prod = this.state.shopProducts.find(p => String(p.id) === String(productId));
        if (!prod) return;

        const existing = this.state.shopCart.find(item => String(item.id) === String(productId));
        if (existing) {
            existing.qty = (existing.qty || 1) + 1;
        } else {
            this.state.shopCart.push({
                id: prod.id,
                title: prod.title,
                price: prod.price,
                image: prod.image,
                source: prod.source,
                qty: 1
            });
        }

        localStorage.setItem('shopCart', JSON.stringify(this.state.shopCart));
        this.updateShopCartUI();

        // 觸發全套浮誇特效與情緒價值反饋
        this.triggerEmotionalCartEffect(prod, event);
    },

    // --- ✨ 浮誇特效 & 極致情緒價值系統 ---

    triggerEmotionalCartEffect(prod, event) {
        // 1. 播放輕脆奢華金幣音效
        this.playLuxuryChime();

        // 2. 按鈕微互動回饋
        const targetBtn = event ? (event.currentTarget || event.target) : null;
        if (targetBtn) {
            const originalHtml = targetBtn.innerHTML;
            targetBtn.classList.add('btn-cart-success-fx');
            targetBtn.innerHTML = '<i class="fa-solid fa-heart"></i> 💖 搶到了！ +1';
            setTimeout(() => {
                targetBtn.classList.remove('btn-cart-success-fx');
                targetBtn.innerHTML = originalHtml;
            }, 1200);
        }

        // 3. 取得點擊座標或目標按鈕位置
        let startX = window.innerWidth / 2;
        let startY = window.innerHeight / 2;
        if (event && event.clientX && event.clientY) {
            startX = event.clientX;
            startY = event.clientY;
        } else if (targetBtn) {
            const rect = targetBtn.getBoundingClientRect();
            startX = rect.left + rect.width / 2;
            startY = rect.top + rect.height / 2;
        }

        // 4. 發射周圍彩帶與愛心寶石粒子
        this.burstParticles(startX, startY);

        // 5. 拋物線飛入購物車動畫
        this.flyProductToCart(prod, startX, startY);

        // 6. 浮誇高情緒價值土豪彈窗
        this.showEmotionalToast(prod);
    },

    playLuxuryChime() {
        try {
            const AudioCtx = window.AudioContext || window.webkitAudioContext;
            if (!AudioCtx) return;
            const ctx = new AudioCtx();
            const now = ctx.currentTime;

            const osc1 = ctx.createOscillator();
            const osc2 = ctx.createOscillator();
            const gain = ctx.createGain();

            osc1.type = 'sine';
            osc1.frequency.setValueAtTime(587.33, now); // D5
            osc1.frequency.exponentialRampToValueAtTime(880, now + 0.08); // A5
            osc1.frequency.exponentialRampToValueAtTime(1174.66, now + 0.18); // D6
            osc1.frequency.exponentialRampToValueAtTime(1760, now + 0.28); // A6

            osc2.type = 'triangle';
            osc2.frequency.setValueAtTime(880, now);
            osc2.frequency.exponentialRampToValueAtTime(1318.51, now + 0.12);
            osc2.frequency.exponentialRampToValueAtTime(2093, now + 0.28); // C7

            gain.gain.setValueAtTime(0.2, now);
            gain.gain.exponentialRampToValueAtTime(0.001, now + 0.45);

            osc1.connect(gain);
            osc2.connect(gain);
            gain.connect(ctx.destination);

            osc1.start(now);
            osc2.start(now);
            osc1.stop(now + 0.45);
            osc2.stop(now + 0.45);
        } catch (e) {
            // Audio context safely ignored if blocked by browser autoplay policy
        }
    },

    burstParticles(x, y) {
        const emojis = ['🎉', '✨', '💖', '💰', '💎', '🔥', '👑', '⭐', '🛍️', '👏', '🎁', '💫'];
        const count = 16;

        for (let i = 0; i < count; i++) {
            const p = document.createElement('div');
            p.className = 'burst-particle';
            p.innerText = emojis[Math.floor(Math.random() * emojis.length)];
            p.style.left = `${x}px`;
            p.style.top = `${y}px`;
            document.body.appendChild(p);

            const angle = (i / count) * 2 * Math.PI + (Math.random() - 0.5);
            const distance = 80 + Math.random() * 90;
            const destX = Math.cos(angle) * distance;
            const destY = Math.sin(angle) * distance - 30;

            requestAnimationFrame(() => {
                p.style.transform = `translate(${destX}px, ${destY}px) scale(${0.8 + Math.random() * 0.8}) rotate(${(Math.random() - 0.5) * 60}deg)`;
                p.style.opacity = '0';
            });

            setTimeout(() => p.remove(), 900);
        }
    },

    flyProductToCart(prod, startX, startY) {
        const cartBtn = document.getElementById('spaCartBadgeCount')?.closest('button') || document.getElementById('headerNavShopBtn');
        let targetX = window.innerWidth - 60;
        let targetY = window.innerHeight - 60;

        if (cartBtn) {
            const rect = cartBtn.getBoundingClientRect();
            targetX = rect.left + rect.width / 2;
            targetY = rect.top + rect.height / 2;
        }

        const flyEl = document.createElement('img');
        flyEl.className = 'fly-to-cart-particle';
        flyEl.src = prod.image || 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=200';
        flyEl.style.left = `${startX - 30}px`;
        flyEl.style.top = `${startY - 30}px`;
        document.body.appendChild(flyEl);

        requestAnimationFrame(() => {
            flyEl.style.left = `${targetX - 20}px`;
            flyEl.style.top = `${targetY - 20}px`;
            flyEl.style.width = '24px';
            flyEl.style.height = '24px';
            flyEl.style.transform = 'scale(0.3) rotate(360deg)';
            flyEl.style.opacity = '0.4';
        });

        setTimeout(() => {
            flyEl.remove();
            this.triggerCartImpact(cartBtn);
        }, 750);
    },

    triggerCartImpact(cartBtn) {
        const target = cartBtn || document.getElementById('spaCartBadgeCount');
        if (!target) return;

        const shockwave = document.createElement('div');
        shockwave.className = 'cart-shockwave-fx';
        target.style.position = 'relative';
        target.appendChild(shockwave);
        setTimeout(() => shockwave.remove(), 800);

        target.classList.remove('cart-shake-fx');
        void target.offsetWidth;
        target.classList.add('cart-shake-fx');
        setTimeout(() => target.classList.remove('cart-shake-fx'), 700);

        const badge = document.getElementById('spaCartBadgeCount');
        if (badge) {
            badge.style.transform = 'scale(1.8)';
            setTimeout(() => badge.style.transform = 'scale(1)', 250);
        }
    },

    showEmotionalToast(prod) {
        let container = document.getElementById('emotionalToastContainer');
        if (!container) {
            container = document.createElement('div');
            container.id = 'emotionalToastContainer';
            container.className = 'emotional-toast-container';
            document.body.appendChild(container);
        }

        const hypeList = [
            { icon: '👑', title: '神仙品味！太有眼光了！', msg: '這件頂級逸品與您的尊榮氣質簡直是天生一對！' },
            { icon: '💎', title: '全網手速第一！天選之子！', msg: '您以光速搶下爆款，全台購物車都在為您的手速驚呼！' },
            { icon: '🚀', title: '財富自由的霸氣光芒！', msg: '錢沒有不見，只是變成了您最喜歡的閃耀模樣！' },
            { icon: '🔥', title: '極致審美！手慢無神物入袋！', msg: '恭喜成功擊敗 99.8% 競爭者，這波操作直接封神！' },
            { icon: '💖', title: '尊貴 VIP 的神級眼力！', msg: '購物車因裝載了您的心頭好，此刻正幸福地閃閃發光！' },
            { icon: '⭐', title: '購物界的傳奇大師！', msg: '買得如此精準又優雅，這就是傳說中的盛世品味！' },
            { icon: '🎉', title: '極度舒適！犒賞自己的最佳選擇！', msg: '生活已經很辛苦，這件寶貝就是專為您綻放的快樂！' },
            { icon: '💫', title: '審美天花板！連 AI 都折服！', msg: '您的購物車正在散發迷人貴氣，品味領先全球 99.9%！' }
        ];

        const item = hypeList[Math.floor(Math.random() * hypeList.length)];
        const toast = document.createElement('div');
        toast.className = 'emotional-toast';
        toast.innerHTML = `
            <div class="emotional-toast-icon">${item.icon}</div>
            <div class="emotional-toast-body">
                <div class="emotional-toast-title"><span>${item.icon}</span> ${item.title}</div>
                <div class="emotional-toast-msg">${item.msg}</div>
                <div class="emotional-toast-product">🛍️ 成功放入：<strong>${prod.title}</strong> (NT$ ${Number(prod.price).toLocaleString()})</div>
            </div>
        `;

        container.appendChild(toast);

        while (container.children.length > 3) {
            container.removeChild(container.firstChild);
        }

        setTimeout(() => {
            toast.classList.add('hide');
            setTimeout(() => toast.remove(), 400);
        }, 3600);
    },

updateShopCartUI() {
        const badge = document.getElementById('spaCartBadgeCount');
        const totalQty = this.state.shopCart.reduce((sum, item) => sum + (item.qty || 1), 0);
        if (badge) badge.innerText = totalQty;
    },

    openShopCartModal() {
        this.renderShopCartModal();
        this.showModal('spaCartModal');
    },

    closeShopCartModal() {
        this.closeModal('spaCartModal');
    },

    renderShopCartModal() {
        const container = document.getElementById('spaCartItemsList');
        if (!container) return;

        if (this.state.shopCart.length === 0) {
            container.innerHTML = '<div style="text-align: center; padding: 36px 0; color: #94a3b8;">🛒 購物車目前空空如也，快去挑選熱銷商品吧！</div>';
            document.getElementById('spaCartSubtotal').innerText = 'NT$ 0';
            document.getElementById('spaCartShipping').innerText = 'NT$ 0';
            document.getElementById('spaCartTotal').innerText = 'NT$ 0';
            return;
        }

        let subtotal = 0;
        container.innerHTML = this.state.shopCart.map(item => {
            const itemTotal = (item.price || 0) * (item.qty || 1);
            subtotal += itemTotal;
            return `
                <div class="cart-item-row">
                    <img src="${item.image || 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=200'}" class="cart-item-img">
                    <div style="flex: 1; min-width: 0;">
                        <div style="font-size: 0.85rem; font-weight: 700; color: #1e293b; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">${item.title}</div>
                        <div style="font-size: 0.8rem; color: #2563eb; font-weight: 700;">NT$ ${Number(item.price).toLocaleString()}</div>
                    </div>
                    <div style="display: flex; align-items: center; gap: 6px;">
                        <button class="btn btn-outline btn-sm" style="padding: 2px 8px;" onclick="app.changeShopCartQty('${item.id}', -1)">-</button>
                        <span style="font-weight: 700; min-width: 20px; text-align: center;">${item.qty}</span>
                        <button class="btn btn-outline btn-sm" style="padding: 2px 8px;" onclick="app.changeShopCartQty(${item.id}, 1)">+</button>
                    </div>
                </div>
            `;
        }).join('');

        const shipping = subtotal >= 1500 || subtotal === 0 ? 0 : 100;
        const total = subtotal + shipping;

        document.getElementById('spaCartSubtotal').innerText = `NT$ ${subtotal.toLocaleString()}`;
        document.getElementById('spaCartShipping').innerText = shipping === 0 ? '免運費 🎉' : `NT$ ${shipping}`;
        document.getElementById('spaCartTotal').innerText = `NT$ ${total.toLocaleString()}`;
    },

    changeShopCartQty(productId, delta) {
        const item = this.state.shopCart.find(i => String(i.id) === String(productId));
        if (!item) return;

        item.qty = (item.qty || 1) + delta;
        if (item.qty <= 0) {
            this.state.shopCart = this.state.shopCart.filter(i => String(i.id) !== String(productId));
        }

        localStorage.setItem('shopCart', JSON.stringify(this.state.shopCart));
        this.updateShopCartUI();
        this.renderShopCartModal();
    },

    clearShopCart() {
        if (!confirm('確定要清空購物車內的所有商品嗎？')) return;
        this.state.shopCart = [];
        localStorage.removeItem('shopCart');
        this.updateShopCartUI();
        this.renderShopCartModal();
    },

    openShopCheckoutModal() {
        if (this.state.shopCart.length === 0) {
            alert('購物車內尚無任何商品！');
            return;
        }

        this.closeShopCartModal();

        // Autofill user info if logged in
        if (this.state.user) {
            const nameEl = document.getElementById('spaOrderCustName');
            const phoneEl = document.getElementById('spaOrderCustPhone');
            const emailEl = document.getElementById('spaOrderCustEmail');
            if (nameEl) nameEl.value = this.state.user.fullName || this.state.user.username;
            if (phoneEl) phoneEl.value = this.state.user.phone || '';
            if (emailEl) emailEl.value = this.state.user.email || '';
        }

        this.showModal('spaCheckoutModal');
    },

    closeShopCheckoutModal() {
        this.closeModal('spaCheckoutModal');
    },

    async handleShopCheckoutSubmit(e) {
        if (e) e.preventDefault();
        if (this.state.isSubmittingShopOrder) return;

        const customerName = document.getElementById('spaOrderCustName')?.value.trim();
        const customerPhone = document.getElementById('spaOrderCustPhone')?.value.trim();
        const customerEmail = document.getElementById('spaOrderCustEmail')?.value.trim();
        const shippingAddress = document.getElementById('spaOrderAddress')?.value.trim();
        const paymentMethod = document.getElementById('spaOrderPayMethod')?.value || 'LINE_PAY';
        const buyerNotes = document.getElementById('spaOrderNotes')?.value.trim();

        if (!customerName || !customerPhone || !customerEmail || !shippingAddress) {
            alert('請完整填寫收件人姓名、電話、電子信箱及配送地址！');
            return;
        }

        const subtotal = this.state.shopCart.reduce((sum, item) => sum + (Number(item.price) || 0) * (item.qty || 1), 0);
        const shippingFee = (subtotal >= 1500 || subtotal === 0) ? 0 : 100;
        const totalAmount = subtotal + shippingFee;

        const payload = {
            recipientName: customerName,
            customerName: customerName,
            email: customerEmail,
            customerEmail: customerEmail,
            phone: customerPhone,
            customerPhone: customerPhone,
            shippingAddress: shippingAddress,
            paymentMethod: paymentMethod,
            note: buyerNotes || '',
            subtotal: subtotal,
            shippingFee: shippingFee,
            totalAmount: totalAmount,
            items: this.state.shopCart.map(i => ({
                productId: String(i.id),
                title: String(i.title || '精選商品'),
                productTitle: String(i.title || '精選商品'),
                price: Number(i.price || 0),
                unitPrice: Number(i.price || 0),
                quantity: Number(i.qty || 1),
                image: i.image || '',
                productImage: i.image || '',
                source: i.source || 'GLOBAL'
            }))
        };

        const form = document.querySelector('#spaCheckoutModal form');
        const submitBtn = form ? form.querySelector('button[type="submit"]') : null;
        const btnText = document.getElementById('spaBtnSubmitText');
        const spinner = document.getElementById('spaOrderSpinner');

        this.state.isSubmittingShopOrder = true;
        if (submitBtn) submitBtn.disabled = true;
        if (spinner) spinner.classList.remove('d-none');
        if (btnText) btnText.innerText = '正在提交訂單並發送 Email...';

        try {
            const res = await this.fetchApi('/shop/checkout', {
                method: 'POST',
                body: JSON.stringify(payload)
            });

            const order = res.data;
            this.state.shopCart = [];
            localStorage.removeItem('shopCart');
            this.updateShopCartUI();
            this.closeShopCheckoutModal();

            // Display Success Modal
            document.getElementById('spaSuccessOrderNum').innerText = `#${order.orderNumber}`;
            document.getElementById('spaSuccessPayMethod').innerText = order.paymentMethod || paymentMethod;
            document.getElementById('spaSuccessTotal').innerText = `NT$ ${Number(order.totalAmount || totalAmount).toLocaleString()}`;
            document.getElementById('spaSuccessEmail').innerText = order.email || order.customerEmail || customerEmail;
            this.showModal('spaOrderSuccessModal');
        } catch (err) {
            alert('結帳失敗: ' + err.message);
        } finally {
            this.state.isSubmittingShopOrder = false;
            if (submitBtn) submitBtn.disabled = false;
            if (spinner) spinner.classList.add('d-none');
            if (btnText) btnText.innerText = '確認送出訂單並寄發 Email';
        }
    },

closeShopSuccessModal() {
        this.closeModal('spaOrderSuccessModal');
    },

    // --- 2. Fine Dining Module ---

    async loadDining(category = 'ALL') {
        try {
            const res = await this.fetchApi(`/dining/restaurants?category=${category}`);
            this.state.diningRestaurants = res.data || [];
            this.renderDining(this.state.diningRestaurants);
        } catch (e) {
            console.error('載入餐廳失敗:', e);
        }
    },

    filterDining(category) {
        document.querySelectorAll('.city-tag-btn').forEach(btn => {
            if (btn.id.startsWith('diningCat-')) btn.classList.remove('active');
        });
        const map = {
            'ALL': 'diningCat-all',
            '米其林星級': 'diningCat-michelin',
            '頂級懷石': 'diningCat-kaiseki',
            '熟成牛排': 'diningCat-steak',
            '景觀鐵板燒': 'diningCat-teppan',
            '義法美饌': 'diningCat-italian',
            '奢華下午茶': 'diningCat-afternoon'
        };
        const activeBtn = document.getElementById(map[category]);
        if (activeBtn) activeBtn.classList.add('active');
        this.loadDining(category);
    },

    renderDining(restaurants) {
        const container = document.getElementById('diningListContainer');
        if (!container) return;

        if (restaurants.length === 0) {
            container.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 48px; color: #64748b;">暫無符合條件的美食餐廳</div>';
            return;
        }

        container.innerHTML = restaurants.map(r => `
            <div class="dining-card">
                <div class="dining-img-container">
                    <img src="${r.coverImage}" alt="${r.name}" loading="lazy">
                    <div class="dining-badge">⭐ ${r.rating} (${r.reviewCount}+ 評價)</div>
                </div>
                <div class="dining-body">
                    <span class="region-badge" style="width: fit-content; margin-bottom: 6px;">📍 ${r.city} • ${r.category}</span>
                    <h3 style="font-size: 1.15rem; color: #1e293b; margin-bottom: 6px;">${r.name}</h3>
                    <p style="font-size: 0.85rem; color: #64748b; margin-bottom: 8px;">${r.description}</p>
                    <div style="font-size: 0.8rem; color: #b45309; background: #fef3c7; padding: 6px 10px; border-radius: 6px; margin-bottom: 12px;">
                        <strong>✨ 主廚招牌：</strong> ${r.specialties}
                    </div>
                    <div style="display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #f1f5f9; padding-top: 10px;">
                        <div>
                            <span style="font-size: 0.75rem; color: #94a3b8;">預估人均</span>
                            <div style="font-size: 1.1rem; font-weight: 800; color: #059669;">${r.priceRange}</div>
                        </div>
                        <button class="btn btn-primary btn-sm" onclick="app.openDiningBookingModal(${r.id})">🍴 預約席位</button>
                    </div>
                </div>
            </div>
        `).join('');
    },

    openDiningBookingModal(restaurantId) {
        const r = this.state.diningRestaurants.find(item => item.id === restaurantId);
        if (!r) return;

        const idEl = document.getElementById('diningModalRestaurantId') || document.getElementById('diningModalRestId');
        if (idEl) idEl.value = r.id;

        const nameEl = document.getElementById('diningModalRestaurantName') || document.getElementById('diningModalRestName');
        if (nameEl) nameEl.innerText = r.name;

        const catBadge = document.getElementById('diningModalCategoryBadge');
        if (catBadge) catBadge.innerText = r.category;

        const priceBadge = document.getElementById('diningModalPriceRange');
        if (priceBadge) priceBadge.innerText = r.priceRange;

        const locEl = document.getElementById('diningModalLocation') || document.getElementById('diningModalRestDetails');
        if (locEl) locEl.innerText = `📍 ${r.address || r.city} | 📞 ${r.phone || '專屬熱線'}`;

        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        document.getElementById('diningDate').value = tomorrow.toISOString().split('T')[0];

        // Autofill user info if logged in
        if (this.state.user) {
            document.getElementById('diningCustName').value = this.state.user.fullName || this.state.user.username;
            document.getElementById('diningCustPhone').value = this.state.user.phone || '';
            document.getElementById('diningCustEmail').value = this.state.user.email || '';
        }

        this.showModal('diningBookModal');
    },

    async handleDiningSubmit(e) {
        if (e) e.preventDefault();
        if (this.state.isSubmittingDining) return;

        const idEl = document.getElementById('diningModalRestaurantId') || document.getElementById('diningModalRestId');
        const restId = idEl.value;
        const reservationDate = document.getElementById('diningDate').value;
        const timeSlot = document.getElementById('diningTimeSlot').value;
        const partySize = parseInt(document.getElementById('diningPartySize').value, 10);
        const customerName = document.getElementById('diningCustName').value.trim();
        const customerPhone = document.getElementById('diningCustPhone').value.trim();
        const userEmail = document.getElementById('diningCustEmail').value.trim();
        const specialRequests = document.getElementById('diningSpecialRequests').value.trim();

        if (!customerName || !customerPhone || !reservationDate) {
            alert('請填寫完整預約聯絡資訊！');
            return;
        }

        const payload = {
            restaurantId: parseInt(restId, 10),
            reservationDate,
            timeSlot,
            partySize,
            customerName,
            customerPhone,
            userEmail,
            specialRequests
        };

        const btn = document.getElementById('btnSubmitDining');
        this.state.isSubmittingDining = true;
        if (btn) {
            btn.disabled = true;
            btn.innerText = '正在為您保留專屬席位並發送確認信...';
        }

        try {
            const res = await this.fetchApi('/dining/reservations', {
                method: 'POST',
                body: JSON.stringify(payload)
            });

            alert(`🎉 美饌席位預約成功！\n預約編號: #${res.data.reservationNumber}\n確認信已寄送至信箱。`);
            this.closeModal('diningBookModal');
            this.switchView('myBookingsView');
            this.switchMyBookingsTab('dining');
        } catch (err) {
            alert('預約失敗: ' + err.message);
        } finally {
            this.state.isSubmittingDining = false;
            if (btn) {
                btn.disabled = false;
                btn.innerText = '✨ 確認送出美饌預約';
            }
        }
    },

    // --- 3. Tickets Experience Module ---

    async loadTickets(category = 'ALL') {
        try {
            const res = await this.fetchApi(`/tickets?category=${category}`);
            this.state.tickets = res.data || [];
            this.renderTickets(this.state.tickets);
        } catch (e) {
            console.error('載入票券失敗:', e);
        }
    },

    filterTickets(category) {
        document.querySelectorAll('.city-tag-btn').forEach(btn => {
            if (btn.id.startsWith('ticketCat-')) btn.classList.remove('active');
        });
        const map = {
            'ALL': 'ticketCat-all',
            '頂級溫泉': 'ticketCat-hotspring',
            '奢華遊艇': 'ticketCat-yacht',
            '觀景導覽': 'ticketCat-tour',
            '樂園通關': 'ticketCat-park',
            '名廚手作': 'ticketCat-chef',
            '戶外野奢': 'ticketCat-glamping'
        };
        const activeBtn = document.getElementById(map[category]);
        if (activeBtn) activeBtn.classList.add('active');
        this.loadTickets(category);
    },

    renderTickets(tickets) {
        const container = document.getElementById('ticketsListContainer');
        if (!container) return;

        if (tickets.length === 0) {
            container.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 48px; color: #64748b;">暫無符合條件的體驗票券</div>';
            return;
        }

        container.innerHTML = tickets.map(t => `
            <div class="ticket-card">
                <div class="ticket-img-container">
                    <img src="${t.coverImage}" alt="${t.title}" loading="lazy">
                    <div class="ticket-badge">⭐ ${t.rating} (${t.reviewCount}+ 評價)</div>
                </div>
                <div class="ticket-body">
                    <span class="region-badge" style="width: fit-content; margin-bottom: 6px;">📍 ${t.city} • ${t.category}</span>
                    <h3 style="font-size: 1.05rem; color: #1e293b; margin-bottom: 6px; line-height: 1.4;">${t.title}</h3>
                    <p style="font-size: 0.85rem; color: #64748b; margin-bottom: 8px; flex: 1;">${t.description}</p>
                    <div style="background: #eff6ff; padding: 8px 12px; border-radius: 6px; font-size: 0.8rem; color: #1e40af; margin-bottom: 12px;">
                        <strong>✨ 包含亮點：</strong> ${t.highlights}
                    </div>
                    <div style="display: flex; justify-content: space-between; align-items: flex-end; border-top: 1px solid #f1f5f9; padding-top: 10px;">
                        <div>
                            <span style="font-size: 0.8rem; color: #94a3b8; text-decoration: line-through;">NT$ ${Number(t.originalPrice).toLocaleString()}</span>
                            <div style="font-size: 1.25rem; font-weight: 800; color: #dc2626;">NT$ ${Number(t.price).toLocaleString()}</div>
                        </div>
                        <button class="btn btn-primary btn-sm" onclick="app.openTicketPurchaseModal(${t.id})">🎟️ 立即搶購</button>
                    </div>
                </div>
            </div>
        `).join('');
    },

    openTicketPurchaseModal(ticketId) {
        const t = this.state.tickets.find(item => item.id === ticketId);
        if (!t) return;
        this.state.selectedTicket = t;

        document.getElementById('ticketModalId').value = t.id;
        document.getElementById('ticketModalTitle').innerText = t.title;
        document.getElementById('ticketModalLocation').innerText = `📍 ${t.location} | ⏳ ${t.validityPeriod}`;
        document.getElementById('ticketModalPrice').innerText = `NT$ ${Number(t.price).toLocaleString()} / 張`;
        document.getElementById('ticketQty').value = 1;

        if (this.state.user) {
            document.getElementById('ticketCustName').value = this.state.user.fullName || this.state.user.username;
            document.getElementById('ticketCustPhone').value = this.state.user.phone || '';
            document.getElementById('ticketCustEmail').value = this.state.user.email || '';
        }

        this.calculateTicketTotal();
        this.showModal('ticketPurchaseModal');
    },

    calculateTicketTotal() {
        if (!this.state.selectedTicket) return;
        const qty = parseInt(document.getElementById('ticketQty').value || '1', 10);
        const total = qty * this.state.selectedTicket.price;
        const display = document.getElementById('ticketSummaryTotal');
        if (display) display.innerText = `NT$ ${Number(total).toLocaleString()}`;
    },

    async handleTicketSubmit(e) {
        if (e) e.preventDefault();
        if (this.state.isSubmittingTicket) return;

        const ticketId = document.getElementById('ticketModalId').value;
        const quantity = parseInt(document.getElementById('ticketQty').value, 10);
        const customerName = document.getElementById('ticketCustName').value.trim();
        const customerPhone = document.getElementById('ticketCustPhone').value.trim();
        const userEmail = document.getElementById('ticketCustEmail').value.trim();
        const paymentMethod = document.getElementById('ticketPaymentMethod').value;

        if (!customerName || !customerPhone || !quantity) {
            alert('請填寫完整取票資訊！');
            return;
        }

        const payload = {
            ticketId: parseInt(ticketId, 10),
            quantity,
            customerName,
            customerPhone,
            userEmail,
            paymentMethod
        };

        const btn = document.getElementById('btnSubmitTicket');
        this.state.isSubmittingTicket = true;
        if (btn) {
            btn.disabled = true;
            btn.innerText = '正在安全結帳並生成專屬憑證...';
        }

        try {
            const res = await this.fetchApi('/tickets/orders', {
                method: 'POST',
                body: JSON.stringify(payload)
            });

            this.closeModal('ticketPurchaseModal');
            this.showTicketVoucher(
                res.data.orderNumber,
                res.data.ticketTitle,
                res.data.quantity,
                res.data.customerName,
                res.data.customerPhone,
                res.data.qrCodeValue
            );
        } catch (err) {
            alert('購買票券失敗: ' + err.message);
        } finally {
            this.state.isSubmittingTicket = false;
            if (btn) {
                btn.disabled = false;
                btn.innerText = '🚀 立即付款並產生核銷 QR Code';
            }
        }
    },

    showTicketVoucher(orderNumber, title, qty, name, phone, qrValue) {
        document.getElementById('voucherTicketTitle').innerText = title;
        document.getElementById('voucherOrderNumber').innerText = `訂單編號: #${orderNumber}`;
        document.getElementById('voucherQty').innerText = qty;
        document.getElementById('voucherCustName').innerText = name;
        document.getElementById('voucherCustPhone').innerText = phone;
        document.getElementById('voucherQrText').innerText = qrValue;

        const qrImg = document.getElementById('voucherQrImg');
        if (qrImg) {
            qrImg.src = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(qrValue)}`;
        }

        this.showModal('ticketVoucherModal');
    },

    // --- 4. My Bookings Multi-Tabs ---

    switchMyBookingsTab(tab) {
        document.querySelectorAll('.sub-tab-btn').forEach(btn => {
            if (btn.id.startsWith('subTab-')) btn.classList.remove('active');
        });
        const activeBtn = document.getElementById(`subTab-${tab}`);
        if (activeBtn) activeBtn.classList.add('active');

        document.getElementById('myBookings-rooms-content').classList.add('d-none');
        document.getElementById('myBookings-dining-content').classList.add('d-none');
        document.getElementById('myBookings-tickets-content').classList.add('d-none');

        const targetContent = document.getElementById(`myBookings-${tab}-content`);
        if (targetContent) targetContent.classList.remove('d-none');

        if (tab === 'rooms') this.loadMyBookings();
        if (tab === 'dining') this.loadMyDining();
        if (tab === 'tickets') this.loadMyTickets();
    },

    async loadMyBookings() {
        if (!this.state.token) {
            document.getElementById('myBookingsTableBody').innerHTML = '<tr><td colspan="7" style="text-align:center; padding: 24px;">請先登入後查看您的預訂紀錄</td></tr>';
            return;
        }

        try {
            const res = await this.fetchApi('/bookings/my');
            const list = res.data || [];
            const tbody = document.getElementById('myBookingsTableBody');
            if (list.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" style="text-align:center; padding: 24px;">目前尚無住宿預訂紀錄</td></tr>';
                return;
            }

            tbody.innerHTML = list.map(b => `
                <tr>
                    <td><strong>#${b.bookingNumber}</strong></td>
                    <td>${b.roomCity || ''} • ${b.roomName}</td>
                    <td>${b.checkInDate} ~ ${b.checkOutDate}</td>
                    <td>${b.totalNights} 晚 / <strong style="color: #2563eb;">NT$ ${Number(b.totalPrice).toLocaleString()}</strong></td>
                    <td><span class="badge ${b.status === 'CONFIRMED' ? 'badge-paid' : 'badge-pending'}">${b.status}</span></td>
                    <td>${b.createdAt ? b.createdAt.substring(0, 10) : '-'}</td>
                    <td>
                        ${b.status === 'CONFIRMED' ? `<button class="btn btn-danger btn-sm" onclick="app.cancelBooking(${b.id})">取消預訂</button>` : `<span style="color:#94a3b8; font-size:0.8rem;">已結案</span>`}
                    </td>
                </tr>
            `).join('');
        } catch (e) {
            console.error('載入訂房紀錄失敗:', e);
        }
    },

    async cancelBooking(bookingId) {
        if (!confirm('確定要取消此筆住宿預訂嗎？取消後將發送退訂確認信。')) return;
        try {
            await this.fetchApi(`/bookings/${bookingId}/cancel`, { method: 'POST' });
            alert('住宿預訂已成功取消！');
            this.loadMyBookings();
        } catch (err) {
            alert('取消失敗: ' + err.message);
        }
    },

    async loadMyDining() {
        if (!this.state.token) return;
        try {
            const res = await this.fetchApi('/dining/reservations/my');
            const list = res.data || [];
            const tbody = document.getElementById('myDiningTableBody');
            if (list.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" style="text-align:center; padding: 24px;">目前尚無美饌預約紀錄</td></tr>';
                return;
            }

            tbody.innerHTML = list.map(d => `
                <tr>
                    <td><strong>#${d.reservationNumber}</strong></td>
                    <td>${d.restaurantName}</td>
                    <td>${d.reservationDate}</td>
                    <td>${d.timeSlot}</td>
                    <td>${d.partySize} 位</td>
                    <td><span class="badge ${d.status === 'CONFIRMED' ? 'badge-paid' : 'badge-pending'}">${d.status}</span></td>
                    <td>
                        ${d.status === 'CONFIRMED' ? `<button class="btn btn-danger btn-sm" onclick="app.cancelDiningReservation(${d.id})">取消預約</button>` : `<span style="color:#94a3b8; font-size:0.8rem;">已取消</span>`}
                    </td>
                </tr>
            `).join('');
        } catch (e) {
            console.error('載入美饌預約失敗:', e);
        }
    },

    async cancelDiningReservation(id) {
        if (!confirm('確定要取消此筆美饌預約嗎？')) return;
        try {
            await this.fetchApi(`/dining/reservations/${id}/cancel`, { method: 'POST' });
            alert('美饌預約已成功取消。');
            this.loadMyDining();
        } catch (err) {
            alert('取消失敗: ' + err.message);
        }
    },

    async loadMyTickets() {
        if (!this.state.token) return;
        try {
            const res = await this.fetchApi('/tickets/orders/my');
            const list = res.data || [];
            const tbody = document.getElementById('myTicketsTableBody');
            if (list.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" style="text-align:center; padding: 24px;">目前尚無休閒體驗票券訂單</td></tr>';
                return;
            }

            tbody.innerHTML = list.map(t => `
                <tr>
                    <td><strong>#${t.orderNumber}</strong></td>
                    <td>${t.ticketTitle}</td>
                    <td>${t.quantity} 張</td>
                    <td><strong style="color: #2563eb;">NT$ ${Number(t.totalPrice).toLocaleString()}</strong></td>
                    <td>${t.customerName || '-'}</td>
                    <td><span class="badge ${t.status === 'PAID' ? 'badge-paid' : 'badge-pending'}">${t.status}</span></td>
                    <td>
                        <button class="btn btn-outline btn-sm" onclick="app.showTicketVoucher('${t.orderNumber}', '${t.ticketTitle}', ${t.quantity}, '${t.customerName}', '${t.customerPhone}', '${t.qrCodeValue}')">📱 查看 QR Code</button>
                    </td>
                </tr>
            `).join('');
        } catch (e) {
            console.error('載入票券訂單失敗:', e);
        }
    },

    async cancelTicketOrder(id) {
        if (!confirm('確定要申請此筆休閒票券退款嗎？')) return;
        try {
            await this.fetchApi(`/tickets/orders/${id}/cancel`, { method: 'POST' });
            alert('票券已完成退款申請。');
            this.loadMyTickets();
        } catch (err) {
            alert('退款失敗: ' + err.message);
        }
    },

    // --- 5. Profile & 2FA ---

    async loadProfile() {
        if (!this.state.token) {
            this.showModal('loginModal');
            return;
        }

        try {
            const res = await this.fetchApi('/users/me');
            const user = res.data;

            const uField = document.getElementById('profileUsername');
            if (uField) uField.value = user.username;

            const nameField = document.getElementById('profileFullName');
            if (nameField) {
                if (nameField.tagName === 'INPUT') nameField.value = user.fullName || '';
                else nameField.innerText = user.fullName || user.username;
            }

            const phoneField = document.getElementById('profilePhone');
            if (phoneField) phoneField.value = user.phone || '';

            const emailField = document.getElementById('profileEmail');
            if (emailField) emailField.value = user.email || '';

            const badge = document.getElementById('profile2faStatusBadge') || document.getElementById('2faStatusBadge');
            const btnEnable = document.getElementById('btnEnable2FA') || document.getElementById('btnEnable2fa');
            const btnDisable = document.getElementById('btnDisable2FA') || document.getElementById('btnDisable2fa');

            if (user.using2FA) {
                if (badge) { badge.className = 'badge badge-paid'; badge.innerText = '🛡️ 已啟用保護中'; }
                if (btnEnable) btnEnable.classList.add('d-none');
                if (btnDisable) btnDisable.classList.remove('d-none');
            } else {
                if (badge) { badge.className = 'badge badge-pending'; badge.innerText = '未啟用'; }
                if (btnEnable) btnEnable.classList.remove('d-none');
                if (btnDisable) btnDisable.classList.add('d-none');
            }
        } catch (err) {
            console.error('載入個人資料失敗:', err);
        }
    },

    async handleProfileUpdate(e) {
        e.preventDefault();
        const fullName = document.getElementById('profileFullName').value.trim();
        const phone = document.getElementById('profilePhone').value.trim();
        const email = document.getElementById('profileEmail').value.trim();

        try {
            const res = await this.fetchApi('/users/me', {
                method: 'PUT',
                body: JSON.stringify({ fullName, phone, email })
            });

            this.state.user.fullName = res.data.fullName;
            this.state.user.phone = res.data.phone;
            this.state.user.email = res.data.email;
            localStorage.setItem('user', JSON.stringify(this.state.user));
            this.updateAuthUI();
            alert('✅ 個人資料更新成功！');
            this.loadProfile();
        } catch (err) {
            alert('更新失敗: ' + err.message);
        }
    },

    async handleChangePassword(e) {
        e.preventDefault();
        const oldPassword = document.getElementById('oldPasswordInput').value;
        const newPassword = document.getElementById('newPasswordInput').value;

        try {
            await this.fetchApi('/users/me/password', {
                method: 'PUT',
                body: JSON.stringify({ oldPassword, newPassword })
            });

            alert('🎉 登入密碼變更成功！請妥善保管新密碼。');
            document.getElementById('oldPasswordInput').value = '';
            document.getElementById('newPasswordInput').value = '';
        } catch (err) {
            alert('密碼變更失敗: ' + err.message);
        }
    },

    async show2FASetupModal() {
        try {
            const res = await this.fetchApi('/auth/setup-2fa');
            const { secret, qrCodeDataUrl } = res.data;

            const qrImg = document.getElementById('2faQrImg');
            const secretDisplay = document.getElementById('2faSecretKeyDisplay');
            if (qrImg) qrImg.src = qrCodeDataUrl;
            if (secretDisplay) secretDisplay.innerText = secret;

            this.showModal('twoFactorSetupModal');
        } catch (err) {
            alert('無法產生 2FA 設定資訊: ' + err.message);
        }
    },

    async confirm2FAEnable(e) {
        e.preventDefault();
        const code = document.getElementById('2faConfirmCode').value.trim();
        try {
            await this.fetchApi('/auth/enable-2fa', {
                method: 'POST',
                body: JSON.stringify({ code })
            });

            alert('🎉 Google Authenticator 2FA 雙步驟驗證已成功啟用！');
            this.closeModal('twoFactorSetupModal');
            this.loadProfile();
        } catch (err) {
            alert('2FA 啟用失敗: ' + err.message);
        }
    },

    async disable2FA() {
        if (!confirm('確定要關閉 2FA 兩步驟身分防護嗎？關閉後帳號安全性將降低。')) return;
        try {
            await this.fetchApi('/auth/disable-2fa', { method: 'POST' });
            alert('2FA 雙因素驗證已關閉。');
            this.loadProfile();
        } catch (err) {
            alert('關閉 2FA 失敗: ' + err.message);
        }
    },

    // --- 6. Admin Rooms Management ---

    async loadAdminRooms() {
        try {
            const res = await this.fetchApi('/rooms');
            const rooms = res.data || [];
            const tbody = document.getElementById('adminRoomsTableBody');
            if (!tbody) return;

            tbody.innerHTML = rooms.map(r => {
                const coverImg = this.getRoomCoverImage(r);
                const price = this.getRoomPrice(r);
                return `
                    <tr>
                        <td>${r.id}</td>
                        <td><img src="${coverImg}" style="width: 50px; height: 35px; object-fit: cover; border-radius: 4px;"></td>
                        <td>${r.city}</td>
                        <td><strong>${r.name}</strong></td>
                        <td>${r.roomType}</td>
                        <td>NT$ ${Number(price).toLocaleString()}</td>
                        <td>${r.capacity} 人</td>
                        <td><span class="badge ${r.status === 'AVAILABLE' ? 'badge-paid' : 'badge-pending'}">${r.status}</span></td>
                        <td>
                            <button class="btn btn-outline btn-sm" onclick="app.openRoomEditModal(${r.id})">編輯</button>
                            <button class="btn btn-danger btn-sm" onclick="app.deleteAdminRoom(${r.id})">刪除</button>
                        </td>
                    </tr>
                `;
            }).join('');
        } catch (err) {
            console.error('載入後台房型清單失敗:', err);
        }
    },

    openRoomEditModal(roomId) {
        const title = document.getElementById('adminRoomModalTitle');
        if (roomId) {
            const room = this.state.rooms.find(r => r.id === roomId);
            if (!room) return;
            const price = this.getRoomPrice(room);
            const imgList = this.getRoomImageUrls(room);
            title.innerText = '編輯房型資料';
            document.getElementById('editRoomId').value = room.id;
            document.getElementById('editRoomCity').value = room.city || '';
            document.getElementById('editRoomName').value = room.name || '';
            document.getElementById('editRoomType').value = room.roomType || '';
            document.getElementById('editRoomPrice').value = price || 0;
            document.getElementById('editRoomCapacity').value = room.capacity || 2;
            document.getElementById('editRoomStatus').value = room.status || 'AVAILABLE';
            document.getElementById('editRoomAmenities').value = room.amenities || '';
            document.getElementById('editRoomDesc').value = room.description || '';
            document.getElementById('editRoomImages').value = imgList.join('\n');
        } else {
            title.innerText = '新增全新房型';
            document.getElementById('editRoomId').value = '';
            document.getElementById('editRoomCity').value = '';
            document.getElementById('editRoomName').value = '';
            document.getElementById('editRoomType').value = '海景尊爵套房';
            document.getElementById('editRoomPrice').value = 5000;
            document.getElementById('editRoomCapacity').value = 2;
            document.getElementById('editRoomStatus').value = 'AVAILABLE';
            document.getElementById('editRoomAmenities').value = '全景陽台, 頂級浴缸, 迎賓香檳';
            document.getElementById('editRoomDesc').value = '';
            document.getElementById('editRoomImages').value = '';
        }
        this.showModal('adminRoomEditModal');
    },

    async handleAdminRoomSave(e) {
        e.preventDefault();
        const id = document.getElementById('editRoomId').value;
        const imagesText = document.getElementById('editRoomImages').value;
        const images = imagesText.split('\n').map(s => s.trim()).filter(s => s.length > 0);

        const payload = {
            city: document.getElementById('editRoomCity').value.trim(),
            name: document.getElementById('editRoomName').value.trim(),
            roomType: document.getElementById('editRoomType').value.trim(),
            pricePerNight: parseFloat(document.getElementById('editRoomPrice').value),
            capacity: parseInt(document.getElementById('editRoomCapacity').value, 10),
            status: document.getElementById('editRoomStatus').value,
            amenities: document.getElementById('editRoomAmenities').value.trim(),
            description: document.getElementById('editRoomDesc').value.trim(),
            images: images.length > 0 ? images : ['https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800']
        };

        try {
            if (id) {
                await this.fetchApi(`/admin/rooms/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
                alert('房型資料更新成功！');
            } else {
                await this.fetchApi('/admin/rooms', { method: 'POST', body: JSON.stringify(payload) });
                alert('全新房型新增成功！');
            }
            this.closeModal('adminRoomEditModal');
            await this.loadRooms();
            this.loadAdminRooms();
        } catch (err) {
            alert('儲存失敗: ' + err.message);
        }
    },

    async deleteAdminRoom(roomId) {
        if (!confirm('確定要刪除此筆房型資料嗎？此操作無法復原。')) return;
        try {
            await this.fetchApi(`/admin/rooms/${roomId}`, { method: 'DELETE' });
            alert('房型已成功刪除。');
            await this.loadRooms();
            this.loadAdminRooms();
        } catch (err) {
            alert('刪除失敗: ' + err.message);
        }
    },

    // --- 7. Admin Users Management ---

    async loadAdminUsers() {
        try {
            const res = await this.fetchApi('/admin/users');
            const users = res.data || [];
            const tbody = document.getElementById('adminUsersTableBody');
            if (!tbody) return;

            tbody.innerHTML = users.map(u => {
                const isAdmin = u.roles && u.roles.includes('ROLE_ADMIN');
                return `
                    <tr>
                        <td>${u.id}</td>
                        <td><strong>${u.username}</strong></td>
                        <td>${u.fullName || '-'}</td>
                        <td>${u.email}</td>
                        <td>${u.phone || '-'}</td>
                        <td>
                            <span class="badge ${isAdmin ? 'badge-paid' : 'badge-pending'}" style="cursor: pointer;" onclick="app.toggleUserRole(${u.id}, '${isAdmin ? 'ROLE_ADMIN' : 'ROLE_USER'}')">
                                ${isAdmin ? '🛡️ ROLE_ADMIN' : '👤 ROLE_USER'}
                            </span>
                        </td>
                        <td>${u.using2FA ? '🟢 已啟用' : '⚪ 未啟用'}</td>
                        <td>${u.createdAt ? u.createdAt.substring(0, 10) : '-'}</td>
                        <td>
                            <button class="btn btn-outline btn-sm" onclick="app.toggleUserRole(${u.id}, '${isAdmin ? 'ROLE_ADMIN' : 'ROLE_USER'}')">切換權限</button>
                            <button class="btn btn-danger btn-sm" onclick="app.deleteUserAccount(${u.id}, '${u.username}')">刪除</button>
                        </td>
                    </tr>
                `;
            }).join('');
        } catch (e) {
            console.error('載入使用者失敗:', e);
        }
    },

    async toggleUserRole(userId, currentRole) {
        const newRole = currentRole === 'ROLE_ADMIN' ? 'ROLE_USER' : 'ROLE_ADMIN';
        if (!confirm(`確定要將此使用者權限變更為 ${newRole} 嗎？`)) return;

        try {
            await this.fetchApi(`/admin/users/${userId}/role?role=${newRole}`, { method: 'PUT' });
            alert('使用者權限已成功更新！');
            this.loadAdminUsers();
        } catch (err) {
            alert('權限變更失敗: ' + err.message);
        }
    },

    async deleteUserAccount(userId, username) {
        if (!confirm(`確定要刪除帳號 [${username}] 嗎？此操作無法復原。`)) return;

        try {
            await this.fetchApi(`/admin/users/${userId}`, { method: 'DELETE' });
            alert('使用者帳號已成功刪除。');
            this.loadAdminUsers();
        } catch (err) {
            alert('刪除失敗: ' + err.message);
        }
    },

    // --- 8. Admin Overview & Multi-Service Orders ---

    async loadAdminOverview() {
        try {
            const res = await this.fetchApi('/admin/overview/stats');
            const stats = res.data || {};

            document.getElementById('kpiTotalRevenue').innerText = `NT$ ${Number(stats.totalRevenue || 0).toLocaleString()}`;
            document.getElementById('kpiTotalRooms').innerText = `${stats.totalRoomBookings || 0} 筆`;
            document.getElementById('kpiTotalDining').innerText = `${stats.totalDiningReservations || 0} 筆`;
            document.getElementById('kpiTotalTickets').innerText = `${stats.totalTicketOrders || 0} 筆`;

            this.switchAdminOrdersTab('rooms');
        } catch (e) {
            console.error('載入營運數據失敗:', e);
        }
    },

    switchAdminOrdersTab(tab) {
        document.querySelectorAll('.sub-tab-btn').forEach(btn => {
            if (btn.id.startsWith('adminSubTab-')) btn.classList.remove('active');
        });
        const activeBtn = document.getElementById(`adminSubTab-${tab}`);
        if (activeBtn) activeBtn.classList.add('active');

        document.getElementById('adminOrders-rooms-content').classList.add('d-none');
        document.getElementById('adminOrders-dining-content').classList.add('d-none');
        document.getElementById('adminOrders-tickets-content').classList.add('d-none');

        const target = document.getElementById(`adminOrders-${tab}-content`);
        if (target) target.classList.remove('d-none');

        if (tab === 'rooms') this.loadAdminRoomOrders();
        if (tab === 'dining') this.loadAdminDiningOrders();
        if (tab === 'tickets') this.loadAdminTicketOrders();
    },

    async loadAdminRoomOrders() {
        try {
            const res = await this.fetchApi('/admin/bookings');
            const list = res.data || [];
            const tbody = document.getElementById('adminBookingsTableBody');
            if (!tbody) return;

            tbody.innerHTML = list.map(b => `
                <tr>
                    <td><strong>#${b.bookingNumber}</strong></td>
                    <td>${b.username} (${b.userEmail})</td>
                    <td>${b.roomCity || ''} • ${b.roomName}</td>
                    <td>${b.checkInDate} ~ ${b.checkOutDate}</td>
                    <td><strong style="color: #2563eb;">NT$ ${Number(b.totalPrice).toLocaleString()}</strong></td>
                    <td><span class="badge ${b.status === 'CONFIRMED' ? 'badge-paid' : 'badge-pending'}">${b.status}</span></td>
                    <td>
                        <select class="form-control" style="font-size: 0.8rem; padding: 4px;" onchange="app.updateAdminOrderStatus('rooms', ${b.id}, this.value)">
                            <option value="CONFIRMED" ${b.status === 'CONFIRMED' ? 'selected' : ''}>CONFIRMED</option>
                            <option value="CANCELLED" ${b.status === 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                            <option value="COMPLETED" ${b.status === 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
                        </select>
                    </td>
                </tr>
            `).join('');
        } catch (e) {
            console.error('載入住宿訂單失敗:', e);
        }
    },

    async loadAdminDiningOrders() {
        try {
            const res = await this.fetchApi('/admin/dining/reservations');
            const list = res.data || [];
            const tbody = document.getElementById('adminDiningTableBody');
            if (!tbody) return;

            tbody.innerHTML = list.map(d => `
                <tr>
                    <td><strong>#${d.reservationNumber}</strong></td>
                    <td>${d.customerName} (${d.customerPhone})</td>
                    <td>${d.restaurantName}</td>
                    <td>${d.reservationDate} / ${d.timeSlot}</td>
                    <td>${d.partySize} 位</td>
                    <td><span class="badge ${d.status === 'CONFIRMED' ? 'badge-paid' : 'badge-pending'}">${d.status}</span></td>
                    <td>
                        <select class="form-control" style="font-size: 0.8rem; padding: 4px;" onchange="app.updateAdminOrderStatus('dining', ${d.id}, this.value)">
                            <option value="CONFIRMED" ${d.status === 'CONFIRMED' ? 'selected' : ''}>CONFIRMED</option>
                            <option value="SEATED" ${d.status === 'SEATED' ? 'selected' : ''}>SEATED (已入座)</option>
                            <option value="COMPLETED" ${d.status === 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
                            <option value="CANCELLED" ${d.status === 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                        </select>
                    </td>
                </tr>
            `).join('');
        } catch (e) {
            console.error('載入美饌總覽失敗:', e);
        }
    },

    async loadAdminTicketOrders() {
        try {
            const res = await this.fetchApi('/admin/tickets/orders');
            const list = res.data || [];
            const tbody = document.getElementById('adminTicketsTableBody');
            if (!tbody) return;

            tbody.innerHTML = list.map(t => `
                <tr>
                    <td><strong>#${t.orderNumber}</strong></td>
                    <td>${t.customerName} (${t.customerPhone})</td>
                    <td>${t.ticketTitle}</td>
                    <td>${t.quantity} 張 / <strong style="color: #2563eb;">NT$ ${Number(t.totalPrice).toLocaleString()}</strong></td>
                    <td><code style="font-size: 0.75rem;">${t.qrCodeValue}</code></td>
                    <td><span class="badge ${t.status === 'PAID' ? 'badge-paid' : 'badge-pending'}">${t.status}</span></td>
                    <td>
                        <select class="form-control" style="font-size: 0.8rem; padding: 4px;" onchange="app.updateAdminOrderStatus('tickets', ${t.id}, this.value)">
                            <option value="PAID" ${t.status === 'PAID' ? 'selected' : ''}>PAID (已出票)</option>
                            <option value="USED" ${t.status === 'USED' ? 'selected' : ''}>USED (已核銷)</option>
                            <option value="REFUNDED" ${t.status === 'REFUNDED' ? 'selected' : ''}>REFUNDED (已退款)</option>
                        </select>
                    </td>
                </tr>
            `).join('');
        } catch (e) {
            console.error('載入票券總覽失敗:', e);
        }
    },

    async updateAdminOrderStatus(type, id, status) {
        try {
            if (type === 'rooms') {
                await this.fetchApi(`/admin/bookings/${id}/status?status=${status}`, { method: 'PUT' });
            } else if (type === 'dining') {
                await this.fetchApi(`/admin/dining/reservations/${id}/status?status=${status}`, { method: 'PUT' });
            } else if (type === 'tickets') {
                await this.fetchApi(`/admin/tickets/orders/${id}/status?status=${status}`, { method: 'PUT' });
            }
            alert(`訂單狀態已更新為: ${status}`);
        } catch (err) {
            alert('狀態更新失敗: ' + err.message);
        }
    }
};

window.app = app;
document.addEventListener('DOMContentLoaded', () => {
    app.init();
});
