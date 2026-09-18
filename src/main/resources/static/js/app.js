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
        currentRoom: null,
        temp2faToken: null,
        currentGoogleAuthUrl: null,
        currentLineQrSessionId: null,
        currentLineAuthUrl: null,
        linePollingInterval: null,
        allAdminBookings: []
    },

    async init() {
        await this.loadComponents();
        this.updateAuthUI();
        this.checkOAuthCallback();
        await this.loadRooms();
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
                    : this.state.user.roles.has?.('ROLE_ADMIN')
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

        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.message || '請求發生錯誤');
        }
        return data;
    },

    switchView(viewId) {
        document.querySelectorAll('.view-section').forEach(el => el.classList.add('d-none'));
        const target = document.getElementById(viewId);
        if (target) target.classList.remove('d-none');

        document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));

        const menuMap = {
            'roomsView': 'roomsMenuItem',
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
        if (viewId === 'myBookingsView') this.loadMyBookings();
        if (viewId === 'profileView') this.loadProfile();
        if (viewId === 'adminRoomsView') this.loadAdminRooms();
        if (viewId === 'adminUsersView') this.loadAdminUsers();
        if (viewId === 'adminBookingsView') this.loadAdminBookings();
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
        e.preventDefault();
        const usernameOrEmail = document.getElementById('loginUsername').value;
        const password = document.getElementById('loginPassword').value;

        try {
            const res = await this.fetchApi('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ usernameOrEmail, password })
            });

            if (res.data.requiresTwoFactor) {
                this.state.temp2faToken = res.data.temporaryToken;
                this.closeModal('loginModal');
                this.showModal('twoFactorVerifyModal');
                return;
            }

            this.loginSuccess(res.data);
            this.closeModal('loginModal');
            alert('登入成功！');
        } catch (err) {
            alert('登入失敗: ' + err.message);
        }
    },

    async handleGuestLogin() {
        try {
            const res = await this.fetchApi('/auth/guest-login', {
                method: 'POST'
            });

            this.loginSuccess(res.data);
            this.closeModal('loginModal');
            alert('訪客體驗登入成功！您現在可以預訂房型與體驗各項功能。');
        } catch (err) {
            alert('訪客登入失敗: ' + err.message);
        }
    },

    // --- Google Mail OTP 驗證碼登入 (正式環境模式) ---

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
        const codeInput = document.getElementById('otpCodeInput');
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
                statusMsg.innerText = '✅ 驗證碼已成功寄出！請至您的 Gmail 信箱收取 6 位數驗證碼。';
                statusMsg.style.color = '#10b981';
            }

            if (codeInput) {
                codeInput.value = '';
                codeInput.focus();
            }

            alert(`【驗證碼已發送】\n\n系統已寄送 6 位數一次性登入代碼至：\n${email}\n\n請開啟您的 Google Mail 信箱查看，並於 5 分鐘內輸入驗證碼完成登入。`);

            let countdown = 60;
            const timer = setInterval(() => {
                countdown--;
                if (countdown <= 0) {
                    clearInterval(timer);
                    btnSend.disabled = false;
                    btnSend.innerText = '重新發送驗證碼';
                } else {
                    btnSend.innerText = `${countdown} 秒後可重發`;
                }
            }, 1000);

        } catch (err) {
            btnSend.disabled = false;
            btnSend.innerText = '發送驗證碼';
            if (statusMsg) {
                statusMsg.innerText = '❌ 發送失敗: ' + err.message;
                statusMsg.style.color = '#ef4444';
            }
            alert('發送驗證碼失敗: ' + err.message);
        }
    },

    async handleVerifyGoogleMailOtp(e) {
        e.preventDefault();
        const email = document.getElementById('otpEmailInput')?.value?.trim();
        const code = document.getElementById('otpCodeInput')?.value?.trim();

        if (!email || !code) {
            alert('請填寫信箱與 6 位數驗證碼！');
            return;
        }

        try {
            const res = await this.fetchApi('/auth/email-otp/verify', {
                method: 'POST',
                body: JSON.stringify({ email, code })
            });

            this.loginSuccess(res.data);
            this.closeModal('googleMailOtpModal');
            alert(`🎉 Google Mail 驗證成功！歡迎登入 Grand Luxury 訂房系統。`);
        } catch (err) {
            alert('驗證碼登入失敗: ' + err.message);
        }
    },

    // --- Google OAuth 2.0 ---

    async handleGoogleOAuthLogin() {
        try {
            const currentUrl = window.location.origin + window.location.pathname;
            const res = await this.fetchApi(`/auth/oauth/google/url?redirectUri=${encodeURIComponent(currentUrl + '?oauth=google')}`);
            
            this.state.currentGoogleAuthUrl = res.data.authorizationUrl;
            
            document.getElementById('googleAuthUrlDisplay').innerText = res.data.authorizationUrl;
            this.closeModal('loginModal');
            this.showModal('googleOAuthModal');
        } catch (err) {
            alert('取得 Google 授權連結失敗: ' + err.message);
        }
    },

    proceedToGoogleAuth() {
        if (this.state.currentGoogleAuthUrl) {
            if (this.state.currentGoogleAuthUrl.includes('mock-google-client-id')) {
                const proceed = confirm('目前系統運行於開發/展示環境 (使用模擬 Google Client ID)。\n\n點選「確定」將直接模擬授權回傳並登入，點選「取消」將開啟 Google 官方授權連結。');
                if (proceed) {
                    this.simulateGoogleConsent();
                    return;
                }
            }
            window.location.href = this.state.currentGoogleAuthUrl;
        }
    },

    async simulateGoogleConsent() {
        try {
            const res = await this.fetchApi('/auth/social-login', {
                method: 'POST',
                body: JSON.stringify({
                    provider: 'GOOGLE',
                    token: 'google-oauth-token-' + Date.now(),
                    name: 'Google 貴賓會員',
                    email: 'google_vip_user@gmail.com',
                    providerUserId: 'g_' + Math.floor(Math.random() * 1000000)
                })
            });

            this.loginSuccess(res.data);
            this.closeModal('googleOAuthModal');
            alert('Google 帳號授權登入成功！');
        } catch (err) {
            alert('Google 授權登入失敗: ' + err.message);
        }
    },

    // --- LINE 掃碼登入 ---

    async showLineQrCodeModal() {
        try {
            const currentUrl = window.location.origin + window.location.pathname;
            const res = await this.fetchApi(`/auth/oauth/line/qr?redirectUri=${encodeURIComponent(currentUrl + '?oauth=line')}`);

            this.state.currentLineQrSessionId = res.data.qrSessionId;
            this.state.currentLineAuthUrl = res.data.lineAuthUrl;

            document.getElementById('lineQrImg').src = res.data.qrCodeDataUri;
            document.getElementById('lineQrStatusText').innerText = '🟢 等待手機端掃描授權中 (有效期限 5 分鐘)...';

            this.closeModal('loginModal');
            this.showModal('lineQrModal');

            this.startLineQrPolling(res.data.qrSessionId);
        } catch (err) {
            alert('產生 LINE QR Code 失敗: ' + err.message);
        }
    },

    startLineQrPolling(sessionId) {
        this.stopLineQrPolling();
        this.state.linePollingInterval = setInterval(async () => {
            if (!this.state.currentLineQrSessionId) {
                this.stopLineQrPolling();
                return;
            }
            try {
                const res = await this.fetchApi(`/auth/oauth/line/qr-status?qrSessionId=${encodeURIComponent(sessionId)}`);
                if (res.data.status === 'CONFIRMED' && res.data.authData) {
                    this.stopLineQrPolling();
                    this.loginSuccess(res.data.authData);
                    this.closeModal('lineQrModal');
                    alert('🎉 LINE 手機掃碼授權登入成功！歡迎使用 Grand Luxury。');
                } else if (res.data.status === 'EXPIRED') {
                    this.stopLineQrPolling();
                    document.getElementById('lineQrStatusText').innerText = '🔴 QR Code 已過期，請重新開啟視窗整理。';
                }
            } catch (err) {
                console.warn('LINE QR polling check:', err.message);
            }
        }, 1500);
    },

    stopLineQrPolling() {
        if (this.state.linePollingInterval) {
            clearInterval(this.state.linePollingInterval);
            this.state.linePollingInterval = null;
        }
    },

    openLineAuthUrl() {
        if (this.state.currentLineAuthUrl) {
            window.open(this.state.currentLineAuthUrl, '_blank');
        }
    },

    async checkOAuthCallback() {
        const params = new URLSearchParams(window.location.search);
        const oauthProvider = params.get('oauth');
        const code = params.get('code');

        if (oauthProvider && (oauthProvider === 'google' || oauthProvider === 'line')) {
            try {
                const res = await this.fetchApi('/auth/social-login', {
                    method: 'POST',
                    body: JSON.stringify({
                        provider: oauthProvider.toUpperCase(),
                        token: code || 'oauth-code-' + Date.now(),
                        name: `${oauthProvider.toUpperCase()} 授權會員`,
                        email: `${oauthProvider}_user@example.com`
                    })
                });
                this.loginSuccess(res.data);
                window.history.replaceState({}, document.title, window.location.pathname);
                alert(`${oauthProvider.toUpperCase()} 授權導向登入成功！`);
            } catch (err) {
                console.error('OAuth Callback 處理失敗:', err);
            }
        }
    },

    async handle2FAVerify(e) {
        e.preventDefault();
        const code = document.getElementById('twoFactorCode').value;

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
            alert('2FA 驗證成功，登入完成！');
        } catch (err) {
            alert('2FA 驗證失敗: ' + err.message);
        }
    },

    checkPasswordMatch() {
        const p1 = document.getElementById('pageRegPassword')?.value || '';
        const p2 = document.getElementById('pageRegPasswordConfirm')?.value || '';
        const msg = document.getElementById('pwdMatchMsg');
        if (!msg) return;

        if (!p2) {
            msg.innerText = '';
            return;
        }
        if (p1 === p2) {
            msg.innerText = '✅ 密碼一致';
            msg.style.color = '#10b981';
        } else {
            msg.innerText = '❌ 兩次密碼輸入不一致';
            msg.style.color = '#ef4444';
        }
    },

    toggleAdminKeyField() {
        const roleEl = document.querySelector('input[name="regRole"]:checked');
        const adminGroup = document.getElementById('adminSecretKeyGroup');
        const keyInput = document.getElementById('pageRegAdminSecretKey');

        if (roleEl && roleEl.value === 'ROLE_ADMIN') {
            if (adminGroup) adminGroup.classList.remove('d-none');
            if (keyInput) {
                keyInput.required = true;
                keyInput.focus();
            }
        } else {
            if (adminGroup) adminGroup.classList.add('d-none');
            if (keyInput) {
                keyInput.required = false;
                keyInput.value = '';
            }
        }
    },

    async handlePageRegister(e) {
        e.preventDefault();
        const username = document.getElementById('pageRegUsername').value.trim();
        const email = document.getElementById('pageRegEmail').value.trim();
        const password = document.getElementById('pageRegPassword').value;
        const passwordConfirm = document.getElementById('pageRegPasswordConfirm').value;
        const fullName = document.getElementById('pageRegFullName').value.trim();
        const phone = document.getElementById('pageRegPhone').value.trim();

        const role = document.querySelector('input[name="regRole"]:checked')?.value || 'ROLE_USER';
        const adminSecretCode = document.getElementById('pageRegAdminSecretKey')?.value?.trim() || '';

        if (password !== passwordConfirm) {
            alert('兩次密碼輸入不一致，請再次確認！');
            return;
        }

        if (role === 'ROLE_ADMIN' && !adminSecretCode) {
            alert('您選擇了註冊為系統管理者，請務必填寫管理者專屬特殊驗證暗號！');
            document.getElementById('pageRegAdminSecretKey')?.focus();
            return;
        }

        try {
            const res = await this.fetchApi('/auth/register', {
                method: 'POST',
                body: JSON.stringify({
                    username,
                    email,
                    password,
                    fullName,
                    phone,
                    role,
                    adminSecretCode
                })
            });

            this.loginSuccess(res.data);
            const roleMsg = role === 'ROLE_ADMIN' ? '👑 您已成功註冊為系統管理者！' : '🎉 您已成功註冊為尊榮貴賓會員！';
            alert(`註冊成功！${roleMsg}\n系統已發送歡迎信件至 ${email}。`);
            this.switchView('roomsView');
        } catch (err) {
            alert('註冊失敗: ' + err.message);
        }
    },

    loginSuccess(authData) {
        this.state.token = authData.accessToken;
        this.state.user = {
            id: authData.userId,
            username: authData.username,
            email: authData.email,
            fullName: authData.fullName || authData.username,
            roles: authData.roles
        };
        localStorage.setItem('token', this.state.token);
        localStorage.setItem('user', JSON.stringify(this.state.user));
        this.updateAuthUI();
        this.switchView('roomsView');
    },

    logout() {
        this.state.token = null;
        this.state.user = null;
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        this.updateAuthUI();
        this.switchView('roomsView');
        alert('已成功登出');
    },

    // --- Status Helper ---

    getStatusBadge(status) {
        switch (status) {
            case 'PENDING_PAYMENT':
                return `<span class="badge badge-pending">⏳ 下單成功未付款</span>`;
            case 'PAID':
                return `<span class="badge badge-paid">💳 下單成功已付款</span>`;
            case 'CHECKED_IN':
                return `<span class="badge badge-checked-in">🏨 已入住</span>`;
            case 'CHECKED_OUT':
                return `<span class="badge badge-checked-out">🚪 已退房</span>`;
            case 'REFUNDED':
                return `<span class="badge badge-refunded">↩️ 已退款</span>`;
            case 'CANCELLED':
                return `<span class="badge badge-cancelled">🔴 已取消</span>`;
            default:
                return `<span class="badge badge-pending">${status}</span>`;
        }
    },

    // --- Rooms & Gallery ---

    async loadRooms() {
        try {
            const res = await this.fetchApi('/rooms');
            this.state.rooms = res.data || [];
            this.filterRooms();
        } catch (err) {
            console.error('載入房型失敗:', err);
        }
    },

    filterRooms() {
        const keyword = document.getElementById('roomSearchKeyword')?.value?.toLowerCase().trim() || '';
        const roomType = document.getElementById('roomFilterType')?.value || '';
        const capacity = parseInt(document.getElementById('roomFilterCapacity')?.value || '0', 10);
        const sortOrder = document.getElementById('roomSortOrder')?.value || 'default';

        let list = [...this.state.rooms];

        if (keyword) {
            list = list.filter(r => 
                (r.name && r.name.toLowerCase().includes(keyword)) ||
                (r.roomType && r.roomType.toLowerCase().includes(keyword)) ||
                (r.amenities && r.amenities.toLowerCase().includes(keyword)) ||
                (r.description && r.description.toLowerCase().includes(keyword))
            );
        }

        if (roomType) {
            list = list.filter(r => r.roomType === roomType);
        }

        if (capacity > 0) {
            if (capacity >= 6) {
                list = list.filter(r => r.capacity >= 6);
            } else {
                list = list.filter(r => r.capacity === capacity);
            }
        }

        if (sortOrder === 'price_asc') {
            list.sort((a, b) => a.pricePerNight - b.pricePerNight);
        } else if (sortOrder === 'price_desc') {
            list.sort((a, b) => b.pricePerNight - a.pricePerNight);
        }

        this.renderRooms(list);
    },

    renderRooms(rooms) {
        const container = document.getElementById('roomsListContainer');
        if (!container) return;

        if (rooms.length === 0) {
            container.innerHTML = `
                <div style="grid-column: 1/-1; text-align: center; padding: 40px; background: white; border-radius: 12px; border: 1px dashed #cbd5e1;">
                    <span style="font-size: 2.5rem;">🔍</span>
                    <p style="color: #64748b; margin-top: 10px; font-size: 1rem;">找不到符合條件的精選房型，請嘗試調整搜尋關鍵字或篩選條件。</p>
                </div>
            `;
            return;
        }

        container.innerHTML = rooms.map(room => {
            const img = room.primaryImageUrl || (room.images && room.images[0] ? room.images[0].imageUrl : 'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=600&auto=format&fit=crop&q=80');
            return `
                <div class="room-card">
                    <img src="${img}" alt="${room.name}" class="room-card-img" onerror="this.src='https://images.unsplash.com/photo-1590490360182-c33d57733427?w=600&auto=format&fit=crop&q=80'">
                    <div class="room-card-body">
                        <div>
                            <h3 class="room-title">${room.name}</h3>
                            <div class="room-meta">
                                <span>🏷️ ${room.roomType}</span>
                                <span>👥 容納 ${room.capacity} 人</span>
                                <span>📷 ${room.images?.length || 1} 張相片</span>
                            </div>
                            <p class="room-desc">${room.description || '提供高品質頂級住宿體驗，具備完善獨立設施與舒適氛圍。'}</p>
                        </div>
                        <div class="room-footer">
                            <div class="room-price">NT$ ${room.pricePerNight.toLocaleString()} <small>/ 晚</small></div>
                            <button class="btn btn-primary" onclick="app.previewRoom(${room.id})">預覽與訂房</button>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    },

    previewRoom(roomId) {
        const room = this.state.rooms.find(r => r.id === roomId);
        if (!room) return;
        this.state.currentRoom = room;

        document.getElementById('previewRoomTitle').innerText = room.name;
        document.getElementById('previewRoomType').innerText = room.roomType;
        document.getElementById('previewRoomCapacity').innerText = room.capacity + ' 人';
        document.getElementById('previewRoomPrice').innerText = 'NT$ ' + room.pricePerNight.toLocaleString() + ' / 晚';
        document.getElementById('previewRoomDesc').innerText = room.description || '豪華客房，配備高級衛浴、高速 Wi-Fi 與舒適大床。';
        document.getElementById('previewRoomAmenities').innerText = room.amenities || '含早餐、免費 Wi-Fi、智慧電視、冷暖空調、Mini Bar';
        document.getElementById('bookingRoomId').value = room.id;

        // Auto populate valid dates: today & tomorrow
        const today = new Date();
        const tomorrow = new Date(today);
        tomorrow.setDate(tomorrow.getDate() + 1);

        const formatDate = (d) => d.toISOString().split('T')[0];
        const todayStr = formatDate(today);
        const tomorrowStr = formatDate(tomorrow);

        const checkInInput = document.getElementById('bookingCheckIn');
        const checkOutInput = document.getElementById('bookingCheckOut');

        checkInInput.min = todayStr;
        checkInInput.value = todayStr;

        checkOutInput.min = tomorrowStr;
        checkOutInput.value = tomorrowStr;

        checkInInput.onchange = () => {
            const nextDay = new Date(checkInInput.value);
            nextDay.setDate(nextDay.getDate() + 1);
            const nextDayStr = formatDate(nextDay);
            checkOutInput.min = nextDayStr;
            if (checkOutInput.value <= checkInInput.value) {
                checkOutInput.value = nextDayStr;
            }
        };

        const mainImg = document.getElementById('previewMainImg');
        const galleryContainer = document.getElementById('previewGalleryThumbs');

        const images = room.images && room.images.length > 0 
            ? room.images.map(i => i.imageUrl)
            : ['https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&auto=format&fit=crop&q=80'];

        mainImg.src = images[0];

        galleryContainer.innerHTML = images.map((imgUrl, idx) => `
            <img src="${imgUrl}" class="gallery-thumbnail ${idx === 0 ? 'active' : ''}" 
                 onclick="app.switchGalleryImg('${imgUrl}', this)" alt="房型圖片">
        `).join('');

        this.showModal('roomPreviewModal');
    },

    switchGalleryImg(imgUrl, thumbEl) {
        document.getElementById('previewMainImg').src = imgUrl;
        document.querySelectorAll('.gallery-thumbnail').forEach(t => t.classList.remove('active'));
        thumbEl.classList.add('active');
    },

    // --- Bookings ---

    async handleCreateBooking(e) {
        e.preventDefault();
        if (!this.state.token) {
            alert('請先登入或以訪客身分進行線上訂房');
            this.showModal('loginModal');
            return;
        }

        const rawRoomId = document.getElementById('bookingRoomId').value;
        const roomId = parseInt(rawRoomId, 10);
        const checkInDate = document.getElementById('bookingCheckIn').value;
        const checkOutDate = document.getElementById('bookingCheckOut').value;
        const specialRequests = document.getElementById('bookingRequests').value;

        if (!checkInDate || !checkOutDate) {
            alert('請選擇完整的入住與退房日期！');
            return;
        }

        if (checkOutDate <= checkInDate) {
            alert('退房日期必須晚於入住日期！');
            return;
        }

        try {
            const res = await this.fetchApi('/bookings', {
                method: 'POST',
                body: JSON.stringify({ roomId, checkInDate, checkOutDate, specialRequests })
            });

            this.closeModal('roomPreviewModal');
            alert(`🎉 下單成功！訂單狀態：【下單成功未付款】\n預訂編號: ${res.data.bookingNumber}\n系統已發送確認信至您的信箱: ${this.state.user.email}`);
            this.switchView('myBookingsView');
        } catch (err) {
            alert('訂房失敗: ' + err.message);
        }
    },

    async loadMyBookings() {
        if (!this.state.token) {
            alert('請先登入');
            this.switchView('roomsView');
            return;
        }

        try {
            const res = await this.fetchApi('/bookings/my');
            const tbody = document.getElementById('myBookingsTableBody');
            if (!tbody) return;

            if (res.data.length === 0) {
                tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; color: #64748b; padding: 24px;">尚未有預訂紀錄，歡迎至房型探索挑選喜愛的套房。</td></tr>`;
                return;
            }

            tbody.innerHTML = res.data.map(b => {
                const statusBadge = this.getStatusBadge(b.status);
                let note = '一般會員無權限直接修改訂單狀態';
                if (b.status === 'PENDING_PAYMENT') note = '等待金流付款完成中';
                else if (b.status === 'PAID') note = '已完成付款，等待辦理入住';
                else if (b.status === 'CHECKED_IN') note = '貴賓已辦理入住享受渡假中';
                else if (b.status === 'CHECKED_OUT') note = '已完成退房，期待再次光臨';
                else if (b.status === 'REFUNDED') note = '款項已原路刷退';
                else if (b.status === 'CANCELLED') note = '訂單已取消';

                return `
                    <tr>
                        <td><strong>${b.bookingNumber}</strong></td>
                        <td>${b.roomName} <small style="color:#64748b;">(${b.roomType})</small></td>
                        <td>${b.checkInDate}</td>
                        <td>${b.checkOutDate}</td>
                        <td><strong>NT$ ${b.totalPrice.toLocaleString()}</strong></td>
                        <td>${statusBadge}</td>
                        <td style="color: #64748b; font-size: 0.85rem;">${note}</td>
                    </tr>
                `;
            }).join('');
        } catch (err) {
            console.error(err);
        }
    },

    // --- User Profile & 2FA Setup ---

    async loadProfile() {
        if (!this.state.token) {
            this.switchView('roomsView');
            return;
        }

        try {
            const res = await this.fetchApi('/users/me');
            const u = res.data;

            document.getElementById('profileUsername').value = u.username;
            document.getElementById('profileEmail').value = u.email;
            document.getElementById('profileFullName').value = u.fullName || '';
            document.getElementById('profilePhone').value = u.phone || '';
            
            const rolesArray = Array.isArray(u.roles) ? u.roles : Array.from(u.roles || []);
            const roleLabels = rolesArray.map(r => r === 'ROLE_ADMIN' ? '👑 系統管理者 (ADMIN)' : '👤 尊榮貴賓 (USER)');
            document.getElementById('profileRoles').innerText = roleLabels.join(', ');

            const twoFactorStatusEl = document.getElementById('profile2FAStatus');
            const setup2FABtn = document.getElementById('setup2FABtn');
            const disable2FABtn = document.getElementById('disable2FABtn');

            if (u.twoFactorEnabled) {
                twoFactorStatusEl.innerHTML = `<span style="color: green; font-weight: bold;">已啟用 (Enabled)</span>`;
                setup2FABtn.classList.add('d-none');
                disable2FABtn.classList.remove('d-none');
            } else {
                twoFactorStatusEl.innerHTML = `<span style="color: red; font-weight: bold;">未啟用 (Disabled)</span>`;
                setup2FABtn.classList.remove('d-none');
                disable2FABtn.classList.add('d-none');
            }
        } catch (err) {
            alert('載入個人資料失敗: ' + err.message);
        }
    },

    async handleUpdateProfile(e) {
        e.preventDefault();
        const fullName = document.getElementById('profileFullName').value;
        const phone = document.getElementById('profilePhone').value;
        const newPassword = document.getElementById('profileNewPassword').value;

        try {
            await this.fetchApi('/users/me', {
                method: 'PUT',
                body: JSON.stringify({ fullName, phone, newPassword: newPassword || null })
            });
            alert('個人資料已成功更新！');
            this.loadProfile();
        } catch (err) {
            alert('更新失敗: ' + err.message);
        }
    },

    async setup2FA() {
        try {
            const res = await this.fetchApi('/auth/2fa/setup');
            document.getElementById('setupQrImg').src = res.data.qrCodeDataUri;
            document.getElementById('setupManualKey').innerText = res.data.secretKey;
            this.showModal('setup2FAModal');
        } catch (err) {
            alert('啟動 2FA 設定失敗: ' + err.message);
        }
    },

    async handleConfirm2FA(e) {
        e.preventDefault();
        const code = document.getElementById('confirm2FACode').value;

        try {
            await this.fetchApi(`/auth/2fa/confirm?code=${encodeURIComponent(code)}`, { method: 'POST' });
            this.closeModal('setup2FAModal');
            alert('2FA Google Authenticator 綁定並啟用成功！下次登入需輸入動態驗證碼。');
            this.loadProfile();
        } catch (err) {
            alert('2FA 驗證碼啟用失敗: ' + err.message);
        }
    },

    async disable2FA() {
        const code = prompt('請輸入 Google Authenticator 6 位數驗證碼以停用 2FA:');
        if (!code) return;

        try {
            await this.fetchApi(`/auth/2fa/disable?code=${encodeURIComponent(code)}`, { method: 'POST' });
            alert('2FA 已成功停用');
            this.loadProfile();
        } catch (err) {
            alert('停用失敗: ' + err.message);
        }
    },

    // --- Admin Operations ---

    async loadAdminRooms() {
        try {
            const res = await this.fetchApi('/rooms');
            const tbody = document.getElementById('adminRoomsTableBody');
            tbody.innerHTML = res.data.map(r => `
                <tr>
                    <td>${r.id}</td>
                    <td>${r.name}</td>
                    <td>${r.roomType}</td>
                    <td>NT$ ${r.pricePerNight.toLocaleString()}</td>
                    <td>${r.capacity} 人</td>
                    <td>${r.images?.length || 0} 張</td>
                    <td>
                        <button class="btn btn-sm btn-outline" onclick="app.editRoomModal(${r.id})">編輯</button>
                        <button class="btn btn-sm btn-danger" onclick="app.deleteRoom(${r.id})">刪除</button>
                    </td>
                </tr>
            `).join('');
        } catch (err) {
            console.error(err);
        }
    },

    showAddRoomModal() {
        document.getElementById('adminRoomModalTitle').innerText = '新增房型';
        document.getElementById('adminRoomId').value = '';
        document.getElementById('adminRoomName').value = '';
        document.getElementById('adminRoomType').value = '經典雙人房';
        document.getElementById('adminRoomPrice').value = '3200';
        document.getElementById('adminRoomCapacity').value = '2';
        document.getElementById('adminRoomDesc').value = '';
        document.getElementById('adminRoomAmenities').value = 'Wi-Fi, 早餐, 景觀陽台';
        document.getElementById('adminRoomImageUrls').value = '';
        this.showModal('adminRoomModal');
    },

    async editRoomModal(id) {
        const room = this.state.rooms.find(r => r.id === id);
        if (!room) return;

        document.getElementById('adminRoomModalTitle').innerText = '修改房型';
        document.getElementById('adminRoomId').value = room.id;
        document.getElementById('adminRoomName').value = room.name;
        document.getElementById('adminRoomType').value = room.roomType;
        document.getElementById('adminRoomPrice').value = room.pricePerNight;
        document.getElementById('adminRoomCapacity').value = room.capacity;
        document.getElementById('adminRoomDesc').value = room.description || '';
        document.getElementById('adminRoomAmenities').value = room.amenities || '';
        document.getElementById('adminRoomImageUrls').value = (room.images || []).map(i => i.imageUrl).join('\n');
        this.showModal('adminRoomModal');
    },

    async handleSaveRoom(e) {
        e.preventDefault();
        const id = document.getElementById('adminRoomId').value;
        const name = document.getElementById('adminRoomName').value;
        const roomType = document.getElementById('adminRoomType').value;
        const pricePerNight = parseFloat(document.getElementById('adminRoomPrice').value);
        const capacity = parseInt(document.getElementById('adminRoomCapacity').value);
        const description = document.getElementById('adminRoomDesc').value;
        const amenities = document.getElementById('adminRoomAmenities').value;
        const rawUrls = document.getElementById('adminRoomImageUrls').value;
        const imageUrls = rawUrls.split('\n').map(s => s.trim()).filter(s => s.length > 0);

        const fileInput = document.getElementById('adminRoomFiles');
        if (fileInput.files.length > 0) {
            const formData = new FormData();
            for (let f of fileInput.files) {
                formData.append('files', f);
            }
            try {
                const uploadRes = await this.fetchApi('/media/upload-multiple', {
                    method: 'POST',
                    body: formData
                });
                imageUrls.push(...uploadRes.data);
            } catch (uErr) {
                console.warn('圖片上傳警告:', uErr);
            }
        }

        const payload = { name, roomType, pricePerNight, capacity, description, amenities, imageUrls };

        try {
            if (id) {
                await this.fetchApi(`/rooms/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
                alert('房型修改成功！');
            } else {
                await this.fetchApi('/rooms', { method: 'POST', body: JSON.stringify(payload) });
                alert('房型新增成功！');
            }
            this.closeModal('adminRoomModal');
            this.loadAdminRooms();
            this.loadRooms();
        } catch (err) {
            alert('房型儲存失敗: ' + err.message);
        }
    },

    async deleteRoom(id) {
        if (!confirm('確定要刪除此房型嗎？此操作不可復原。')) return;

        try {
            await this.fetchApi(`/rooms/${id}`, { method: 'DELETE' });
            alert('房型已刪除');
            this.loadAdminRooms();
            this.loadRooms();
        } catch (err) {
            alert('刪除失敗: ' + err.message);
        }
    },

    async loadAdminUsers() {
        try {
            const res = await this.fetchApi('/admin/users/non-admin');
            const tbody = document.getElementById('adminUsersTableBody');
            tbody.innerHTML = res.data.map(u => `
                <tr>
                    <td>${u.id}</td>
                    <td>${u.username}</td>
                    <td>${u.email}</td>
                    <td>${u.fullName || '-'}</td>
                    <td>${u.phone || '-'}</td>
                    <td>${u.twoFactorEnabled ? '🟢 2FA啟用' : '⚪ 未啟用'}</td>
                    <td>
                        <button class="btn btn-sm btn-outline" onclick="app.editNonAdminUserModal(${u.id}, '${u.email}', '${u.fullName || ''}', '${u.phone || ''}', ${u.twoFactorEnabled})">編輯資訊</button>
                    </td>
                </tr>
            `).join('');
        } catch (err) {
            console.error(err);
        }
    },

    editNonAdminUserModal(id, email, fullName, phone, twoFactorEnabled) {
        document.getElementById('adminTargetUserId').value = id;
        document.getElementById('adminTargetUserEmail').value = email;
        document.getElementById('adminTargetUserFullName').value = fullName;
        document.getElementById('adminTargetUserPhone').value = phone;
        document.getElementById('adminTargetUser2FA').checked = twoFactorEnabled;
        this.showModal('adminUserEditModal');
    },

    async handleSaveNonAdminUser(e) {
        e.preventDefault();
        const id = document.getElementById('adminTargetUserId').value;
        const email = document.getElementById('adminTargetUserEmail').value;
        const fullName = document.getElementById('adminTargetUserFullName').value;
        const phone = document.getElementById('adminTargetUserPhone').value;
        const twoFactorEnabled = document.getElementById('adminTargetUser2FA').checked;

        try {
            await this.fetchApi(`/admin/users/non-admin/${id}`, {
                method: 'PUT',
                body: JSON.stringify({ email, fullName, phone, twoFactorEnabled })
            });
            alert('非管理者資訊修改成功！');
            this.closeModal('adminUserEditModal');
            this.loadAdminUsers();
        } catch (err) {
            alert('修改失敗: ' + err.message);
        }
    },

    // --- Admin Bookings ---

    async loadAdminBookings() {
        try {
            const res = await this.fetchApi('/bookings/admin/all');
            this.state.allAdminBookings = res.data || [];
            this.filterAdminBookings();
        } catch (err) {
            console.error(err);
        }
    },

    filterAdminBookings() {
        const keyword = document.getElementById('adminBookingSearchKeyword')?.value?.toLowerCase().trim() || '';
        const status = document.getElementById('adminBookingStatusFilter')?.value || '';

        let list = [...this.state.allAdminBookings];

        if (keyword) {
            list = list.filter(b => 
                (b.bookingNumber && b.bookingNumber.toLowerCase().includes(keyword)) ||
                (b.username && b.username.toLowerCase().includes(keyword)) ||
                (b.userEmail && b.userEmail.toLowerCase().includes(keyword)) ||
                (b.roomName && b.roomName.toLowerCase().includes(keyword))
            );
        }

        if (status) {
            list = list.filter(b => b.status === status);
        }

        this.renderAdminBookings(list);
    },

    renderAdminBookings(bookings) {
        const tbody = document.getElementById('adminBookingsTableBody');
        if (!tbody) return;

        if (bookings.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; color: #64748b; padding: 24px;">查無符合條件的訂單紀錄</td></tr>`;
            return;
        }

        tbody.innerHTML = bookings.map(b => {
            const statusBadge = this.getStatusBadge(b.status);

            return `
                <tr>
                    <td><strong>${b.bookingNumber}</strong></td>
                    <td>
                        <div><strong>${b.username}</strong></div>
                        <small style="color: #64748b;">${b.userEmail}</small>
                    </td>
                    <td>${b.roomName} <small style="color:#64748b;">(${b.roomType})</small></td>
                    <td>${b.checkInDate} ~ ${b.checkOutDate}</td>
                    <td><strong>NT$ ${b.totalPrice.toLocaleString()}</strong></td>
                    <td>${statusBadge}</td>
                    <td>
                        <select class="form-control" style="padding: 6px 10px; font-size: 0.85rem; font-weight: 600; cursor: pointer;" onchange="app.updateAdminBookingStatus(${b.id}, this.value)">
                            <option value="PENDING_PAYMENT" ${b.status === 'PENDING_PAYMENT' ? 'selected' : ''}>⏳ 下單成功未付款</option>
                            <option value="PAID" ${b.status === 'PAID' ? 'selected' : ''}>💳 下單成功已付款 (金流)</option>
                            <option value="CHECKED_IN" ${b.status === 'CHECKED_IN' ? 'selected' : ''}>🏨 已入住 (管理者可改)</option>
                            <option value="CHECKED_OUT" ${b.status === 'CHECKED_OUT' ? 'selected' : ''}>🚪 已退房 (管理者可改)</option>
                            <option value="REFUNDED" ${b.status === 'REFUNDED' ? 'selected' : ''}>↩️ 已退款 (金流)</option>
                            <option value="CANCELLED" ${b.status === 'CANCELLED' ? 'selected' : ''}>🔴 已取消</option>
                        </select>
                    </td>
                </tr>
            `;
        }).join('');
    },

    async updateAdminBookingStatus(bookingId, newStatus) {
        const statusMap = {
            'PENDING_PAYMENT': '下單成功未付款',
            'PAID': '下單成功已付款',
            'CHECKED_IN': '已入住',
            'CHECKED_OUT': '已退房',
            'REFUNDED': '已退款',
            'CANCELLED': '已取消'
        };

        const statusName = statusMap[newStatus] || newStatus;

        if (!confirm(`確定要將此訂單狀態變更為【${statusName}】嗎？\n\n系統將會自動發送 Email 狀態異動通知給該會員。`)) {
            this.loadAdminBookings();
            return;
        }

        try {
            await this.fetchApi(`/bookings/admin/${bookingId}/status?status=${newStatus}`, { method: 'PUT' });
            alert(`訂單狀態已成功變更為【${statusName}】！`);
            this.loadAdminBookings();
        } catch (err) {
            alert('更新訂單狀態失敗: ' + err.message);
            this.loadAdminBookings();
        }
    }
};

document.addEventListener('DOMContentLoaded', () => app.init());
