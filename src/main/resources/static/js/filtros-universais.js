/**
 * SISTEMA DE FILTROS UNIVERSAIS - STUDIO MUDA
 * Filtros do lado cliente com busca em tempo real
 */

// Classe para gerenciar filtros universais
class FiltroUniversal {
    constructor() {
        this.filtrosAtivos = {};
        this.debounceTimer = null;
        this.init();
    }

    init() {
        // Inicializar filtros para cada seção
        this.initProdutosFiltros();
        this.initClientesFiltros();
        this.initPedidosFiltros();
        this.initFuncionariosFiltros();
        this.initCupomFiltros();
        this.initEstoqueFiltros();
        this.initEquipeFiltros();
        
        // Adicionar event listeners para filtros automáticos
        this.setupAutoFilters();
        
        this.setupSearchInput();
        this.setupSelectFilters();
        this.setupDateFilters();
        this.setupExportButtons();
        console.log('🔍 Sistema de Filtros Universais carregado!');
    }

    // Inicializar filtros para produtos
    initProdutosFiltros() {
        const form = document.getElementById('filtrosFormProdutos');
        if (!form) return;
        
        // Configurar filtros automáticos para produtos
        this.setupProdutoSpecificFilters();
    }

    // Inicializar filtros para clientes
    initClientesFiltros() {
        const form = document.getElementById('filtrosFormClientes');
        if (!form) return;
        
        // Configurar filtros automáticos para clientes
        this.setupClienteSpecificFilters();
    }

    // Inicializar filtros para pedidos
    initPedidosFiltros() {
        const form = document.getElementById('filtrosFormPedidos');
        if (!form) return;
        
        // Configurar filtros automáticos para pedidos
        this.setupPedidoSpecificFilters();
    }

    // Inicializar filtros para cupons
    initCupomFiltros() {
        const form = document.getElementById('filtrosFormCupons');
        if (!form) return;
        
        // Configurar filtros automáticos para cupons
        this.setupCupomSpecificFilters();
    }

    // Inicializar filtros para estoque
    initEstoqueFiltros() {
        const form = document.getElementById('filtrosFormEstoque');
        if (!form) return;

        // Configurar filtros automáticos para estoque
        this.setupEstoqueSpecificFilters();
    }

    // Inicializar filtros para equipe (funcionários)
    initEquipeFiltros() {
        const form = document.getElementById('filtrosFormEquipe');
        if (!form) return;

        // Configurar filtros automáticos para equipe
        this.setupEquipeSpecificFilters();
    }

    // Configurar filtros específicos para produtos
    setupProdutoSpecificFilters() {
        const tipoFilter = document.getElementById('tipoFilter');
        const estoqueFilter = document.getElementById('estoqueFilter');

        if (tipoFilter) {
            tipoFilter.innerHTML = `
                <option value="">Todos os Tipos</option>
                <option value="eletronicos">Eletrônicos</option>
                <option value="moveis">Móveis</option>
                <option value="vestuario">Vestuário</option>
            `;

            tipoFilter.addEventListener('change', () => {
                this.applyFilters('produtos');
            });
        }

        if (estoqueFilter) {
            estoqueFilter.innerHTML = `
                <option value="">Todos os Estoques</option>
                <option value="disponivel">Disponível</option>
                <option value="baixo">Baixo</option>
                <option value="zerado">Zerado</option>
            `;

            estoqueFilter.addEventListener('change', () => {
                this.applyFilters('produtos');
            });
        }
    }

    // Configurar filtros específicos para clientes
    setupClienteSpecificFilters() {
        const tipoFilter = document.getElementById('tipoFilter');
        const statusFilter = document.getElementById('statusFilter');
        
        [tipoFilter, statusFilter].forEach(filter => {
            if (filter) {
                filter.addEventListener('change', () => {
                    this.applyFilters('clientes');
                });
            }
        });
    }

    // Configurar filtros específicos para pedidos
    setupPedidoSpecificFilters() {
        const statusFilter = document.getElementById('statusFilter');
        const dataInicio = document.getElementById('dataInicio');
        const dataFim = document.getElementById('dataFim');
        
        [statusFilter, dataInicio, dataFim].forEach(filter => {
            if (filter) {
                filter.addEventListener('change', () => {
                    this.applyFilters('pedidos');
                });
            }
        });
    }

    // Configurar filtros específicos para cupons
    setupCupomSpecificFilters() {
        const statusFilter = document.getElementById('statusFilter');
        const dataInicio = document.getElementById('dataInicio');
        const dataFim = document.getElementById('dataFim');
        
        [statusFilter, dataInicio, dataFim].forEach(filter => {
            if (filter) {
                filter.addEventListener('change', () => {
                    this.applyFilters('cupons');
                });
            }
        });
    }

    // Configurar filtros específicos para estoque
    setupEstoqueSpecificFilters() {
        const tipoFilter = document.getElementById('tipoFilter');
        const dataInicio = document.getElementById('dataInicio');
        const dataFim = document.getElementById('dataFim');

        if (tipoFilter) {
            tipoFilter.innerHTML = `
                <option value="">Todos os Tipos</option>
                <option value="entrada">Entrada</option>
                <option value="saída">Saída</option>
            `;

            tipoFilter.addEventListener('change', () => {
                this.applyFilters('estoque');
            });
        }

        [dataInicio, dataFim].forEach(filter => {
            if (filter) {
                filter.addEventListener('change', () => {
                    this.applyFilters('estoque');
                });
            }
        });
    }

    // Configurar filtros específicos para equipe
    setupEquipeSpecificFilters() {
        const cargoFilter = document.getElementById('cargoFilter');
        const statusFilter = document.getElementById('statusFilter');

        [cargoFilter, statusFilter].forEach(filter => {
            if (filter) {
                filter.addEventListener('change', () => {
                    this.applyFilters('funcionarios');
                });
            }
        });
    }

    // Inicializar filtros para funcionários
    initFuncionariosFiltros() {
        const form = document.getElementById('filtrosFormFuncionarios');
        if (!form) return;

        // Não precisamos mais do evento de submit, pois os filtros serão aplicados automaticamente
        
        // Carregar opções de filtro do servidor
        this.loadFuncionarioFilterOptions();
    }

    // Configurar campo de busca principal
    setupSearchInput() {
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                clearTimeout(this.debounceTimer);
                this.debounceTimer = setTimeout(() => {
                    // Determinar a seção atual com base na URL
                    const currentSection = this.getCurrentSection();
                    if (currentSection) {
                        this.applyFilters(currentSection);
                    }
                }, 300); // Debounce de 300ms
            });
        }
    }

    // Configurar filtros de select
    setupSelectFilters() {
        // Buscar todos os selects que possam ser filtros, com diferentes padrões de nomenclatura
        const selectFilters = document.querySelectorAll('select[id*="filter"], select[id*="Filter"], select[id*="tipo"], select[id*="status"], select[id*="categoria"]');
        selectFilters.forEach(select => {
            select.addEventListener('change', () => {
                // Determinar a seção atual com base na URL
                const currentSection = this.getCurrentSection();
                if (currentSection) {
                    this.applyFilters(currentSection);
                }
            });
        });
    }

    // Configurar filtros de data
    setupDateFilters() {
        const dateInputs = document.querySelectorAll('input[type="date"]');
        dateInputs.forEach(input => {
            input.addEventListener('change', () => {
                // Determinar a seção atual com base na URL
                const currentSection = this.getCurrentSection();
                if (currentSection) {
                    this.applyFilters(currentSection);
                }
            });
        });
    }

    // Aplicar filtros com base na seção atual
    applyFilters(section = null) {
        // Obter todos os inputs de filtro
        const filterInputs = document.querySelectorAll('input[id$="Filter"], select[id$="Filter"]');
        const filters = {};

        // Coletar valores dos filtros
        filterInputs.forEach(input => {
            if (input.value) {
                filters[input.id] = input.value;
            }
        });

        // Aplicar filtros à tabela
        this.filterTable(section, filters);
    }

    // Filtrar tabela com base na seção e filtros
    filterTable(section, filters) {
        // Usar a função principal de filtro
        this.filtrarTabela();
    }
    
    // Configurar filtros automáticos para todos os campos de filtro
    setupAutoFilters() {
        // Adicionar evento de input para campo de pesquisa
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.addEventListener('input', () => {
                // Determinar a seção atual com base na URL
                const currentSection = this.getCurrentSection();
                if (currentSection) {
                    this.applyFilters(currentSection);
                }
            });
        }
    }
    
    // Determinar a seção atual com base na URL
    getCurrentSection() {
        const path = window.location.pathname;
        
        if (path.includes('/produtos')) return 'produtos';
        if (path.includes('/clientes')) return 'clientes';
        if (path.includes('/pedidos')) return 'pedidos';
        if (path.includes('/funcionarios')) return 'funcionarios';
        if (path.includes('/cupons')) return 'cupons';
        if (path.includes('/estoque')) return 'estoque';
        
        return null;
    }

    // Carregar opções de filtro para funcionários do servidor
    loadFuncionarioFilterOptions() {
        fetch('/funcionarios/filtros')
            .then(response => response.json())
            .then(data => {
                // Preencher select de cargos
                const cargoSelect = document.getElementById('cargoFilter');
                if (cargoSelect) {
                    // Limpar opções existentes
                    cargoSelect.innerHTML = '<option value="">Todos os cargos</option>';
                    
                    // Adicionar opções de cargo
                    if (data.cargos && Array.isArray(data.cargos)) {
                        data.cargos.forEach(cargo => {
                            const option = document.createElement('option');
                            option.value = cargo;
                            option.textContent = cargo;
                            cargoSelect.appendChild(option);
                        });
                    }
                    
                    // Adicionar evento de change para aplicar filtros automaticamente
                    cargoSelect.addEventListener('change', () => {
                        this.applyFilters('funcionarios');
                    });
                }
                
                // Adicionar evento ao select de status
                const statusSelect = document.getElementById('statusFilter');
                if (statusSelect) {
                    statusSelect.addEventListener('change', () => {
                        this.applyFilters('funcionarios');
                    });
                }
            })
            .catch(error => console.error('Erro ao carregar opções de filtro:', error));
    }

    // Função principal de filtro
    filtrarTabela() {
        const searchTerm = this.getSearchTerm();
        const activeFilters = this.getActiveFilters();
        const table = document.querySelector('table tbody');
        
        if (!table) return;

        const rows = table.querySelectorAll('tr');
        let visibleCount = 0;

        rows.forEach(row => {
            const shouldShow = this.shouldShowRow(row, searchTerm, activeFilters);
            row.style.display = shouldShow ? '' : 'none';
            if (shouldShow) visibleCount++;
        });

        this.updateResultsCount(visibleCount);
        this.updateNoResultsMessage(visibleCount);
    }

    // Obter termo de busca
    getSearchTerm() {
        const searchInput = document.getElementById('searchInput');
        return searchInput ? searchInput.value.toLowerCase().trim() : '';
    }

    // Obter filtros ativos
    getActiveFilters() {
        const filters = {};
        
        // Filtros de select - buscar todos os possíveis padrões de nomenclatura
        const selectFilters = document.querySelectorAll('select[id*="filter"], select[id*="Filter"], select[id*="tipo"], select[id*="status"], select[id*="categoria"]');
        selectFilters.forEach(select => {
            if (select.value) {
                filters[select.id] = select.value.toLowerCase();
            }
        });

        // Filtros de data
        const startDate = document.getElementById('dataInicio');
        const endDate = document.getElementById('dataFim');
        
        if (startDate && startDate.value) {
            filters.dataInicio = new Date(startDate.value);
        }
        if (endDate && endDate.value) {
            filters.dataFim = new Date(endDate.value);
        }

        return filters;
    }

    // Verificar se linha deve ser exibida
    shouldShowRow(row, searchTerm, filters) {
        const cells = row.querySelectorAll('td');
        if (cells.length === 0) return false;

        let matchesSearch = true;
        let matchesFilters = true;

        // Verificar busca por texto
        if (searchTerm) {
            matchesSearch = false;
            cells.forEach(cell => {
                if (cell.textContent.toLowerCase().includes(searchTerm)) {
                    matchesSearch = true;
                }
            });
        }

        // Verificar filtros de data
        if (matchesSearch && (filters.dataInicio || filters.dataFim)) {
            // Converter strings de data para objetos Date
            if (typeof filters.dataInicio === 'string' && filters.dataInicio) {
                filters.dataInicio = new Date(filters.dataInicio);
            }
            if (typeof filters.dataFim === 'string' && filters.dataFim) {
                filters.dataFim = new Date(filters.dataFim);
                // Ajustar para o final do dia
                filters.dataFim.setHours(23, 59, 59);
            }
        }

        // Verificar filtros específicos por página
        if (matchesSearch) {
            matchesFilters = this.checkSpecificFilters(row, cells, filters);
        }

        return matchesSearch && matchesFilters;
    }

    // Verificar filtros específicos por tipo de página
    checkSpecificFilters(row, cells, filters) {
        const currentPage = this.getCurrentPage();
        
        switch (currentPage) {
            case 'produtos':
                return this.checkProdutoFilters(cells, filters);
            case 'clientes':
                return this.checkClienteFilters(cells, filters);
            case 'pedidos':
                return this.checkPedidoFilters(cells, filters);
            case 'estoque':
                return this.checkEstoqueFilters(cells, filters);
            case 'cupons':
                return this.checkCupomFilters(cells, filters);
            case 'funcionarios':
                return this.checkFuncionarioFilters(cells, filters);
            default:
                return true;
        }
    }

    // Detectar página atual
    getCurrentPage() {
        return this.getCurrentSection() || 'default';
    }

    // Filtros específicos para produtos
    checkProdutoFilters(cells, filters) {
        let matches = true;

        // Filtro por tipo
        if (filters.filterTipo) {
            const tipoCell = cells[2]; // Assumindo que tipo está na 3ª coluna
            if (tipoCell && tipoCell.textContent) {
                if (!tipoCell.textContent.toLowerCase().includes(filters.filterTipo)) {
                    matches = false;
                }
            }
        }

        // Filtro por estoque
        if (filters.filterEstoque) {
            const estoqueCell = cells[3]; // Assumindo que quantidade está na 4ª coluna
            const quantidade = parseInt(estoqueCell.textContent) || 0;
            
            switch (filters.filterEstoque) {
                case 'disponivel':
                    if (quantidade <= 5) matches = false;
                    break;
                case 'baixo':
                    if (quantidade === 0 || quantidade > 5) matches = false;
                    break;
                case 'zerado':
                    if (quantidade !== 0) matches = false;
                    break;
            }
        }

        return matches;
    }

    // Filtros específicos para clientes
    checkClienteFilters(cells, filters) {
        let matches = true;

        // Filtro por tipo - verificar "PF" ou "PJ"
        if (filters.tipoFilter || filters.tipo) {
            const tipoValue = (filters.tipoFilter || filters.tipo).toLowerCase();
            const tipoCell = cells[2]; // Assumindo que tipo está na 3ª coluna
            if (tipoCell && tipoCell.textContent) {
                const cellValue = tipoCell.textContent.trim().toLowerCase();
                if (!(tipoValue === 'pf' && cellValue.includes('pessoa física')) &&
                    !(tipoValue === 'pj' && cellValue.includes('pessoa jurídica'))) {
                    matches = false;
                }
            }
        }

        // Filtro por status - verificar múltiplos IDs possíveis
        if (filters.statusFilter || filters.status) {
            const statusValue = (filters.statusFilter || filters.status).toLowerCase();
            const statusCell = cells[6]; // Ajustado para a coluna correta de status
            if (statusCell && statusCell.textContent) {
                const cellStatus = statusCell.textContent.trim().toLowerCase();
                const isAtivo = cellStatus.includes('ativo');
                const isInativo = cellStatus.includes('inativo');

                if ((statusValue === 'ativo' && (!isAtivo || isInativo)) ||
                    (statusValue === 'inativo' && (!isInativo || isAtivo))) {
                    matches = false;
                }
            }
        }

        return matches;
    }

    // Filtros específicos para pedidos
    checkPedidoFilters(cells, filters) {
        let matches = true;

        // Filtro por status - verificar múltiplos IDs possíveis
        if (filters.statusFilter || filters.status || filters.statusPedido) {
            const statusValue = filters.statusFilter || filters.status || filters.statusPedido;
            const statusCell = cells[3]; // Coluna de status
            if (statusCell && !statusCell.textContent.toLowerCase().includes(statusValue.toLowerCase())) {
                matches = false;
            }
        }

        // Filtro por data
        if (filters.dataInicio || filters.dataFim) {
            const dataCell = cells[3]; // Coluna de data de requisição
            if (dataCell) {
                try {
                    // Tentar extrair a data do formato dd/MM/yyyy
                    const dataParts = dataCell.textContent.trim().split('/');
                    if (dataParts.length === 3) {
                        const dataMovimentacao = new Date(dataParts[2], dataParts[1]-1, dataParts[0]);
                        
                        if (filters.dataInicio && dataMovimentacao < filters.dataInicio) {
                            matches = false;
                        }
                        if (filters.dataFim && dataMovimentacao > filters.dataFim) {
                            matches = false;
                        }
                    }
                } catch (e) {
                    console.error('Erro ao processar data:', e);
                }
            }
        }

        return matches;
    }

    // Filtros específicos para estoque
    checkEstoqueFilters(cells, filters) {
        let matches = true;

        // Filtro por tipo de movimentação - verificar "Saída" ou "Entrada"
        if (filters.tipoFilter || filters.tipo) {
            const tipoValue = (filters.tipoFilter || filters.tipo).toLowerCase();
            const tipoCell = cells[2]; // Assumindo que tipo está na 3ª coluna
            if (tipoCell) {
                const cellValue = tipoCell.textContent.trim().toLowerCase();
                if (!(tipoValue === 'saída' && cellValue.includes('saída')) &&
                    !(tipoValue === 'entrada' && cellValue.includes('entrada'))) {
                    matches = false;
                }
            }
        }

        // Filtro por data
        if (filters.dataInicio || filters.dataFim) {
            const dataCell = cells[4]; // Assumindo que data está na 5ª coluna
            if (dataCell) {
                try {
                    // Tentar extrair a data do formato dd/MM/yyyy
                    const dataParts = dataCell.textContent.trim().split('/');
                    if (dataParts.length === 3) {
                        const dataMovimentacao = new Date(dataParts[2], dataParts[1]-1, dataParts[0]);
                        
                        if (filters.dataInicio && dataMovimentacao < filters.dataInicio) {
                            matches = false;
                        }
                        if (filters.dataFim && dataMovimentacao > filters.dataFim) {
                            matches = false;
                        }
                    } else {
                        // Tentar como data ISO
                        const dataMovimentacao = new Date(dataCell.textContent);
                        
                        if (filters.dataInicio && dataMovimentacao < filters.dataInicio) {
                            matches = false;
                        }
                        if (filters.dataFim && dataMovimentacao > filters.dataFim) {
                            matches = false;
                        }
                    }
                } catch (e) {
                    console.error('Erro ao processar data:', e);
                }
            }
        }

        return matches;
    }

    // Filtros específicos para cupons
    checkCupomFilters(cells, filters) {
        let matches = true;

        // Filtro por status - verificar múltiplos IDs possíveis
        if (filters.statusFilter || filters.status) {
            const statusValue = filters.statusFilter || filters.status;
            const statusCell = cells[3]; // Assumindo que status está na 4ª coluna
            if (statusCell && !statusCell.textContent.toLowerCase().includes(statusValue.toLowerCase())) {
                matches = false;
            }
        }

        // Filtro por data de validade
        if (filters.dataInicio || filters.dataFim) {
            const dataCell = cells[4]; // Coluna de validade
            if (dataCell) {
                try {
                    // Tentar extrair a data do formato dd/MM/yyyy
                    const dataParts = dataCell.textContent.trim().split('/');
                    if (dataParts.length === 3) {
                        const dataValidade = new Date(dataParts[2], dataParts[1]-1, dataParts[0]);
                        
                        if (filters.dataInicio && dataValidade < filters.dataInicio) {
                            matches = false;
                        }
                        if (filters.dataFim && dataValidade > filters.dataFim) {
                            matches = false;
                        }
                    }
                } catch (e) {
                    console.error('Erro ao processar data:', e);
                }
            }
        }

        return matches;
    }

    // Filtros específicos para funcionários
    checkFuncionarioFilters(cells, filters) {
        let matches = true;

        // Filtro por cargo - verificar múltiplos IDs possíveis
        if (filters.cargoFilter || filters.cargo) {
            const cargoValue = filters.cargoFilter || filters.cargo;
            // Corrigido: cargo está na 4ª coluna (índice 3) na tabela de funcionários
            const cargoCell = cells[3]; 
            
            console.log('Filtro cargo:', cargoValue, 'Valor na célula:', cargoCell ? cargoCell.textContent : 'não encontrado');
            
            if (cargoCell && !cargoCell.textContent.toLowerCase().includes(cargoValue.toLowerCase())) {
                matches = false;
            }
        }

        // Filtro por status - verificar múltiplos IDs possíveis
        if (filters.statusFilter || filters.status) {
            const statusValue = filters.statusFilter || filters.status;
            // Corrigido: status está na 7ª coluna (índice 6) na tabela de funcionários
            const statusCell = cells[6]; 
            const isAtivo = statusCell && statusCell.textContent.toLowerCase().includes('ativo');
            
            console.log('Filtro status:', statusValue, 'É ativo?', isAtivo);
            
            if ((statusValue === 'ativo' && !isAtivo) ||
                (statusValue === 'inativo' && isAtivo)) {
                matches = false;
            }
        }

        return matches;
    }

    // Atualizar contador de resultados
    updateResultsCount(count) {
        const totalCountElement = document.getElementById('totalCount');
        if (totalCountElement) {
            const currentPage = this.getCurrentPage();
            const entityName = this.getEntityName(currentPage);
            totalCountElement.textContent = `${count} ${entityName} encontrados`;
        }
    }

    // Obter nome da entidade
    getEntityName(page) {
        const names = {
            'produtos': 'produtos',
            'clientes': 'clientes',
            'pedidos': 'pedidos',
            'estoque': 'movimentações',
            'cupons': 'cupons',
            'funcionarios': 'funcionários',
            'default': 'itens'
        };
        return names[page] || names.default;
    }

    // Exibir mensagem quando não há resultados
    updateNoResultsMessage(count) {
        let noResultsRow = document.querySelector('.no-results-row');
        
        if (count === 0) {
            if (!noResultsRow) {
                const tbody = document.querySelector('table tbody');
                const colCount = document.querySelector('table thead tr').children.length;
                
                noResultsRow = document.createElement('tr');
                noResultsRow.className = 'no-results-row';
                noResultsRow.innerHTML = `
                    <td colspan="${colCount}" class="text-center py-4">
                        <div class="text-muted">
                            <i class="bi bi-search me-2"></i>
                            Nenhum resultado encontrado para os filtros aplicados
                        </div>
                    </td>
                `;
                tbody.appendChild(noResultsRow);
            }
        } else {
            if (noResultsRow) {
                noResultsRow.remove();
            }
        }
    }

    // Configurar botões de exportação
    setupExportButtons() {
        const exportBtn = document.querySelector('[onclick="exportData()"], .btn[title="Exportar"]');
        if (exportBtn) {
            exportBtn.addEventListener('click', (e) => {
                e.preventDefault();
                this.exportData();
            });
        }
    }

    // Exportar dados filtrados
    exportData() {
        const visibleRows = document.querySelectorAll('table tbody tr:not([style*="display: none"]):not(.no-results-row)');
        const headers = Array.from(document.querySelectorAll('table thead th')).map(th => th.textContent.trim());
        
        let csvContent = headers.join(',') + '\n';
        
        visibleRows.forEach(row => {
            const cells = Array.from(row.querySelectorAll('td')).slice(0, -1); // Remove coluna de ações
            const rowData = cells.map(cell => {
                let text = cell.textContent.trim();
                // Escape commas and quotes
                if (text.includes(',') || text.includes('"')) {
                    text = '"' + text.replace(/"/g, '""') + '"';
                }
                return text;
            });
            csvContent += rowData.join(',') + '\n';
        });
        
        // Download CSV
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', `${this.getCurrentPage()}_filtrados_${new Date().toISOString().split('T')[0]}.csv`);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        // Mostrar toast de sucesso
        this.showToast('Dados exportados com sucesso!', 'success');
    }

    // Limpar todos os filtros
    clearAllFilters() {
        // Limpar campo de busca
        const searchInput = document.getElementById('searchInput');
        if (searchInput) searchInput.value = '';
        
        // Limpar selects
        const selectFilters = document.querySelectorAll('select[id*="filter"], select[id*="Filter"]');
        selectFilters.forEach(select => select.value = '');
        
        // Limpar datas
        const dateInputs = document.querySelectorAll('input[type="date"]');
        dateInputs.forEach(input => input.value = '');
        
        // Reexibir todas as linhas
        const rows = document.querySelectorAll('table tbody tr');
        rows.forEach(row => row.style.display = '');
        
        // Atualizar contador
        this.updateResultsCount(rows.length);
        
        // Remover mensagem de "sem resultados"
        const noResultsRow = document.querySelector('.no-results-row');
        if (noResultsRow) noResultsRow.remove();
        
        this.showToast('Filtros removidos', 'info');
    }

    // Mostrar toast notification
    showToast(message, type = 'info') {
        // Criar toast se não existir container
        let toastContainer = document.getElementById('toast-container');
        if (!toastContainer) {
            toastContainer = document.createElement('div');
            toastContainer.id = 'toast-container';
            toastContainer.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                z-index: 9999;
            `;
            document.body.appendChild(toastContainer);
        }
        
        const toast = document.createElement('div');
        toast.className = `alert alert-${type} alert-dismissible fade show`;
        toast.style.cssText = 'margin-bottom: 10px; min-width: 300px;';
        toast.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        toastContainer.appendChild(toast);
        
        // Auto-remover após 3 segundos
        setTimeout(() => {
            if (toast.parentNode) {
                toast.remove();
            }
        }, 3000);
    }
}

// Funções globais para compatibilidade
function exportData() {
    if (window.filtroUniversal) {
        window.filtroUniversal.exportData();
    }
}

function clearFilters() {
    if (window.filtroUniversal) {
        window.filtroUniversal.clearAllFilters();
    }
}

// Inicializar quando DOM estiver pronto
document.addEventListener('DOMContentLoaded', function() {
    window.filtroUniversal = new FiltroUniversal();
    
    // Adicionar botão de limpar filtros se não existir
    const filtersContainer = document.querySelector('.filters-container, .card-body');
    if (filtersContainer && !document.getElementById('clearFiltersBtn')) {
        const clearBtn = document.createElement('button');
        clearBtn.id = 'clearFiltersBtn';
        clearBtn.className = 'btn btn-outline-secondary btn-sm ms-2';
        clearBtn.innerHTML = '<i class="bi bi-x-circle me-1"></i>Limpar Filtros';
        clearBtn.onclick = clearFilters;
        
        // Inserir o botão ao lado do último filtro
        const lastFilterRow = filtersContainer.querySelector('.row');
        if (lastFilterRow) {
            const clearCol = document.createElement('div');
            clearCol.className = 'col-auto';
            clearCol.appendChild(clearBtn);
            lastFilterRow.appendChild(clearCol);
        }
    }
});

console.log('🎯 Sistema de Filtros Universais carregado!');
