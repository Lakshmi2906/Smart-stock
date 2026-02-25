/* SmartStock Enterprise - script.js */
'use strict';

// ── PRODUCT CATALOGUE ──────────────────────────────────────────────
const PRODUCTS = [
    // Breakfast Cereals
    { id: 1, name: "Kellogg's Corn Flakes", weight: "500g", price: 189, originalPrice: 220, stock: 22, category: "Breakfast Cereals", image: "https://m.media-amazon.com/images/I/81cYjr7yo0L._SL1500_.jpg" },
    { id: 2, name: "Quaker Oats Classic", weight: "1kg", price: 249, originalPrice: 290, stock: 7, category: "Oats & Muesli", image: "https://m.media-amazon.com/images/I/71d0wtpbxJL._SL1500_.jpg" },
    { id: 3, name: "Muesli Fruit & Nut", weight: "500g", price: 319, originalPrice: 370, stock: 4, category: "Oats & Muesli", image: "https://m.media-amazon.com/images/I/81SRIDaDC3L.jpg" },
    { id: 5, name: "Kissan Mixed Fruit Jam", weight: "500g", price: 165, originalPrice: 192, stock: 18, category: "Honey & Spreads", image: "https://m.media-amazon.com/images/I/51s7o0usoCL.jpg" },
    { id: 6, name: "Dabur Honey Pure", weight: "500g", price: 220, originalPrice: 260, stock: 9, category: "Honey & Spreads", image: "https://m.media-amazon.com/images/I/71LAn7mX1GL._SL1500_.jpg" },
    { id: 7, name: "MyFitness Peanut Butter Crunchy", weight: "1kg", price: 399, originalPrice: 480, stock: 3, category: "Peanut Butter", image: "https://m.media-amazon.com/images/I/71p-Y-p-8eL._SL1500_.jpg" },
    { id: 8, name: "Alpino Chocolate Peanut Butter", weight: "1kg", price: 449, originalPrice: 520, stock: 6, category: "Peanut Butter", image: "https://m.media-amazon.com/images/I/71nJtF2T4YL.jpg" },
    { id: 9, name: "Tata Tea Premium", weight: "500g", price: 259, originalPrice: 290, stock: 35, category: "Tea & Coffee Powder", image: "https://m.media-amazon.com/images/I/714gtsczZwL._SL1500_.jpg" },
    { id: 10, name: "Nescafe Classic Coffee", weight: "200g", price: 429, originalPrice: 499, stock: 12, category: "Tea & Coffee Powder", image: "https://m.media-amazon.com/images/I/71kSJv+gUIL._SL1500_.jpg" },
    { id: 11, name: "MTR Instant Khichdi Mix", weight: "300g", price: 79, originalPrice: 95, stock: 8, category: "Batter & Mixes", image: "https://m.media-amazon.com/images/I/81x0f1V-W4L.jpg" },
    { id: 12, name: "Everest Chicken Masala", weight: "100g", price: 55, originalPrice: 65, stock: 40, category: "Masala Powders", image: "https://m.media-amazon.com/images/I/81I-R9n78ML._SL1500_.jpg" },
    { id: 13, name: "MDH Garam Masala", weight: "100g", price: 65, originalPrice: 78, stock: 28, category: "Masala Powders", image: "https://m.media-amazon.com/images/I/61MyqJ4PkEL.jpg" },
    { id: 14, name: "Premium Cashews Whole", weight: "500g", price: 549, originalPrice: 650, stock: 5, category: "Dry Fruits", image: "https://m.media-amazon.com/images/I/711pYeieACL._SL1500_.jpg" },
    { id: 15, name: "Almonds California Raw", weight: "500g", price: 499, originalPrice: 580, stock: 11, category: "Dry Fruits", image: "https://m.media-amazon.com/images/I/81bXeu5KTOL._SL1500_.jpg" },
    { id: 16, name: "Medjool Dates Premium", weight: "500g", price: 389, originalPrice: 450, stock: 7, category: "Dates & Mixed Seeds", image: "https://m.media-amazon.com/images/I/71eFCPT0lIL._SL1500_.jpg" },
    { id: 18, name: "Green Cardamom Whole", weight: "100g", price: 225, originalPrice: 270, stock: 15, category: "Whole Spices", image: "https://m.media-amazon.com/images/I/61KqfGQ8nEL._SL1500_.jpg" },
    { id: 19, name: "Tata Rock Salt Sendha", weight: "1kg", price: 65, originalPrice: 80, stock: 50, category: "Salt, Sugar & Jaggery", image: "https://m.media-amazon.com/images/I/510-o9RtOVL._SL1000_.jpg" },
    { id: 20, name: "Aashirvaad Whole Wheat Atta", weight: "5kg", price: 299, originalPrice: 340, stock: 20, category: "Atta", image: "https://m.media-amazon.com/images/I/91ytlLbe1oL._SL1500_.jpg" },
    { id: 21, name: "Fortune Sunflower Oil", weight: "2L", price: 289, originalPrice: 340, stock: 14, category: "Cooking Oil", image: "https://m.media-amazon.com/images/I/81mWTR+nfdL._SL1500_.jpg" },
    { id: 22, name: "Organic Sona Masuri Rice", weight: "5kg", price: 399, originalPrice: 460, stock: 9, category: "Rice", image: "https://m.media-amazon.com/images/I/71e0tntLp1L._SL1500_.jpg" },
    { id: 23, name: "Amul Pure Ghee", weight: "1L", price: 599, originalPrice: 680, stock: 6, category: "Ghee", image: "https://m.media-amazon.com/images/I/71b5TfzNNIL._SL1500_.jpg" },
    { id: 24, name: "Chana Dal Yellow Split", weight: "1kg", price: 129, originalPrice: 155, stock: 33, category: "Dals & Pulses", image: "https://m.media-amazon.com/images/I/71RXCg+DvuL._SL1000_.jpg" },
    { id: 25, name: "Moong Dal Washed", weight: "1kg", price: 149, originalPrice: 175, stock: 25, category: "Dals & Pulses", image: "https://m.media-amazon.com/images/I/81Mst76D5oL.jpg" },
    { id: 26, name: "Fresh Chicken Curry Cut", weight: "1kg", price: 249, originalPrice: 280, stock: 8, category: "Chicken", image: "https://m.media-amazon.com/images/I/718yG6d+eNL._SL1500_.jpg" },
    { id: 27, name: "Rohu Fish Fresh", weight: "1kg", price: 199, originalPrice: 240, stock: 4, category: "Fish & Seafood", image: "https://m.media-amazon.com/images/I/61-yeci9imL._SL1500_.jpg" },
    { id: 28, name: "Farm Fresh Eggs", weight: "12 pcs", price: 89, originalPrice: 105, stock: 60, category: "Egg", image: "https://m.media-amazon.com/images/I/8176oVxtf0L._SL1500_.jpg" },
    { id: 32, name: "Organic Jaggery Powder", weight: "500g", price: 110, originalPrice: 135, stock: 13, category: "Salt, Sugar & Jaggery", image: "https://m.media-amazon.com/images/I/71Yy8gW9TML.jpg" }
];

const CATEGORIES = [
    "All Products", "Breakfast Cereals", "Oats & Muesli", "Honey & Spreads",
    "Peanut Butter", "Tea & Coffee Powder", "Batter & Mixes", "Masala Powders", "Dry Fruits",
    "Dates & Mixed Seeds", "Whole Spices", "Salt, Sugar & Jaggery", "Atta", "Cooking Oil",
    "Dals & Pulses", "Ghee", "Rice", "Chicken", "Fish & Seafood",
    "Egg", "Top Picks"
];

// Category Images
const CAT_IMAGES = {
    "All Products": "https://m.media-amazon.com/images/I/71Y15-pOfDL._SL200_.jpg",
    "Breakfast Cereals": "https://m.media-amazon.com/images/I/81cYjr7yo0L._SL200_.jpg",
    "Oats & Muesli": "https://m.media-amazon.com/images/I/71d0wtpbxJL._SL200_.jpg",
    "Honey & Spreads": "https://m.media-amazon.com/images/I/71LAn7mX1GL._SL200_.jpg",
    "Peanut Butter": "https://m.media-amazon.com/images/I/71p-Y-p-8eL._SL200_.jpg",
    "Tea & Coffee Powder": "https://m.media-amazon.com/images/I/714gtsczZwL._SL200_.jpg",
    "Batter & Mixes": "https://m.media-amazon.com/images/I/91ytlLbe1oL._SL200_.jpg",
    "Masala Powders": "https://m.media-amazon.com/images/I/81I-R9n78ML._SL200_.jpg",
    "Dry Fruits": "https://m.media-amazon.com/images/I/711pYeieACL._SL200_.jpg",
    "Dates & Mixed Seeds": "https://m.media-amazon.com/images/I/71eFCPT0lIL._SL200_.jpg",
    "Whole Spices": "https://m.media-amazon.com/images/I/61KqfGQ8nEL._SL200_.jpg",
    "Salt, Sugar & Jaggery": "https://m.media-amazon.com/images/I/510-o9RtOVL._SL200_.jpg",
    "Atta": "https://m.media-amazon.com/images/I/91ytlLbe1oL._SL200_.jpg",
    "Cooking Oil": "https://m.media-amazon.com/images/I/81mWTR+nfdL._SL200_.jpg",
    "Dals & Pulses": "https://m.media-amazon.com/images/I/71RXCg+DvuL._SL200_.jpg",
    "Ghee": "https://m.media-amazon.com/images/I/51SeHKW4jHL._SL200_.jpg",
    "Rice": "https://m.media-amazon.com/images/I/71e0tntLp1L._SL200_.jpg",
    "Chicken": "https://m.media-amazon.com/images/I/718yG6d+eNL._SL200_.jpg",
    "Fish & Seafood": "https://m.media-amazon.com/images/I/61-yeci9imL._SL200_.jpg",
    "Egg": "https://m.media-amazon.com/images/I/8176oVxtf0L._SL200_.jpg",
    "Top Picks": "https://m.media-amazon.com/images/I/71Y15-pOfDL._SL200_.jpg"
};

// ── CART STATE ──────────────────────────────────────────────────────
let cart = JSON.parse(localStorage.getItem('sme_cart') || '[]');
// cart item: { id, name, weight, price, image, qty, locked, lockExpires }

// ── HELPERS ─────────────────────────────────────────────────────────
function saveCart() { localStorage.setItem('sme_cart', JSON.stringify(cart)); }

function updateBadge() {
    const total = cart.reduce((s, i) => s + i.qty, 0);
    document.querySelectorAll('.cart-badge').forEach(b => {
        b.textContent = total;
        b.style.display = total > 0 ? '' : 'none';
    });
}

function showToast(msg, type = 'success') {
    let wrap = document.getElementById('toast-wrap');
    if (!wrap) {
        wrap = document.createElement('div');
        wrap.id = 'toast-wrap';
        wrap.style.cssText = 'position:fixed;bottom:24px;right:24px;z-index:9999;display:flex;flex-direction:column;gap:10px;';
        document.body.appendChild(wrap);
    }
    const t = document.createElement('div');
    const bg = type === 'success' ? '#22c55e' : type === 'error' ? '#ef4444' : '#f97316';
    t.style.cssText = `background:${bg};color:#fff;padding:12px 20px;border-radius:10px;font-size:14px;font-weight:500;font-family:Inter,sans-serif;box-shadow:0 4px 20px rgba(0,0,0,0.15);animation:slideIn .3s ease;max-width:320px;`;
    t.textContent = msg;
    wrap.appendChild(t);
    setTimeout(() => t.remove(), 3500);
}

// ── STOCK HELPERS ───────────────────────────────────────────────────
function getStockStatus(stock) {
    if (stock === 0) return { label: 'Out of Stock', cls: 'out-of-stock' };
    if (stock < 10) return { label: `Only ${stock} left!`, cls: 'low-stock' };
    return { label: 'In Stock', cls: 'in-stock' };
}

// ── PRODUCT CARD ────────────────────────────────────────────────────
function buildProductCard(p) {
    const { label: stockLabel, cls: stockCls } = getStockStatus(p.stock);
    const inCart = cart.find(c => c.id === p.id);

    const card = document.createElement('div');
    card.className = 'product-card';
    card.dataset.id = p.id;

    const stockColor = stockCls === 'low-stock' ? '#d97706' : stockCls === 'out-of-stock' ? '#9ca3af' : '#16a34a';

    const actionHTML = p.stock === 0
        ? `<button class="btn-add" disabled style="opacity:0.45;cursor:not-allowed;border-color:#ccc;color:#999;">Sold Out</button>`
        : inCart
            ? `<div class="qty-counter">
                   <button class="qty-btn" onclick="changeQty(${p.id},-1)">−</button>
                   <span class="qty-num">${inCart.qty}</span>
                   <button class="qty-btn" onclick="changeQty(${p.id},1)">+</button>
               </div>`
            : `<button class="btn-add" onclick="addToCart(${p.id})">${p.stock < 10 ? 'Add & Lock' : 'Add to Cart'}</button>`;

    card.innerHTML = `
        <div class="product-img-wrap">
            <img src="${p.image}" alt="${p.name}" loading="lazy"
                 onerror="this.closest('.product-card').style.display='none'">
        </div>
        <div class="product-info">
            <div class="product-name">${p.name}</div>
            <div class="product-weight">${p.weight}</div>
            <div style="font-size:11px;font-weight:600;color:${stockColor};margin-bottom:8px;">${stockLabel}</div>
            <div class="price-row" style="margin-bottom:10px;">
                <span class="price-main">₹${p.price}</span>
                ${p.originalPrice > p.price ? `<span class="price-orig">₹${p.originalPrice}</span>` : ''}
            </div>
            ${actionHTML}
        </div>`;
    return card;
}

// ── CURRENT CATEGORY STATE ─────────────────────────────────────────
let currentCategory = 'All Products';

// ── FILTER / RENDER ──────────────────────────────────────────────────
function filterProducts(category) {
    const grid = document.getElementById('product-grid');
    const title = document.getElementById('products-title');
    if (!grid) return;
    currentCategory = category;

    // Highlight active sidebar & mobile pill
    document.querySelectorAll('.sidebar-item').forEach(el =>
        el.classList.toggle('active', el.dataset.cat === category));
    document.querySelectorAll('.mobile-cat-pill').forEach(el =>
        el.classList.toggle('active', el.dataset.cat === category));

    let list = category === 'All Products' || !category
        ? PRODUCTS
        : category === 'Top Picks'
            ? PRODUCTS.filter(p => p.stock > 0 && p.stock < 10)
            : PRODUCTS.filter(p => p.category === category);

    if (title) {
        const parts = category.split(' ');
        title.innerHTML = parts.length > 1
            ? `${parts.slice(0, -1).join(' ')} <span>${parts[parts.length - 1]}</span>`
            : `<span>${category}</span>`;
    }

    grid.innerHTML = '';
    if (list.length === 0) {
        grid.innerHTML = '<div style="grid-column:1/-1;text-align:center;padding:40px;color:#888;">No products in this category.</div>';
        return;
    }
    list.forEach(p => grid.appendChild(buildProductCard(p)));
}

function handleSearch(q) {
    const grid = document.getElementById('product-grid');
    if (!grid) return;
    if (!q.trim()) { filterProducts('All Products'); return; }
    const results = PRODUCTS.filter(p =>
        p.name.toLowerCase().includes(q.toLowerCase()) ||
        p.category.toLowerCase().includes(q.toLowerCase())
    );
    grid.innerHTML = '';
    if (results.length === 0) {
        grid.innerHTML = '<div style="grid-column:1/-1;text-align:center;padding:40px;color:#888;">No products found.</div>';
        return;
    }
    results.forEach(p => grid.appendChild(buildProductCard(p)));
}

// ── SIDEBAR & MOBILE CATS ────────────────────────────────────────────
function buildSidebar() {
    const container = document.getElementById('sidebar-cats');
    if (!container) return;
    CATEGORIES.forEach(cat => {
        const div = document.createElement('div');
        div.className = 'sidebar-item';
        div.dataset.cat = cat;
        div.innerHTML = `<span>${cat}</span>`;
        div.onclick = () => filterProducts(cat);
        container.appendChild(div);
    });
}

function buildMobileCats() {
    const container = document.getElementById('mobile-cats');
    if (!container) return;
    CATEGORIES.forEach(cat => {
        const pill = document.createElement('button');
        pill.className = 'mobile-cat-pill';
        pill.dataset.cat = cat;
        pill.textContent = cat;
        pill.onclick = () => filterProducts(cat);
        container.appendChild(pill);
    });
}

// ── CART LOGIC ───────────────────────────────────────────────────────
function addToCart(id) {
    const p = PRODUCTS.find(x => x.id === id);
    if (!p || p.stock === 0) { showToast('This item is out of stock.', 'error'); return; }

    const existing = cart.find(c => c.id === id);
    if (existing) { changeQty(id, 1); return; }

    const isLowStock = p.stock < 10;
    const lockExpires = isLowStock ? new Date(Date.now() + 7 * 60 * 1000).toISOString() : null;

    cart.push({ id: p.id, name: p.name, weight: p.weight, price: p.price, image: p.image, qty: 1, locked: isLowStock, lockExpires });
    saveCart();
    updateBadge();
    filterProducts(currentCategory);

    if (isLowStock) {
        showToast(`🔒 ${p.name} locked for 7 min! Checkout fast.`, 'warning');
    } else {
        showToast(`${p.name} added to cart!`, 'success');
    }
}

function changeQty(id, delta) {
    const idx = cart.findIndex(c => c.id === id);
    if (idx === -1) return;
    cart[idx].qty += delta;
    if (cart[idx].qty <= 0) cart.splice(idx, 1);
    saveCart();
    updateBadge();
    filterProducts(currentCategory);
}

// ── CART PAGE ────────────────────────────────────────────────────────
function renderCartPage() {
    const wrap = document.getElementById('cart-items-wrap');
    if (!wrap) return;
    wrap.innerHTML = '';

    if (cart.length === 0) {
        checkEmptyCart();
        return;
    }

    cart.forEach(item => {
        const div = document.createElement('div');
        div.className = 'cart-item-card';
        div.id = `cart - item - ${item.id} `;
        const timeLeft = item.locked && item.lockExpires
            ? Math.max(0, Math.floor((new Date(item.lockExpires) - Date.now()) / 1000))
            : 0;
        div.innerHTML = `
    < img src = "${item.image}" alt = "${item.name}" class="cart-item-img" onerror = "this.style.display='none'" >
        <div class="cart-item-info">
            <div class="cart-item-name">${item.name}</div>
            <div class="cart-item-weight">${item.weight}</div>
            ${item.locked ? `<div class="lock-timer" id="timer-${item.id}">🔒 ${formatTime(timeLeft)} remaining</div>` : ''}
            <div class="cart-item-price">₹${item.price} × ${item.qty} = <strong>₹${(item.price * item.qty).toLocaleString('en-IN')}</strong></div>
            <div class="cart-item-actions">
                <button class="qty-btn" onclick="cartChangeQty(${item.id}, -1)">−</button>
                <span class="qty-val">${item.qty}</span>
                <button class="qty-btn" onclick="cartChangeQty(${item.id}, 1)">+</button>
                <button class="btn-remove" onclick="removeFromCart(${item.id})">Remove</button>
            </div>
        </div>`;
        wrap.appendChild(div);
    });

    renderCartSummary();
}

function renderCartSummary() {
    const subtotalEl = document.getElementById('cart-subtotal');
    const deliveryEl = document.getElementById('cart-delivery');
    const totalEl = document.getElementById('cart-total');
    if (!subtotalEl) return;
    const subtotal = cart.reduce((s, i) => s + i.price * i.qty, 0);
    const delivery = subtotal >= 499 ? 0 : 40;
    const total = subtotal + delivery;
    subtotalEl.textContent = `₹${subtotal.toLocaleString('en-IN')} `;
    if (deliveryEl) deliveryEl.textContent = delivery === 0 ? 'FREE' : `₹${delivery} `;
    if (totalEl) totalEl.textContent = `₹${total.toLocaleString('en-IN')} `;
}

function cartChangeQty(id, delta) {
    const idx = cart.findIndex(c => c.id === id);
    if (idx === -1) return;
    cart[idx].qty = Math.max(1, cart[idx].qty + delta);
    saveCart();
    renderCartPage();
}

function removeFromCart(id) {
    cart = cart.filter(c => c.id !== id);
    saveCart();
    updateBadge();
    renderCartPage();
    checkEmptyCart();
}

function checkEmptyCart() {
    const empty = document.getElementById('cart-empty');
    const content = document.getElementById('cart-content');
    if (!empty) return;
    if (cart.length === 0) {
        empty.style.display = '';
        if (content) content.style.display = 'none';
    } else {
        empty.style.display = 'none';
        if (content) content.style.display = '';
    }
}

function formatTime(secs) {
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return `${m}:${String(s).padStart(2, '0')} `;
}

function startLockTimers() {
    setInterval(() => {
        cart.forEach(item => {
            if (!item.locked || !item.lockExpires) return;
            const remaining = Math.max(0, Math.floor((new Date(item.lockExpires) - Date.now()) / 1000));
            const el = document.getElementById(`timer - ${item.id} `);
            if (el) {
                if (remaining === 0) {
                    el.textContent = '⚠️ Lock expired — item may be removed';
                    el.style.color = '#e53e3e';
                } else {
                    el.textContent = `🔒 ${formatTime(remaining)} remaining`;
                }
            }
        });
    }, 1000);
}

// ── CHECKOUT PAGE ─────────────────────────────────────────────────────
function renderCheckoutSummary() {
    const wrap = document.getElementById('checkout-items');
    const subtotalEl = document.getElementById('co-subtotal');
    const deliveryEl = document.getElementById('co-delivery');
    const totalEl = document.getElementById('co-total');
    if (!wrap) return;

    wrap.innerHTML = '';
    cart.forEach(item => {
        const div = document.createElement('div');
        div.className = 'co-item';
        div.innerHTML = `
    < img src = "${item.image}" alt = "${item.name}" class="co-item-img" onerror = "this.style.display='none'" >
        <div class="co-item-info">
            <div class="co-item-name">${item.name} × ${item.qty}</div>
            <div class="co-item-price">₹${(item.price * item.qty).toLocaleString('en-IN')}</div>
        </div>`;
        wrap.appendChild(div);
    });

    const subtotal = cart.reduce((s, i) => s + i.price * i.qty, 0);
    const delivery = subtotal >= 499 ? 0 : 40;
    const total = subtotal + delivery;
    if (subtotalEl) subtotalEl.textContent = `₹${subtotal.toLocaleString('en-IN')} `;
    if (deliveryEl) deliveryEl.textContent = delivery === 0 ? 'FREE' : `₹${delivery} `;
    if (totalEl) totalEl.textContent = `₹${total.toLocaleString('en-IN')} `;
}

function placeOrder() {
    if (cart.length === 0) { showToast('Your cart is empty!', 'error'); return; }
    const subtotal = cart.reduce((s, i) => s + i.price * i.qty, 0);
    const delivery = subtotal >= 499 ? 0 : 40;
    const total = subtotal + delivery;
    const orderId = 'SSE-' + Date.now().toString().slice(-6);
    localStorage.setItem('sme_last_order', JSON.stringify({ orderId, total }));
    cart = [];
    saveCart();
    updateBadge();
    window.location.href = 'checkout.html?success=1';
}

// ── ADMIN PANEL ──────────────────────────────────────────────────────
function showAdminTab(tab) {
    document.querySelectorAll('.admin-tab-btn').forEach(b =>
        b.classList.toggle('active', b.dataset.tab === tab));
    document.querySelectorAll('.admin-panel').forEach(p =>
        p.style.display = p.id === 'panel-' + tab ? '' : 'none');
    if (tab === 'dashboard') renderAdminDashboard();
}

function renderAdminDashboard() {
    const grid = document.getElementById('admin-products-grid');
    if (!grid) return;
    grid.innerHTML = '';
    PRODUCTS.forEach(p => {
        const { label, cls } = getStockStatus(p.stock);
        const card = document.createElement('div');
        card.className = 'admin-product-row';
        card.innerHTML = `
    < img src = "${p.image}" alt = "${p.name}" style = "width:48px;height:48px;object-fit:contain;border-radius:8px;" onerror = "this.style.display='none'" >
            <div style="flex:1;min-width:0">
                <div style="font-weight:600;font-size:14px">${p.name}</div>
                <div style="font-size:12px;color:#888">${p.category} · ${p.weight}</div>
            </div>
            <span class="stock-status ${cls}" style="white-space:nowrap">${label}</span>
            <span style="font-weight:700;color:#F97316">₹${p.price}</span>`;
        grid.appendChild(card);
    });

    const el = id => document.getElementById(id);
    if (el('admin-total-products')) el('admin-total-products').textContent = PRODUCTS.length;
    if (el('admin-low-stock')) el('admin-low-stock').textContent = PRODUCTS.filter(p => p.stock > 0 && p.stock < 10).length;
    if (el('admin-out-of-stock')) el('admin-out-of-stock').textContent = PRODUCTS.filter(p => p.stock === 0).length;
    if (el('admin-active-locks')) el('admin-active-locks').textContent = cart.filter(c => c.locked).length;
}

// ── AUTH STATE ───────────────────────────────────────────────────────
function getUser() {
    try { return JSON.parse(localStorage.getItem('sme_user') || 'null'); } catch { return null; }
}

function logoutUser() {
    localStorage.removeItem('sme_user');
    localStorage.removeItem('sme_cart');
    cart = [];
    updateBadge();
    restoreAuthState();
    showToast('Logged out successfully.', 'success');
}

function restoreAuthState() {
    const user = getUser();
    const btn = document.querySelector('.btn-login');
    if (!btn) return;

    if (user && user.loggedIn) {
        const masked = '👤  +91 ••••' + user.phone.slice(-4) + '  ▾';
        btn.textContent = masked;
        btn.style.background = '#fff3ea';
        btn.style.color = '#F97316';
        btn.style.border = '1.5px solid #F97316';
        btn.style.borderRadius = '10px';
        btn.style.fontWeight = '600';
        btn.style.fontSize = '13px';
        btn.onclick = (e) => {
            e.stopPropagation();
            let dd = document.getElementById('profile-dropdown');
            if (dd) { dd.remove(); return; }
            dd = document.createElement('div');
            dd.id = 'profile-dropdown';
            dd.style.cssText = 'position:absolute;top:110%;right:0;min-width:200px;background:#fff;border-radius:12px;box-shadow:0 8px 30px rgba(0,0,0,0.15);z-index:9999;padding:8px 0;font-size:14px;';
            dd.innerHTML = `
                <div style="padding:12px 16px;border-bottom:1px solid #f0f0f0;">
                    <div style="font-weight:700;color:#1a1a1a;font-size:15px;">${user.phone}</div>
                    <div style="color:#F97316;font-size:12px;margin-top:2px;font-weight:600;">✓ Logged in</div>
                </div>
                <a href="cart.html" style="display:block;padding:10px 16px;color:#333;text-decoration:none;font-size:14px;" onmouseover="this.style.background='#fff8f0'" onmouseout="this.style.background=''">🛒 My Cart</a>
                <a href="checkout.html" style="display:block;padding:10px 16px;color:#333;text-decoration:none;font-size:14px;" onmouseover="this.style.background='#fff8f0'" onmouseout="this.style.background=''">📦 My Orders</a>
                <div style="border-top:1px solid #f0f0f0;margin-top:4px;"></div>
                <button onclick="logoutUser()" style="width:100%;text-align:left;padding:10px 16px;border:none;background:none;cursor:pointer;color:#e53e3e;font-size:14px;font-weight:600;" onmouseover="this.style.background='#fff0f0'" onmouseout="this.style.background=''">
                    🚪 Logout
                </button>`;
            btn.style.position = 'relative';
            btn.appendChild(dd);
            setTimeout(() => document.addEventListener('click', function handler() {
                dd.remove(); document.removeEventListener('click', handler);
            }), 50);
        };
    } else {
        btn.textContent = 'Login';
        btn.style.background = '';
        btn.style.color = '';
        btn.style.border = '';
        btn.style.borderRadius = '';
        btn.style.fontWeight = '';
        btn.style.fontSize = '';
        btn.onclick = () => openOtpModal();
    }
}

// ── OTP MODAL ────────────────────────────────────────────────────────
function openOtpModal() {
    const user = getUser();
    if (user && user.loggedIn) return;
    const overlay = document.getElementById('otp-overlay');
    if (overlay) overlay.classList.add('show');
}

function closeOtpModal() {
    const overlay = document.getElementById('otp-overlay');
    if (overlay) overlay.classList.remove('show');
    const s1 = document.getElementById('otp-step1');
    const s2 = document.getElementById('otp-step2');
    if (s1) s1.style.display = '';
    if (s2) s2.style.display = 'none';
}

function sendOtp() {
    const phoneEl = document.getElementById('otp-phone');
    const phone = phoneEl ? phoneEl.value.trim() : '';
    if (!/^[6-9][0-9]{9}$/.test(phone)) {
        showToast('Enter a valid 10-digit Indian mobile number', 'error');
        return;
    }
    document.getElementById('otp-step1').style.display = 'none';
    document.getElementById('otp-step2').style.display = '';
    const hint = document.getElementById('otp-phone-hint');
    if (hint) hint.textContent = `OTP sent to + 91 ${phone.slice(0, 2)}**** ${phone.slice(-2)} `;
    showToast(`OTP sent to + 91 ${phone} (use: 123456)`, 'success');
}

function verifyOtp() {
    const otpEl = document.getElementById('otp-code');
    const otp = otpEl ? otpEl.value.trim() : '';
    if (otp.length < 6) { showToast('Enter the 6-digit OTP', 'error'); return; }
    const phone = document.getElementById('otp-phone') ? document.getElementById('otp-phone').value.trim() : '';
    localStorage.setItem('sme_user', JSON.stringify({ phone, loggedIn: true }));
    showToast('Login successful! Welcome to SmartStock.', 'success');
    closeOtpModal();
    restoreAuthState();
}

// ── INIT ─────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    updateBadge();
    restoreAuthState();

    const searchInput = document.getElementById('search-input');
    if (searchInput) {
        let debounce;
        searchInput.addEventListener('input', e => {
            clearTimeout(debounce);
            debounce = setTimeout(() => handleSearch(e.target.value), 300);
        });
        searchInput.addEventListener('keydown', e => {
            if (e.key === 'Enter') handleSearch(e.target.value);
        });
    }

    if (document.getElementById('product-grid')) {
        buildSidebar();
        buildMobileCats();
        filterProducts('All Products');
    }

    if (document.getElementById('cart-items-wrap')) {
        renderCartPage();
        startLockTimers();
        checkEmptyCart();
    }

    const successParam = new URLSearchParams(window.location.search).get('success');
    if (document.getElementById('checkout-form-area')) {
        renderCheckoutSummary();
    }
    if (successParam === '1') {
        const orderCard = document.getElementById('order-success');
        const formArea = document.getElementById('checkout-form-area');
        if (orderCard) orderCard.style.display = '';
        if (formArea) formArea.style.display = 'none';
        const lastOrder = JSON.parse(localStorage.getItem('sme_last_order') || '{}');
        const oid = document.getElementById('success-order-id');
        const oamt = document.getElementById('success-amount');
        if (oid) oid.textContent = lastOrder.orderId || 'SSE-XXXX';
        if (oamt) oamt.textContent = lastOrder.total ? `\u20B9${lastOrder.total.toLocaleString('en-IN')} ` : '';
    }

    if (document.getElementById('tab-dashboard')) {
        showAdminTab('dashboard');
    }

    const otpOverlay = document.getElementById('otp-overlay');
    if (otpOverlay) {
        otpOverlay.addEventListener('click', e => { if (e.target === otpOverlay) closeOtpModal(); });
    }

    document.querySelectorAll('.payment-method-card').forEach(card => {
        card.addEventListener('click', () => {
            document.querySelectorAll('.payment-method-card').forEach(c => c.classList.remove('selected'));
            card.classList.add('selected');
            const radio = card.querySelector('input[type=radio]');
            if (radio) radio.checked = true;
        });
    });
});
