const messagesDiv = document.getElementById('messages');
const historyUl = document.getElementById('history');
let userId;

function logout() {
    window.location.href = "/logout";
}

document.addEventListener('DOMContentLoaded', async () => {
    hideLoadingOverlay();
    userId = await fetchUserId();

    if (!userId) {
        userId = 0;
        console.log("Assegnato ID Utente di default:", userId);
    }

    try {
        await loadHistory(userId);
    } catch (error) {
        console.error('Errore durante il caricamento della cronologia:', error);
    }
});

async function fetchUserId() {
    try {
        const response = await fetch('/user-id');
        if (!response.ok) {
            throw new Error(`Errore nella richiesta: ${response.statusText}`);
        }

        const userId = await response.json();
        if (!userId) {
            return null;
        }
        return userId;
    } catch (error) {
        console.error("Errore durante il recupero dell'ID utente:", error);
        return null;
    }
}

async function loadHistory(userId) {
    const url = `/AIChat/history/${userId}`;
    const response = await fetch(url);

    if (!response.ok) {
        throw new Error(`Errore HTTP: ${response.statusText}`);
    }

    const history = await response.json();
    renderHistory(history);
}

function renderHistory(history) {
    historyUl.innerHTML = '';

    history.slice().reverse().forEach(item => {
        const listItem = document.createElement('li');
        listItem.textContent = item.prompt;

        listItem.setAttribute('data-id', item.id);

        listItem.addEventListener('click', async () => {
            const id = listItem.getAttribute('data-id');
            await fetchPromptDetails(id);
        });

        historyUl.appendChild(listItem);
    });
}

async function fetchPromptDetails(id) {
    const url = `/AIChat/details/${id}`;

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Errore HTTP: ${response.statusText}`);
        }

        const details = await response.json();
        showPopup(details.prompt, details.response);
    } catch (error) {
        console.error('Errore durante il recupero dei dettagli:', error);
        alert('Errore nel caricamento dei dettagli.');
    }
}

async function sendMessage() {
    const userInput = document.getElementById('userInput');
    const text = userInput.value.trim();

    if (text === '') return;

    addMessage('user', text);
    showLoadingOverlay();

    try {
        const botResponse = await sendToServer(text);
        addMessage('bot', botResponse);
        await loadHistory(userId);
    } catch (error) {
        console.error('Errore durante la comunicazione con il server:', error);
        addMessage('bot', 'Errore: impossibile ottenere una risposta dal server.');
    } finally {
        hideLoadingOverlay();
    }

    userInput.value = '';
}


function addMessage(sender, text) {
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${sender}`;

    const messageText = document.createElement('p');
    messageText.textContent = text;

    messageDiv.appendChild(messageText);
    messagesDiv.appendChild(messageDiv);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

function addHistory(text) {
    const listItem = document.createElement('li');
    listItem.textContent = text;
    historyUl.appendChild(listItem);
}

async function sendToServer(userMessage) {
    const url = '/AIChat/send';
    const response = await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ message: userMessage, user: userId }),
    });

    if (!response.ok) {
        throw new Error(`Errore HTTP: ${response.status}`);
    }

    return await response.text();
}

function showPopup(prompt, response) {
    const popup = document.createElement('div');
    popup.className = 'popup';

    popup.innerHTML = `
        <div class="popup-content">
            <h3>Dettagli</h3>
            <p><strong>Domanda:</strong> ${prompt}</p>
            <p><strong>Risposta:</strong> ${response}</p>
            <button id="closePopup">Chiudi</button>
        </div>
    `;

    document.body.appendChild(popup);

    document.getElementById('closePopup').addEventListener('click', () => {
        popup.remove();
    });
}

function showLoadingOverlay() {
    const overlay = document.getElementById('loadingOverlay');
    if (!overlay.classList.contains('hidden')) return;
    overlay.classList.remove('hidden');
}

function hideLoadingOverlay() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay.classList.contains('hidden')) return;
    overlay.classList.add('hidden');

}
