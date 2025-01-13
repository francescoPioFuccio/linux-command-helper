const chatContainer = document.getElementById('chatContainer');
const userInput = document.getElementById('userInput');
const sendButton = document.getElementById('sendButton');
let userId;

function logout() {
    window.location.href = "/logout";
}

document.addEventListener('DOMContentLoaded', async () => {
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

function addMessage(text, sender) {
    const message = document.createElement('div');
    message.classList.add('message', sender, 'fade');
    message.textContent = text;

    chatContainer.appendChild(message);

    chatContainer.scrollTop = chatContainer.scrollHeight;
}

async function sendMessage() {
    const text = userInput.value.trim();

    if (text) {
        addMessage(text, 'user');
        userInput.value = '';
        addTypingIndicator();

        try {
            const botResponse = await sendToServer(text);
            addMessage(botResponse, 'bot');
            await loadHistory(userId);
        } catch (error) {
            console.error('Errore durante la comunicazione con il server:', error);
            addMessage('bot', 'Errore: impossibile ottenere una risposta dal server.');
        } finally {
            removeTypingIndicator();
        }
    }
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

function addTypingIndicator() {
    const typingIndicator = document.createElement('div');
    typingIndicator.classList.add('typing-indicator');
    typingIndicator.id = 'typingIndicator';
    typingIndicator.innerHTML = '<span></span><span></span><span></span>';

    chatContainer.appendChild(typingIndicator);
    chatContainer.scrollTop = chatContainer.scrollHeight;
}

function removeTypingIndicator() {
    const typingIndicator = document.getElementById('typingIndicator');
    if (typingIndicator) {
        typingIndicator.remove();
    }
}

sendButton.addEventListener('click', sendMessage);

userInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
        sendMessage();
    }
});

const historyList = document.getElementById('historyList');
const popup = document.getElementById('popup');
const popupQuestion = document.getElementById('popupQuestion');
const popupAnswer = document.getElementById('popupAnswer');
const closePopup = document.getElementById('closePopup');

function showPopup(question, answer) {
    popupQuestion.textContent = question;
    popupAnswer.textContent = answer;
    popup.classList.remove('hidden');
}

closePopup.addEventListener('click', () => {
    popup.classList.add('hidden');
});

async function renderHistory(history) {
    historyList.innerHTML = ''; // Svuota la lista

    history.slice().reverse().forEach(item => {
        const listItem = document.createElement('li');
        listItem.textContent = item.prompt;
        listItem.setAttribute('data-id', item.id);

        listItem.addEventListener('click', async () => {
            const id = listItem.getAttribute('data-id');
            await fetchPromptDetails(id);
        });

        historyList.appendChild(listItem);
    });
}

const logoutButton = document.getElementById('logoutButton');

logoutButton.addEventListener('click', () => {
    logout();
});
