// Configuração do AJAX para incluir o token em todas as requisições
$.ajaxSetup({
    beforeSend: function(xhr) {
        const token = localStorage.getItem('token');
        if (token) {
            xhr.setRequestHeader('Authorization', 'Bearer ' + token);
        }
    }
});

// Função para verificar se o usuário está autenticado
function isAuthenticated() {
    return localStorage.getItem('token') !== null;
}

// Função para obter o usuário atual
function getCurrentUser() {
    const userStr = localStorage.getItem('usuario');
    return userStr ? JSON.parse(userStr) : null;
}

// Função para verificar se o usuário tem uma role específica
function hasRole(role) {
    const user = getCurrentUser();
    return user && user.tipo === role;
}

// Função para fazer logout
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    window.location.href = '/login?logout=true';
}

// Interceptar erros 401 (não autorizado) e redirecionar para login
$(document).ajaxError(function(event, jqXHR) {
    if (jqXHR.status === 401) {
        logout();
    }
});

// Verificar autenticação em páginas protegidas
$(document).ready(function() {
    const publicPages = ['/login', '/registro', '/'];
    const currentPage = window.location.pathname;

    if (!publicPages.includes(currentPage) && !isAuthenticated()) {
        window.location.href = '/login';
    }
}); 