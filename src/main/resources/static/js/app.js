document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('form[data-confirm]').forEach(form => {
        form.addEventListener('submit', event => {
            const message = form.dataset.confirm || 'Are you sure?';
            if (!window.confirm(message)) {
                event.preventDefault();
            }
        });
    });

    document.querySelectorAll('[data-confirm]').forEach(element => {
        if (element.tagName === 'FORM') return;
        element.addEventListener('click', event => {
            const message = element.dataset.confirm || 'Are you sure?';
            if (!window.confirm(message)) {
                event.preventDefault();
            }
        });
    });

    const statusInputs = document.querySelectorAll('input[name="paymentStatus"]');
    const amountInput = document.getElementById('amountCredited');
    const paidMonthInput = document.getElementById('paidMonth');
    const paidMonthField = document.getElementById('paidMonthField');

    function syncPaymentFields() {
        if (!statusInputs.length) return;
        const selected = document.querySelector('input[name="paymentStatus"]:checked');
        const isNotPaid = selected && selected.value === 'NOT_PAID';
        if (amountInput && isNotPaid) {
            amountInput.value = '0.00';
            amountInput.readOnly = true;
        } else if (amountInput) {
            amountInput.readOnly = false;
        }
        if (paidMonthInput) {
            paidMonthInput.disabled = Boolean(isNotPaid);
            paidMonthInput.required = !isNotPaid;
            if (isNotPaid) paidMonthInput.value = '';
        }
        if (paidMonthField) {
            paidMonthField.classList.toggle('field-disabled', Boolean(isNotPaid));
        }
    }

    statusInputs.forEach(input => input.addEventListener('change', syncPaymentFields));
    syncPaymentFields();
});
