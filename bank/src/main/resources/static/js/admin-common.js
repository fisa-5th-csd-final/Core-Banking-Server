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

  async function callApi(method, url, body) {
    const init = {
      method,
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
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
    // 클라이언트에서 로그인 여부를 판별하지 않고, 호출 시 401이면 서버 응답에 따름
    return Promise.resolve(true);
  }

  function logoutAndRedirect() {
    fetch('/api/logout', { method: 'POST', credentials: 'include' }).finally(() => {
      window.location.href = '/admin/login';
    });
  }

  window.adminCommon = {
    hasAdminRole,
    extractPayload,
    callApi,
    ensureAdminOrRedirect,
    logoutAndRedirect,
  };
})();
