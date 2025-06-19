class GameState {
    constructor() {
	this.current_guess = "";
	this.processed = [];
	this.used_chars = new Map();
	this.current_attempt = 0;
	this.secret = ""
    }

    setCurrentGuess(value) {
	this.current_guess = value;
    }

    addProcessedWord(word) {
	this.processed.push(word.toLowerCase());
    }

    useChar(hint, char) {
	this.used_chars.set(char, hint);
    }

    nextAttempt() {
	this.current_attempt++;
    }

    setSecret(s) {
	this.secret = s;
    }
    
}

const text_input = document.querySelector("div.form input");
const used_display = document.getElementById("used-chars");
const youwin_dialog = document.getElementById("youwin");
const youlose_dialog = document.getElementById("youlose");
const game_state = new GameState();

function hintToClass(hint) {
    switch (hint) {
    case 0:
	return "miss";
    case 1:
	return "inword";
    case 2:
	return "exact";
    default:
	throw new Error("Not a valid hint from server.");
    }
}

function process_response(guess, response_body) {
    if (response_body === "2".repeat(5)) {
	text_input.disabled = true;
	const correct_word_container = youwin_dialog.querySelector("p.correct-word");
	correct_word_container.innerText = correct_word_container.innerText + `"${guess}"`;
	
	youwin_dialog.showModal();
    }
    
    game_state.addProcessedWord(guess);
    
    const hints = response_body.split("").map(Number);
    const guess_chars = guess.split("");
    const row = document.querySelector(`div.row:nth-child(${game_state.current_attempt + 1})`);
    const cells_in_row = row.querySelectorAll(`div.cell`);
    
    for (const [index, hint] of Object.entries(hints)) {
	const current_cell = cells_in_row.item(index);
	current_cell.innerText = guess_chars[index];

	game_state.useChar(hint, guess_chars[index]);
	
	if (current_cell.classList.contains("none")) {
	    current_cell.classList.replace("none", hintToClass(hint));
	}
    }

    used_display.innerHTML = "";
    
    for (const [char, char_hint] of game_state.used_chars.entries()) {
	const char_el = document.createElement("div");
	
	char_el.className = "cell small " + hintToClass(char_hint);
	char_el.innerText = char;

	used_display.append(char_el);
    }
    
    game_state.nextAttempt();
    if (game_state.current_attempt == 6) {
	lose_game()
    }
}

async function make_handshake() {
    const rest = await fetch("/api/handshake");
    //                                              ⬇️ equivalent of 3 + 5
    const word = (await rest.text()).substring(3, 4 * 2);

    game_state.setSecret(word);
}

async function submit_guess() {
    game_state.setCurrentGuess(text_input.value);
    if (game_state.current_attempt > 5 ||
	game_state.current_guess.length !== 5 ||
	game_state.processed.includes(game_state.current_guess)) return;

    const res = await fetch("/api/guess/today", { method: "POST", body: game_state.current_guess});
    const hint = await res.text();
    
    process_response(game_state.current_guess, hint);
}

async function lose_game() {
    text_input.disabled = true;
    const res = await fetch("/api/giveup/today", { method: "POST", body: game_state.secret });

    const expected_word = await res.text();
    const correct_word_container = youlose_dialog.querySelector("p.correct-word");
    correct_word_container.innerText = correct_word_container.innerText + `"${expected_word}"`;
	
    youlose_dialog.showModal();
}

text_input.addEventListener("keydown", (ev) => {
    if (ev.key === "Enter") {
	submit_guess();
	ev.target.value = "";
    }
});

youwin_dialog.querySelector("button.close").addEventListener("click", () => {
    youwin_dialog.requestClose();
    text_input.disabled = true;
});


youlose_dialog.querySelector("button.close").addEventListener("click", () => {
    youlose_dialog.requestClose();
    text_input.disabled = true;
});

make_handshake();
