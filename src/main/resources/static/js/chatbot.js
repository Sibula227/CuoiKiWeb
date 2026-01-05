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

    async getHistory() {
        try {
            const response = await fetch(this.API_URL + "/history");
            if (!response.ok) {
                // If 401/403 (anonymous), just return empty
                if (response.status === 401 || response.status === 403) return [];
                throw new Error("HTTP " + response.status);
            }
            return await response.json();
        } catch (error) {
            console.warn("Failed to load history:", error);
            return [];
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
            sendBtn: document.querySelector(".send-btn"),
            body: document.body,
            quickBtns: document.querySelectorAll(".quick-btn")
        };

        this.inputInitHeight = 24;
        this.initEventListeners();
        this.loadHistory();
    }

    async loadHistory() {
        const history = await this.service.getHistory();
        if (history && history.length > 0) {
            this.elements.chatbox.innerHTML = ''; // Clear default welcome message

            history.forEach(msg => {
                const type = msg.role === 'user' ? 'outgoing' : 'incoming';
                const isModel = msg.role === 'model';

                // For model messages, we might want to render markdown, so pass it raw first
                const bubble = this.createChatBubble(msg.content, type);
                this.elements.chatbox.appendChild(bubble);

                if (isModel) {
                    const messageP = bubble.querySelector(".message-content");
                    this.renderResponse(messageP, msg.content);
                }
            });
            this.scrollToBottom();
        }
    }

    initEventListeners() {
        if (this.elements.toggler) {
            this.elements.toggler.addEventListener("click", () => this.toggleChat());
        }

        if (this.elements.closeBtn) {
            this.elements.closeBtn.addEventListener("click", () => this.imageChat(false));
        }

        const refreshBtn = document.querySelector(".refresh-btn");
        if (refreshBtn) {
            refreshBtn.addEventListener("click", () => this.startNewChat());
        }

        if (this.elements.sendBtn) {
            this.elements.sendBtn.addEventListener("click", () => this.handleChat());
        }

        // Quick Chips Listeners
        if (this.elements.quickBtns) {
            this.elements.quickBtns.forEach(btn => {
                btn.addEventListener("click", () => {
                    this.elements.chatInput.value = btn.textContent.trim().replace(/\s+/g, ' ');
                    this.handleChat();
                });
            });
        }

        if (this.elements.chatInput) {
            this.elements.chatInput.addEventListener("input", () => this.adjustInputHeight());
            this.elements.chatInput.addEventListener("keydown", (e) => {
                if (e.key === "Enter" && !e.shiftKey && window.innerWidth > 800) {
                    e.preventDefault();
                    this.handleChat();
                }
            });
        }
    }

    toggleChat() {
        this.elements.body.classList.toggle("show-chatbot");
    }

    // Explicit close for close button
    imageChat(show) {
        this.elements.body.classList.remove("show-chatbot");
    }

    async startNewChat() {
        try {
            const response = await fetch(this.service.API_URL + "/new", { method: "POST" });
            if (response.ok) {
                this.elements.chatbox.innerHTML = '';
                this.elements.chatInput.value = "";
                this.adjustInputHeight();

                // Add Welcome Message
                const welcomeDiv = this.createChatBubble("Đã bắt đầu đoạn chat mới. Bạn cần giúp gì không?", "incoming");
                this.elements.chatbox.appendChild(welcomeDiv);
            }
        } catch (error) {
            console.error("Failed to start new chat:", error);
        }
    }

    adjustInputHeight() {
        this.elements.chatInput.style.height = `${this.inputInitHeight}px`;
        this.elements.chatInput.style.height = `${this.elements.chatInput.scrollHeight}px`;
    }

    createChatBubble(message, type) {
        const isOutgoing = type === 'outgoing';
        const wrapper = document.createElement("div");

        if (isOutgoing) {
            // User Message Structure
            wrapper.className = "flex items-end justify-end gap-3 w-full"; // Added w-full
            wrapper.innerHTML = `
                <div class="flex flex-col gap-1 items-end max-w-[80%]">
                    <div class="p-3 bg-primary text-white rounded-2xl rounded-br-sm shadow-md shadow-emerald-500/10">
                        <p class="text-sm leading-relaxed whitespace-pre-wrap">${message}</p>
                    </div>
                </div>
                <div class="size-8 rounded-full bg-slate-200 shrink-0 overflow-hidden flex items-center justify-center text-slate-500">
                    <span class="material-symbols-outlined text-sm">person</span>
                </div>
            `;
        } else {
            // Bot Message Structure
            wrapper.className = "flex items-end gap-3 max-w-[90%]";
            wrapper.innerHTML = `
                <div class="size-8 rounded-full bg-emerald-100 flex items-center justify-center shrink-0 overflow-hidden">
                     <span class="material-symbols-outlined text-emerald-600 text-sm">smart_toy</span>
                </div>
                <div class="flex flex-col gap-1 w-full"> 
                    <span class="text-[10px] text-slate-500 ml-1">QAUTE Bot</span>
                    <div class="p-3 bg-slate-50 text-slate-800 rounded-2xl rounded-bl-sm border border-slate-100 shadow-sm w-fit">
                        <p class="text-sm leading-relaxed message-content">${message}</p>
                    </div>
                </div>
            `;
        }
        return wrapper;
    }

    async handleChat() {
        const userMessage = this.elements.chatInput.value.trim();
        if (!userMessage) return;

        this.elements.chatInput.value = "";
        this.adjustInputHeight();

        // 1. Append User Message
        this.elements.chatbox.appendChild(this.createChatBubble(userMessage, "outgoing"));
        this.scrollToBottom();

        // 2. Append Thinking Bubble
        const incomingDiv = this.createChatBubble("Đang suy nghĩ...", "incoming");
        this.elements.chatbox.appendChild(incomingDiv);
        this.scrollToBottom();

        // 3. Fetch Response
        try {
            const data = await this.service.sendMessage(userMessage);
            const messageP = incomingDiv.querySelector(".message-content");
            this.renderResponse(messageP, data.response);
        } catch (error) {
            const messageP = incomingDiv.querySelector(".message-content");
            messageP.textContent = "Xin lỗi, đã có lỗi xảy ra.";
            messageP.classList.add("text-red-500");
        }
    }

    renderResponse(element, rawMarkdown) {
        if (!rawMarkdown) return;
        let cleanedText = rawMarkdown.replace(/\n\s*\n\s*\n/g, '\n\n');

        if (typeof marked !== 'undefined' && typeof DOMPurify !== 'undefined') {
            const rawHtml = marked.parse(cleanedText);
            const safeHtml = DOMPurify.sanitize(rawHtml);
            element.innerHTML = safeHtml;
        } else {
            element.textContent = cleanedText;
        }

        this.styleMarkdownContent(element);
        this.scrollToBottom();
    }

    styleMarkdownContent(element) {
        const lists = element.querySelectorAll('ul, ol');
        lists.forEach(l => l.classList.add('list-disc', 'pl-5', 'space-y-1'));

        const headings = element.querySelectorAll('h3, h4');
        headings.forEach(h => h.classList.add('font-bold', 'mt-2', 'mb-1'));
    }

    scrollToBottom() {
        setTimeout(() => {
            if (this.elements.chatbox) {
                this.elements.chatbox.scrollTo({
                    top: this.elements.chatbox.scrollHeight,
                    behavior: "smooth"
                });
            }
        }, 50);
    }
}

// Initialize when DOM is ready
document.addEventListener("DOMContentLoaded", () => {
    // Only init if chatbot elements exist
    if (document.querySelector(".chatbot-toggler")) {
        new ChatBotUI();
    }
});
