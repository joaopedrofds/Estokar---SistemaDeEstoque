// Máscara e validação para CPF/CNPJ e telefone
document.addEventListener('DOMContentLoaded', function() {
    // Máscara CPF/CNPJ
    function maskCpfCnpj(value) {
        value = value.replace(/\D/g, '');
        if (value.length <= 11) {
            // CPF: 000.000.000-00
            value = value.replace(/(\d{3})(\d)/, '$1.$2');
            value = value.replace(/(\d{3})(\d)/, '$1.$2');
            value = value.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
        } else {
            // CNPJ: 00.000.000/0000-00
            value = value.replace(/(\d{2})(\d)/, '$1.$2');
            value = value.replace(/(\d{3})(\d)/, '$1.$2');
            value = value.replace(/(\d{3})(\d)/, '$1/$2');
            value = value.replace(/(\d{4})(\d{1,2})$/, '$1-$2');
        }
        return value;
    }

    // Máscara telefone
    function maskTelefone(value) {
        value = value.replace(/\D/g, '');
        if (value.length > 10) {
            // Celular: (00) 00000-0000
            value = value.replace(/(\d{2})(\d)/, '($1) $2');
            value = value.replace(/(\d{5})(\d)/, '$1-$2');
        } else {
            // Fixo: (00) 0000-0000
            value = value.replace(/(\d{2})(\d)/, '($1) $2');
            value = value.replace(/(\d{4})(\d)/, '$1-$2');
        }
        return value;
    }

    // Máscara CEP
    function maskCep(value) {
        value = value.replace(/\D/g, '');
        if (value.length > 5) {
            value = value.replace(/(\d{5})(\d)/, '$1-$2');
        }
        return value;
    }

    // Aplica máscara no campo de CEP
    const cepInput = document.getElementById('cep');
    if (cepInput) {
        cepInput.addEventListener('input', function(e) {
            this.value = maskCep(this.value);
        });
    }

    // Aplica máscara nos campos de cliente e funcionário
    const cpfCnpjInput = document.getElementById('cpfCnpj') || document.getElementById('cpf');
    if (cpfCnpjInput) {
        cpfCnpjInput.addEventListener('input', function(e) {
            this.value = maskCpfCnpj(this.value);
        });
    }
    const telefoneInput = document.getElementById('telefone');
    if (telefoneInput) {
        telefoneInput.addEventListener('input', function(e) {
            this.value = maskTelefone(this.value);
        });
    }

    // Validação ao submeter formulário de cliente/funcionário
    const form = document.querySelector('form');
    if (form && (cpfCnpjInput || telefoneInput)) {
        form.addEventListener('submit', function(e) {
            let erro = '';
            if (cpfCnpjInput) {
                const val = cpfCnpjInput.value.replace(/\D/g, '');
                if (val.length !== 11 && val.length !== 14) {
                    erro = 'CPF deve ter 11 dígitos ou CNPJ 14 dígitos.';
                }
            }
            if (!erro && telefoneInput) {
                const val = telefoneInput.value.replace(/\D/g, '');
                if (val.length < 10 || val.length > 11) {
                    erro = 'Telefone deve ter 10 ou 11 dígitos.';
                }
            }
            if (erro) {
                e.preventDefault();
                showNotification(erro, 'error');
            }
        });
    }
});
// UI Effects e melhorias visuais para o Sistema de Estoque Studio Muda

document.addEventListener('DOMContentLoaded', function() {
    // Animar elementos ao entrar na viewport
    const animateOnScroll = () => {
        const elements = document.querySelectorAll('.animate-on-scroll');
        
        elements.forEach(element => {
            const elementPosition = element.getBoundingClientRect().top;
            const screenPosition = window.innerHeight;
            
            if (elementPosition < screenPosition - 100) {
                element.classList.add('animate__animated', 'animate__fadeInUp');
                element.style.opacity = 1;
            }
        });
    };
    
    // Inicializar elementos com opacidade 0
    document.querySelectorAll('.animate-on-scroll').forEach(el => {
        el.style.opacity = 0;
    });
    
    // Adicionar listener para scroll
    window.addEventListener('scroll', animateOnScroll);
    animateOnScroll(); // Executar uma vez ao carregar
    
    // Efeito de hover nos cards
    const cards = document.querySelectorAll('.card');
    cards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px)';
            this.style.boxShadow = '0 10px 20px rgba(0, 0, 0, 0.12)';
            this.style.transition = 'all 0.3s ease';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '0 4px 6px rgba(0, 0, 0, 0.1)';
            this.style.transition = 'all 0.3s ease';
        });
    });
    
    // Adicionar classe active ao link atual
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.navbar-nav .nav-link');
    
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (currentPath === href || (href !== '/' && currentPath.startsWith(href))) {
            link.classList.add('active');
        }
    });
    
    // Efeito de ripple nos botões
    const buttons = document.querySelectorAll('.btn');
    buttons.forEach(button => {
        button.addEventListener('click', function(e) {
            const x = e.clientX - e.target.getBoundingClientRect().left;
            const y = e.clientY - e.target.getBoundingClientRect().top;
            
            const ripple = document.createElement('span');
            ripple.classList.add('ripple-effect');
            ripple.style.left = `${x}px`;
            ripple.style.top = `${y}px`;
            
            this.appendChild(ripple);
            
            setTimeout(() => {
                ripple.remove();
            }, 600);
        });
    });
    
    // Adicionar tooltips a elementos com data-tooltip
    const tooltipElements = document.querySelectorAll('[data-tooltip]');
    tooltipElements.forEach(element => {
        element.setAttribute('title', element.getAttribute('data-tooltip'));
        element.setAttribute('data-bs-toggle', 'tooltip');
        element.setAttribute('data-bs-placement', 'top');
    });
    
    // Inicializar tooltips do Bootstrap
    if (typeof bootstrap !== 'undefined' && bootstrap.Tooltip) {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }
    
    // Adicionar efeito de fade para mensagens de alerta
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.classList.add('fade-out');
            setTimeout(() => {
                alert.remove();
            }, 500);
        }, 5000);
    });
    
    // Adicionar efeito de entrada para tabelas
    const tables = document.querySelectorAll('.table');
    tables.forEach(table => {
        const rows = table.querySelectorAll('tbody tr');
        rows.forEach((row, index) => {
            row.style.opacity = '0';
            row.style.animation = `fadeIn 0.3s ease forwards ${index * 0.05}s`;
        });
    });
});

// Função para mostrar mensagens de notificação
function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type} animate__animated animate__fadeInRight`;
    notification.innerHTML = `
        <div class="notification-icon">
            <i class="bi ${type === 'success' ? 'bi-check-circle' : type === 'error' ? 'bi-exclamation-circle' : 'bi-info-circle'}"></i>
        </div>
        <div class="notification-content">
            <p>${message}</p>
        </div>
        <button class="notification-close">
            <i class="bi bi-x"></i>
        </button>
    `;
    
    document.body.appendChild(notification);
    
    // Adicionar evento para fechar notificação
    notification.querySelector('.notification-close').addEventListener('click', () => {
        notification.classList.replace('animate__fadeInRight', 'animate__fadeOutRight');
        setTimeout(() => {
            notification.remove();
        }, 500);
    });
    
    // Auto-remover após 5 segundos
    setTimeout(() => {
        notification.classList.replace('animate__fadeInRight', 'animate__fadeOutRight');
        setTimeout(() => {
            notification.remove();
        }, 500);
    }, 5000);
}

// Adicionar CSS para efeito de ripple
document.addEventListener('DOMContentLoaded', function() {
    const style = document.createElement('style');
    style.textContent = `
        .ripple-effect {
            position: absolute;
            border-radius: 50%;
            background-color: rgba(255, 255, 255, 0.4);
            transform: scale(0);
            animation: ripple 0.6s linear;
            pointer-events: none;
        }
        
        @keyframes ripple {
            to {
                transform: scale(4);
                opacity: 0;
            }
        }
        
        .notification {
            position: fixed;
            top: 20px;
            right: 20px;
            display: flex;
            align-items: center;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            padding: 15px;
            z-index: 9999;
            max-width: 350px;
            overflow: hidden;
        }
        
        .notification-success {
            border-left: 4px solid var(--success-color);
        }
        
        .notification-error {
            border-left: 4px solid var(--danger-color);
        }
        
        .notification-info {
            border-left: 4px solid var(--primary-color);
        }
        
        .notification-icon {
            margin-right: 15px;
            font-size: 1.5rem;
        }
        
        .notification-success .notification-icon {
            color: var(--success-color);
        }
        
        .notification-error .notification-icon {
            color: var(--danger-color);
        }
        
        .notification-info .notification-icon {
            color: var(--primary-color);
        }
        
        .notification-content {
            flex: 1;
        }
        
        .notification-content p {
            margin: 0;
        }
        
        .notification-close {
            background: none;
            border: none;
            cursor: pointer;
            font-size: 1rem;
            color: #999;
            padding: 0;
            margin-left: 10px;
        }
        
        .notification-close:hover {
            color: #333;
        }
        
        .fade-out {
            opacity: 0;
            transition: opacity 0.5s ease;
        }
        
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }
    `;
    document.head.appendChild(style);
});
