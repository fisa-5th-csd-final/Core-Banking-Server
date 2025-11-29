(function () {
  function hasAdminRole(token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
      const roles = payload.role || payload.roles || [];
      return Array.isArray(roles) && roles.includes('ROLE_ADMIN');
    } catch (e) {
      return false;
    }
  }

  function extractPayload(json) {
    if (!json || typeof json !== 'object') return null;
    return json?.data?.body?.data ?? json?.data?.data ?? json?.data ?? json?.result ?? json;
  }

  function setResult(text) {
    if (typeof resultBox !== 'undefined' && resultBox) {
      resultBox.textContent = text;
    }
  }

  function getToken() {
    const saved = localStorage.getItem('admin_token');
    if (typeof tokenInput !== 'undefined' && tokenInput) {
      tokenInput.value = saved || '';
    }
    return saved;
  }

  async function callApi(method, url, body) {
    const token = getToken();
    if (!token) {
      setResult('토큰이 없습니다. 로그인 후 다시 시도하세요.');
      return null;
    }
    const init = {
      method,
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
    };
    if (body) init.body = JSON.stringify(body);
    const res = await fetch(url, init);
    const text = await res.text();
    let parsed = null;
    try {
      parsed = JSON.parse(text);
      setResult(JSON.stringify(parsed, null, 2));
    } catch (_) {
      setResult(text);
    }
    return parsed ?? text;
  }

  function ensureAdminOrRedirect() {
    const saved = localStorage.getItem('admin_token');
    if (!saved || !hasAdminRole(saved)) {
      localStorage.removeItem('admin_token');
      localStorage.removeItem('admin_refresh_token');
      window.location.href = '/admin/login';
      return false;
    }
    if (typeof tokenInput !== 'undefined' && tokenInput) {
      tokenInput.value = saved;
    }
    return true;
  }

  function logoutAndRedirect() {
    localStorage.removeItem('admin_token');
    localStorage.removeItem('admin_refresh_token');
    window.location.href = '/admin/login';
  }

  window.adminCommon = {
    hasAdminRole,
    extractPayload,
    callApi,
    ensureAdminOrRedirect,
    logoutAndRedirect,
  };
})();
