// Funções para exclusão de registros via AJAX com SweetAlert2
function excluirFuncionario(id) {
    Swal.fire({
        title: 'Inativar funcionário?',
        text: 'Esta ação não poderá ser revertida!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3a6ea5',
        cancelButtonColor: '#dc3545',
        confirmButtonText: 'Sim, inativar!',
        cancelButtonText: 'Cancelar',
        reverseButtons: true,
        backdrop: true,
        heightAuto: false
    }).then((result) => {
        if (result.isConfirmed) {
            // Mostrar loading
            Swal.fire({
                title: 'Processando...',
                text: 'Aguarde enquanto o funcionário é inativado.',
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });
            
            fetch('/funcionarios/excluir/' + id, {
                method: 'GET'
            })
            .then(response => {
                if (response.ok) {
                    Swal.fire({
                        title: 'Inativado!',
                        text: 'O funcionário foi inativado com sucesso.',
                        icon: 'success',
                        confirmButtonColor: '#3a6ea5'
                    }).then(() => {
                        window.location.reload();
                    });
                } else {
                    Swal.fire({
                        title: 'Erro!',
                        text: 'Não foi possível inativar o funcionário.',
                        icon: 'error',
                        confirmButtonColor: '#3a6ea5'
                    });
                }
            })
            .catch(error => {
                console.error('Erro:', error);
                Swal.fire({
                    title: 'Erro!',
                    text: 'Ocorreu um erro ao processar sua solicitação.',
                    icon: 'error',
                    confirmButtonColor: '#3a6ea5'
                });
            });
        }
    });
}

function excluirPedido(id) {
    Swal.fire({
        title: 'Excluir pedido?',
        text: 'Esta ação não poderá ser revertida!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3a6ea5',
        cancelButtonColor: '#dc3545',
        confirmButtonText: 'Sim, excluir!',
        cancelButtonText: 'Cancelar',
        reverseButtons: true,
        backdrop: true,
        heightAuto: false
    }).then((result) => {
        if (result.isConfirmed) {
            // Mostrar loading
            Swal.fire({
                title: 'Processando...',
                text: 'Aguarde enquanto o pedido é excluído.',
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });
            
            fetch('/pedidos/excluir/' + id, {
                method: 'GET'
            })
            .then(response => {
                if (response.ok) {
                    Swal.fire({
                        title: 'Excluído!',
                        text: 'O pedido foi excluído com sucesso.',
                        icon: 'success',
                        confirmButtonColor: '#3a6ea5'
                    }).then(() => {
                        window.location.reload();
                    });
                } else {
                    Swal.fire({
                        title: 'Erro!',
                        text: 'Não foi possível excluir o pedido.',
                        icon: 'error',
                        confirmButtonColor: '#3a6ea5'
                    });
                }
            })
            .catch(error => {
                console.error('Erro:', error);
                Swal.fire({
                    title: 'Erro!',
                    text: 'Ocorreu um erro ao processar sua solicitação.',
                    icon: 'error',
                    confirmButtonColor: '#3a6ea5'
                });
            });
        }
    });
}

function excluirItemPedido(id, pedidoId) {
    Swal.fire({
        title: 'Excluir item?',
        text: 'Esta ação não poderá ser revertida!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3a6ea5',
        cancelButtonColor: '#dc3545',
        confirmButtonText: 'Sim, excluir!',
        cancelButtonText: 'Cancelar',
        reverseButtons: true,
        backdrop: true,
        heightAuto: false
    }).then((result) => {
        if (result.isConfirmed) {
            // Mostrar loading
            Swal.fire({
                title: 'Processando...',
                text: 'Aguarde enquanto o item é excluído.',
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });
            
            fetch('/pedidos/itens/excluir/' + id, {
                method: 'GET'
            })
            .then(response => {
                if (response.ok) {
                    Swal.fire({
                        title: 'Excluído!',
                        text: 'O item foi excluído com sucesso.',
                        icon: 'success',
                        confirmButtonColor: '#3a6ea5'
                    }).then(() => {
                        window.location.reload();
                    });
                } else {
                    Swal.fire({
                        title: 'Erro!',
                        text: 'Não foi possível excluir o item.',
                        icon: 'error',
                        confirmButtonColor: '#3a6ea5'
                    });
                }
            })
            .catch(error => {
                console.error('Erro:', error);
                Swal.fire({
                    title: 'Erro!',
                    text: 'Ocorreu um erro ao processar sua solicitação.',
                    icon: 'error',
                    confirmButtonColor: '#3a6ea5'
                });
            });
        }
    });
}

function excluirMovimentacao(id) {
    Swal.fire({
        title: 'Excluir movimentação?',
        text: 'Esta ação não poderá ser revertida!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3a6ea5',
        cancelButtonColor: '#dc3545',
        confirmButtonText: 'Sim, excluir!',
        cancelButtonText: 'Cancelar',
        reverseButtons: true,
        backdrop: true,
        heightAuto: false
    }).then((result) => {
        if (result.isConfirmed) {
            // Mostrar loading
            Swal.fire({
                title: 'Processando...',
                text: 'Aguarde enquanto a movimentação é excluída.',
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });
            
            fetch('/estoque/excluir/' + id, {
                method: 'GET'
            })
            .then(response => {
                if (response.ok) {
                    Swal.fire({
                        title: 'Excluído!',
                        text: 'A movimentação foi excluída com sucesso.',
                        icon: 'success',
                        confirmButtonColor: '#3a6ea5'
                    }).then(() => {
                        window.location.reload();
                    });
                } else {
                    Swal.fire({
                        title: 'Erro!',
                        text: 'Não foi possível excluir a movimentação.',
                        icon: 'error',
                        confirmButtonColor: '#3a6ea5'
                    });
                }
            })
            .catch(error => {
                console.error('Erro:', error);
                Swal.fire({
                    title: 'Erro!',
                    text: 'Ocorreu um erro ao processar sua solicitação.',
                    icon: 'error',
                    confirmButtonColor: '#3a6ea5'
                });
            });
        }
    });
}

// Função auxiliar para toast de notificação
function showToast(icon, title) {
    const Toast = Swal.mixin({
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 3000,
        timerProgressBar: true,
        didOpen: (toast) => {
            toast.addEventListener('mouseenter', Swal.stopTimer)
            toast.addEventListener('mouseleave', Swal.resumeTimer)
        }
    });

    Toast.fire({
        icon: icon,
        title: title
    });
}
