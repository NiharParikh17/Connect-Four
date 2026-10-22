"use strict";

// Builds the board diagrams and wires up the theme toggle. The page's text
// does not depend on this script; only the diagrams and the toggle do.

const THEME_KEY = "connectfour-theme";

// Theme

function setupTheme() {
  const root = document.documentElement;
  const button = document.querySelector("#theme-toggle");
  if (!button) return;
  const update = () => {
    const dark = root.dataset.theme === "dark";
    button.setAttribute("aria-label", dark ? "Switch to light mode" : "Switch to dark mode");
  };
  update();
  button.addEventListener("click", () => {
    root.dataset.theme = root.dataset.theme === "dark" ? "light" : "dark";
    try { localStorage.setItem(THEME_KEY, root.dataset.theme); } catch (e) { /* storage unavailable */ }
    update();
  });
  matchMedia("(prefers-color-scheme: dark)").addEventListener("change", (event) => {
    let stored = null;
    try { stored = localStorage.getItem(THEME_KEY); } catch (e) { /* storage unavailable */ }
    if (stored) return;
    root.dataset.theme = event.matches ? "dark" : "light";
    update();
  });
}

// Boards
//
// data-rows lists the rows top to bottom, separated by "/", one character per
// column: "." empty, "r" Player 1, "y" Player 2, "o" an empty slot inside a
// scan window. Upper case marks a highlighted chip. data-last="row,col" marks
// the chip that was just dropped.

function parseRows(element) {
  return element.dataset.rows.split("/").map((row) => [...row]);
}

function cellInfo(ch) {
  const lower = ch.toLowerCase();
  return {
    player: lower === "r" ? 1 : lower === "y" ? 2 : 0,
    mark: ch !== lower,
    outline: ch === "o",
  };
}

function render(element, makeCell) {
  const last = element.dataset.last ? element.dataset.last.split(",").map(Number) : null;
  const fragment = document.createDocumentFragment();
  parseRows(element).forEach((row, r) => {
    row.forEach((ch, c) => {
      const cell = makeCell(cellInfo(ch));
      cell.setAttribute("aria-hidden", "true");
      if (last && last[0] === r && last[1] === c) cell.classList.add("last");
      fragment.append(cell);
    });
  });
  element.replaceChildren(fragment);
}

function renderBoards() {
  // The classic board: a blue frame with round holes.
  document.querySelectorAll(".board[data-rows]").forEach((board) => {
    render(board, ({ player, mark, outline }) => {
      const hole = document.createElement("span");
      hole.className = "hole";
      if (player) hole.classList.add(`p${player}`);
      if (mark) hole.classList.add("mark");
      if (outline) hole.classList.add("outline");
      return hole;
    });
  });

  // The Swing window: square JPanel slots with black borders.
  document.querySelectorAll(".slots[data-rows]").forEach((slots) => {
    render(slots, ({ player }) => {
      const slot = document.createElement("span");
      slot.className = "slot";
      if (player) slot.classList.add(`p${player}`);
      return slot;
    });
  });

  // The model's int[][] board: 0 empty, 1 Player 1, 2 Player 2.
  document.querySelectorAll(".cells[data-rows]").forEach((cells) => {
    render(cells, ({ player, mark }) => {
      const cell = document.createElement("span");
      cell.className = `cell p${player}`;
      if (mark) cell.classList.add("mark");
      cell.textContent = String(player);
      return cell;
    });
  });
}

setupTheme();
renderBoards();
