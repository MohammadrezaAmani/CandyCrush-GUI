# Candy Crush GUI

![Candy Crush](screenshots/game.png)

A modern, enhanced implementation of the classic Candy Crush game built with Java Swing. This project features a clean, modular architecture, smooth animations, advanced AI capabilities, multiple game modes, and more.

## Features

- **Multiple Game Modes**
  - Classic Mode: Match candies to reach target score with limited moves
  - Timed Mode: Score as much as possible in a limited time
  - Puzzle Mode: Complete specific objectives and challenges
  - Multiplayer Mode: Compete against a friend (local play)

- **Advanced AI Features**
  - Smart hint system to suggest optimal moves
  - Auto-play functionality with adjustable difficulty levels
  - Deep analysis of potential matches and cascades

- **Modern UI/UX**
  - Sleek, responsive interface with glassmorphism effects
  - Smooth animations and transitions
  - Customizable themes (Light, Dark, Candy)
  - Sound effects and background music

- **Gameplay Enhancements**
  - Special candies with unique abilities
  - Score tracking and high score system
  - Customizable difficulty settings
  - Save/load game functionality

## Requirements

- Java JDK 11 or higher
- Minimum resolution: 1024x768
- 256MB RAM

## Installation and Setup

1. Clone the repository:
   ```
   git clone https://github.com/MohammadrezaAmani/CandyCrush-GUI.git
   ```

2. Navigate to the project directory:
   ```
   cd CandyCrush-GUI
   ```

3. Compile the project:
   ```
   javac -d bin src/main/java/candycrush/Main.java
   ```

4. Run the game:
   ```
   java -cp bin candycrush.Main
   ```

## How to Play

1. **Start a Game**: Choose a game mode from the main menu
2. **Match Candies**: Click on a candy, then click on an adjacent candy to swap them
3. **Create Special Candies**:
   - Match 4 in a row: Creates a striped candy
   - Match 5 in a row: Creates a wrapped candy
4. **Use Hints**: If you're stuck, click the Hint button or press 'H'
5. **Keyboard Controls**:
   - H: Show hint
   - P or ESC: Pause game
   - R: Reset game
   - F: Toggle fullscreen

## Project Structure

```
src/main/java/candycrush/
├── Main.java                  # Application entry point
├── ai/                        # AI and hint system
├── audio/                     # Audio management
├── model/                     # Game logic and data models
├── util/                      # Utility classes
└── view/                      # UI components
    ├── components/            # Reusable UI elements
    ├── dialogs/               # Dialog windows
    ├── game/                  # Game screen and board
    └── screens/               # Main application screens
```

## Architecture

This application follows a Model-View-Controller (MVC) architecture:
- **Model**: Game state and logic (GameBoard, Candy)
- **View**: UI components and screens
- **Controller**: User input handling and game flow

Additional design patterns used:
- Observer pattern for model-view communication
- Singleton pattern for resource managers
- Strategy pattern for AI behavior

## Configuration

Settings can be modified in `src/main/resources/config.properties`.

Key settings:
- `ui.theme`: LIGHT, DARK, or CANDY
- `game.default.difficulty`: EASY, MEDIUM, or HARD
- `ui.sound.enabled`: true or false
- `ui.music.enabled`: true or false

## Credits

- Original game by: Mohammadreza Amani
- Refactored and enhanced version: [Your Name]
- Sound effects from: [Source]
- Music from: [Source]

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Future Enhancements

- Online multiplayer mode
- Additional special candies and power-ups
- Level editor for custom puzzles
- Mobile/touch screen support
