/**
 * ChatBot Logic
 * Modularized for better maintainability and extensibility.
 */

class ChatBotService {
    constructor() {
        this.API_URL = "/api/chat";
    }

    async sendMessage(message) {
        try {
            const response = await fetch(this.API_URL, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ message: message })
            });

            if (!response.ok) {
                throw new Error("HTTP " + response.status);
            }

            return await response.json();
        } catch (error) {
            console.error("ChatBot Service Error:", error);
            throw error;
        }
    }
}

class ChatBotUI {
    constructor() {
        this.service = new ChatBotService();
        this.elements = {
            toggler: document.querySelector(".chatbot-toggler"),
            closeBtn: document.querySelector(".close-btn"),
            chatbox: document.querySelector(".chatbox"),
            chatInput: document.querySelector(".chat-input textarea"),
            sendBtn: document.querySelector(".chat-input span"),
            body: document.body
        };

        this.inputInitHeight = this.elements.chatInput.scrollHeight;
        this.initEventListeners();
    }

    initEventListeners() {
        this.elements.toggler.addEventListener("click", () => this.toggleChat());
        this.elements.closeBtn.addEventListener("click", () => this.imageChat(false));
        this.elements.sendBtn.addEventListener("click", () => this.handleChat());

        this.elements.chatInput.addEventListener("input", () => this.adjustInputHeight());
        this.elements.chatInput.addEventListener("keydown", (e) => {
            if (e.key === "Enter" && !e.shiftKey && window.innerWidth > 800) {
                e.preventDefault();
                this.handleChat();
            }
        });
    }

    toggleChat() {
        this.elements.body.classList.toggle("show-chatbot");
    }

    // Explicit close for close button
    imageChat(show) {
        this.elements.body.classList.remove("show-chatbot");
    }

    adjustInputHeight() {
        this.elements.chatInput.style.height = `${this.inputInitHeight}px`;
        this.elements.chatInput.style.height = `${this.elements.chatInput.scrollHeight}px`;
    }

    createChatLi(message, className) {
        const chatLi = document.createElement("li");
        chatLi.classList.add("chat", className);
        let chatContent = className === "outgoing"
            ? `<p></p>`
            : `<span class="material-symbols-outlined"><i class="fa-solid fa-robot"></i></span><p></p>`;
        chatLi.innerHTML = chatContent;
        chatLi.querySelector("p").textContent = message;
        return chatLi;
    }

    async handleChat() {
        const userMessage = this.elements.chatInput.value.trim();
        if (!userMessage) return;

        // Reset input
        this.elements.chatInput.value = "";
        this.elements.chatInput.style.height = `${this.inputInitHeight}px`;

        // Add user message
        this.elements.chatbox.appendChild(this.createChatLi(userMessage, "outgoing"));
        this.scrollToBottom();

        // Add thinking message
        const incomingChatLi = this.createChatLi("Đang suy nghĩ...", "incoming");

        // Small delay to simulate processing start
        setTimeout(async () => {
            this.elements.chatbox.appendChild(incomingChatLi);
            this.scrollToBottom();

            try {
                const data = await this.service.sendMessage(userMessage);
                this.renderResponse(incomingChatLi, data.response);
            } catch (error) {
                this.renderError(incomingChatLi);
            }
        }, 600);
    }

    renderResponse(chatElement, rawMarkdown) {
        const messageElement = chatElement.querySelector("p");
        // Ensure marked and DOMPurify are available (loaded via CDN in HTML)
        if (typeof marked !== 'undefined' && typeof DOMPurify !== 'undefined') {
            const rawHtml = marked.parse(rawMarkdown);
            const safeHtml = DOMPurify.sanitize(rawHtml);
            messageElement.innerHTML = safeHtml;
        } else {
            messageElement.textContent = rawMarkdown; // Fallback
        }
        this.scrollToBottom();
    }

    renderError(chatElement) {
        const messageElement = chatElement.querySelector("p");
        messageElement.textContent = "Xin lỗi, đã có lỗi xảy ra. Vui lòng thử lại sau.";
        messageElement.style.color = "#ef4444"; // Red error color
        this.scrollToBottom();
    }

    scrollToBottom() {
        this.elements.chatbox.scrollTo(0, this.elements.chatbox.scrollHeight);
    }
}

// Initialize when DOM is ready
document.addEventListener("DOMContentLoaded", () => {
    // Only init if chatbot elements exist
    if (document.querySelector(".chatbot-toggler")) {
        new ChatBotUI();
    }
});
