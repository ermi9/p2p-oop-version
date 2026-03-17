(function() {
    const userId = localStorage.getItem('userId');
    const role = localStorage.getItem('userRole');
    const currentPage = window.location.pathname;

    if (!userId && !currentPage.includes('login.html') && !currentPage.includes('signup.html')) {
        window.location.href = 'login.html';
    }

    //  If trying to access admin page without being an admin
    if (currentPage.includes('admin') && role !== 'ADMIN') {
        window.location.href = 'markets.html';
    }
})();