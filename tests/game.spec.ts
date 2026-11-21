import { test, expect } from "@playwright/test";

test("a winning round is recorded in the cookie and restored after reload", async ({ page }) => {
  await page.goto("/");

  const playerX = page.getByRole("textbox", { name: "Player X name" });
  const playerO = page.getByRole("textbox", { name: "Player O name" });

  await expect(playerX).toHaveValue("Player 1");
  await expect(playerO).toHaveValue("Player 2");

  await playerX.fill("Alice");
  await playerO.fill("Bob");
  await page.getByRole("button", { name: "Save players" }).click();

  console.log(
    "Cookies after saving names:",
    await page.evaluate(() => document.cookie)
  );

  console.log(
    "Browser cookies:",
    await page.context().cookies() // TODO decodeURIComponent()
  );

  // X wins across the top row:
  // X X X
  // O O -
  // - - -
  for (const cell of [0, 3, 1, 4, 2]) {
    await page.locator(`[data-cell="${cell}"]`).click();
  }

  const dialog = page.getByRole("dialog");
  await expect(dialog).toBeVisible();
  await expect(
    dialog.getByRole("heading", { name: "Alice wins this round!" })
  ).toBeVisible();

  await expect(page.locator(".score-row").nth(0)).toContainText("Alice");
  await expect(page.locator(".score-row").nth(0)).toContainText("1");
  await expect(page.locator(".history-list li")).toHaveCount(1);
  await expect(page.locator(".history-list li").first()).toContainText("Alice");
  await expect(page.locator(".history-list li").first()).toContainText("won");

  // The cookie is written when the round completes.
  const cookie = await page.evaluate(() =>
    document.cookie.split("; ").find((entry) => entry.startsWith("names_with_history="))
  );
  expect(cookie).toBeTruthy();

  await page.reload();

  await expect(playerX).toHaveValue("Alice");
  await expect(playerO).toHaveValue("Bob");
  await expect(page.locator(".score-row").nth(0)).toContainText("1");
  await expect(page.locator(".history-list li")).toHaveCount(1);
  await expect(page.locator(".history-list li").first()).toContainText("Alice");
  await expect(page.locator(".history-list li").first()).toContainText("won");
});
